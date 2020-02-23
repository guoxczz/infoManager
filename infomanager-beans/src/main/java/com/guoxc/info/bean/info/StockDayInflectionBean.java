package com.guoxc.info.bean.info;

import java.sql.Timestamp;

public class StockDayInflectionBean {
    private String stockCode;
    private Timestamp operTime;
    private float closePrice;
    private float preClosePrice;
    private int upDownType;
    private long intervalDay;
    private int swingRate;
    private long seq;
    private long preSeq;

    public long getPreSeq() {
        return preSeq;
    }

    public void setPreSeq(long preSeq) {
        this.preSeq = preSeq;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public Timestamp getOperTime() {
        return operTime;
    }

    public void setOperTime(Timestamp operTime) {
        this.operTime = operTime;
    }

    public float getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(float closePrice) {
        this.closePrice = closePrice;
    }

    public float getPreClosePrice() {
        return preClosePrice;
    }

    public void setPreClosePrice(float preClosePrice) {
        this.preClosePrice = preClosePrice;
    }

    public int getUpDownType() {
        return upDownType;
    }

    public void setUpDownType(int upDownType) {
        this.upDownType = upDownType;
    }

    public long getIntervalDay() {
        return intervalDay;
    }

    public void setIntervalDay(long intervalDay) {
        this.intervalDay = intervalDay;
    }

    public int getSwingRate() {
        return swingRate;
    }

    public void setSwingRate(int swingRate) {
        this.swingRate = swingRate;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }
}
