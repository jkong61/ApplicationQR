package com.example.applicationqr.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.Date;

public class ClassSession implements Parcelable
{
    private String firebaseUID;
    public static final Creator<ClassSession> CREATOR = new Creator<ClassSession>()
    {
        @Override
        public ClassSession createFromParcel(Parcel in)
        {
            return new ClassSession(in);
        }

        @Override
        public ClassSession[] newArray(int size)
        {
            return new ClassSession[size];
        }
    };
    private String classFirebaseUID;
    private Timestamp sessionTime;
    private int sessionAttendance;

    public ClassSession(String firebaseUID, String classFirebaseUID, Timestamp sessionTime, int sessionAttendance)
    {
        this.firebaseUID = firebaseUID;
        this.classFirebaseUID = classFirebaseUID;
        this.sessionTime = sessionTime;
        this.sessionAttendance = sessionAttendance;
    }

    protected ClassSession(Parcel in)
    {
        firebaseUID = in.readString();
        classFirebaseUID = in.readString();
        sessionTime = in.readParcelable(Timestamp.class.getClassLoader());
        sessionAttendance = in.readInt();
    }

    public String getFirebaseUID()
    {
        return firebaseUID;
    }

    public Date getSessionTime()
    {
        return sessionTime.toDate();
    }

    public String getClassFirebaseUID()
    {
        return classFirebaseUID;
    }

    public int getSessionAttendance()
    {
        return sessionAttendance;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(firebaseUID);
        dest.writeString(classFirebaseUID);
        dest.writeParcelable(sessionTime, flags);
        dest.writeInt(sessionAttendance);
    }
}
