package com.dummycoding.blinxtools.usecases;

import com.dummycoding.blinxtools.data.network.CoinDeskApi;
import com.dummycoding.blinxtools.pojos.coindesk.CurrentPrice;

import io.reactivex.Single;

public class FetchPricesUseCase {

    private CoinDeskApi mCoinDeskApi;

    public FetchPricesUseCase(CoinDeskApi coinDeskApi) {
        mCoinDeskApi = coinDeskApi;
    }

    public Single<CurrentPrice> getCurrentPrice(String currency) {
        return mCoinDeskApi.getCurrentPriceFiat(currency);
    }
}
