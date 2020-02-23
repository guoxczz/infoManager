package com.guoxc.info.web.services;

import java.io.File;
import java.sql.Timestamp;

public class SaveStockDataAnaThread extends Thread {
    private String  fileName;
    private StockService stockService;
    private Timestamp lastDealDay;

    public SaveStockDataAnaThread(String fileName, Timestamp lastDealDay, StockService stockService){
        this.fileName=fileName;
        this.stockService=stockService;
        this.lastDealDay= lastDealDay;
    }

    public void run(){

        File file = new File(fileName);
        stockService.putStockZSDataFile2DBAnaly(file,lastDealDay,"");

    }



}
