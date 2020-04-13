package com.dummycoding.mycrypto;


import android.annotation.SuppressLint;
import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.dummycoding.mycrypto.di.CompositionRoot;
import com.dummycoding.mycrypto.models.OwnedToken;

import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class MyCrypto extends Application {

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

        if (mCompositionRoot.getRepository().isFirstLaunched()) {
           createFirstLaunchData();
        }
    }

    @SuppressLint("CheckResult")
    private void createFirstLaunchData() {
        mCompositionRoot.getRepository().setFirstLaunched();

        OwnedToken gtfta = new OwnedToken("GTFTA", 10000);
        OwnedToken gtplus = new OwnedToken("GTPLUS", 21000);
        OwnedToken gtplus2 = new OwnedToken("GTPLUS", 500);
        OwnedToken btc = new OwnedToken("BTC", 1.2);

        mCompositionRoot.getRepository().insertOwnedTokens(gtfta, gtplus, gtplus2, btc)
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {}, throwable -> Timber.e(throwable, "editOwnedToken: "));
    }

    public CompositionRoot getCompositionRoot() {
        return mCompositionRoot;
    }

}
