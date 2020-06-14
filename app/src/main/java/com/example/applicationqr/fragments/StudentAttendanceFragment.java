package com.example.applicationqr.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.example.applicationqr.adapters.StudentAdapter;
import com.example.applicationqr.model.ClassSession;
import com.example.applicationqr.model.Student;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableReference;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudentAttendanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentAttendanceFragment extends Fragment
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private final String TAG = StudentAttendanceFragment.class.getName();
    // TODO: Rename and change types of parameters
    private ClassSession currentClassSession;
    private String mParam2;
    private String mParam3;

    private FirebaseFunctions firebaseFunctions;
    private TextView nothingHere;
    private RecyclerView recyclerView_students;
    private ArrayList<Student> students;
    private StudentAdapter studentAdapter;


    public StudentAttendanceFragment()
    {
        // Required empty public constructor
        firebaseFunctions = FirebaseFunctions.getInstance();
        students = new ArrayList<>();
        studentAdapter = new StudentAdapter(students);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentAttendanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentAttendanceFragment newInstance(ClassSession param1, String param2, String param3)
    {
        StudentAttendanceFragment fragment = new StudentAttendanceFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            currentClassSession = getArguments().getParcelable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_student_attendance, container, false);

        InitUI(v);
        getAttendanceList();
        return v;
    }

    private void InitUI(View v)
    {
        nothingHere = v.findViewById(R.id.nothing_here);
        recyclerView_students = v.findViewById(R.id.recyclerView_students);

        getActivity().findViewById(R.id.loading_panel).setVisibility(View.VISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView_students.setLayoutManager(layoutManager);
        recyclerView_students.setAdapter(studentAdapter);
    }

    private void getAttendanceList()
    {
        Map<String, Object> data = new HashMap<>();
        data.put("classid", currentClassSession.getClassFirebaseUID());
        data.put("sessionid", currentClassSession.getFirebaseUID());

        HttpsCallableReference attendanceFunction = firebaseFunctions.getHttpsCallable("getStudentAttendance");
        attendanceFunction.call(data)
        .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>()
        {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult)
            {
                getActivity().findViewById(R.id.loading_panel).setVisibility(View.INVISIBLE);
                ArrayList<HashMap<String,Object>> result = (ArrayList<HashMap<String, Object>>) httpsCallableResult.getData();
                if(result.size() > 0)
                {
                    students.clear();
                    for (HashMap<String, Object> data: result)
                    {
                        Log.d(TAG, "onSuccess: " + data);
                        Student s = new Student(data.get("id").toString(), data.get("name").toString(), 2, data.get("studentid").toString(), (Integer)data.get("attended"));
                        students.add(s);
                        studentAdapter.notifyItemInserted(result.size() - 1);
                    }
                    Log.d(TAG, "onSuccess: " + result);
                }
                else
                    nothingHere.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.e(TAG, "onFailure: ", e);
            }
        });
    }
}
