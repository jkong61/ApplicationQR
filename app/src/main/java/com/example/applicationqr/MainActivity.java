package com.example.applicationqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements LoginFragment.onFragmentInteractionListener {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null)
        {
            LoginFragment loginFragment = LoginFragment.newInstance(null,null);
            getSupportFragmentManager().beginTransaction().add(R.id.main_layout, loginFragment).commit();
        }
        else
        {
            // TODO start the menu fragment
            Log.d("MainActivity",mAuth.getCurrentUser().toString());
            if(true)
            {
                //TODO start teacher fragment
            }
            else
            {
                //TODO start student fragment
            }
        }
    }

    @Override
    public void startRegisterFragment()
    {
        SignUpFragment signUpFragment = SignUpFragment.newInstance(null,null);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, signUpFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.signOut();
    }

    private void InitUI()
    {
        ImageView qrCode = findViewById(R.id.qr_code);
    }

}
