package com.example.applicationqr.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.applicationqr.model.Classroom;
import com.example.applicationqr.model.QRGenerator;
import com.example.applicationqr.R;
import com.example.applicationqr.model.User;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplayQRCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayQRCodeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private QRGenerator codeGenerator;
    private ImageView qrCode;
    // TODO: Rename and change types of parameters
    private User currentUser;
    private Classroom currentClassroom;

    public DisplayQRCodeFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DisplayQRCodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DisplayQRCodeFragment newInstance(User param1, Classroom param2)
    {
        DisplayQRCodeFragment fragment = new DisplayQRCodeFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, param1);
        args.putParcelable(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = getArguments().getParcelable(ARG_PARAM1);
            currentClassroom = getArguments().getParcelable(ARG_PARAM2);
        }
        codeGenerator = QRGenerator.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_display_code, container, false);
        qrCode = v.findViewById(R.id.qr_code_display_image);
        TextView qrCodePurpose = v.findViewById(R.id.qr_code_display_purpose);
        if(currentUser.getType() == 2)
        {
            qrCode.setImageBitmap(codeGenerator.getQRBitmap(currentUser.getFirebaseUID(), getContext()));
            qrCodePurpose.setText("code for Student Registration");
        }
        else
        {
            qrCode.setImageBitmap(codeGenerator.getQRBitmap(currentClassroom.getFirebaseUID(), getContext()));
            qrCodePurpose.setText(String.format("Attendance code for Class : %s", currentClassroom.getClassCode() ));
        }
        return v;
    }
}
