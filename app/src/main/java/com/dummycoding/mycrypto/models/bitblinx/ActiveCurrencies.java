package com.dummycoding.mycrypto.models.bitblinx;

import com.squareup.moshi.Json;

import java.util.List;

public class ActiveCurrencies {
    @Json(name = "status")
    private Boolean status;
    @Json(name = "result")
    private List<Result> result = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }
}
