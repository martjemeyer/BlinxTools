package com.dummycoding.blinxtools.pojos.coindesk.fiat;

import com.squareup.moshi.Json;

public class USD {
    @Json(name = "code")
    private String code;
    @Json(name = "symbol")
    private String symbol;
    @Json(name = "rate")
    private String rate;
    @Json(name = "description")
    private String description;
    @Json(name = "rate_float")
    private Double rateFloat;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRateFloat() {
        return rateFloat;
    }

    public void setRateFloat(Double rateFloat) {
        this.rateFloat = rateFloat;
    }
}
