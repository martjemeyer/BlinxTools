package com.dummycoding.blinxtools.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.dummycoding.blinxtools.BaseActivity;
import com.dummycoding.blinxtools.databinding.ActivityMainBinding;
import com.dummycoding.blinxtools.usecases.FetchActiveCurrenciesUseCase;
import com.dummycoding.blinxtools.usecases.FetchPricesUseCase;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements MainViewMvc.Listener {

    private MainViewMvc mViewMvc;
    private FetchActiveCurrenciesUseCase mFetchActiveCurrenciesUseCase;
    private FetchPricesUseCase mFetchPricesUseCase;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = new MainViewMvcImpl(LayoutInflater.from(this), this, this);
        setContentView(mViewMvc.getRootView());

        mFetchActiveCurrenciesUseCase = getCompositionRoot().getFetchActiveCurrenciesUseCase();
        mFetchPricesUseCase = getCompositionRoot().getFetchPricesUseCase();
        buttonPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("CheckResult")
    @Override
    public void buttonPressed() {
        mViewMvc.showProgressBar(true);
        mFetchActiveCurrenciesUseCase.getActiveCurrencies()
                .subscribeOn(Schedulers.io())
                .map(activeCurrencies -> activeCurrencies.getResult())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(results -> {

                    mViewMvc.updateAdapter(results);
                    mViewMvc.showProgressBar(false);
                }, throwable -> {
                    Toast.makeText(this, "Error getting bitblinx data", Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", "buttonPressedE: ", throwable);
                    mViewMvc.showProgressBar(false);
                });

        mFetchPricesUseCase.getCurrentPrice()
                .subscribeOn(Schedulers.io())
                .map(prices -> prices.getBpi().getEUR())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(results -> {
                    Log.d("MainActivity", "buttonPressed: " + results.getRateFloat());
                    mViewMvc.updateCurrentValueBtc(Double.toString(results.getRateFloat()));
                }, throwable -> {
                    Toast.makeText(this, "Error getting coindesk data", Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", "buttonPressedE: ", throwable);
                    mViewMvc.showProgressBar(false);
        });
    }
}
