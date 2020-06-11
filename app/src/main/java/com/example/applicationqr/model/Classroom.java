package com.example.applicationqr.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Classroom implements Parcelable
{
    public static final Creator<Classroom> CREATOR = new Creator<Classroom>()
    {
        @Override
        public Classroom createFromParcel(Parcel in)
        {
            return new Classroom(in);
        }

        @Override
        public Classroom[] newArray(int size)
        {
            return new Classroom[size];
        }
    };
    private String firebaseUID;
    private String className;
    private String classCode;
    private int numEnrol;

    public Classroom(String firebaseUID, String className, String classCode, int numEnrol)
    {
        this.firebaseUID = firebaseUID;
        this.className = className;
        this.classCode = classCode;
        this.numEnrol = numEnrol;
    }

    protected Classroom(Parcel in)
    {
        firebaseUID = in.readString();
        className = in.readString();
        classCode = in.readString();
        numEnrol = in.readInt();
    }

    public String getFirebaseUID()
    {
        return firebaseUID;
    }

    public String getClassName()
    {
        return className;
    }

    public String getClassCode()
    {
        return classCode;
    }

    public int getNumEnrol()
    {
        return numEnrol;
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
        dest.writeString(className);
        dest.writeString(classCode);
        dest.writeInt(numEnrol);
    }
}
