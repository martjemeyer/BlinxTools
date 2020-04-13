package com.dummycoding.mycrypto.usecases;

import com.dummycoding.mycrypto.data.network.CoinDeskApi;
import com.dummycoding.mycrypto.models.coindesk.CurrentPrice;

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
