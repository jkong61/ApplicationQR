package com.example.applicationqr.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.applicationqr.R;
import com.example.applicationqr.adapters.ClassAdapter;
import com.example.applicationqr.onFragmentInteractionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClassListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClassListFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String TAG = ClassListFragment.class.getName();
    private String mParam1;
    private int request;
    private onFragmentInteractionListener fragmentInteractionListener;

    private RecyclerView recyclerView;
    private FloatingActionButton addButton;

    public ClassListFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClassListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClassListFragment newInstance(String param1, int param2)
    {
        ClassListFragment fragment = new ClassListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
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
            request = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_class_list, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ClassAdapter(request));

        addButton = v.findViewById(R.id.addButton);

        if(request == R.id.button_register_student || request == R.id.button_take_attendance)
            addButton.setVisibility(View.INVISIBLE);
        else
        {
            addButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    fragmentInteractionListener.onFragmentMessage(TAG, null);
                }
            });
        }
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
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
