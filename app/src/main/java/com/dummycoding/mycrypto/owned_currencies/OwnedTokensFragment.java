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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dummycoding.mycrypto.R;
import com.dummycoding.mycrypto.adapters.OwnedTokensAdapterCallback;
import com.dummycoding.mycrypto.adapters.OwnedTokensFragmentAdapter;
import com.dummycoding.mycrypto.common.BaseFragment;
import com.dummycoding.mycrypto.databinding.FragmentOwnedTokensBinding;
import com.dummycoding.mycrypto.models.OwnedToken;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        // SwipeRefreshLayout
  /*
        mSwipeRefreshLayout = binding.swipeContainer;
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary);
*/

        binding.addFab.setOnClickListener(c -> addButtonClicked());

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
                    .subscribe(() -> {}, throwable -> Timber.e(throwable, "createEditOwnedTokenDialog: ")));
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
                    return result;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tokens -> createEditOwnedTokenDialog(tokens, ownedToken), throwable -> Timber.e(throwable, "fabClicked: "))
        );
    }
}
