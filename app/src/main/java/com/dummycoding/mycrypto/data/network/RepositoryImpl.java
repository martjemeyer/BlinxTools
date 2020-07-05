package com.dummycoding.mycrypto.data.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.dummycoding.mycrypto.R;
import com.dummycoding.mycrypto.models.OwnedToken;
import com.dummycoding.mycrypto.models.bitblinx.Result;
import com.dummycoding.mycrypto.models.coindesk.Currency;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.processors.BehaviorProcessor;

public class RepositoryImpl implements Repository {
    private final String DB_NAME = "db-mycrypto";
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

    @Override
    public void setFirstLaunched() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(mContext.getString(R.string.fist_launch_key), false);
        editor.apply();
    }

    @Override
    public boolean isFirstLaunched() {
        return mPreferences.getBoolean(mContext.getString(R.string.fist_launch_key), true);
    }

    @Override
    public boolean isDarkMode() {
        return mPreferences.getBoolean(mContext.getString(R.string.dark_mode_key), true);
    }

    @Override
    public boolean isFollowSystemSetting() {
        return mPreferences.getBoolean(mContext.getString(R.string.follow_system_setting_key), false);
    }

    @Override
    public void setHideDoubleClickHint() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(mContext.getString(R.string.hide_double_click_key), true);
        editor.apply();
    }

    @Override
    public boolean isDoubleClickHintHidden() {
        return mPreferences.getBoolean(mContext.getString(R.string.hide_double_click_key), false);
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
    public boolean getShowOwnedTokens() {
        return mPreferences.getBoolean(mContext.getString(R.string.owned_currency_key), true);
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
    public Completable insertOwnedTokens(OwnedToken... ownedTokens) {
        return mDatabase.ownedTokenDao().insertOwnedTokens(ownedTokens);
    }

    @Override
    public Completable insertOwnedTokens(List<OwnedToken> ownedTokens) {
        return mDatabase.ownedTokenDao().insertOwnedTokens(ownedTokens);
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
    public Single<List<OwnedToken>> getOwnedTokensSingle() {
        return mDatabase.ownedTokenDao().getAllOwnedTokensSingle();
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
    public Single<List<Result>> getAllBtcPairsComplete() {
        return mDatabase.blinxResultDao().getAllBtcPairsComplete();
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

    @Override
    public Completable updateOwnedTokenPosition(int id, int price) {
        return mDatabase.ownedTokenDao().update(id, price);
    }
}
