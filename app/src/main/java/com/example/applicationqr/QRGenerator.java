package com.example.applicationqr;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRGenerator
{
    private static QRGenerator instance = null;

    private QRGenerator()
    {
    }

    static QRGenerator getInstance() {
        if (instance == null) {
            instance = new QRGenerator();
        }
        return instance;
    }

    Bitmap getQRBitmap(String content, float displayScale)
    {
        try
        {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            int p = (int) (400 * displayScale + 0.5f);
            return barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, p, p);
        }
        catch(Exception ignored)
        {
            Log.d(getClass().getName(),"Unable to create QR Code");
        }
        return null;
    }
}
