package com.dummycoding.blinxtools.usecases;

import com.dummycoding.blinxtools.data.network.BitBlinxApi;
import com.dummycoding.blinxtools.models.bitblinx.ActiveCurrencies;

import io.reactivex.Single;

public class FetchActiveTokenPairsUseCase {

    private BitBlinxApi mBitBlinxApi;

    public FetchActiveTokenPairsUseCase(BitBlinxApi bitBlinxApi) {
        mBitBlinxApi = bitBlinxApi;
    }

    public Single<ActiveCurrencies> getActivePairs() {
        return mBitBlinxApi.getActivePairs();
    }
}
