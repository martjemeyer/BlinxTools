package com.dummycoding.mycrypto.adapters;

import com.dummycoding.mycrypto.models.bitblinx.Result;

public interface BitBlinxMainAdapterCallback {
    void setFavorite(Result result);

    void handleLongClicked(Result result);

    void notifyDoubleTapToFavorite();
}
