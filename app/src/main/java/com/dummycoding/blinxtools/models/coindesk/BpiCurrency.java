package com.dummycoding.blinxtools.models.coindesk;

import com.squareup.moshi.Json;

public class BpiCurrency {
    @Json(name = "code")
    private String code;
    @Json(name = "rate")
    private String rate;
    @Json(name = "description")
    private String description;
    @Json(name = "rate_float")
    private Float rateFloat;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Float getRateFloat() {
        return rateFloat;
    }

    public void setRateFloat(Float rateFloat) {
        this.rateFloat = rateFloat;
    }
}
