package com.example.applicationqr.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationqr.BarcodeScannerActivity;
import com.example.applicationqr.MainMenuActivity;
import com.example.applicationqr.R;
import com.example.applicationqr.fragments.DisplayQRCodeFragment;
import com.example.applicationqr.model.Classroom;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableReference;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.MyViewHolder>
{

    private static final String TAG = ClassAdapter.class.getName();
    private Context thisContext;
    private ArrayList<Classroom> classrooms;
    private int requestID;
    private FirebaseFirestore db;
    private FirebaseFunctions firebaseFunctions;

    public ClassAdapter(int requestID, ArrayList<Classroom> classes)
    {
        this.requestID = requestID;
        classrooms = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        firebaseFunctions = FirebaseFunctions.getInstance();
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

    private void deleteClassroom(int i)
    {
        Classroom c = classrooms.get(i);
        final String path = String.format("/classes/%s/sessions", c.getFirebaseUID());
        Map<String, Object> data = new HashMap<>();
        data.put("path", path);

        HttpsCallableReference deleteFn = firebaseFunctions.getHttpsCallable("recursiveDelete");
        deleteFn.call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult)
                    {
                        // Delete Success and finally delete the classroom document reference
                        deleteClassroomDoc(i);
                        Log.d(TAG, "onSuccess: Sessions deleted");
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Delete failed
                        Log.e(TAG, "onFailure: ", e);
                    }
                });
    }

    private void deleteClassroomDoc(int i)
    {
        db.collection("classes")
                .document(classrooms.get(i).getFirebaseUID())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                classrooms.remove(i);
                notifyItemRemoved(i);
                Toast.makeText(thisContext, "Successfully deleted classroom!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d(TAG, "onFailure: Failed to delete document");
            }
        });

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        public TextView className, classCode, classEnrolments;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            className = itemView.findViewById(R.id.class_title);
            classCode = itemView.findViewById(R.id.class_code);
            classEnrolments = itemView.findViewById(R.id.class_enrolment);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            switch (requestID)
            {
                case (R.id.button_register_student):
                    //TODO need to test
                    Intent requestIntent = new Intent(thisContext, BarcodeScannerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("USER", MainMenuActivity.currentUser);
                    bundle.putString("classID", classrooms.get(getAdapterPosition()).getFirebaseUID());
                    requestIntent.putExtras(bundle);
                    ((AppCompatActivity) thisContext).startActivityForResult(requestIntent, MainMenuActivity.BARCODE_REGISTER_REQUEST);
                    break;

                case (R.id.button_classes):
                    // Open Session Fragment
                    break;

                case (R.id.button_take_attendance):
                    int i = getAdapterPosition();
                    Classroom c = classrooms.get(i);
                    FragmentManager manager = ((AppCompatActivity) thisContext).getSupportFragmentManager();
                    // TODO replace url with api url + firebase classroom UID
                    DisplayQRCodeFragment displayCodeFragment = DisplayQRCodeFragment.newInstance(MainMenuActivity.currentUser, c);
                    manager.beginTransaction().replace(R.id.main_menu_view, displayCodeFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                    break;
            }
        }

        // Long Click for delete via Cloud Functions and Delete the Firestore Document
        @Override
        public boolean onLongClick(View v)
        {
            if (requestID == R.id.button_classes)
            {
                // Show Alert Dialog before deleting
                final AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
                // Add the buttons
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // User clicked OK button
                        Toast.makeText(thisContext, "Deleting classroom..", Toast.LENGTH_SHORT).show();

                        // Delete the document on Firestore
                        // Need to delete subcollection first before deleting document
                        deleteClassroom(getAdapterPosition());
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // Do nothing
                    }
                });
                builder.setTitle("Confirm deletion");
                builder.setMessage("Are you sure you want to delete this classroom?");

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
            return false;
        }
    }
}

