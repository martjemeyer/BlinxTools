package com.dummycoding.mycrypto.data.network;

import com.dummycoding.mycrypto.models.bitblinx.ActiveCurrencies;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BitBlinxApi {
    @GET("api/prices")
    Single<ActiveCurrencies> getActivePairs();

    @GET("api/prices/{currencyPair}")
    Single<ActiveCurrencies> getActiveCurrenciePair(@Path("currencyPair") String currencyPair);
}
