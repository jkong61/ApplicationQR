package com.example.applicationqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainMenuActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private Toolbar main_menu_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        mAuth = FirebaseAuth.getInstance();
        InitUI();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // User validity
        if(mAuth.getCurrentUser() == null)
        {
            // Start auth activity activity if user is already logged in
            Intent intent = new Intent();
            intent.setClass(this,AuthActivity.class);
            startActivity(intent);
            this.finish();
        }
        else
        {
            MainMenuFragment mainMenuFragment = MainMenuFragment.newInstance(null,null);
            getSupportFragmentManager().beginTransaction().add(R.id.main_menu_view, mainMenuFragment).commit();

            // TODO Start menu fragments if user is already logged in

        }
    }


    private void InitUI()
    {
        // Initialize the toolbar
        main_menu_toolbar = findViewById(R.id.main_menu_toolbar);
        setSupportActionBar(main_menu_toolbar);
        main_menu_toolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings)
        {
            Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show();
            Log.d("MainMenuActivity","Signed Out");
            mAuth.signOut();
            Intent intent = new Intent();
            intent.setClass(this, AuthActivity.class);
            startActivity(intent);
            this.finish();
            return true;
        }
        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }
}
