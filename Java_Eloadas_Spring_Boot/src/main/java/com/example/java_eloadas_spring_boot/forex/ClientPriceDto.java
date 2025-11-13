package com.example.java_eloadas_spring_boot.forex;

import java.util.List;

public class ClientPriceDto {
    private String instrument;
    private String time;
    private String status;
    private boolean tradeable;
    private double closeoutBid;
    private double closeoutAsk;
    private double mid;
    private List<PricePoint> bids;
    private List<PricePoint> asks;

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isTradeable() {
        return tradeable;
    }

    public void setTradeable(boolean tradeable) {
        this.tradeable = tradeable;
    }

    public double getCloseoutBid() {
        return closeoutBid;
    }

    public void setCloseoutBid(double closeoutBid) {
        this.closeoutBid = closeoutBid;
    }

    public double getCloseoutAsk() {
        return closeoutAsk;
    }

    public void setCloseoutAsk(double closeoutAsk) {
        this.closeoutAsk = closeoutAsk;
    }

    public double getMid() {
        return mid;
    }

    public void setMid(double mid) {
        this.mid = mid;
    }

    public List<PricePoint> getBids() {
        return bids;
    }

    public void setBids(List<PricePoint> bids) {
        this.bids = bids;
    }

    public List<PricePoint> getAsks() {
        return asks;
    }

    public void setAsks(List<PricePoint> asks) {
        this.asks = asks;
    }
}
