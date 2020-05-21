package com.example.applicationqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity implements onFragmentInteractionListener {

    private FirebaseAuth mAuth;
    private Toolbar myToolbar;
    LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        InitUI();
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null)
        {
            if (savedInstanceState != null)
                getSupportFragmentManager().findFragmentByTag("login_tag");
            else
            {
                loginFragment = LoginFragment.newInstance(null,null);
                getSupportFragmentManager().beginTransaction().add(R.id.main_layout_view, loginFragment,"login_tag").commit();
            }
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

    private void InitUI()
    {
        myToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    public void onFragmentMessage(String TAG, Object data)
    {
        if (TAG.equals(LoginFragment.class.getName()))
        {
            SignUpFragment signUpFragment = SignUpFragment.newInstance(null,null);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_layout_view, signUpFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }
        else if (TAG.equals(SignUpFragment.class.getName()))
        {
            getSupportFragmentManager().popBackStack();
        }
    }
}
