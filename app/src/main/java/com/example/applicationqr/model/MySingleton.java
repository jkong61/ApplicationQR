package com.example.applicationqr.model;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton
{
    private static MySingleton instance;
    private RequestQueue requestQueue;
    private QRGenerator qrGenerator;

    private MySingleton(Context context)
    {
        requestQueue = Volley.newRequestQueue(context);
        qrGenerator = QRGenerator.getInstance();
    }

    public static MySingleton getInstance(Context context)
    {
        if (instance == null)
            instance = new MySingleton(context);
        return instance;
    }

    public RequestQueue getRequestQueue()
    {
        return requestQueue;
    }

    public QRGenerator getQrGenerator()
    {
        return qrGenerator;
    }

    public <T> void addToRequestQueue(Request<T> req)
    {
        getRequestQueue().add(req);
    }
}