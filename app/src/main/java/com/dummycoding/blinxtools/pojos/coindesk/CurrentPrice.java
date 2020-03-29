package com.dummycoding.blinxtools.pojos.coindesk;

import com.squareup.moshi.Json;

public class CurrentPrice {
    @Json(name = "time")
    private Time time;
    @Json(name = "disclaimer")
    private String disclaimer;
    @Json(name = "chartName")
    private String chartName;
    @Json(name = "bpi")
    private Bpi bpi;

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public String getChartName() {
        return chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    public Bpi getBpi() {
        return bpi;
    }

    public void setBpi(Bpi bpi) {
        this.bpi = bpi;
    }

}
