package com.example.applicationqr.model;

import java.util.Date;

public class ClassSession
{
    private String firebaseUID;
    private Date sessionTime;

    public ClassSession(String firebaseUID, Date sessionTime)
    {
        this.firebaseUID = firebaseUID;
        this.sessionTime = sessionTime;
    }

    public String getFirebaseUID()
    {
        return firebaseUID;
    }

    public Date getSessionTime()
    {
        return sessionTime;
    }
}
