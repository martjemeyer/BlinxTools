package com.dummycoding.blinxtools.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dummycoding.blinxtools.R;
import com.dummycoding.blinxtools.adapters.BitBlinxMainAdapter;
import com.dummycoding.blinxtools.common.mvcviews.BaseViewMvc;
import com.dummycoding.blinxtools.databinding.ActivityMainBinding;
import com.dummycoding.blinxtools.pojos.bitblinx.Result;

import java.util.ArrayList;
import java.util.List;

public class MainViewMvcImpl extends BaseViewMvc<MainViewMvc.Listener> implements MainViewMvc {

    private ActivityMainBinding view;

    private BitBlinxMainAdapter mAdapter;
    private final Listener mListener;

    MainViewMvcImpl(LayoutInflater inflater, Listener listener, Context context) {

        view = ActivityMainBinding.inflate(inflater);
        setRootView(view.getRoot());

        mAdapter = new BitBlinxMainAdapter(context, new ArrayList<>());
        view.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        view.recyclerView.setAdapter(mAdapter);

        mListener = listener;
    }

    @Override
    public void updateAdapter(List<Result> results) {
        mAdapter.updateAdapter(results);
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
    public void updateCurrentValueOwnedToken(String value) {
        view.minimumFundAccountTv.setText(String.format(getString(R.string.min_gtplus_for_funding), value, "EUR\n" +
                "5000 GTFTA = 3507.0365 EUR"));
    }

    @Override
    public Toolbar getToolbar() {
        return view.toolbar;
    }

    @Override
    public void refreshButtonPressed() {
        mListener.buttonPressed();
    }
}
