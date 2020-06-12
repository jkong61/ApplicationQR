package com.example.applicationqr.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.applicationqr.MainMenuActivity;
import com.example.applicationqr.R;
import com.example.applicationqr.onFragmentInteractionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddClassFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddClassFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String TAG = AddClassFragment.class.getName();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private onFragmentInteractionListener fragmentInteractionListener;
    private FirebaseFirestore db;

    private TextInputLayout classNameInput, classCodeInput;
    private Button addClassButton;

    public AddClassFragment()
    {
        db = FirebaseFirestore.getInstance();
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddClassFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddClassFragment newInstance(String param1, String param2)
    {
        AddClassFragment fragment = new AddClassFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_add_class, container, false);
        // Inflate the layout for this fragment
        InitUI(v);

        return v;
    }

    private void InitUI(View v)
    {
        classNameInput = v.findViewById(R.id.add_class_name);
        classCodeInput = v.findViewById(R.id.add_class_code);
        addClassButton = v.findViewById(R.id.add_class_button);

        addClassButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    Toast.makeText(getContext(), "Adding classroom..", Toast.LENGTH_SHORT).show();
                    String name = classNameInput.getEditText().getText().toString();
                    String code = classCodeInput.getEditText().getText().toString();
                    CreateClassroom(name, code);
                }
                catch (Exception e)
                {
                    Toast.makeText(getContext(), "Name and Code cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void CreateClassroom(String classname, String classcode)
    {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("coursename", classname);
        data.put("coursecode", classcode);
        data.put("enrolled", new ArrayList<>());

        db.collection("classes").add(data)
        .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
        {
            @Override
            public void onSuccess(DocumentReference documentReference)
            {
                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                Toast.makeText(getContext(), "Successfully added classroom!", Toast.LENGTH_SHORT).show();
                fragmentInteractionListener.onFragmentMessage(TAG, null);
            }
        })
        .addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.w(TAG, "Error writing document", e);
            }
        });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            fragmentInteractionListener = (onFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement onFragmentInteractionListener");
        }
    }
}
