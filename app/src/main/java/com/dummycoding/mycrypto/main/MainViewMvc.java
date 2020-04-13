package com.dummycoding.mycrypto.main;

import androidx.appcompat.widget.Toolbar;

import com.dummycoding.mycrypto.adapters.BitBlinxMainAdapter;
import com.dummycoding.mycrypto.adapters.OwnedTokensAdapter;
import com.dummycoding.mycrypto.common.mvcviews.ObservableViewMvc;

public interface MainViewMvc extends ObservableViewMvc<MainViewMvc.Listener> {

    void setPairsRecyclerViewAdapter(BitBlinxMainAdapter adapter);

    void setOwnedTokensRecyclerViewAdapter(OwnedTokensAdapter adapter);

    void showProgressBar(boolean show);

    void updateCurrentValueBtc(String value, String currency);

    void showOwnedTokens(boolean show);

    Toolbar getToolbar();

    interface Listener {
        void fabClicked();

        void onInfoFabPressed();
    }
}
