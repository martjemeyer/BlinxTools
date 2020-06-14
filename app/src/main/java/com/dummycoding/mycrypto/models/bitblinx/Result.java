package com.dummycoding.mycrypto.models.bitblinx;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.squareup.moshi.Json;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class Result implements Serializable {

    @PrimaryKey
    @NonNull
    @Json(name = "symbol")
    private String symbol = "";
    @Json(name = "ask")
    private String ask;
    @Json(name = "bid")
    private String bid;
    @Json(name = "last")
    private String last;
    @Json(name = "open")
    private String open;
    @Json(name = "low")
    private String low;
    @Json(name = "high")
    private String high;
    @Json(name = "priceChange")
    private String priceChange;
    @Ignore
    @Json(name = "liquidity")
    private Liquidity liquidity;
    @Ignore
    @Json(name = "volume")
    private Volume volume;
    @Json(name = "timestamp")
    private String timestamp;

    private boolean favorited = false;

    @Ignore
    private boolean singlePressed = false;

    public Result() {
    }

    @Ignore
    public Result(@NonNull String symbol) {
        this.symbol = symbol;
    }

    public String getAsk() {
        return ask;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(String priceChange) {
        this.priceChange = priceChange;
    }

    public Liquidity getLiquidity() {
        return liquidity;
    }

    public void setLiquidity(Liquidity liquidity) {
        this.liquidity = liquidity;
    }

    public Volume getVolume() {
        return volume;
    }

    public void setVolume(Volume volume) {
        this.volume = volume;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return symbol.equals(result.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}
