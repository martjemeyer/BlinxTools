
package com.dummycoding.blinxtools.models.bitblinx;

import com.squareup.moshi.Json;

public class Liquidity {

    @Json(name = "base")
    public String base;
    @Json(name = "quote")
    public String quote;

}
