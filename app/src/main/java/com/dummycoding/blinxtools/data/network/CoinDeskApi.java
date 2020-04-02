package com.dummycoding.blinxtools.data.network;

import com.dummycoding.blinxtools.pojos.coindesk.Currency;
import com.dummycoding.blinxtools.pojos.coindesk.CurrentPrice;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CoinDeskApi {

    // Returns BTC price in EUR, USD and GBP
    @GET("bpi/currentprice.json")
    Single<CurrentPrice> getCurrentPrice();

    // Needed for different fiat currency then EUR, USD or GBP
    @GET("bpi/currentprice/{fiatCurrency}.json")
    Single<CurrentPrice> getCurrentPriceFiat(@Path("fiatCurrency") String fiatCurrency);

    @GET("https://api.coindesk.com/v1/bpi/supported-currencies.json")
    Single<List<Currency>> getCurrencies();
}
