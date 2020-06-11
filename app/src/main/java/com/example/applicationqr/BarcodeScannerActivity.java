package com.example.applicationqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.os.Bundle;

import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;

import android.widget.Toast;

import com.example.applicationqr.model.User;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import java.util.List;


public class BarcodeScannerActivity extends AppCompatActivity implements ImageAnalysis.Analyzer
{
    private final String TAG = getClass().getName();
    private User currentUser;
    private int REQUEST_CODE_PERMISSIONS = 101;

    private TextureView textureView;
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
    }

    private void InitUI()
    {
        textureView = findViewById(R.id.texture_view);
    }

    private void startCamera()
    {
        PreviewConfig previewConfig = new PreviewConfig.Builder().setLensFacing(CameraX.LensFacing.BACK).build();
        Preview preview = new Preview(previewConfig);

        preview.setOnPreviewOutputUpdateListener(output ->
        {
            ViewGroup parent = (ViewGroup) textureView.getParent();
            parent.removeView(textureView);
            parent.addView(textureView, 0);

            textureView.setSurfaceTexture(output.getSurfaceTexture());
            updateTransform();
        });

        ImageAnalysisConfig imageAnalysisConfig = new ImageAnalysisConfig.Builder().build();
        ImageAnalysis imageAnalysis = new ImageAnalysis(imageAnalysisConfig);

        imageAnalysis.setAnalyzer(this);

        CameraX.bindToLifecycle(this, preview, imageAnalysis);
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

    private void updateTransform(){
        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        int rotationDgr;
        int rotation = (int)textureView.getRotation();

        switch(rotation){
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        mx.postRotate((float)rotationDgr, cX, cY);
        textureView.setTransform(mx);
    }

    @Override
    public void analyze(ImageProxy image, int rotationDegrees)
    {
        FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE).build();

        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

        int rotation = rotationDegreesToFirebaseRotation(rotationDegrees);
        FirebaseVisionImage visionImage = FirebaseVisionImage.fromMediaImage(image.getImage(), rotation);

        detector.detectInImage(visionImage)
                .addOnSuccessListener(firebaseVisionBarcodes ->
                {
                    Log.d("QrCodeAnalyzer", "something detected");
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
            finish();
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
