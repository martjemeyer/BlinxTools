package com.dummycoding.blinxtools;


import android.app.Application;

import com.dummycoding.blinxtools.di.CompositionRoot;


public class BlinxTools extends Application {

    private CompositionRoot mCompositionRoot;

    @Override
    public void onCreate() {
        super.onCreate();
        mCompositionRoot = new CompositionRoot();
    }

    public CompositionRoot getCompositionRoot() {
        return mCompositionRoot;
    }

}
