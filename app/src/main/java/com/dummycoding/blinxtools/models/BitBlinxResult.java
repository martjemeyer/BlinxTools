package com.dummycoding.blinxtools.models;

import com.dummycoding.blinxtools.pojos.bitblinx.Result;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BitBlinxResult extends RealmObject {
    @PrimaryKey
    private String symbol;
    private String ask;
    private String bid;
    private String last;
    private String open;
    private String low;
    private String high;
    private String priceChange;
    private BitBlinxLiquidity liquidity;
    private BitBlinxVolume volume;
    private String timestamp;

    public static BitBlinxResult shallowCopy(Result result) {
        BitBlinxResult copy = new BitBlinxResult();
        copy.symbol = result.symbol;
        copy.ask = result.ask;
        copy.bid = result.bid;
        copy.last = result.last;
        copy.open = result.open;
        copy.low = result.low;
        copy.high = result.high;
        copy.priceChange = result.priceChange;
        copy.timestamp = result.timestamp;

        return copy;
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

    public BitBlinxLiquidity getLiquidity() {
        return liquidity;
    }

    public void setLiquidity(BitBlinxLiquidity liquidity) {
        this.liquidity = liquidity;
    }

    public BitBlinxVolume getVolume() {
        return volume;
    }

    public void setVolume(BitBlinxVolume volume) {
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
}
