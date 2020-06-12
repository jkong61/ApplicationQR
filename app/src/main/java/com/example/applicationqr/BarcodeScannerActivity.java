package com.example.applicationqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageInfo;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.ImageAnalysisConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.os.Bundle;

import android.util.Log;
import android.view.Surface;
import android.view.ViewGroup;

import android.widget.Toast;

import com.example.applicationqr.model.User;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BarcodeScannerActivity extends AppCompatActivity implements ImageAnalysis.Analyzer
{
    private final String TAG = getClass().getName();
    private User currentUser;
    private int REQUEST_CODE_PERMISSIONS = 101;
    private ExecutorService cameraExecutor;
    private FirebaseVisionBarcodeDetector detector;

    private PreviewView viewFinder;
    private ProcessCameraProvider cameraProvider;
    private CameraSelector cameraSelector;
    private Preview preview;
    private ImageAnalysis imageAnalysis;
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        InitUI();
        InitBundles();
        if(isCameraPermissionGranted())
            startCamera(); //start camera if permission has been granted by user
        else
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA"}, REQUEST_CODE_PERMISSIONS);

        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void InitUI()
    {
        viewFinder = findViewById(R.id.viewFinder);
    }

    private void startCamera()
    {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() ->
        {
            try
            {
                cameraProvider = cameraProviderFuture.get();

                Log.d(TAG, "startCamera: start lifecycle");
                cameraProvider.unbindAll();
                cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

                preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.createSurfaceProvider());

                imageAnalysis = new ImageAnalysis.Builder().build();
                imageAnalysis.setAnalyzer((Executor) this, this);

                FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder().setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE).build();
                detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
            }
            catch (ExecutionException | InterruptedException e)
            {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(isCameraPermissionGranted())
            startCamera();
        else
        {
            Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean isCameraPermissionGranted()
    {
        return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void analyze(ImageProxy image)
    {
        int degrees = rotationDegreesToFirebaseRotation(image.getImageInfo().getRotationDegrees());
        @SuppressLint("UnsafeExperimentalUsageError") FirebaseVisionImage visionImage = FirebaseVisionImage.fromMediaImage(image.getImage(), degrees);

        detector.detectInImage(visionImage)
                .addOnSuccessListener(firebaseVisionBarcodes ->
                {
                    Log.d("QrCodeAnalyzer", "something detected" + firebaseVisionBarcodes);
                    onQRCodesDetected(firebaseVisionBarcodes);
                })
                .addOnFailureListener(e -> Log.e("QrCodeAnalyzer", "something went wrong", e));
    }

    private void onQRCodesDetected(List<FirebaseVisionBarcode> firebaseVisionBarcodes)
    {
        if(firebaseVisionBarcodes.size() > 0)
        {
            for (FirebaseVisionBarcode barcode: firebaseVisionBarcodes)
            {
                Log.d(TAG, "onQRCodesDetected_TYPE: " + barcode.getDisplayValue());
                Log.d(TAG, "onQRCodesDetected_RAW: " + barcode.getRawValue());
                Log.d(TAG, "onQRCodesDetected_VALUE: " + barcode.toString());

                // Return the data to main menu to start another fragment
                returnReply(barcode);
                break;
            }
        }
    }

    private int rotationDegreesToFirebaseRotation(int rotationDegrees)
    {
        switch (rotationDegrees)
        {
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
        }
        throw new IllegalArgumentException("Not supported");
    }

    private void InitBundles()
    {
        Intent intent = getIntent();
        currentUser = intent.getExtras().getParcelable("USER");
    }

    private void returnReply(FirebaseVisionBarcode barcode)
    {
        if(barcode.getValueType() == FirebaseVisionBarcode.TYPE_URL && currentUser.getType() == 2)
        {
            Intent resultIntent = new Intent(this, MainMenuActivity.class);
            resultIntent.putExtra("URL_VALUE", barcode.getDisplayValue());
            setResult(RESULT_OK, resultIntent);
//            finish();
        }

        if(barcode.getValueType() == FirebaseVisionBarcode.TYPE_TEXT && currentUser.getType() == 1)
        {
            Intent resultIntent = new Intent(this, MainMenuActivity.class);
            resultIntent.putExtra("STUDENT_ID", barcode.getDisplayValue());
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}
