package com.guoxc.info.bean.info;

import com.guoxc.info.bean.base.BaseBean;

import java.sql.Timestamp;
import java.util.Date;

public class StockDayBean  extends BaseBean {
    private String stockCode;
    private Timestamp operTime;
    private float openPrice;
    private float highPrice;
    private float lowPrice;
    private float closePrice;
    private long volume;
    private long turnover;
    private float c5Avg;
    private float c20Avg;
    private float c60Avg;
    private long v5Avg;
    private long v20Avg;
    private long v60Avg;

    private long bigInP ;
    private long bignetInP;
    private long smallInP;
    private long smallNetInP;
    private Timestamp  highTime;
    private Timestamp lowTime;

    private float preClosePrice;
    private long priceRate;
    private long swing;
    private long v20Rate;

    private int period;
    private int periodBorder;
    private int highLow;
    private long seq;
    private int macdCross;
    private float dif;
    private float dea;
    private float vMacdCross;
    private  float vDif;
    private float vDea;
    private int jzTail;
    private int jzBody;
    private float dayLow;
    private float dayHigh;
    private String macdInfo;


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

    public long getTurnover() {
        return turnover;
    }

    public void setTurnover(long turnover) {
        this.turnover = turnover;
    }

    public float getC5Avg() {
        return c5Avg;
    }

    public void setC5Avg(float c5Avg) {
        this.c5Avg = c5Avg;
    }

    public float getC20Avg() {
        return c20Avg;
    }

    public void setC20Avg(float c20Avg) {
        this.c20Avg = c20Avg;
    }

    public float getC60Avg() {
        return c60Avg;
    }

    public void setC60Avg(float c60Avg) {
        this.c60Avg = c60Avg;
    }

    public long getV5Avg() {
        return v5Avg;
    }

    public void setV5Avg(long v5Avg) {
        this.v5Avg = v5Avg;
    }

    public long getV20Avg() {
        return v20Avg;
    }

    public void setV20Avg(long v20Avg) {
        this.v20Avg = v20Avg;
    }

    public long getV60Avg() {
        return v60Avg;
    }

    public void setV60Avg(long v60Avg) {
        this.v60Avg = v60Avg;
    }

    public long getBigInP() {
        return bigInP;
    }

    public void setBigInP(long bigInP) {
        this.bigInP = bigInP;
    }

    public long getBignetInP() {
        return bignetInP;
    }

    public void setBignetInP(long bignetInP) {
        this.bignetInP = bignetInP;
    }

    public long getSmallInP() {
        return smallInP;
    }

    public void setSmallInP(long smallInP) {
        this.smallInP = smallInP;
    }

    public long getSmallNetInP() {
        return smallNetInP;
    }

    public void setSmallNetInP(long smallNetInP) {
        this.smallNetInP = smallNetInP;
    }

    public Timestamp getHighTime() {
        return highTime;
    }

    public void setHighTime(Timestamp highTime) {
        this.highTime = highTime;
    }

    public Timestamp getLowTime() {
        return lowTime;
    }

    public void setLowTime(Timestamp lowTime) {
        this.lowTime = lowTime;
    }

    public float getPreClosePrice() {
        return preClosePrice;
    }

    public void setPreClosePrice(float preClosePrice) {
        this.preClosePrice = preClosePrice;
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

    public long getV20Rate() {
        return v20Rate;
    }

    public void setV20Rate(long v20Rate) {
        this.v20Rate = v20Rate;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getPeriodBorder() {
        return periodBorder;
    }

    public void setPeriodBorder(int periodBorder) {
        this.periodBorder = periodBorder;
    }

    public int getHighLow() {
        return highLow;
    }

    public void setHighLow(int highLow) {
        this.highLow = highLow;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public int getMacdCross() {
        return macdCross;
    }

    public void setMacdCross(int macdCross) {
        this.macdCross = macdCross;
    }

    public float getDif() {
        return dif;
    }

    public void setDif(float dif) {
        this.dif = dif;
    }

    public float getDea() {
        return dea;
    }

    public void setDea(float dea) {
        this.dea = dea;
    }

    public float getvMacdCross() {
        return vMacdCross;
    }

    public void setvMacdCross(float vMacdCross) {
        this.vMacdCross = vMacdCross;
    }

    public float getvDif() {
        return vDif;
    }

    public void setvDif(float vDif) {
        this.vDif = vDif;
    }

    public float getvDea() {
        return vDea;
    }

    public void setvDea(float vDea) {
        this.vDea = vDea;
    }


    public float getDayLow() {
        return dayLow;
    }

    public void setDayLow(float dayLow) {
        this.dayLow = dayLow;
    }

    public float getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(float dayHigh) {
        this.dayHigh = dayHigh;
    }

    public String getMacdInfo() {
        return macdInfo;
    }

    public void setMacdInfo(String macdInfo) {
        this.macdInfo = macdInfo;
    }

    public int getJzTail() {
        return jzTail;
    }

    public void setJzTail(int jzTail) {
        this.jzTail = jzTail;
    }

    public int getJzBody() {
        return jzBody;
    }

    public void setJzBody(int jzBody) {
        this.jzBody = jzBody;
    }
}
