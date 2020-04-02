package com.dummycoding.blinxtools.pojos.coindesk;
import com.squareup.moshi.Json;

import java.util.Map;

public class Bpi {

    @Json(name = "bpi")
    public Map<String, BpiCurrency> bpiCurrencies;

    public Map<String, BpiCurrency> getBpiCurrencies() {
        return bpiCurrencies;
    }

    public void setBpiCurrencies(Map<String, BpiCurrency> bpiCurrencies) {
        this.bpiCurrencies = bpiCurrencies;
    }
}
