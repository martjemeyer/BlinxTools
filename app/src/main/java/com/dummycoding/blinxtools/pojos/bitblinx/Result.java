
package com.dummycoding.blinxtools.pojos.bitblinx;

import com.squareup.moshi.Json;

public class Result {

    @Json(name = "ask")
    public String ask;
    @Json(name = "bid")
    public String bid;
    @Json(name = "last")
    public String last;
    @Json(name = "open")
    public String open;
    @Json(name = "low")
    public String low;
    @Json(name = "high")
    public String high;
    @Json(name = "priceChange")
    public String priceChange;
    @Json(name = "liquidity")
    public Liquidity liquidity;
    @Json(name = "volume")
    public Volume volume;
    @Json(name = "timestamp")
    public String timestamp;
    @Json(name = "symbol")
    public String symbol;
}
