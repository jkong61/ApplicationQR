package com.example.applicationqr;

import androidx.annotation.Nullable;

public interface onFragmentInteractionListener<T>
{
    public void onFragmentMessage(String TAG, T data);
}
