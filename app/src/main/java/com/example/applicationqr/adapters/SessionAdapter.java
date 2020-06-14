package com.example.applicationqr.adapters;

import android.content.Context;
import android.content.DialogInterface;
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

import com.example.applicationqr.MainMenuActivity;
import com.example.applicationqr.R;
import com.example.applicationqr.fragments.DisplayQRCodeFragment;
import com.example.applicationqr.fragments.StudentAttendanceFragment;
import com.example.applicationqr.model.ClassSession;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.MyViewHolder>
{
    private final String TAG = SessionAdapter.class.getName();
    private Context thisContext;
    private int request;
    private ArrayList<ClassSession> classSessions;
    private FirebaseFirestore db;

    public SessionAdapter(int request, ArrayList<ClassSession> sessions)
    {
        this.request = request;
        classSessions = sessions;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public SessionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        thisContext = parent.getContext();
        View v = LayoutInflater.from(thisContext).inflate(R.layout.card_layout, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionAdapter.MyViewHolder holder, int position)
    {
        ClassSession cs = classSessions.get(position);
        holder.sessionAttendance.setText(String.format("Attended: %d", cs.getSessionAttendance()));
        holder.sessionBlank.setText("");
        holder.sessionTime.setText(String.format("Session: %s", cs.getSessionTime().toString()));
    }

    @Override
    public int getItemCount()
    {
        return classSessions.size();
    }

    private void deleteSessionDoc(int adapterPosition)
    {
        CollectionReference sessionRef = db.collection("classes").document(classSessions.get(adapterPosition).getClassFirebaseUID()).collection("sessions");
        sessionRef.document(classSessions.get(adapterPosition).getFirebaseUID())
        .delete()
        .addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                classSessions.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
                Toast.makeText(thisContext, "Successfully deleted session!", Toast.LENGTH_SHORT).show();
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
        public TextView sessionAttendance, sessionTime, sessionBlank;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            sessionAttendance = itemView.findViewById(R.id.card_enrolment);
            sessionBlank = itemView.findViewById(R.id.card_code);
            sessionTime = itemView.findViewById(R.id.card_title);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            int i = getAdapterPosition();
            FragmentManager manager = ((AppCompatActivity) thisContext).getSupportFragmentManager();

            if(request == R.id.button_take_attendance)
            {
                DisplayQRCodeFragment displayCodeFragment = DisplayQRCodeFragment.newInstance(MainMenuActivity.currentUser, classSessions.get(i));
                manager.beginTransaction().replace(R.id.main_menu_view, displayCodeFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            }
            else
            {
                StudentAttendanceFragment studentAttendanceFragment = StudentAttendanceFragment.newInstance(classSessions.get(i),null,null);
                manager.beginTransaction().replace(R.id.main_menu_view, studentAttendanceFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            }
        }

        @Override
        public boolean onLongClick(View v)
        {
            if (request == R.id.button_classes)
            {
                // Show Alert Dialog before deleting
                final AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
                // Add the buttons
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // User clicked OK button
                        Toast.makeText(thisContext, "Deleting session..", Toast.LENGTH_SHORT).show();

                        // Delete the document on Firestore
                        // Need to delete subcollection first before deleting document
                        deleteSessionDoc(getAdapterPosition());
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
                builder.setMessage("Are you sure you want to delete this session?");

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
            return false;

        }
    }
}
