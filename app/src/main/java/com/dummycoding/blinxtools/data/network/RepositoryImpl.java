package com.dummycoding.blinxtools.data.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.dummycoding.blinxtools.R;

public class RepositoryImpl implements Repository{

    private final Context mContext;
    private final SharedPreferences mPreferences;

    public RepositoryImpl(Context context, SharedPreferences sharedPreferences) {
        mContext = context;
        mPreferences = sharedPreferences;
    }

    private SharedPreferences.Editor getEditor() {
        return mPreferences.edit();
    }

    @Override
    public String getPreferredCurrency() {
        return mPreferences.getString(mContext.getString(R.string.preferred_currency_key), "EUR");
    }

    @Override
    public void setBtcValueForPreferredCurrency(float value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putFloat(mContext.getString(R.string.btc_value_for_preferred_currency_key), value);
        editor.apply();
    }

    @Override
    public float getBtcValueForPreferredCurrency() {
        return mPreferences.getFloat(mContext.getString(R.string.btc_value_for_preferred_currency_key), -1);
    }
}
