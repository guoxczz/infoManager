package com.guoxc.info.web.services;

import com.guoxc.info.utils.DateUtil;
import com.guoxc.info.web.common.ConstantsInfo;

import java.sql.Timestamp;

public class SaveStockMinuteThread extends Thread {
    private String  stockCode;
    private StockService stockService;

    public SaveStockMinuteThread(String stockCode,StockService stockService){
        this.stockCode=stockCode;
        this.stockService=stockService;
    }

    public void run(){

        stockService.saveStockMinuteDataByStockCode(stockCode);

    }



}
