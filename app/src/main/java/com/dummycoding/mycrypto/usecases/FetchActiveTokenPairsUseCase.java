package com.dummycoding.mycrypto.usecases;

import com.dummycoding.mycrypto.data.network.BitBlinxApi;
import com.dummycoding.mycrypto.models.bitblinx.ActiveCurrencies;

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
