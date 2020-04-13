package com.dummycoding.mycrypto.models.coindesk;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.squareup.moshi.Json;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity
public class Currency implements Serializable {
    @PrimaryKey
    @NonNull
    @Json(name = "currency")
    private String currency = "";
    @Json(name = "country")
    private String country;

    public Currency(@NonNull String currency, String country) {
        this.currency = currency;
        this.country = country;
    }

    @NotNull
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(@NotNull String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
