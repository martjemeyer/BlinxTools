package com.dummycoding.blinxtools.pojos.coindesk;

import com.dummycoding.blinxtools.pojos.coindesk.fiat.EUR;
import com.dummycoding.blinxtools.pojos.coindesk.fiat.GBP;
import com.dummycoding.blinxtools.pojos.coindesk.fiat.USD;
import com.squareup.moshi.Json;


public class Bpi {

    @Json(name = "USD")
    private USD uSD;
    @Json(name = "GBP")
    private GBP gBP;
    @Json(name = "EUR")
    private EUR eUR;

    public USD getUSD() {
        return uSD;
    }

    public void setUSD(USD uSD) {
        this.uSD = uSD;
    }

    public GBP getGBP() {
        return gBP;
    }

    public void setGBP(GBP gBP) {
        this.gBP = gBP;
    }

    public EUR getEUR() {
        return eUR;
    }

    public void setEUR(EUR eUR) {
        this.eUR = eUR;
    }

}
