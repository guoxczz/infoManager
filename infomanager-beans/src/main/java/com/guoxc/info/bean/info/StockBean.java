package com.guoxc.info.bean.info;

import com.guoxc.info.bean.base.BaseBean;

import java.sql.Timestamp;

public class StockBean extends BaseBean {
    private String stockCode;
    private String stockName;
    private String hangYe;
    private String prov;
    private float zgb;
    private float ltgb;
    private float ltsz;
    private long stockType;
    private float maxCprice;
    private float minCprice;
    private float max8MCprice;
    private long maxVol;
    private long max8MVol;
    private long minVol;
    private String hangye2;
    private String st;
    private float priceRate3D ;
    private float priceRate20D ;
    private float priceRate60D ;
    private String recentTips ;
    private Timestamp financeUpdateDay;
    private Timestamp onMarketDay;
    private float debtRatio ;

    private float netProfitRate ;
    private float profitRateBefore ;
    private String stockerNum ;
    private float stockerNumRate ;

    public long getMinVol() {
        return minVol;
    }

    public void setMinVol(long minVol) {
        this.minVol = minVol;
    }

    public float getNetProfitRate() {
        return netProfitRate;
    }

    public void setNetProfitRate(float netProfitRate) {
        this.netProfitRate = netProfitRate;
    }

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

    public String getHangYe() {
        return hangYe;
    }

    public void setHangYe(String hangYe) {
        this.hangYe = hangYe;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public float getZgb() {
        return zgb;
    }

    public void setZgb(float zgb) {
        this.zgb = zgb;
    }

    public float getLtgb() {
        return ltgb;
    }

    public void setLtgb(float ltgb) {
        this.ltgb = ltgb;
    }

    public float getLtsz() {
        return ltsz;
    }

    public void setLtsz(float ltsz) {
        this.ltsz = ltsz;
    }

    public long getStockType() {
        return stockType;
    }

    public void setStockType(long stockType) {
        this.stockType = stockType;
    }

    public float getMaxCprice() {
        return maxCprice;
    }

    public void setMaxCprice(float maxCprice) {
        this.maxCprice = maxCprice;
    }

    public float getMinCprice() {
        return minCprice;
    }
    public void setMinCprice(float minCprice) {
        this.minCprice = minCprice;
    }

    public float getMax8MCprice() {
        return max8MCprice;
    }

    public void setMax8MCprice(float max8MCprice) {
        this.max8MCprice = max8MCprice;
    }

    public long getMaxVol() {
        return maxVol;
    }

    public void setMaxVol(long maxVol) {
        this.maxVol = maxVol;
    }

    public long getMax8MVol() {
        return max8MVol;
    }

    public void setMax8MVol(long max8MVol) {
        this.max8MVol = max8MVol;
    }

    public String getHangye2() {
        return hangye2;
    }

    public void setHangye2(String hangye2) {
        this.hangye2 = hangye2;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public float getPriceRate3D() {
        return priceRate3D;
    }

    public void setPriceRate3D(float priceRate3D) {
        this.priceRate3D = priceRate3D;
    }

    public float getPriceRate20D() {
        return priceRate20D;
    }

    public void setPriceRate20D(float priceRate20D) {
        this.priceRate20D = priceRate20D;
    }

    public float getPriceRate60D() {
        return priceRate60D;
    }

    public void setPriceRate60D(float priceRate60D) {
        this.priceRate60D = priceRate60D;
    }

    public String getRecentTips() {
        return recentTips;
    }

    public void setRecentTips(String recentTips) {
        this.recentTips = recentTips;
    }

    public Timestamp getFinanceUpdateDay() {
        return financeUpdateDay;
    }

    public void setFinanceUpdateDay(Timestamp financeUpdateDay) {
        this.financeUpdateDay = financeUpdateDay;
    }

    public Timestamp getOnMarketDay() {
        return onMarketDay;
    }

    public void setOnMarketDay(Timestamp onMarketDay) {
        this.onMarketDay = onMarketDay;
    }

    public float getDebtRatio() {
        return debtRatio;
    }

    public void setDebtRatio(float debtRatio) {
        this.debtRatio = debtRatio;
    }

    public float getProfitRateBefore() {
        return profitRateBefore;
    }

    public void setProfitRateBefore(float profitRateBefore) {
        this.profitRateBefore = profitRateBefore;
    }

    public String getStockerNum() {
        return stockerNum;
    }

    public void setStockerNum(String stockerNum) {
        this.stockerNum = stockerNum;
    }

    public float getStockerNumRate() {
        return stockerNumRate;
    }

    public void setStockerNumRate(float stockerNumRate) {
        this.stockerNumRate = stockerNumRate;
    }
}
