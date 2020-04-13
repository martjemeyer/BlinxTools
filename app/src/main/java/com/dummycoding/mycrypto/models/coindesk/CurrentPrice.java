package com.dummycoding.mycrypto.models.coindesk;

import com.squareup.moshi.Json;

import java.util.Map;

public class CurrentPrice {
    @Json(name = "time")
    private Time time;
    @Json(name = "disclaimer")
    private String disclaimer;
    @Json(name = "chartName")
    private String chartName;
    @Json(name = "bpi")
    private Map<String, BpiCurrency> bpiCurrencies;

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public String getChartName() {
        return chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    public Map<String, BpiCurrency> getBpiCurrencies() {
        return bpiCurrencies;
    }

    public void setBpiCurrencies(Map<String, BpiCurrency> bpiCurrencies) {
        this.bpiCurrencies = bpiCurrencies;
    }
}
