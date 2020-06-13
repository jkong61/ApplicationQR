package com.example.applicationqr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationqr.R;
import com.example.applicationqr.model.ClassSession;
import com.example.applicationqr.model.Student;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder>
{
    private ArrayList<Student> students;
    private Context thisContext;

    public StudentAdapter(ArrayList<Student> students)
    {
        this.students = students;
    }

    @NonNull
    @Override
    public StudentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        thisContext = parent.getContext();
        View v = LayoutInflater.from(thisContext).inflate(R.layout.row_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.MyViewHolder holder, int position)
    {
        Student s = students.get(position);
        holder.studentAttendance.setText(String.format("Attended: %s", s.isHasAttended() ? "Yes" : "No"));
        holder.studentID.setText(String.format("%s", s.getUserID()));
        holder.studentName.setText(String.format("Name: %s", s.getName()));
    }

    @Override
    public int getItemCount()
    {
        return students.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView studentName, studentAttendance, studentID;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            studentAttendance = itemView.findViewById(R.id.class_enrolment);
            studentID = itemView.findViewById(R.id.class_code);
            studentName = itemView.findViewById(R.id.class_title);
        }
    }
}
