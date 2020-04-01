package com.dummycoding.blinxtools.main;

import androidx.appcompat.widget.Toolbar;

import com.dummycoding.blinxtools.common.mvcviews.ObservableViewMvc;
import com.dummycoding.blinxtools.pojos.bitblinx.Result;

import java.util.List;

public interface MainViewMvc extends ObservableViewMvc<MainViewMvc.Listener> {

    interface Listener {
        void buttonPressed();
    }

    void updateAdapter(List<Result> results);
    void refreshButtonPressed();
    void showProgressBar(boolean show);
    void updateCurrentValueBtc(String value);
    void updateCurrentValueOwnedToken(String value);
    Toolbar getToolbar();
}
