package com.dummycoding.mycrypto.data.network;

import com.dummycoding.mycrypto.models.OwnedToken;
import com.dummycoding.mycrypto.models.bitblinx.Result;
import com.dummycoding.mycrypto.models.coindesk.Currency;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface Repository {

    // Shared preferences
    void setFirstLaunched();

    void setDisclaimerShown();

    boolean isDisclaimerShown();

    void setHideDoubleClickHint();

    boolean isDoubleClickHintHidden();

    boolean isFirstLaunched();

    boolean isFollowSystemSetting();

    boolean isDarkMode();

    String getPreferredCurrency();

    float getBtcValueForPreferredCurrency();

    void setBtcValueForPreferredCurrency(float value);

    boolean getOnlyFavoritesResults();

    boolean getShowOwnedTokens();


    void setUseCoinDesk(boolean value);
    boolean getUseCoinDesk();

    List<String> getFavorites();

    void setFavorites(List<String> favorites);

    void setBtcFixPassed();
    boolean btcFixPassed();

    // DB
    Completable storeLatestBitBlinxData(List<Result> bitBlinxResult);

    Completable storeCoinDeskCurrencies(List<Currency> coinDeskCurrencies);

    Completable storeOwnedToken(OwnedToken ownedToken);

    Completable insertOwnedTokens(OwnedToken... ownedTokens);

    Completable insertOwnedTokens(List<OwnedToken> ownedTokens);

    Completable deleteOwnedToken(OwnedToken ownedToken);

    Completable updateBitBlinxResult(Result result);

    Completable updateOwnedTokenPosition(int id, int position);

    Flowable<List<Result>> getBitBlinxDataFlowable();

    Flowable<List<Result>> getBitBlinxFavoriteDataFlowable();

    Flowable<List<OwnedToken>> getOwnedTokensFlowable();

    Single<List<OwnedToken>> getOwnedTokensSingle();

    Single<List<OwnedToken>> getBtcOwnedTokensSingle();

    Flowable<List<Currency>> getCoinDeskCurrenciesFlowable();

    Flowable<List<String>> getCoinDeskCurrencyStringsFlowable();

    Flowable<List<String>> getCoinDeskCountryStringsFlowable();

    Single<List<String>> getAllBtcPairs();

    Single<List<Result>> getAllBtcPairsComplete();

    Single<List<Result>> getTokenBySymbol(String symbol);


}
