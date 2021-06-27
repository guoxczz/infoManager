package com.guoxc.info.bean.info;

import com.guoxc.info.bean.base.BaseBean;

import java.sql.Timestamp;
import java.util.Date;

public class StockDayBean  extends BaseBean {
    private String stockCode;
    private String stockName ;
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

    private long openRate;
    private long highRate;
    private long lowRate;
    private long closeRate;
    private long priceDesc;

    private float maxC20Price;
    private float maxC60Price;
    private float minC20Price;
    private float minC60Price;
    private float maxC5Price;
    private float maxC10Price;
    private float minC5Price;
    private float minC10Price;
    private String recentHorizon;
    private String recentBox;
    private long s5Avg ;
    private long s10Avg ;
    private long s20Avg ;
    private long v5Rate;
    private long minV5Vol;
    private long maxV5Vol;
    private long minV10Vol;
    private long maxV10Vol;
    private long minV20Vol;
    private long maxV20Vol;
    private long minV60Vol;
    private long maxV60Vol;
    private long s20Rate;

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
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

    public long getOpenRate() {
        return openRate;
    }

    public void setOpenRate(long openRate) {
        this.openRate = openRate;
    }

    public long getHighRate() {
        return highRate;
    }

    public void setHighRate(long highRate) {
        this.highRate = highRate;
    }

    public long getLowRate() {
        return lowRate;
    }

    public void setLowRate(long lowRate) {
        this.lowRate = lowRate;
    }

    public long getCloseRate() {
        return closeRate;
    }

    public void setCloseRate(long closeRate) {
        this.closeRate = closeRate;
    }

    public long getPriceDesc() {
        return priceDesc;
    }

    public void setPriceDesc(long priceDesc) {
        this.priceDesc = priceDesc;
    }

    public float getMaxC20Price() {
        return maxC20Price;
    }

    public void setMaxC20Price(float maxC20Price) {
        this.maxC20Price = maxC20Price;
    }

    public float getMaxC60Price() {
        return maxC60Price;
    }

    public void setMaxC60Price(float maxC60Price) {
        this.maxC60Price = maxC60Price;
    }

    public float getMinC20Price() {
        return minC20Price;
    }

    public void setMinC20Price(float minC20Price) {
        this.minC20Price = minC20Price;
    }

    public float getMinC60Price() {
        return minC60Price;
    }

    public void setMinC60Price(float minC60Price) {
        this.minC60Price = minC60Price;
    }

    public float getMaxC5Price() {
        return maxC5Price;
    }

    public void setMaxC5Price(float maxC5Price) {
        this.maxC5Price = maxC5Price;
    }

    public float getMaxC10Price() {
        return maxC10Price;
    }

    public void setMaxC10Price(float maxC10Price) {
        this.maxC10Price = maxC10Price;
    }

    public float getMinC5Price() {
        return minC5Price;
    }

    public void setMinC5Price(float minC5Price) {
        this.minC5Price = minC5Price;
    }

    public float getMinC10Price() {
        return minC10Price;
    }

    public void setMinC10Price(float minC10Price) {
        this.minC10Price = minC10Price;
    }

    public String getRecentHorizon() {
        return recentHorizon;
    }

    public void setRecentHorizon(String recentHorizon) {
        this.recentHorizon = recentHorizon;
    }

    public String getRecentBox() {
        return recentBox;
    }

    public void setRecentBox(String recentBox) {
        this.recentBox = recentBox;
    }

    public long getS5Avg() {
        return s5Avg;
    }

    public void setS5Avg(long s5Avg) {
        this.s5Avg = s5Avg;
    }

    public long getS10Avg() {
        return s10Avg;
    }

    public void setS10Avg(long s10Avg) {
        this.s10Avg = s10Avg;
    }

    public long getS20Avg() {
        return s20Avg;
    }

    public void setS20Avg(long s20Avg) {
        this.s20Avg = s20Avg;
    }

    public long getV5Rate() {
        return v5Rate;
    }

    public void setV5Rate(long v5Rate) {
        this.v5Rate = v5Rate;
    }

    public long getMinV5Vol() {
        return minV5Vol;
    }

    public void setMinV5Vol(long minV5Vol) {
        this.minV5Vol = minV5Vol;
    }

    public long getMaxV5Vol() {
        return maxV5Vol;
    }

    public void setMaxV5Vol(long maxV5Vol) {
        this.maxV5Vol = maxV5Vol;
    }

    public long getMinV10Vol() {
        return minV10Vol;
    }

    public void setMinV10Vol(long minV10Vol) {
        this.minV10Vol = minV10Vol;
    }

    public long getMaxV10Vol() {
        return maxV10Vol;
    }

    public void setMaxV10Vol(long maxV10Vol) {
        this.maxV10Vol = maxV10Vol;
    }

    public long getMinV20Vol() {
        return minV20Vol;
    }

    public void setMinV20Vol(long minV20Vol) {
        this.minV20Vol = minV20Vol;
    }

    public long getMaxV20Vol() {
        return maxV20Vol;
    }

    public void setMaxV20Vol(long maxV20Vol) {
        this.maxV20Vol = maxV20Vol;
    }

    public long getMinV60Vol() {
        return minV60Vol;
    }

    public void setMinV60Vol(long minV60Vol) {
        this.minV60Vol = minV60Vol;
    }

    public long getMaxV60Vol() {
        return maxV60Vol;
    }

    public void setMaxV60Vol(long maxV60Vol) {
        this.maxV60Vol = maxV60Vol;
    }

    public long getS20Rate() {
        return s20Rate;
    }

    public void setS20Rate(long s20Rate) {
        this.s20Rate = s20Rate;
    }
}
