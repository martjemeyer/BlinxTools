package com.dummycoding.blinxtools.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dummycoding.blinxtools.BaseActivity;
import com.dummycoding.blinxtools.R;
import com.dummycoding.blinxtools.models.BitBlinxResult;
import com.dummycoding.blinxtools.pojos.bitblinx.Result;
import com.dummycoding.blinxtools.pojos.coindesk.BpiCurrency;
import com.dummycoding.blinxtools.pojos.coindesk.CurrentPrice;
import com.dummycoding.blinxtools.preferences.SettingsActivity;
import com.dummycoding.blinxtools.usecases.FetchActiveCurrenciesUseCase;
import com.dummycoding.blinxtools.usecases.FetchAvailableCurrenciesUseCase;
import com.dummycoding.blinxtools.usecases.FetchPricesUseCase;

import org.intellij.lang.annotations.Flow;

import java.util.Objects;
import java.util.Observable;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements MainViewMvc.Listener, SwipeRefreshLayout.OnRefreshListener {

    private MainViewMvc mViewMvc;
    private CompositeDisposable subscriptions = new CompositeDisposable();
    private FetchActiveCurrenciesUseCase mFetchActiveCurrenciesUseCase;
    private FetchPricesUseCase mFetchPricesUseCase;
    private FetchAvailableCurrenciesUseCase mFetchAvailableCurrenciesUseCase;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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

        // SwipeRefreshLayout
        mSwipeRefreshLayout = findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                onRefresh();
            }
        });
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

    @Override
    public void onRefresh() {
        getActiveCurrencies();

        mSwipeRefreshLayout.setRefreshing(true);

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

        mFetchActiveCurrenciesUseCase.getActiveCurrencies()
                .subscribeOn(Schedulers.io())
                .map(activeCurrencies -> activeCurrencies.getResult()).toFlowable()
                .flatMap(result -> Flowable.fromIterable(result))
                .map(result -> BitBlinxResult.shallowCopy(result))
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(results -> {
                    /*BitBlinxResult firstResult = results.get(0);
                    if (firstResult.symbol.contains("GTPLUS/BTC")) {
                        float preferredCurrency = getCompositionRoot().getRepository().getBtcValueForPreferredCurrency();
                        mViewMvc.updateCurrentValueOwnedToken(Float.parseFloat(firstResult.last) * preferredCurrency * 500 + "");
                    }*/
                    //mViewMvc.updateAdapter(results);
                    mSwipeRefreshLayout.setRefreshing(false);
                    getCompositionRoot().getRepository().storeLatestBitBlinxData(results);
                }, throwable -> {
                    Toast.makeText(this, "Error getting BitBlinx data", Toast.LENGTH_LONG).show();
                    Timber.e(throwable, "buttonPressed: ");
                    mSwipeRefreshLayout.setRefreshing(false);
                });
    }

    @Override
    public void buttonPressed() {

    }
}
