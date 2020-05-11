package com.dummycoding.mycrypto.main;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dummycoding.mycrypto.R;
import com.dummycoding.mycrypto.adapters.BitBlinxMainAdapter;
import com.dummycoding.mycrypto.adapters.OwnedTokensAdapter;
import com.dummycoding.mycrypto.common.mvcviews.BaseViewMvc;

public class MainViewMvcImpl {//extends BaseViewMvc<MainViewMvc.Listener> implements MainViewMvc {

    //private final Listener mListener;

    MainViewMvcImpl(LayoutInflater inflater, MainViewMvc.Listener listener, Context context) {

/*        view = com.dummycoding.mycrypto.databinding.ActivityMainBinding.inflate(inflater);
        setRootView(view.getRoot());

        view.pairsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        view.ownedTokensRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        view.fab.setOnClickListener(v -> editOwnedCurrency());
        view.poweredByCoinDesk.setOnClickListener(v -> coinDeskHyperLinkPressed());
        view.poweredByBitBlinx.setOnClickListener(v -> bitBlinxHyperLinkPressed());
        view.infoFab.setOnClickListener(v -> onInfoFabPressed());*/

        //mListener = listener;
    }

 /*   private void onInfoFabPressed() {
        mListener.onInfoFabPressed();
    }

    private void bitBlinxHyperLinkPressed() {
        view.poweredByBitBlinx.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void coinDeskHyperLinkPressed() {
        view.poweredByCoinDesk.setMovementMethod(LinkMovementMethod.getInstance());
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
    public void showOwnedTokens(boolean show) {
        view.ownedTokensContainer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public Toolbar getToolbar() {
        return view.toolbar;
    }*/

}
