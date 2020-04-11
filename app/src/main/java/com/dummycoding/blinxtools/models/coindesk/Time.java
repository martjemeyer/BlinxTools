package com.dummycoding.blinxtools.models.coindesk;

import com.squareup.moshi.Json;

public class Time {
    @Json(name = "updated")
    private String updated;
    @Json(name = "updatedISO")
    private String updatedISO;
    @Json(name = "updateduk")
    private String updateduk;

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getUpdatedISO() {
        return updatedISO;
    }

    public void setUpdatedISO(String updatedISO) {
        this.updatedISO = updatedISO;
    }

    public String getUpdateduk() {
        return updateduk;
    }

    public void setUpdateduk(String updateduk) {
        this.updateduk = updateduk;
    }
}
