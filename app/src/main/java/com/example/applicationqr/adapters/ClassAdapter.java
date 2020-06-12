package com.example.applicationqr.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationqr.BarcodeScannerActivity;
import com.example.applicationqr.MainMenuActivity;
import com.example.applicationqr.R;
import com.example.applicationqr.fragments.DisplayQRCodeFragment;
import com.example.applicationqr.model.Classroom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.MyViewHolder>
{

    private static final String TAG = ClassAdapter.class.getName();
    private Context thisContext;
    private ArrayList<Classroom> classrooms;
    private int requestID;
    private FirebaseFirestore db;

    public ClassAdapter(int requestID, ArrayList<Classroom> classes)
    {
        this.requestID = requestID;
        classrooms = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        classrooms = classes;
    }

    @NonNull
    @Override
    public ClassAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        thisContext = parent.getContext();
        View v = LayoutInflater.from(thisContext).inflate(R.layout.row_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassAdapter.MyViewHolder holder, int position)
    {
        Classroom c = classrooms.get(position);
        holder.className.setText(c.getClassName());
        holder.classCode.setText(c.getClassCode());
        holder.classEnrolments.setText(String.format("Enrolments : %d", c.getNumEnrol()));
    }

    @Override
    public int getItemCount()
    {
        return classrooms.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView className, classCode, classEnrolments;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            className = itemView.findViewById(R.id.class_title);
            classCode = itemView.findViewById(R.id.class_code);
            classEnrolments = itemView.findViewById(R.id.class_enrolment);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            switch (requestID)
            {
                case(R.id.button_register_student):
                    //TODO need to test
                    Intent requestIntent = new Intent(thisContext, BarcodeScannerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("USER", MainMenuActivity.currentUser);
                    requestIntent.putExtras(bundle);
                    ((AppCompatActivity) thisContext).startActivityForResult(requestIntent, MainMenuActivity.BARCODE_URL_REQUEST);
                    break;

                case(R.id.button_take_attendance):
                    int i = getAdapterPosition();
                    Classroom c = classrooms.get(i);
                    FragmentManager manager = ((AppCompatActivity) thisContext).getSupportFragmentManager();
                    // TODO replace url with api url + firebase classroom UID
                    DisplayQRCodeFragment displayCodeFragment = DisplayQRCodeFragment.newInstance(MainMenuActivity.currentUser, c);
                    manager.beginTransaction().replace(R.id.main_menu_view, displayCodeFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                    break;
            }
        }
    }

    private void getClassrooms()
    {
        ArrayList<Classroom> tempcollection = new ArrayList<>();
        db.collection("classes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        Map<String,Object> fields = document.getData();
                        ArrayList<DocumentReference> documentReferences = (ArrayList<DocumentReference>) fields.get("enrolled");
                        tempcollection.add(new Classroom(document.getId(), fields.get("coursename").toString(), fields.get("coursecode").toString(), documentReferences.size()));
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                    classrooms.addAll(tempcollection);
                    notifyDataSetChanged();
                }
                else
                    Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }
}
