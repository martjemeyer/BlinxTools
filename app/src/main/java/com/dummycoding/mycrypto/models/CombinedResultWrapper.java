package com.dummycoding.mycrypto.models;

import androidx.annotation.Nullable;

import com.dummycoding.mycrypto.models.bitblinx.Result;
import com.dummycoding.mycrypto.models.coindesk.BpiCurrency;

import java.util.List;

public class CombinedResultWrapper {
    private List<Result> result;
    private BpiCurrency bpi;

    public CombinedResultWrapper(BpiCurrency bpi, List<Result> result) {
        this.result = result;
        this.bpi = bpi;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public BpiCurrency getBpi() {
        return bpi;
    }

    public void setBpi(BpiCurrency bpi) {
        this.bpi = bpi;
    }
}
