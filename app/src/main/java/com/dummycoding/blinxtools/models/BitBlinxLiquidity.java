
package com.dummycoding.blinxtools.models;

import io.realm.RealmObject;

public class BitBlinxLiquidity extends RealmObject {

    private String base;
    private String quote;

    public BitBlinxLiquidity() { }

    public BitBlinxLiquidity(String base, String quote) {
        this.base = base;
        this.quote = quote;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }
}
