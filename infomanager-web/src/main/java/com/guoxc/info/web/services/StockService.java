package com.guoxc.info.web.services;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.StreamServer;
import com.googlecode.jsonrpc4j.VarArgsUtil;
import com.guoxc.info.bean.info.StockDayBean;
import com.guoxc.info.dao.StockDao;
import com.guoxc.info.utils.DateUtil;
import com.guoxc.info.utils.FileUtil;
import com.guoxc.info.web.control.StockControl;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class StockService {
    private final static Logger logger = LoggerFactory.getLogger(StockControl.class);
    @Autowired
 private StockDao stockDao  ;

    public String  putRecentData2StockData(){

        StockDayBean bean = new StockDayBean();
        File[] files = new File("E:\\stock\\data\\zhishu\\").listFiles();
       for(File file :files ){
           putStockDataFile2DDB(file);
       }
       //603682 sz002951 002952 002958

  return "success";
    }


    private void  putStockDataFile2DDB(File file ) {

    try{


         List stockList = new ArrayList();
         //file 示例 SH#600000.txt
         String fileName = file.getName();
         String stockCode = fileName.substring(3,9);
         List  list = FileUtil.readFileXml(file,"gbk");

         for(int i=1; i<list.size();i++){
             String line = (String) list.get(i);
             if(line.contains("日期")){
                 continue; //标题跳过.
             }
             // 标题  日期	    开盘	    最高	    最低	    收盘	    成交量	    成交额
             //2010/01/04	6.69	6.70	6.41	6.42	66191338	1419984128.00
             String[] cols = line.split("\t");
             if(cols.length>5){
                 StockDayBean bean = new StockDayBean();
                 bean.setStockCode(stockCode);
                 bean.setOperTime(DateUtil.convertStringToTimestamp(cols[0],"yyyy/MM/dd"));
                 bean.setOpenPrice(Float.valueOf(cols[1]));
                 bean.setHighPrice(Float.valueOf(cols[2]));
                 bean.setLowPrice(Float.valueOf(cols[3]));
                 bean.setClosePrice(Float.valueOf(cols[4]));
                 bean.setVolume(Double.valueOf(cols[5])/100);
                 bean.setTurnover( Double.valueOf(cols[6].substring(0,cols[6].length()-3))/10000);
                 stockList.add(bean);
             }
         }
        logger.info(stockCode+"stockList size="+stockList.size());
        stockDao.insertStockDayList(stockList);

    }catch (Exception e){
        logger.error("putStockDataFile2DDB_err",e);
    }

    }



}
