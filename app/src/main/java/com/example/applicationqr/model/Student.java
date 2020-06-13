package com.example.applicationqr.model;

public class Student extends User
{
    private boolean hasAttended;

    public Student(String UUID, String name, long type, String userID, boolean attended)
    {
        super(UUID, name, type, userID);
        hasAttended = attended;
    }

    public boolean isHasAttended()
    {
        return hasAttended;
    }
}
