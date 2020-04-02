package com.dummycoding.blinxtools;


import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;

import com.dummycoding.blinxtools.di.CompositionRoot;


@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    protected CompositionRoot getCompositionRoot() {
        return ((BlinxTools)getApplication()).getCompositionRoot();
    }
}
