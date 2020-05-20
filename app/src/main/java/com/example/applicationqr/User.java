package com.example.applicationqr;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable
{
    private String firebaseUID;
    private String name;
    private long type;
    private String userID;

    public User(String UUID, String name, long type, String userID)
    {
        this.firebaseUID = UUID;
        this.name = name;
        this.type = type;
        this.userID = userID;
    }

    protected User(Parcel in) {
        firebaseUID = in.readString();
        name = in.readString();
        type = in.readLong();
        userID = in.readString();
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

    public String getUserID()
    {
        return userID;
    }

    public String getFirebaseUID()
    {
        return firebaseUID;
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
        dest.writeString(name);
        dest.writeLong(type);
        dest.writeString(userID);
    }
}
