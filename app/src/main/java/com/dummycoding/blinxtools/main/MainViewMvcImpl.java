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
import com.dummycoding.blinxtools.pojos.bitblinx.Result;

import java.util.ArrayList;
import java.util.List;

public class MainViewMvcImpl extends BaseViewMvc<MainViewMvc.Listener> implements MainViewMvc {

    private final AppCompatButton mRefreshButton;
    private ProgressBar mProgressBar;
    private androidx.recyclerview.widget.RecyclerView mRecyclerView;
    private BitBlinxMainAdapter mAdapter;
    private TextView mCurrentValueBtc;
    private TextView mCurrentValueOwnedToken;

    private final Listener mListener;

    public MainViewMvcImpl(LayoutInflater inflater, ViewGroup container, Listener listener, Context context) {
        setRootView(inflater.inflate(R.layout.activity_main, container, false));

        mListener = listener;
        mRefreshButton = findViewById(R.id.refreshButton);
        mProgressBar = findViewById(R.id.progress);
        mRecyclerView = findViewById(R.id.recyclerView);
        mCurrentValueBtc = findViewById(R.id.currentValueBtcDetailTv);
        mCurrentValueOwnedToken = findViewById(R.id.currentValueOwnedTokensTv);

        mRefreshButton.setOnClickListener(c -> refreshButtonPressed());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        mAdapter = new BitBlinxMainAdapter(context, new ArrayList<>());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void updateAdapter(List<Result> results) {
        mAdapter.updateAdapter(results);
    }

    @Override
    public void showProgressBar(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void updateCurrentValueBtc(String value) {
        mCurrentValueBtc.setText(value);
    }

    @Override
    public void updateCurrentValueOwnedToken(String value) {

    }

    @Override
    public void refreshButtonPressed() {
        mListener.buttonPressed();
    }
}
