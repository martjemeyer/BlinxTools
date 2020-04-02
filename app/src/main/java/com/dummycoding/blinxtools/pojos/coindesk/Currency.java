package com.dummycoding.blinxtools.pojos.coindesk;

import com.squareup.moshi.Json;

public class Currency {
    @Json(name = "currency")
    private String currency;
    @Json(name = "country")
    private String country;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
