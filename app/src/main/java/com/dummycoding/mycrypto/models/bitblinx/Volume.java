
package com.dummycoding.mycrypto.models.bitblinx;

import com.squareup.moshi.Json;

public class Volume {

    @Json(name = "base")
    public String base;
    @Json(name = "quote")
    public String quote;

}
