package com.guoxc.info.bean.info;

import com.guoxc.info.bean.base.BaseBean;

import java.sql.Timestamp;
import java.util.Date;

public class StockDayBean  extends BaseBean {
    private String stockCode;
    private Date operTime;
    private float openPrice;
    private float highPrice;
    private float lowPrice;
    private float closePrice;
    private double volume;
    private double turnover;

    private int bigInP ;
    private int bignetInP;
    private int smallInP;
    private int smallNetInP;
    private Date  highTime;
    private Date lowTime;


    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public Date getOperTime() {
        return operTime;
    }

    public void setOperTime(Date operTime) {
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

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getTurnover() {
        return turnover;
    }

    public void setTurnover(double turnover) {
        this.turnover = turnover;
    }

    public int getBigInP() {
        return bigInP;
    }

    public void setBigInP(int bigInP) {
        this.bigInP = bigInP;
    }

    public int getBignetInP() {
        return bignetInP;
    }

    public void setBignetInP(int bignetInP) {
        this.bignetInP = bignetInP;
    }

    public int getSmallInP() {
        return smallInP;
    }

    public void setSmallInP(int smallInP) {
        this.smallInP = smallInP;
    }

    public int getSmallNetInP() {
        return smallNetInP;
    }

    public void setSmallNetInP(int smallNetInP) {
        this.smallNetInP = smallNetInP;
    }

    public Date getHighTime() {
        return highTime;
    }

    public void setHighTime(Date highTime) {
        this.highTime = highTime;
    }

    public Date getLowTime() {
        return lowTime;
    }

    public void setLowTime(Date lowTime) {
        this.lowTime = lowTime;
    }
}
