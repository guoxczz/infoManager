package com.guoxc.info.bean.info;

import com.guoxc.info.bean.base.BaseBean;

import java.sql.Timestamp;

public class StockPredictBean extends BaseBean {
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

    private float preClosePrice;
    private long priceRate;
    private long swing;
    private long v5Rate;
    private long v20Rate;

    private long seq;
    private int jzTail;
    private int jzBody;

    private float maxC20Price;
    private float maxC60Price;
    private float minC20Price;
    private float minC60Price;
    private float maxC5Price;
    private float maxC10Price;
    private float minC5Price;
    private float minC10Price;
    private long minV5Vol;
    private long maxV5Vol;
    private long minV10Vol;
    private long maxV10Vol;
    private long minV20Vol;
    private long maxV20Vol;
    private long minV60Vol;
    private long maxV60Vol;
    private Timestamp sellOperTime;
    private long sellVol;
    private float sellClosePrice;
    private long sellV5Avg;
    private long sellV20Avg;
    private long sellV5Rate;
    private long sellV20Rate;
    private float sellPriceRate;
    private String sellDesc;
    private String buyDesc;
    private float buyMaxPrice;
    private long buyLastDayNum;
    private long lastDayNum;
    private long profit;

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
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

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
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

    public Timestamp getSellOperTime() {
        return sellOperTime;
    }

    public void setSellOperTime(Timestamp sellOperTime) {
        this.sellOperTime = sellOperTime;
    }

    public long getSellVol() {
        return sellVol;
    }

    public void setSellVol(long sellVol) {
        this.sellVol = sellVol;
    }

    public float getSellClosePrice() {
        return sellClosePrice;
    }

    public void setSellClosePrice(float sellClosePrice) {
        this.sellClosePrice = sellClosePrice;
    }

    public long getSellV5Avg() {
        return sellV5Avg;
    }

    public void setSellV5Avg(long sellV5Avg) {
        this.sellV5Avg = sellV5Avg;
    }

    public long getSellV20Avg() {
        return sellV20Avg;
    }

    public void setSellV20Avg(long sellV20Avg) {
        this.sellV20Avg = sellV20Avg;
    }

    public float getSellPriceRate() {
        return sellPriceRate;
    }

    public void setSellPriceRate(float sellPriceRate) {
        this.sellPriceRate = sellPriceRate;
    }

    public String getSellDesc() {
        return sellDesc;
    }

    public void setSellDesc(String sellDesc) {
        this.sellDesc = sellDesc;
    }

    public String getBuyDesc() {
        return buyDesc;
    }

    public void setBuyDesc(String buyDesc) {
        this.buyDesc = buyDesc;
    }

    public float getBuyMaxPrice() {
        return buyMaxPrice;
    }

    public void setBuyMaxPrice(float buyMaxPrice) {
        this.buyMaxPrice = buyMaxPrice;
    }

    public long getBuyLastDayNum() {
        return buyLastDayNum;
    }

    public void setBuyLastDayNum(long buyLastDayNum) {
        this.buyLastDayNum = buyLastDayNum;
    }

    public long getLastDayNum() {
        return lastDayNum;
    }

    public void setLastDayNum(long lastDayNum) {
        this.lastDayNum = lastDayNum;
    }

    public long getProfit() {
        return profit;
    }

    public void setProfit(long profit) {
        this.profit = profit;
    }


    public long getV5Rate() {
        return v5Rate;
    }

    public void setV5Rate(long v5Rate) {
        this.v5Rate = v5Rate;
    }

    public long getSellV5Rate() {
        return sellV5Rate;
    }

    public void setSellV5Rate(long sellV5Rate) {
        this.sellV5Rate = sellV5Rate;
    }

    public long getSellV20Rate() {
        return sellV20Rate;
    }

    public void setSellV20Rate(long sellV20Rate) {
        this.sellV20Rate = sellV20Rate;
    }
}
