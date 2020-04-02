package com.dummycoding.blinxtools.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dummycoding.blinxtools.BaseActivity;
import com.dummycoding.blinxtools.R;
import com.dummycoding.blinxtools.pojos.coindesk.BpiCurrency;
import com.dummycoding.blinxtools.pojos.coindesk.Currency;
import com.dummycoding.blinxtools.pojos.coindesk.CurrentPrice;
import com.dummycoding.blinxtools.preferences.SettingsActivity;
import com.dummycoding.blinxtools.usecases.FetchActiveCurrenciesUseCase;
import com.dummycoding.blinxtools.usecases.FetchAvailableCurrenciesUseCase;
import com.dummycoding.blinxtools.usecases.FetchPricesUseCase;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements MainViewMvc.Listener {

    private MainViewMvc mViewMvc;
    private CompositeDisposable subscriptions = new CompositeDisposable();
    private FetchActiveCurrenciesUseCase mFetchActiveCurrenciesUseCase;
    private FetchPricesUseCase mFetchPricesUseCase;
    private FetchAvailableCurrenciesUseCase mFetchAvailableCurrenciesUseCase;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = new MainViewMvcImpl(LayoutInflater.from(this), this, this);
        setContentView(mViewMvc.getRootView());

        setSupportActionBar(mViewMvc.getToolbar());

        mFetchActiveCurrenciesUseCase = getCompositionRoot().getFetchActiveCurrenciesUseCase();
        mFetchPricesUseCase = getCompositionRoot().getFetchPricesUseCase();
        mFetchAvailableCurrenciesUseCase = getCompositionRoot().getFetchAvailableCurrenciesUseCase();

        buttonPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            openSettings();
        }
        return super.onOptionsItemSelected(item);
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

    void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @SuppressLint("CheckResult")
    @Override
    public void buttonPressed() {

        getActiveCurrencies();

       /* mFetchAvailableCurrenciesUseCase.getCurrencies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {

                    for (Currency cur : result) {
                        Timber.d(cur.getCurrency() + " " + cur.getCountry());
                    }
                    mViewMvc.showProgressBar(false);
                }, throwable -> {
                    Toast.makeText(this, "Error getting bitblinx data", Toast.LENGTH_LONG).show();
                    Timber.e(throwable, "buttonPressed: ");
                    mViewMvc.showProgressBar(false);
                });*/

        mViewMvc.showProgressBar(true);

        mFetchActiveCurrenciesUseCase.getActiveCurrencies()
                .subscribeOn(Schedulers.io())
                .map(activeCurrencies -> activeCurrencies.getResult())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(results -> {
                    mViewMvc.updateAdapter(results);
                    mViewMvc.showProgressBar(false);
                }, throwable -> {
                    Toast.makeText(this, "Error getting BitBlinx data", Toast.LENGTH_LONG).show();
                    Timber.e(throwable, "buttonPressed: ");
                    mViewMvc.showProgressBar(false);
                });


    }

    private void getActiveCurrencies() {
        String preferredCurrency = getCompositionRoot().getRepository().getPreferredCurrency();
        mFetchPricesUseCase.getCurrentPrice(preferredCurrency)
                .subscribeOn(Schedulers.io())
                .map(CurrentPrice::getBpiCurrencies)
                .map(bpi -> {
                    BpiCurrency bpiCurrency = Objects.requireNonNull(bpi.get(preferredCurrency));
                    getCompositionRoot().getRepository().setBtcValueForPreferredCurrency(bpiCurrency.getRateFloat());
                    return bpiCurrency;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bpiCurrency -> mViewMvc.updateCurrentValueBtc(bpiCurrency.getRate(), preferredCurrency),
                        throwable -> {
                            Toast.makeText(this, "Error getting CoinDesk data", Toast.LENGTH_LONG).show();
                            Timber.e(throwable, "buttonPressed: ");
                            mViewMvc.showProgressBar(false);
                });
    }
}
