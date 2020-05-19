package com.example.applicationqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity implements LoginFragment.onFragmentInteractionListener {

    private FirebaseAuth mAuth;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        InitUI();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(mAuth.getCurrentUser() == null)
        {
            LoginFragment loginFragment = LoginFragment.newInstance(null,null);
            getSupportFragmentManager().beginTransaction().add(R.id.main_layout_view, loginFragment).commit();
        }
        else
        {
            Log.d("AuthActivity",mAuth.getCurrentUser().toString());

            // Start menu activity if user is already logged in
            Intent intent = new Intent();
            intent.setClass(this,MainMenuActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    @Override
    public void startRegisterFragment()
    {
        SignUpFragment signUpFragment = SignUpFragment.newInstance(null,null);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout_view, signUpFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    private void InitUI()
    {
        myToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
    }
}
