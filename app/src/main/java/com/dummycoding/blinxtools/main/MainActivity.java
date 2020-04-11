package com.dummycoding.blinxtools.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dummycoding.blinxtools.BaseActivity;
import com.dummycoding.blinxtools.R;
import com.dummycoding.blinxtools.adapters.BitBlinxMainAdapter;
import com.dummycoding.blinxtools.adapters.OwnedTokensAdapter;
import com.dummycoding.blinxtools.adapters.OwnedTokensAdapterCallback;
import com.dummycoding.blinxtools.helpers.CurrencyHelper;
import com.dummycoding.blinxtools.models.CombinedResultWrapper;
import com.dummycoding.blinxtools.models.OwnedToken;
import com.dummycoding.blinxtools.models.bitblinx.ActiveCurrencies;
import com.dummycoding.blinxtools.models.bitblinx.Result;
import com.dummycoding.blinxtools.models.coindesk.BpiCurrency;
import com.dummycoding.blinxtools.models.coindesk.CurrentPrice;
import com.dummycoding.blinxtools.preferences.SettingsActivity;
import com.dummycoding.blinxtools.usecases.FetchActiveTokenPairsUseCase;
import com.dummycoding.blinxtools.usecases.FetchAvailableCurrenciesUseCase;
import com.dummycoding.blinxtools.usecases.FetchPricesUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements MainViewMvc.Listener, OwnedTokensAdapterCallback, SwipeRefreshLayout.OnRefreshListener {

    private MainViewMvc mViewMvc;
    private BitBlinxMainAdapter mBitBlinxMainAdapter;
    private OwnedTokensAdapter mOwnedTokensAdapter;

    private CompositeDisposable subscriptions = new CompositeDisposable();
    private FetchActiveTokenPairsUseCase mFetchActiveTokenPairsUseCase;
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

        mFetchActiveTokenPairsUseCase = getCompositionRoot().getFetchActiveCurrenciesUseCase();
        mFetchPricesUseCase = getCompositionRoot().getFetchPricesUseCase();
        mFetchAvailableCurrenciesUseCase = getCompositionRoot().getFetchAvailableCurrenciesUseCase();

        // SwipeRefreshLayout
        mSwipeRefreshLayout = findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary);

        mBitBlinxMainAdapter = new BitBlinxMainAdapter(this, new ArrayList<>());
        mOwnedTokensAdapter = new OwnedTokensAdapter(this, new ArrayList<>(), this);

        mViewMvc.setPairsRecyclerViewAdapter(mBitBlinxMainAdapter);
        mViewMvc.setOwnedTokensRecyclerViewAdapter(mOwnedTokensAdapter);

        // only once per app live time, since this list is not likely to update often.
        // maybe make it once per app installation
        updateFiatCurrencies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            openSettings();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscriptions.addAll(getBitBlinxResultDbChangesSubscription(), getOwnedTokenDbChangesSubscription());
        getBitBlinxResultDbChangesSubscription();
        getOwnedTokenDbChangesSubscription();

        onRefresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        subscriptions.clear();
    }
    
    private void updatePairsAdapter(List<Result> results) {
        mBitBlinxMainAdapter.updateAdapter(results);
    }

    private void updateOwnedTokensAdapter(List<OwnedToken> ownedTokens) {
        mOwnedTokensAdapter.updateAdapter(ownedTokens);
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private Disposable getBitBlinxResultDbChangesSubscription() {
        return getCompositionRoot().getRepository().getBitBlinxDataFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    String preferredCurrency = getCompositionRoot().getRepository().getPreferredCurrency();
                    mViewMvc.updateCurrentValueBtc(CurrencyHelper.round(getCompositionRoot().getRepository().getBtcValueForPreferredCurrency()), preferredCurrency);
                    updatePairsAdapter(result);
                    mOwnedTokensAdapter.notifyDataSetChanged(); // use latest rates
                }, throwable -> Timber.e(throwable, "subscribeToBitBlinxResultDbChanges: "));

    }

    private Disposable getOwnedTokenDbChangesSubscription() {
        return getCompositionRoot().getRepository().getOwnedTokensFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateOwnedTokensAdapter,
                        throwable -> Timber.e(throwable, "subscribeToOwnedTokenDbChanges: "));
    }

    private Single<List<Result>> getActiveTokenPairs() {
        return mFetchActiveTokenPairsUseCase.getActivePairs()
                .subscribeOn(Schedulers.io())
                .map(ActiveCurrencies::getResult);
    }

    private Single<BpiCurrency> getActiveCurrencies() {
        String preferredCurrency = getCompositionRoot().getRepository().getPreferredCurrency();
        return mFetchPricesUseCase.getCurrentPrice(preferredCurrency)
                .map(CurrentPrice::getBpiCurrencies)
                .map(bpi -> Objects.requireNonNull(bpi.get(preferredCurrency)));
    }

    @SuppressLint("CheckResult")
    private void updateFiatCurrencies() {
        mFetchAvailableCurrenciesUseCase.getCurrencies()
                .subscribeOn(Schedulers.io())
                .subscribe(result -> getCompositionRoot().getRepository().storeCoinDeskCurrencies(result)
                        .subscribe(), throwable -> Timber.e(throwable, "buttonPressed: "));
    }

    @SuppressLint("CheckResult")
    private void getLatestData() {
        Single.zip(getActiveCurrencies(), getActiveTokenPairs(), CombinedResultWrapper::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(wrapper -> mViewMvc.showProgressBar(true))
                .doFinally(() -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mViewMvc.showProgressBar(false);
                })
                .subscribe(resultWrapper -> {
                    getCompositionRoot().getRepository().setBtcValueForPreferredCurrency(resultWrapper.getBpi().getRateFloat());
                    getCompositionRoot().getRepository().storeLatestBitBlinxData(resultWrapper.getResult())
                            .subscribeOn(Schedulers.io())
                            .subscribe();
                }, throwable -> Timber.e(throwable, "onRefresh: "));
    }

    @Override
    public void onRefresh() {
        getLatestData();
    }

    @Override
    public void updateOwnedToken(OwnedToken ownedToken) {
        getCompositionRoot().getRepository().storeOwnedToken(ownedToken)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    @SuppressLint("CheckResult")
    @Override
    public void fabClicked() {
        getCompositionRoot().getRepository().getAllBtcPairs()
                .toFlowable()
                .flatMapIterable(result -> result)
                .map(result -> result.substring(0, result.indexOf("/")))
                .toList()
                .map(result -> {
                    result.add(0, "BTC");
                    return result;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tokens -> createEditOwnedTokenDialog(tokens, "Add Token"), throwable -> Timber.e(throwable, "fabClicked: "));
    }

    private void createEditOwnedTokenDialog(List<String> tokens, String operation) {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(operation);
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_owned_currency, null);
        builder.setView(customLayout);

        Spinner spinner = customLayout.findViewById(R.id.dropdown);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tokens);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // add a button
        builder.setPositiveButton("Ok", (dialog, id) -> {
            // send data from the AlertDialog to the Activity
            EditText editText = customLayout.findViewById(R.id.amount);
            sendDialogDataToActivity(editText.getText().toString(), spinner.getSelectedItem().toString());
        });

        builder.setNegativeButton("Cancel", (dialog, id) -> {
            // do nothing
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("CheckResult")
    private void sendDialogDataToActivity(String amount, String selectedToken) {
        double btcInCurrency = getCompositionRoot().getRepository().getBtcValueForPreferredCurrency();
        String preferredCurrency = getCompositionRoot().getRepository().getPreferredCurrency();
        getCompositionRoot().getRepository().getTokenBySymbol(selectedToken + "/BTC")
                .map(result -> result.get(0))
                .subscribeOn(Schedulers.io())
                .subscribe(token -> {
                    OwnedToken ownedToken = new OwnedToken(selectedToken, Integer.parseInt(amount), Double.parseDouble(token.getLast()), btcInCurrency, preferredCurrency);
                    getCompositionRoot().getRepository().storeOwnedToken(ownedToken).subscribe();
                }, throwable -> Timber.e(throwable, "sendDialogDataToActivity: "));

    }
}
