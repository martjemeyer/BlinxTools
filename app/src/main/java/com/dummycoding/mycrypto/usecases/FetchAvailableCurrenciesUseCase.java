package com.dummycoding.mycrypto.usecases;

import com.dummycoding.mycrypto.data.network.CoinDeskApi;
import com.dummycoding.mycrypto.models.coindesk.Currency;

import java.util.List;

import io.reactivex.Single;

public class FetchAvailableCurrenciesUseCase {
    private CoinDeskApi mCoinDeskApi;

    public FetchAvailableCurrenciesUseCase(CoinDeskApi coinDeskApi) {
        mCoinDeskApi = coinDeskApi;
    }

    public Single<List<Currency>> getCurrencies() {
        return mCoinDeskApi.getCurrencies();
    }
}
