package com.dummycoding.blinxtools.usecases;

import com.dummycoding.blinxtools.data.network.CoinDeskApi;
import com.dummycoding.blinxtools.pojos.coindesk.Currency;

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
