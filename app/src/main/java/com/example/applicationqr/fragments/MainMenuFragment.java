package com.example.applicationqr.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.applicationqr.R;
import com.example.applicationqr.model.User;
import com.example.applicationqr.onFragmentInteractionListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMenuFragment extends Fragment
{
    private static final String TAG = MainMenuFragment.class.getName();
    private FirebaseAuth mAuth;
    private TextView mainMenuHeader;
    private LinearLayout teacherMenu, studentMenu;
    private int[] teacherButtonIDs= {R.id.button_register_student,R.id.button_add_class, R.id.button_view_attendance, R.id.button_take_attendance};
    private int[] studentButtonIDs= {R.id.button_display_student_code, R.id.button_update_student,R.id.button_scan_code};
    private ArrayList<Button> teacherButtonViews = new ArrayList<>();
    private ArrayList<Button> studentButtonViews = new ArrayList<>();

    private onFragmentInteractionListener menuFragmentInteractionListener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private User userObj;
    private String mParam2;

    public MainMenuFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainMenuFragment newInstance(User param1, String param2) {
        MainMenuFragment fragment = new MainMenuFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userObj = getArguments().getParcelable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);
        InitUI(v, (int) userObj.getType());

        return v;
    }


    private void InitUI(View v, int type)
    {
        mainMenuHeader = v.findViewById(R.id.menu_layout_header);
        mainMenuHeader.setText(String.format("Welcome back, %s.", userObj.getName()));

        teacherMenu = v.findViewById(R.id.teacher_menu);
        studentMenu = v.findViewById(R.id.student_menu);

        if (type == 1)
        {
            teacherMenu.setVisibility(View.VISIBLE);
            InitTeacherUI(v);
        }
        else
        {
            studentMenu.setVisibility(View.VISIBLE);
            InitStudentUI(v);
        }
    }

    private void InitTeacherUI(View v)
    {
        for (int id: teacherButtonIDs)
        {
            teacherButtonViews.add((Button)v.findViewById(id));
        }

        for (Button b: teacherButtonViews)
        {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    menuFragmentInteractionListener.onFragmentMessage(TAG, v.getId());
                }
            });
        }
    }

    private void InitStudentUI(View v)
    {
        for (int id: studentButtonIDs)
        {
            studentButtonViews.add((Button)v.findViewById(id));
        }

        // Set on click listeners
        for (Button b: studentButtonViews)
        {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    menuFragmentInteractionListener.onFragmentMessage(TAG, v.getId());
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
            menuFragmentInteractionListener = (onFragmentInteractionListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + "must implement onFragmentInteractionListener");
        }
    }

    public void UpdateSelf(User user)
    {
        userObj = user;
    }
}
