package com.dummycoding.mycrypto.markets;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dummycoding.mycrypto.R;
import com.dummycoding.mycrypto.adapters.BitBlinxMainAdapter;
import com.dummycoding.mycrypto.adapters.BitBlinxMainAdapterCallback;
import com.dummycoding.mycrypto.adapters.OwnedTokensAdapter;
import com.dummycoding.mycrypto.calculator.CalculatorActivity;
import com.dummycoding.mycrypto.common.BaseFragment;
import com.dummycoding.mycrypto.databinding.FragmentMarketsBinding;
import com.dummycoding.mycrypto.helpers.CurrencyHelper;
import com.dummycoding.mycrypto.models.CombinedResultWrapper;
import com.dummycoding.mycrypto.models.OwnedToken;
import com.dummycoding.mycrypto.models.bitblinx.ActiveCurrencies;
import com.dummycoding.mycrypto.models.bitblinx.Result;
import com.dummycoding.mycrypto.models.coindesk.BpiCurrency;
import com.dummycoding.mycrypto.models.coindesk.CurrentPrice;
import com.dummycoding.mycrypto.usecases.FetchActiveTokenPairsUseCase;
import com.dummycoding.mycrypto.usecases.FetchAvailableCurrenciesUseCase;
import com.dummycoding.mycrypto.usecases.FetchPricesUseCase;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MarketsFragment extends BaseFragment implements BitBlinxMainAdapterCallback, SwipeRefreshLayout.OnRefreshListener {

    private FragmentMarketsBinding binding;
    private MarketsViewModel mViewModel;

    private BitBlinxMainAdapter mBitBlinxMainAdapter;
    private OwnedTokensAdapter mOwnedTokensAdapter;

    private CompositeDisposable subscriptions = new CompositeDisposable();
    private FetchActiveTokenPairsUseCase mFetchActiveTokenPairsUseCase;
    private FetchPricesUseCase mFetchPricesUseCase;
    private FetchAvailableCurrenciesUseCase mFetchAvailableCurrenciesUseCase;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean matchConstraintAlreadySet = false;
    private boolean wrapContentAlreadySet = false;


    public static MarketsFragment newInstance() {
        return new MarketsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMarketsBinding.inflate(inflater, container, false);

        //binding.fab.setOnClickListener(v -> editOwnedCurrency());

        mFetchActiveTokenPairsUseCase = getCompositionRoot().getFetchActiveCurrenciesUseCase();
        mFetchPricesUseCase = getCompositionRoot().getFetchPricesUseCase();
        mFetchAvailableCurrenciesUseCase = getCompositionRoot().getFetchAvailableCurrenciesUseCase();

        // SwipeRefreshLayout
        mSwipeRefreshLayout = binding.swipeContainer;
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary);

        setupRecyclerViews();

        binding.infoFab.setOnClickListener(v -> fabClicked());

        // only once per app live time, since this list is not likely to update often.
        // maybe make it once per app installation
        updateFiatCurrencies();

        return binding.getRoot();
    }

    private void fabClicked() {
        Intent intent = new Intent(getActivity(), CalculatorActivity.class);
        startActivity(intent);
    }

    private void setupRecyclerViews() {
        mBitBlinxMainAdapter = new BitBlinxMainAdapter(getContext(), new ArrayList<>(), this);
        mOwnedTokensAdapter = new OwnedTokensAdapter(getContext(), new ArrayList<>());

        binding.pairsRecyclerView.setAdapter(mBitBlinxMainAdapter);
        binding.ownedTokensRecyclerView.setAdapter(mOwnedTokensAdapter);

        binding.pairsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.ownedTokensRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MarketsViewModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        subscriptions.addAll(getBitBlinxResultDbChangesSubscription(), getOwnedTokenDbChangesSubscription());
        getOwnedTokenDbChangesSubscription();

        showOwnedTokens(getCompositionRoot().getRepository().getShowOwnedTokens());
        onRefresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptions.clear();
    }

    public void showOwnedTokens(boolean show) {
        binding.ownedTokensContainer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void updateCurrentValueBtc(String value, String currency) {
        binding.currentValueBtcDetailTv.setVisibility(View.VISIBLE);
        binding.currentValueBtcDetailTv.setText(String.format(getString(R.string.current_btc_price), value, currency));
    }

    public void showProgressBar(boolean show) {
        binding.progress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }


    private void updatePairsAdapter(List<Result> results) {
        mBitBlinxMainAdapter.updateAdapter(results);
    }

    private void updateOwnedTokensAdapter(List<OwnedToken> ownedTokens) {
        double btcInCurrency = getCompositionRoot().getRepository().getBtcValueForPreferredCurrency();
        String preferredCurrency = getCompositionRoot().getRepository().getPreferredCurrency();
        mOwnedTokensAdapter.updateAdapter(ownedTokens, btcInCurrency, preferredCurrency);
    }

    private Disposable getBitBlinxResultDbChangesSubscription() {
        return (getCompositionRoot().getRepository().getOnlyFavoritesResults()
                ? getCompositionRoot().getRepository().getBitBlinxFavoriteDataFlowable()
                : getCompositionRoot().getRepository().getBitBlinxDataFlowable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    String preferredCurrency = getCompositionRoot().getRepository().getPreferredCurrency();
                    double btcValue = getCompositionRoot().getRepository().getBtcValueForPreferredCurrency();
                    if (btcValue > 0) {
                        updateCurrentValueBtc(CurrencyHelper.round(btcValue), preferredCurrency);
                    }
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
        List<String> favorites = getCompositionRoot().getRepository().getFavorites();

        return mFetchActiveTokenPairsUseCase.getActivePairs()
                .subscribeOn(Schedulers.io())
                .map(ActiveCurrencies::getResult)
                .flattenAsFlowable(list -> list)
                .map(result -> {
                    if (favorites.contains(result.getSymbol())) {
                        result.setFavorited(true);
                    }
                    return result;
                })
                .toList();
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
                .doOnSubscribe(wrapper -> showProgressBar(true))
                .doFinally(() -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    showProgressBar(false);
                })
                .subscribe(resultWrapper -> {
                    getCompositionRoot().getRepository().setBtcValueForPreferredCurrency(resultWrapper.getBpi().getRateFloat());
                    getCompositionRoot().getRepository().storeLatestBitBlinxData(resultWrapper.getResult())
                            .subscribeOn(Schedulers.io())
                            .subscribe(() -> {
                            }, throwable -> Timber.e(throwable, "getLatestData: "));
                    if (getCompositionRoot().getRepository().getShowOwnedTokens()) {
                        updateOwnedTokensRates(resultWrapper);
                    }
                }, throwable -> Timber.e(throwable, "getLatestData: "));
    }

    @SuppressLint("CheckResult")
    private void updateOwnedTokensRates(CombinedResultWrapper resultWrapper) {
        mOwnedTokensAdapter.refreshWithUpdatedBtcCurrency(resultWrapper.getBpi().getRateFloat());
        List<Result> results = resultWrapper.getResult();

        getCompositionRoot().getRepository().getOwnedTokensSingle()
                .subscribeOn(Schedulers.io())
                .toFlowable()
                .flatMapIterable(list -> list)
                .map(ownedToken -> {
                    int index = results.indexOf(new Result(ownedToken.getToken() + "/BTC"));

                    if (index != -1) {
                        ownedToken.setTokenInBtc(Double.parseDouble(results.get(index).getLast()));
                        getCompositionRoot().getRepository().storeOwnedToken(ownedToken)
                                .subscribe(() -> {
                                }, throwable -> Timber.e(throwable, "updateOwnedTokensRates: "));
                    }
                    return ownedToken;
                })
                .toList()
                .subscribe(tokens -> Timber.d("Updated ownedTokens"),
                        throwable -> Timber.e(throwable, "updateOwnedTokensRates: "));
    }

    public void onRefresh() {
        getLatestData();
    }

    @SuppressLint("CheckResult")
    public void setFavorite(Result result) {
        List<String> favorites = getCompositionRoot().getRepository().getFavorites();
        if (favorites.contains(result.getSymbol())) {
            favorites.remove(result.getSymbol());
        } else {
            favorites.add(result.getSymbol());
        }
        getCompositionRoot().getRepository().setFavorites(favorites);
        getCompositionRoot().getRepository().updateBitBlinxResult(result)
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                }, throwable -> Timber.e(throwable, "setFavorite: "));
    }

    @Override
    public void handleLongClicked(Result result) {
        String pair = result.getSymbol().replace("/", "-");
        ;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://trade.bitblinx.com/sessions/market-view?symbol=" + pair));
        startActivity(intent);
    }

    @Override
    public void notifyDoubleTapToFavorite() {
        if (getCompositionRoot().getRepository().isDoubleClickHintHidden()) {
            return;
        }
        Snackbar snackbar = Snackbar.make(binding.getRoot(), "Tap again to (un)favorite", Snackbar.LENGTH_LONG);
        snackbar.setAction("Don't show again", new HideSnackBarListener());
        snackbar.show();
    }

    private class HideSnackBarListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            getCompositionRoot().getRepository().setHideDoubleClickHint();
        }
    }
}
