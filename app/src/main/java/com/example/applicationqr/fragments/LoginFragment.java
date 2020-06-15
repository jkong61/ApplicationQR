package com.example.applicationqr.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.applicationqr.MainMenuActivity;
import com.example.applicationqr.R;
import com.example.applicationqr.onFragmentInteractionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment
{
    private static final String TAG = LoginFragment.class.getName();
    private TextView register_link;
    private Button login_button;
    private EditText login_email, login_password;
    private FirebaseAuth mAuth;
    private ConstraintLayout loading_panel, login_form;

    private onFragmentInteractionListener fragmentInteractionListener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment()
    {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2)
    {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        InitUI(v);
        return v;
    }

    private void InitUI(View v)
    {
        login_email = v.findViewById(R.id.login_email_input);
        login_password = v.findViewById(R.id.login_password_input);
        login_button = v.findViewById(R.id.login_button);
        loading_panel = getActivity().findViewById(R.id.loading_panel);
        loading_panel.setVisibility(View.INVISIBLE);
        login_form = v.findViewById(R.id.login_form_layout);

        login_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = login_email.getText().toString();
                String password = login_password.getText().toString();
                if (email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(getContext(), "Email and Password cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getContext(), "Logging in..", Toast.LENGTH_SHORT).show();
                    login_button.setEnabled(false);
                    loading_panel.setVisibility(View.VISIBLE);
                    login_form.setVisibility(View.INVISIBLE);
                    SignInUser(email,password);
                }
            }
        });

        // Initialize register link
        register_link = v.findViewById(R.id.register_link);
        register_link.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                fragmentInteractionListener.onFragmentMessage(TAG,null);
            }
        });
    }

    private void SignInUser(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(getContext(), "Login Success!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    // Start the "main" Activity
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), MainMenuActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    login_button.setEnabled(true);
                    loading_panel.setVisibility(View.INVISIBLE);
                    login_form.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), String.format("Authentication failed. %s", task.getException().getMessage()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        setRetainInstance(true);
        try
        {
            fragmentInteractionListener = (onFragmentInteractionListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + "must implement onFragmentInteractionListener");
        }
    }

}
