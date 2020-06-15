package com.example.applicationqr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

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

import com.example.applicationqr.fragments.AddClassFragment;
import com.example.applicationqr.fragments.ClassListFragment;
import com.example.applicationqr.fragments.DisplayQRCodeFragment;
import com.example.applicationqr.fragments.MainMenuFragment;
import com.example.applicationqr.fragments.ResultsFragment;
import com.example.applicationqr.fragments.SessionListFragment;
import com.example.applicationqr.fragments.UpdateProfileFragment;
import com.example.applicationqr.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Map;

public class MainMenuActivity extends AppCompatActivity implements onFragmentInteractionListener
{
    private static final String TAG = MainMenuActivity.class.getName();
    public final static int BARCODE_URL_REQUEST = 1;
    public static User currentUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Toolbar mainMenuToolbar;
    public final static int BARCODE_REGISTER_REQUEST = 2;
    private MainMenuFragment mainMenuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (savedInstanceState != null)
        {
            findViewById(R.id.loading_panel).setVisibility(View.INVISIBLE);
            getSupportFragmentManager().findFragmentByTag("menu_tag");
            currentUser = savedInstanceState.getParcelable("USER");
        }
        else
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
            // Start login activity activity if user is not logged in
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
        mainMenuToolbar.setTitle("Main Menu");
        setSupportActionBar(mainMenuToolbar);
        mainMenuToolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
    }

    private void setUser()
    {
        DocumentReference docRef = db.collection("users").document(mAuth.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> db_data = document.getData();
                        currentUser = new User(mAuth.getUid(), db_data.get("name").toString(), (long)db_data.get("type"), db_data.get("userID").toString());
                        findViewById(R.id.loading_panel).setVisibility(View.INVISIBLE);

                        // Set proper Fragment for Activity
                        mainMenuFragment = MainMenuFragment.newInstance(currentUser, null);
                        getSupportFragmentManager().beginTransaction().add(R.id.main_menu_view, mainMenuFragment,"menu_tag").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                    }
                    else
                        Log.d(TAG, "No such document");
                }
                else
                    Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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

    @Override
    public void onFragmentMessage(String TAG, Object data)
    {
        // Main Menu interaction
        if (TAG.equals(MainMenuFragment.class.getName()) && data != null)
        {
            int id = (int) data;
            switch (id)
            {
                // Student Menus
                case (R.id.button_display_student_code):
                {
                    Log.d(TAG, "student display code button");
                    DisplayQRCodeFragment displayCodeFragment = DisplayQRCodeFragment.newInstance(currentUser, null);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_menu_view, displayCodeFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                    break;
                }
                case (R.id.button_update_student):
                {
                    Log.d(TAG, "student update button");
                    UpdateProfileFragment updateProfileFragment = UpdateProfileFragment.newInstance(currentUser, null);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_menu_view, updateProfileFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                    break;
                }
                case (R.id.button_scan_code):
                {
                    Log.d(TAG, "student scan button");
                    Intent barcodeScannerIntent = new Intent(this, BarcodeScannerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("USER",currentUser);
                    barcodeScannerIntent.putExtras(bundle);
                    startActivityForResult(barcodeScannerIntent, BARCODE_URL_REQUEST);
                    break;
                }

                // Teacher Menus
                case (R.id.button_register_student):
                {
                    ClassListFragment classListFragment = ClassListFragment.newInstance(null, id);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_menu_view, classListFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                    break;
                }
                case (R.id.button_classes):
                {
                    Log.d(TAG, "classes button");
                    ClassListFragment classListFragment = ClassListFragment.newInstance(null, id);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_menu_view, classListFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                    break;
                }
                case (R.id.button_take_attendance):
                {
                    Log.d(TAG, "take attendance button");
                    ClassListFragment classListFragment = ClassListFragment.newInstance(null, id);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_menu_view, classListFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                    break;
                }
            }
        }

        // Finish Update Profile Fragment
        else if (TAG.equals(UpdateProfileFragment.class.getName()))
        {
            if (data != null)
            {
                currentUser = (User) data;
                if (mainMenuFragment != null)
                    mainMenuFragment.UpdateSelf(currentUser);
                getSupportFragmentManager().popBackStack();
                findViewById(R.id.loading_panel).setVisibility(View.INVISIBLE);
            }
        }

        else if(TAG.equals(ResultsFragment.class.getName()))
        {
            getSupportFragmentManager().popBackStack();
            findViewById(R.id.loading_panel).setVisibility(View.INVISIBLE);
        }

        else if(TAG.equals(SessionListFragment.class.getName()))
        {
            findViewById(R.id.loading_panel).setVisibility(View.INVISIBLE);
        }

        else if(TAG.equals(ClassListFragment.class.getName()))
        {
            // Start new Class fragment
            findViewById(R.id.loading_panel).setVisibility(View.INVISIBLE);
            AddClassFragment addClassFragment = AddClassFragment.newInstance(null, null);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_menu_view, addClassFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }

        else if(TAG.equals(AddClassFragment.class.getName()))
        {
            // Complete the Add Class fragment
            getSupportFragmentManager().popBackStack();
            findViewById(R.id.loading_panel).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable("USER", currentUser);
        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == BARCODE_URL_REQUEST && resultCode == RESULT_OK)
        {
            String url = data.getStringExtra("URL_VALUE");
            Log.d(TAG, "onActivityResult: URL "+ url);
            ResultsFragment resultsFragment = ResultsFragment.newInstance(currentUser, url);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_menu_view, resultsFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }

        if(requestCode == BARCODE_REGISTER_REQUEST && resultCode == RESULT_OK)
        {
            String student_id = data.getStringExtra("STUDENT_ID");
            Log.d(TAG, "onActivityResult: ID "+ student_id);
            ResultsFragment resultsFragment = ResultsFragment.newInstance(currentUser, student_id);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_menu_view, resultsFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }
    }
}
