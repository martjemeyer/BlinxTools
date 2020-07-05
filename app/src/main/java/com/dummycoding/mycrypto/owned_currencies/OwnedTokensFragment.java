package com.dummycoding.mycrypto.owned_currencies;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dummycoding.mycrypto.R;
import com.dummycoding.mycrypto.adapters.OwnedTokensAdapterCallback;
import com.dummycoding.mycrypto.adapters.OwnedTokensFragmentAdapter;
import com.dummycoding.mycrypto.common.BaseFragment;
import com.dummycoding.mycrypto.databinding.FragmentOwnedTokensBinding;
import com.dummycoding.mycrypto.models.CombinedResultWrapper;
import com.dummycoding.mycrypto.models.OwnedToken;
import com.dummycoding.mycrypto.models.bitblinx.ActiveCurrencies;
import com.dummycoding.mycrypto.models.bitblinx.Result;
import com.dummycoding.mycrypto.models.coindesk.BpiCurrency;
import com.dummycoding.mycrypto.models.coindesk.CurrentPrice;
import com.dummycoding.mycrypto.usecases.FetchActiveTokenPairsUseCase;
import com.dummycoding.mycrypto.usecases.FetchPricesUseCase;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class OwnedTokensFragment extends BaseFragment implements OwnedTokensAdapterCallback, SwipeRefreshLayout.OnRefreshListener {

    private FragmentOwnedTokensBinding binding;
    private OwnedTokensViewModel mViewModel;
    private OwnedTokensFragmentAdapter mOwnedTokensAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CompositeDisposable disposeBag = new CompositeDisposable();
    private FetchActiveTokenPairsUseCase mFetchActiveTokenPairsUseCase;
    private FetchPricesUseCase mFetchPricesUseCase;

    public static OwnedTokensFragment newInstance() {
        return new OwnedTokensFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOwnedTokensBinding.inflate(inflater, container, false);

        mOwnedTokensAdapter = new OwnedTokensFragmentAdapter(getContext(), new ArrayList<>(), this);
        binding.ownedTokensFragmentRecyclerView.setAdapter(mOwnedTokensAdapter);
        binding.ownedTokensFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mOwnedTokensAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.ownedTokensFragmentRecyclerView);

        // SwipeRefreshLayout
        mSwipeRefreshLayout = binding.swipeContainer;
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary);

        binding.addFab.setOnClickListener(c -> addButtonClicked());

        mFetchActiveTokenPairsUseCase = getCompositionRoot().getFetchActiveCurrenciesUseCase();
        mFetchPricesUseCase = getCompositionRoot().getFetchPricesUseCase();

        return binding.getRoot();
    }

    private void addButtonClicked() {
        updateOwnedToken(new OwnedToken());
    }

    private void createEditOwnedTokenDialog(List<String> tokens, OwnedToken ownedToken) {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle("Add Token");
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_owned_token, null);
        builder.setView(customLayout);

        Spinner spinner = customLayout.findViewById(R.id.dropdown);
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_spinner_item, tokens);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        EditText editText = customLayout.findViewById(R.id.amount);

        if (ownedToken.getId() >= 0) {
            NumberFormat nf = new DecimalFormat("##.###");
            int index = tokens.indexOf(ownedToken.getToken());
            editText.setText(nf.format(ownedToken.getTokenAmount()));
            spinner.setSelection(index);
        }

        // add a button
        builder.setPositiveButton("Ok", (dialog, id) -> {
            try {
                ownedToken.setToken(spinner.getSelectedItem().toString());
                ownedToken.setTokenAmount(Double.parseDouble(editText.getText().toString()));
                editOwnedToken(ownedToken);
            } catch (Exception ex) {
                Timber.e(ex.getMessage(), "createEditOwnedTokenDialog: ");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, id) -> {
            // do nothing
        });

        if (ownedToken.getToken() != null) {
            builder.setNegativeButton("Delete", (dialog, id) -> getCompositionRoot().getRepository().deleteOwnedToken(ownedToken)
                    .subscribeOn(Schedulers.io())
                    .subscribe(() -> {
                    }, throwable -> Timber.e(throwable, "createEditOwnedTokenDialog: ")));
        }
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("CheckResult")
    private void editOwnedToken(OwnedToken ownedToken) {

        if (ownedToken.getToken().equals("BTC")) {
            ownedToken.setTokenInBtc(getCompositionRoot().getRepository().getBtcValueForPreferredCurrency());
            getCompositionRoot().getRepository().storeOwnedToken(ownedToken)
                    .subscribeOn(Schedulers.io())
                    .subscribe(() -> {
                    }, throwable -> Timber.e(throwable, "editOwnedToken: "));
        } else if (ownedToken.getToken().equals("GTFTA-MA")) {
            getCompositionRoot().getRepository().getTokenBySymbol("GTFTA/BTC")
                    .map(result -> result.get(0))
                    .subscribeOn(Schedulers.io())
                    .subscribe(token -> {
                        ownedToken.setTokenInBtc(Double.parseDouble(token.getLast()) * 50);
                        getCompositionRoot().getRepository().storeOwnedToken(ownedToken).subscribe();
                    }, throwable -> Timber.e(throwable, "editOwnedToken: "));
        } else {
            getCompositionRoot().getRepository().getTokenBySymbol(ownedToken.getToken() + "/BTC")
                    .map(result -> result.get(0))
                    .subscribeOn(Schedulers.io())
                    .subscribe(token -> {
                        ownedToken.setTokenInBtc(Double.parseDouble(token.getLast()));
                        getCompositionRoot().getRepository().storeOwnedToken(ownedToken).subscribe();
                    }, throwable -> Timber.e(throwable, "editOwnedToken: "));
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(OwnedTokensViewModel.class);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        subscribe();
        onRefresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        unsubscribe();
    }

    private void subscribe() {
        disposeBag.add(mViewModel.subscribeToOwnedTokenDbChanges()
                .subscribe(result -> {
                            mOwnedTokensAdapter.updateAdapter(
                                    result,
                                    getCompositionRoot().getRepository().getBtcValueForPreferredCurrency(),
                                    getCompositionRoot().getRepository().getPreferredCurrency()
                            );
                        },
                        throwable -> {
                            Timber.e(throwable, "subscribeToOwnedTokenDbChanges: ");
                        }
                ));
    }

    private void unsubscribe() {
        disposeBag.clear();
    }

    @Override
    public void onRefresh() {
        getLatestData();
    }

    public void showProgressBar(boolean show) {
        binding.progress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

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

    @Override
    public void updateOwnedToken(OwnedToken ownedToken) {
        disposeBag.add(
                getCompositionRoot().getRepository().getAllBtcPairs()
                        .toFlowable()
                        .flatMapIterable(result -> result)
                        .map(result -> result.substring(0, result.indexOf("/")))
                        .toList()
                        .map(result -> {
                            result.add(0, "BTC");
                            if (result.size() > 2) {
                                result.add(3, "GTFTA-MA");
                            }
                            return result;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(tokens -> createEditOwnedTokenDialog(tokens, ownedToken), throwable -> Timber.e(throwable, "fabClicked: "))
        );
    }

    @Override
    public void updateOwnedTokensOrder(List<OwnedToken> ownedTokens) {
        disposeBag.add(
                getCompositionRoot().getRepository().insertOwnedTokens(ownedTokens)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe()
        );
    }
}
