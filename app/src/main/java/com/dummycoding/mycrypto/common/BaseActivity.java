package com.dummycoding.mycrypto.common;


import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;

import com.dummycoding.mycrypto.MyCrypto;
import com.dummycoding.mycrypto.di.CompositionRoot;


@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    public CompositionRoot getCompositionRoot() {
        return ((MyCrypto)getApplication()).getCompositionRoot();
    }
}
