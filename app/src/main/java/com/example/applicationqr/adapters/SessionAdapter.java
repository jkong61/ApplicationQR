package com.example.applicationqr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationqr.R;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.MyViewHolder>
{
    private Context thisContext;

    @NonNull
    @Override
    public SessionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        thisContext = parent.getContext();
        View v = LayoutInflater.from(thisContext).inflate(R.layout.row_layout, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionAdapter.MyViewHolder holder, int position)
    {

    }

    @Override
    public int getItemCount()
    {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        public TextView sessionNumber, sessionTime, sessionBlank;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            sessionNumber = itemView.findViewById(R.id.class_title);
            sessionBlank = itemView.findViewById(R.id.class_code);
            sessionTime = itemView.findViewById(R.id.class_enrolment);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v)
        {

        }

        @Override
        public boolean onLongClick(View v)
        {
            return false;
        }
    }
}
