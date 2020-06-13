package com.example.applicationqr.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.applicationqr.R;
import com.example.applicationqr.onFragmentInteractionListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddSessionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddSessionFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private onFragmentInteractionListener fragmentInteractionListener;
    private FirebaseFirestore db;

    private TextInputLayout sessionTime;
    private Button addSessionButton;

    public AddSessionFragment()
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
     * @return A new instance of fragment AddSessionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddSessionFragment newInstance(String param1, String param2)
    {
        AddSessionFragment fragment = new AddSessionFragment();
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
        sessionTime = v.findViewById(R.id.add_class_name);
        addSessionButton = v.findViewById(R.id.add_class_button);

        addSessionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    Toast.makeText(getContext(), "Adding Session..", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(getContext(), "Name and Code cannot be empty", Toast.LENGTH_SHORT).show();
                }

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
