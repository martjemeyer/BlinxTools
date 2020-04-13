package com.dummycoding.mycrypto.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class OwnedToken {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String token;
    private double tokenAmount;
    private double tokenInBtc;

    public OwnedToken(String token, double tokenAmount) {
        this.token = token;
        this.tokenAmount = tokenAmount;
    }

    @Ignore
    public OwnedToken() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public double getTokenAmount() {
        return tokenAmount;
    }

    public void setTokenAmount(double tokenAmount) {
        this.tokenAmount = tokenAmount;
    }

    public double getTokenInBtc() {
        return tokenInBtc;
    }

    public void setTokenInBtc(double tokenInBtc) {
        this.tokenInBtc = tokenInBtc;
    }
}
