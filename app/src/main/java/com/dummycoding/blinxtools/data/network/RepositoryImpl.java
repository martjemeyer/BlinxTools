package com.dummycoding.blinxtools.data.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.dummycoding.blinxtools.R;
import com.dummycoding.blinxtools.models.OwnedToken;
import com.dummycoding.blinxtools.models.bitblinx.Result;
import com.dummycoding.blinxtools.models.coindesk.Currency;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.processors.BehaviorProcessor;

public class RepositoryImpl implements Repository {
    private final String DB_NAME = "db-blinxtools";
    private final Context mContext;
    private final SharedPreferences mPreferences;
    private final Gson mGson;
    private BlinxRoomDatabase mDatabase;
    private BehaviorProcessor<List<Result>> bitBlinxResultProcessor = BehaviorProcessor.create();

    public RepositoryImpl(Context context, SharedPreferences sharedPreferences) {
        mContext = context;
        mPreferences = sharedPreferences;
        mDatabase = BlinxRoomDatabase.getDatabase(context);
        mGson = new Gson();
    }

    private SharedPreferences.Editor getEditor() {
        return mPreferences.edit();
    }

    private void getBitBlinxResultAsFlowable() {

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
    public boolean getOnlyFavoritesResults() {
        return mPreferences.getBoolean(mContext.getString(R.string.show_only_favorite_pairs_key), false);
    }

    @Override
    public List<String> getFavorites() {
        String favoritesJson = mPreferences.getString(mContext.getString(R.string.list_favorites_key), "");
        List<String> favorites = mGson.fromJson(favoritesJson, new TypeToken<ArrayList<String>>() {}.getType());
        return favorites != null ? favorites : new ArrayList<>();
    }

    @Override
    public void setFavorites(List<String> favorites) {
        String favoritesJson = mGson.toJson(favorites);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(mContext.getString(R.string.list_favorites_key), favoritesJson);
        editor.apply();
    }

    @Override
    public Completable storeLatestBitBlinxData(List<Result> bitBlinxResult) {
        return mDatabase.blinxResultDao().insertResults(bitBlinxResult);
    }

    @Override
    public Completable storeCoinDeskCurrencies(List<Currency> coinDeskCurrencies) {
        return mDatabase.coinDeskCurrencyDao().insertCurrencies(coinDeskCurrencies);
    }

    @Override
    public Completable storeOwnedToken(OwnedToken ownedToken) {
        return mDatabase.ownedTokenDao().insertOwnedToken(ownedToken);
    }

    @Override
    public Flowable<List<Result>> getBitBlinxDataFlowable() {
        return mDatabase.blinxResultDao().getAllResults();
    }

    @Override
    public Flowable<List<Result>> getBitBlinxFavoriteDataFlowable() {
        return mDatabase.blinxResultDao().getAllFavoriteResults();
    }

    @Override
    public Flowable<List<OwnedToken>> getOwnedTokensFlowable() {
        return mDatabase.ownedTokenDao().getAllOwnedTokens();
    }

    @Override
    public Flowable<List<Currency>> getCoinDeskCurrenciesFlowable() {
        return mDatabase.coinDeskCurrencyDao().getAllCurrencies();
    }

    @Override
    public Flowable<List<String>> getCoinDeskCurrencyStringsFlowable() {
        return mDatabase.coinDeskCurrencyDao().getAllCurrencyStrings();
    }

    @Override
    public Flowable<List<String>> getCoinDeskCountryStringsFlowable() {
        return mDatabase.coinDeskCurrencyDao().getAllCountryStrings();
    }

    @Override
    public Single<List<String>> getAllBtcPairs() {
        return mDatabase.blinxResultDao().getAllBtcPairs();
    }

    @Override
    public Single<List<Result>> getTokenBySymbol(String symbol) {
        return mDatabase.blinxResultDao().getTokenBySymbol(symbol);
    }

    @Override
    public Completable deleteOwnedToken(OwnedToken ownedToken) {
        return mDatabase.ownedTokenDao().delete(ownedToken);
    }

    @Override
    public Completable updateBitBlinxResult(Result result) {
        return mDatabase.blinxResultDao().updateResult(result);
    }
}
