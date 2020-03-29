package com.dummycoding.blinxtools.di;

import com.dummycoding.blinxtools.data.network.BitBlinxApi;
import com.dummycoding.blinxtools.data.network.CoinDeskApi;
import com.dummycoding.blinxtools.usecases.FetchActiveCurrenciesUseCase;
import com.dummycoding.blinxtools.usecases.FetchPricesUseCase;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class CompositionRoot {
    private final String BITBLINX_BASE_URL = "https://trade.bitblinx.com/";
    private final String COINDESK_BASE_URL = "https://api.coindesk.com/v1/";

    private Retrofit mBitBlinxRetrofit = null;
    private Retrofit mCoinDeskRetrofit = null;
    private BitBlinxApi mBitBlinxApi;
    private CoinDeskApi mCoinDeskApi;

    private Retrofit getBitBlinxRetrofit() {
        if (mBitBlinxRetrofit == null) {
            mBitBlinxRetrofit = new Retrofit.Builder()
                    .baseUrl(BITBLINX_BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build();
        }
        return mBitBlinxRetrofit;
    }

    private Retrofit getCoinDeskRetrofit() {
        if (mCoinDeskRetrofit == null) {
            mCoinDeskRetrofit = new Retrofit.Builder()
                    .baseUrl(COINDESK_BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build();
        }
        return mCoinDeskRetrofit;
    }

    private BitBlinxApi getBitBlinxApi() {
        if (mBitBlinxApi == null) {
            mBitBlinxApi = getBitBlinxRetrofit().create(BitBlinxApi.class);
        }
        return mBitBlinxApi;
    }

    private CoinDeskApi getCoinDeskApi() {
        if (mCoinDeskApi == null) {
            mCoinDeskApi = getCoinDeskRetrofit().create(CoinDeskApi.class);
        }
        return mCoinDeskApi;
    }

    public FetchPricesUseCase getFetchPricesUseCase() {
        return new FetchPricesUseCase(getCoinDeskApi());
    }

    public FetchActiveCurrenciesUseCase getFetchActiveCurrenciesUseCase() {
        return new FetchActiveCurrenciesUseCase(getBitBlinxApi());
    }

}
