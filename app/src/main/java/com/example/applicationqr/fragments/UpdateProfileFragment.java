package com.example.applicationqr.fragments;

import android.content.Context;
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
import android.widget.Toast;

import com.example.applicationqr.R;
import com.example.applicationqr.model.User;
import com.example.applicationqr.onFragmentInteractionListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateProfileFragment extends Fragment
{
    private static final String TAG = UpdateProfileFragment.class.getName();
    private EditText studentName, studentID;
    private Button updateButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ConstraintLayout loadingPanel, update_layout;

    private onFragmentInteractionListener fragmentInteractionListener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private User mParam1;
    private String mParam2;

    public UpdateProfileFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateProfileFragment newInstance(User param1, String param2) {
        UpdateProfileFragment fragment = new UpdateProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, param1);
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
            mParam1 = getArguments().getParcelable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loadingPanel = getActivity().findViewById(R.id.loading_panel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_update_profile, container, false);
        InitUI(v);
        return v;
    }

    private void InitUI(View v)
    {
        studentName = v.findViewById(R.id.student_update_name_input);
        studentName.setText(mParam1.getName());

        studentID = v.findViewById(R.id.student_update_id_input);
        studentID.setText(mParam1.getUserID());

        update_layout = v.findViewById(R.id.update_form_layout);
        updateButton = v.findViewById(R.id.student_update_button);
        updateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String name = studentName.getText().toString();
                final String id = studentID.getText().toString();
                Log.d(TAG,String.format("Update button clicked. Student Name: %s, ID: %s",name,id));
                loadingPanel.setVisibility(View.VISIBLE);
                update_layout.setVisibility(View.INVISIBLE);

                Map<String, Object> data = new HashMap<String, Object>();
                data.put("name", name);
                data.put("userID", id);

                DocumentReference docRef = db.collection("users").document(mAuth.getUid());
                docRef.update(data).addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Toast.makeText(getContext(),"Profile Successfully Updated",Toast.LENGTH_SHORT).show();

                        User u = new User(mAuth.getUid(), name,2,id);

                        // Finish Fragment
                        fragmentInteractionListener.onFragmentMessage(TAG,u);
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.w(TAG, "Error updating document", e);
                        Toast.makeText(getContext(),String.format("Failed to Update Document: %s", e.getMessage()),Toast.LENGTH_SHORT).show();
                        loadingPanel.setVisibility(View.INVISIBLE);
                        update_layout.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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
