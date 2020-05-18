package com.example.applicationqr;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SignUpFragment signUpFragment = SignUpFragment.newInstance(null,null);
        getSupportFragmentManager().beginTransaction().add(R.id.main_layout, signUpFragment).commit();

    }

    private void InitUI()
    {
        ImageView qrCode = findViewById(R.id.qr_code);
    }
}
