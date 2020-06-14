package com.dummycoding.mycrypto.common;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.dummycoding.mycrypto.MyCrypto;
import com.dummycoding.mycrypto.di.CompositionRoot;

public class BaseViewModel extends AndroidViewModel {

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    public CompositionRoot getCompositionRoot() {
        return ((MyCrypto)getApplication()).getCompositionRoot();
    }
}
