package com.guoxc.info.bean.info;

import java.sql.Timestamp;

public class StockTransverseBean {

    private String stockCode;
    private Timestamp operTime;
    private float maxPrice;
    private float minPrice;
    private long maxVol;
    private long minVol;
    private long lastDay;
    private float avgPrice;
    private long avgVol;
    private long swingSeq;
    private long stockSeq;
    private String swingType;
    private int priceRate;
    private int volRate;

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

    public float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(float maxPrice) {
        this.maxPrice = maxPrice;
    }

    public float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }

    public long getMaxVol() {
        return maxVol;
    }

    public void setMaxVol(long maxVol) {
        this.maxVol = maxVol;
    }

    public long getMinVol() {
        return minVol;
    }

    public void setMinVol(long minVol) {
        this.minVol = minVol;
    }

    public long getLastDay() {
        return lastDay;
    }

    public void setLastDay(long lastDay) {
        this.lastDay = lastDay;
    }

    public float getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(float avgPrice) {
        this.avgPrice = avgPrice;
    }

    public long getAvgVol() {
        return avgVol;
    }

    public void setAvgVol(long avgVol) {
        this.avgVol = avgVol;
    }

    public long getSwingSeq() {
        return swingSeq;
    }

    public void setSwingSeq(long swingSeq) {
        this.swingSeq = swingSeq;
    }

    public long getStockSeq() {
        return stockSeq;
    }

    public void setStockSeq(long stockSeq) {
        this.stockSeq = stockSeq;
    }

    public String getSwingType() {
        return swingType;
    }

    public void setSwingType(String swingType) {
        this.swingType = swingType;
    }

    public int getPriceRate() {
        return priceRate;
    }

    public void setPriceRate(int priceRate) {
        this.priceRate = priceRate;
    }

    public int getVolRate() {
        return volRate;
    }

    public void setVolRate(int volRate) {
        this.volRate = volRate;
    }
}
