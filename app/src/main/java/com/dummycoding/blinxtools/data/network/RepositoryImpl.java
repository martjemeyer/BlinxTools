package com.dummycoding.blinxtools.data.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.dummycoding.blinxtools.R;
import com.dummycoding.blinxtools.models.BitBlinxResult;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class RepositoryImpl implements Repository {

    private final Context mContext;
    private final SharedPreferences mPreferences;
    private final RealmManager mRealmManager;

    public RepositoryImpl(Context context, SharedPreferences sharedPreferences, RealmManager realmManager) {
        mContext = context;
        mPreferences = sharedPreferences;
        mRealmManager = realmManager;
        mRealmManager.openLocalInstance();
    }

    private SharedPreferences.Editor getEditor() {
        return mPreferences.edit();
    }
    private Realm getRealm() {
        return mRealmManager.getLocalInstance();
    }

    @Override
    public String getPreferredCurrency() {
        return mPreferences.getString(mContext.getString(R.string.preferred_currency_key), "EUR");
    }

    @Override
    public float getBtcValueForPreferredCurrency() {
        return mPreferences.getFloat(mContext.getString(R.string.btc_value_for_preferred_currency_key), -1);
    }

    @Override
    public void setBtcValueForPreferredCurrency(float value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putFloat(mContext.getString(R.string.btc_value_for_preferred_currency_key), value);
        editor.apply();
    }

    @Override
    public boolean storeLatestBitBlinxData(List<BitBlinxResult> bitBlinxResult) {
        RealmList<BitBlinxResult> realmList = new RealmList<>();
        realmList.addAll(bitBlinxResult);

        getRealm().beginTransaction();
        getRealm().insertOrUpdate(realmList);
        getRealm().commitTransaction();
        return true;
    }

    @Override
    public List<BitBlinxResult> getLatestBitBlinxData() {
        RealmResults<BitBlinxResult> results = getRealm().where(BitBlinxResult.class).findAll();
        return getRealm().copyFromRealm(results);
    }
}
