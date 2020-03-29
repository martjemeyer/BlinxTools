package com.dummycoding.blinxtools;


import androidx.appcompat.app.AppCompatActivity;

import com.dummycoding.blinxtools.di.CompositionRoot;


public class BaseActivity extends AppCompatActivity {

    protected CompositionRoot getCompositionRoot() {
        return ((BlinxTools)getApplication()).getCompositionRoot();
    }
}
