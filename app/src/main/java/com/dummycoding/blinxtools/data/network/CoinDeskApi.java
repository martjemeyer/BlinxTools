package com.dummycoding.blinxtools.data.network;

import com.dummycoding.blinxtools.pojos.coindesk.CurrentPrice;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CoinDeskApi {
    @GET("bpi/currentprice.json")
    Single<CurrentPrice> getCurrentPrice();

    @GET("bpi/currentprice/{fiatCurrency}.json")
    Single<CurrentPrice> getCurrentPriceFiat(@Path("fiatCurrency") String fiatCurrency);
}
