package com.dummycoding.blinxtools.data.network;

import com.dummycoding.blinxtools.models.BitBlinxResult;

import java.util.ArrayList;
import java.util.List;

public interface Repository {

    // Shared preferences
    String getPreferredCurrency();

    float getBtcValueForPreferredCurrency();

    void setBtcValueForPreferredCurrency(float value);

    // DB
    boolean storeLatestBitBlinxData(List<BitBlinxResult> bitBlinxResult);

    List<BitBlinxResult> getLatestBitBlinxData();


}
