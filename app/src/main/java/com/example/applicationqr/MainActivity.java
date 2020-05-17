package com.example.applicationqr;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitUI();


        float scale = this.getResources().getDisplayMetrics().density;
        String content = "1";
        Bitmap b = QRGenerator.getInstance().getQRBitmap(content, scale);
//        qrCode.setImageBitmap(b);
    }

    private void InitUI()
    {
        ImageView qrCode = findViewById(R.id.qr_code);
    }
}
