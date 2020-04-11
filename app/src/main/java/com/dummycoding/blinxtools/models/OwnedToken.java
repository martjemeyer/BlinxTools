package com.dummycoding.blinxtools.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class OwnedToken {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String token;
    private int tokenAmount;
    private double tokenInBtc;
    private double btcInFiat;
    private String fiatCurrency;

    public OwnedToken(String token, int tokenAmount, double tokenInBtc, double btcInFiat, String fiatCurrency) {
        this.token = token;
        this.tokenAmount = tokenAmount;
        this.tokenInBtc = tokenInBtc;
        this.btcInFiat = btcInFiat;
        this.fiatCurrency = fiatCurrency;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTokenInBtc(double tokenInBtc) {
        this.tokenInBtc = tokenInBtc;
    }

    public void setBtcInFiat(double btcInFiat) {
        this.btcInFiat = btcInFiat;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getTokenAmount() {
        return tokenAmount;
    }

    public void setTokenAmount(int tokenAmount) {
        this.tokenAmount = tokenAmount;
    }

    public double getTokenInBtc() {
        return tokenInBtc;
    }

    public void setTokenInBtc(float tokenInBtc) {
        this.tokenInBtc = tokenInBtc;
    }

    public double getBtcInFiat() {
        return btcInFiat;
    }

    public void setBtcInFiat(float btcInFiat) {
        this.btcInFiat = btcInFiat;
    }

    public String getFiatCurrency() {
        return fiatCurrency;
    }

    public void setFiatCurrency(String fiatCurrency) {
        this.fiatCurrency = fiatCurrency;
    }
}
