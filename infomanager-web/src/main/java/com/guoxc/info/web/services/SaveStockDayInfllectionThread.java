package com.guoxc.info.web.services;

import com.guoxc.info.bean.info.StockDayInflectionBean;

import java.io.File;
import java.sql.Timestamp;

public class SaveStockDayInfllectionThread extends Thread {
    private String  fileName;
    private StockService stockService;
    private Timestamp lastDealDay;
    private StockDayInflectionBean bean;

    public SaveStockDayInfllectionThread(String fileName, Timestamp lastDealDay, StockService stockService,StockDayInflectionBean bean){
        this.fileName=fileName;
        this.stockService=stockService;
        this.lastDealDay= lastDealDay;
        this.bean = bean;
    }

    public void run(){

        File file = new File(fileName);
        stockService.putStockDayInflectionNew2DB(file,lastDealDay,bean);

    }



}
