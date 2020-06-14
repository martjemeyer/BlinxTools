package com.dummycoding.mycrypto.owned_currencies;

import android.app.Application;

import androidx.annotation.NonNull;
import com.dummycoding.mycrypto.common.BaseViewModel;
import com.dummycoding.mycrypto.models.OwnedToken;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class OwnedTokensViewModel extends BaseViewModel {


    public OwnedTokensViewModel(@NonNull Application application) {
        super(application);
    }

    public Flowable<List<OwnedToken>> subscribeToOwnedTokenDbChanges() {

        return getCompositionRoot()
                .getRepository().getOwnedTokensFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

}
