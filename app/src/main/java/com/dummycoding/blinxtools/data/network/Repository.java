package com.dummycoding.blinxtools.data.network;

public interface Repository {

    String getPreferredCurrency();
    void setBtcValueForPreferredCurrency(float value);
    float getBtcValueForPreferredCurrency();

}
