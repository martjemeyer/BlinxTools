package com.dummycoding.blinxtools.main;

import androidx.appcompat.widget.Toolbar;

import com.dummycoding.blinxtools.adapters.BitBlinxMainAdapter;
import com.dummycoding.blinxtools.adapters.OwnedTokensAdapter;
import com.dummycoding.blinxtools.common.mvcviews.ObservableViewMvc;
import com.dummycoding.blinxtools.models.OwnedToken;
import com.dummycoding.blinxtools.models.bitblinx.Result;

import java.util.List;

public interface MainViewMvc extends ObservableViewMvc<MainViewMvc.Listener> {

    interface Listener {
        void fabClicked();
    }

    void setPairsRecyclerViewAdapter(BitBlinxMainAdapter adapter);
    void setOwnedTokensRecyclerViewAdapter(OwnedTokensAdapter adapter);

    void showProgressBar(boolean show);
    void updateCurrentValueBtc(String value, String currency);
    Toolbar getToolbar();
}
