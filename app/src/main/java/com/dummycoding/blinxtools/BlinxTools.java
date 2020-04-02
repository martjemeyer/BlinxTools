package com.dummycoding.blinxtools;


import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.dummycoding.blinxtools.di.CompositionRoot;

import timber.log.Timber;


public class BlinxTools extends Application {

    private CompositionRoot mCompositionRoot;

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        mCompositionRoot = new CompositionRoot(this);
    }

    public CompositionRoot getCompositionRoot() {
        return mCompositionRoot;
    }

}
