package com.dummycoding.blinxtools.data.network;

import com.dummycoding.blinxtools.models.OwnedToken;
import com.dummycoding.blinxtools.models.bitblinx.Result;
import com.dummycoding.blinxtools.models.coindesk.Currency;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface Repository {

    // Shared preferences
    String getPreferredCurrency();

    float getBtcValueForPreferredCurrency();

    void setBtcValueForPreferredCurrency(float value);

    boolean getOnlyFavoritesResults();

    void setFavorites(List<String> favorites);

    List<String> getFavorites();

    // DB
    Completable storeLatestBitBlinxData(List<Result> bitBlinxResult);

    Completable storeCoinDeskCurrencies(List<Currency> coinDeskCurrencies);

    Completable storeOwnedToken(OwnedToken ownedToken);

    Completable deleteOwnedToken(OwnedToken ownedToken);

    Completable updateBitBlinxResult(Result result);

    Flowable<List<Result>> getBitBlinxDataFlowable();

    Flowable<List<Result>> getBitBlinxFavoriteDataFlowable();

    Flowable<List<OwnedToken>> getOwnedTokensFlowable();

    Flowable<List<Currency>> getCoinDeskCurrenciesFlowable();

    Flowable<List<String>> getCoinDeskCurrencyStringsFlowable();

    Flowable<List<String>> getCoinDeskCountryStringsFlowable();

    Single<List<String>> getAllBtcPairs();

    Single<List<Result>> getTokenBySymbol(String symbol);



}
