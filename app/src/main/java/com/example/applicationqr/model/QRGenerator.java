package com.example.applicationqr.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRGenerator
{
    private static QRGenerator instance;

    private QRGenerator()
    {
    }

    public static QRGenerator getInstance() {
        if (instance == null)
            instance = new QRGenerator();
        return instance;
    }

    public Bitmap getQRBitmap(String content, Context context)
    {
        try
        {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            float displayScale = context.getResources().getDisplayMetrics().density;
            int p = (int) (300 * displayScale + 0.5f);
            return barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, p, p);
        }
        catch(Exception ignored)
        {
            Log.d(getClass().getName(),"Unable to create QR Code");
        }
        return null;
    }
}
