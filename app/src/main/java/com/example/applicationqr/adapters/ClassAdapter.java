package com.example.applicationqr.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.MyViewHolder>
{

    private Context thisContext;
    private ArrayList<Classroom> classrooms;
    private int requestID;

    public ClassAdapter(int requestID)
    {
        this.requestID = requestID;
        classrooms = new ArrayList<>();
        classrooms.add(new Classroom("Bflb2Czil55WxDDGhlc8","Some Interesting Class","COS12021",5));
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
                case(R.id.button_classes):
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
}
