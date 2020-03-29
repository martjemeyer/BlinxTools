package com.dummycoding.blinxtools.usecases;

import com.dummycoding.blinxtools.data.network.BitBlinxApi;
import com.dummycoding.blinxtools.pojos.bitblinx.ActiveCurrencies;

import io.reactivex.Single;

public class FetchActiveCurrenciesUseCase {

    private BitBlinxApi mBitBlinxApi;

    public FetchActiveCurrenciesUseCase(BitBlinxApi bitBlinxApi) {
        mBitBlinxApi = bitBlinxApi;
    }

    public Single<ActiveCurrencies> getActiveCurrencies() {
        return mBitBlinxApi.getActiveCurrencies();
    }
}
