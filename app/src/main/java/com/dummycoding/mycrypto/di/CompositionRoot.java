package com.dummycoding.mycrypto.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.dummycoding.mycrypto.data.network.BitBlinxApi;
import com.dummycoding.mycrypto.data.network.CoinDeskApi;
import com.dummycoding.mycrypto.data.network.Repository;
import com.dummycoding.mycrypto.data.network.RepositoryImpl;
import com.dummycoding.mycrypto.usecases.FetchActiveTokenPairsUseCase;
import com.dummycoding.mycrypto.usecases.FetchAvailableCurrenciesUseCase;
import com.dummycoding.mycrypto.usecases.FetchPricesUseCase;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class CompositionRoot {
    private final String BITBLINX_BASE_URL = "https://trade.bitblinx.com/";
    private final String COINDESK_BASE_URL = "https://api.coindesk.com/v1/";

    private final Context mContext;
    private Retrofit mBitBlinxRetrofit = null;
    private Retrofit mCoinDeskRetrofit = null;
    private BitBlinxApi mBitBlinxApi;
    private CoinDeskApi mCoinDeskApi;
    private SharedPreferences mSharedPreferences;
    private Repository mRepository;

    public CompositionRoot(Context context) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
    }

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

    private Context getApplicationContext() {
        return mContext;
    }

    private SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    public Repository getRepository() {
        if (mRepository == null) {
            mRepository = new RepositoryImpl(getApplicationContext(), getSharedPreferences());
        }
        return mRepository;
    }

    /// Public methods

    public FetchPricesUseCase getFetchPricesUseCase() {
        return new FetchPricesUseCase(getCoinDeskApi());
    }

    public FetchAvailableCurrenciesUseCase getFetchAvailableCurrenciesUseCase() {
        return new FetchAvailableCurrenciesUseCase(getCoinDeskApi());
    }

    public FetchActiveTokenPairsUseCase getFetchActiveCurrenciesUseCase() {
        return new FetchActiveTokenPairsUseCase(getBitBlinxApi());
    }

}
