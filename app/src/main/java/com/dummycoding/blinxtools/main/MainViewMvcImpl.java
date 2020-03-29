package com.dummycoding.blinxtools.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
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

        mListener = listener;

        view.refreshButton.setOnClickListener(c -> refreshButtonPressed());

        mAdapter = new BitBlinxMainAdapter(context, new ArrayList<>());
        view.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        view.recyclerView.setAdapter(mAdapter);
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
    public void updateCurrentValueBtc(String value) {
        view.currentValueBtcDetailTv.setText(value);
    }

    @Override
    public void updateCurrentValueOwnedToken(String value) {

    }

    @Override
    public void refreshButtonPressed() {
        mListener.buttonPressed();
    }
}
