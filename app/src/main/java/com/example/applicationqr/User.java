package com.example.applicationqr;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable
{
    private String name;
    private long type;

    public User(String name, long type)
    {
        this.name = name;
        this.type = type;
    }

    protected User(Parcel in) {
        name = in.readString();
        type = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getName()
    {
        return name;
    }

    public long getType()
    {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(type);
    }
}
