package com.guoxc.info.bean.info;

import java.sql.Timestamp;

public class StockMinuteAna implements Comparable {

    private String stockCode ;
    private Timestamp operTime;
    private int timeSeq;
    private float openPrice;
    private float highPrice;
    private float lowPrice;
    private float closePrice;
    private long volume;
    private long priceRate;
    private long swing;
    private long bigVolS;
    private long bigVolB;
    private long flowVolS;
    private long flowVolB;
    private long norVolS;
    private long norVolB;
    private int  period;
    private int maxUpDown;

    public int getTimeSeq() {
        return timeSeq;
    }

    public void setTimeSeq(int timeSeq) {
        this.timeSeq = timeSeq;
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

    public float getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(float openPrice) {
        this.openPrice = openPrice;
    }

    public float getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(float highPrice) {
        this.highPrice = highPrice;
    }

    public float getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(float lowPrice) {
        this.lowPrice = lowPrice;
    }

    public float getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(float closePrice) {
        this.closePrice = closePrice;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public long getPriceRate() {
        return priceRate;
    }

    public void setPriceRate(long priceRate) {
        this.priceRate = priceRate;
    }

    public long getSwing() {
        return swing;
    }

    public void setSwing(long swing) {
        this.swing = swing;
    }

    public long getBigVolS() {
        return bigVolS;
    }

    public void setBigVolS(long bigVolS) {
        this.bigVolS = bigVolS;
    }

    public long getBigVolB() {
        return bigVolB;
    }

    public void setBigVolB(long bigVolB) {
        this.bigVolB = bigVolB;
    }

    public long getFlowVolS() {
        return flowVolS;
    }

    public void setFlowVolS(long flowVolS) {
        this.flowVolS = flowVolS;
    }

    public long getFlowVolB() {
        return flowVolB;
    }

    public void setFlowVolB(long flowVolB) {
        this.flowVolB = flowVolB;
    }

    public long getNorVolS() {
        return norVolS;
    }

    public void setNorVolS(long norVolS) {
        this.norVolS = norVolS;
    }

    public long getNorVolB() {
        return norVolB;
    }

    public void setNorVolB(long norVolB) {
        this.norVolB = norVolB;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getMaxUpDown() {
        return maxUpDown;
    }

    public void setMaxUpDown(int maxUpDown) {
        this.maxUpDown = maxUpDown;
    }

    @Override
    public int compareTo(Object o) {
        StockMinuteAna obj = (StockMinuteAna)o;
        return ((Long)obj.getVolume()).compareTo(this.getVolume());
    }
}
