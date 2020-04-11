package com.dummycoding.blinxtools.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dummycoding.blinxtools.R;
import com.dummycoding.blinxtools.adapters.BitBlinxMainAdapter;
import com.dummycoding.blinxtools.adapters.OwnedTokensAdapter;
import com.dummycoding.blinxtools.common.mvcviews.BaseViewMvc;
import com.dummycoding.blinxtools.databinding.ActivityMainBinding;

public class MainViewMvcImpl extends BaseViewMvc<MainViewMvc.Listener> implements MainViewMvc {

    private final Listener mListener;
    private ActivityMainBinding view;

    MainViewMvcImpl(LayoutInflater inflater, Listener listener, Context context) {

        view = ActivityMainBinding.inflate(inflater);
        setRootView(view.getRoot());

        view.pairsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        view.ownedTokensRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        view.fab.setOnClickListener(v -> editOwnedCurrency());

        mListener = listener;
    }

    private void editOwnedCurrency() {
        mListener.fabClicked();
    }


    @Override
    public void setPairsRecyclerViewAdapter(BitBlinxMainAdapter adapter) {
        view.pairsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void setOwnedTokensRecyclerViewAdapter(OwnedTokensAdapter adapter) {
        view.ownedTokensRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showProgressBar(boolean show) {
        view.progress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void updateCurrentValueBtc(String value, String currency) {
        view.currentValueBtcDetailTv.setVisibility(View.VISIBLE);
        view.currentValueBtcDetailTv.setText(String.format(getString(R.string.current_btc_price), value, currency));
    }

    @Override
    public Toolbar getToolbar() {
        return view.toolbar;
    }

}
