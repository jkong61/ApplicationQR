package com.example.applicationqr.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.applicationqr.R;
import com.example.applicationqr.adapters.ClassAdapter;
import com.example.applicationqr.model.Classroom;
import com.example.applicationqr.onFragmentInteractionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

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
    private ArrayList<Classroom> classrooms;
    private ClassAdapter classAdapter;
    private FirebaseFirestore db;
    private onFragmentInteractionListener fragmentInteractionListener;

    private TextView nothingHere;
    private RecyclerView recyclerView;
    private FloatingActionButton addButton;

    public ClassListFragment()
    {
        db = FirebaseFirestore.getInstance();
        classrooms = new ArrayList<>();
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
        InitUI(v);
        getClassrooms();
        return v;
    }

    private void InitUI(View v)
    {
        Toolbar mainMenuToolbar = getActivity().findViewById(R.id.main_menu_toolbar);
        if(request == R.id.button_take_attendance)
            mainMenuToolbar.setTitle("Classrooms (Take Attendance)");
        else if (request == R.id.button_register_student)
            mainMenuToolbar.setTitle("Classrooms (Register Student)");
        else
            mainMenuToolbar.setTitle("Classrooms (View Details)");

        nothingHere = v.findViewById(R.id.nothing_here);
        recyclerView = v.findViewById(R.id.recyclerView_class);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        classAdapter = new ClassAdapter(request, classrooms);
        recyclerView.setAdapter(classAdapter);

        addButton = v.findViewById(R.id.addButton);
        // Checks from which button did the click originate from to hide the floating action button
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

    private void getClassrooms()
    {
        ArrayList<Classroom> tempcollection = new ArrayList<>();
        Source source = Source.DEFAULT;

        db.collection("classes").get(source).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    if(classrooms.size() != task.getResult().size())
                    {
                        getActivity().findViewById(R.id.loading_panel).setVisibility(View.VISIBLE);

                        classrooms.clear();
                        for (QueryDocumentSnapshot document : task.getResult())
                        {
                            Map<String,Object> fields = document.getData();
                            ArrayList<DocumentReference> documentReferences = (ArrayList<DocumentReference>) fields.get("enrolled");
                            tempcollection.add(new Classroom(document.getId(), fields.get("coursename").toString(), fields.get("coursecode").toString(), documentReferences.size()));
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                        // Turn off loading screen

                        if(task.getResult().isEmpty())
                            nothingHere.setVisibility(View.VISIBLE);
                        else
                        {
                            Collections.sort(tempcollection, Classroom.nameComparator);
                            classrooms.addAll(tempcollection);
                            classAdapter.notifyItemRangeChanged(0, task.getResult().size());
                        }
                    }
                    getActivity().findViewById(R.id.loading_panel).setVisibility(View.INVISIBLE);
                }
                else
                    Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

}
