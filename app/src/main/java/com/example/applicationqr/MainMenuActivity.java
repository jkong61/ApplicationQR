package com.example.applicationqr;

import androidx.annotation.NonNull;
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
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Map;

public class MainMenuActivity extends AppCompatActivity
{
    private static final String TAG = "MainMenuActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Toolbar mainMenuToolbar;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        setUser();
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
    }


    private void InitUI()
    {
        // Initialize the toolbar
        mainMenuToolbar = findViewById(R.id.main_menu_toolbar);
        setSupportActionBar(mainMenuToolbar);
        mainMenuToolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
    }

    private void setUser()
    {
        DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> db_data = document.getData();
                        currentUser = new User(db_data.get("name").toString(), (long)db_data.get("type"));
                        findViewById(R.id.loading_panel).setVisibility(View.GONE);

                        MainMenuFragment mainMenuFragment = MainMenuFragment.newInstance(currentUser,null);
                        getSupportFragmentManager().beginTransaction().add(R.id.main_menu_view, mainMenuFragment).commit();

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
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
