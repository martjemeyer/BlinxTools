package com.dummycoding.mycrypto;


import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;

import com.dummycoding.mycrypto.di.CompositionRoot;


@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    public CompositionRoot getCompositionRoot() {
        return ((MyCrypto)getApplication()).getCompositionRoot();
    }
}
