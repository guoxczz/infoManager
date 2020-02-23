package com.guoxc.info.web.control;

import com.alibaba.fastjson.JSON;
import com.guoxc.info.bean.info.StockDayBean;
import com.guoxc.info.bean.info.StockDayInflectionBean;
import com.guoxc.info.utils.DateUtil;
import com.guoxc.info.web.common.ConstantsInfo;
import com.guoxc.info.web.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("/stock")
public class StockControl {
 private final static Logger logger = LoggerFactory.getLogger(StockControl.class);
	
    @Autowired
    private StockService stockService;

    @Autowired
    private BsStaticDataService bsStaticDataService;

   @Autowired
    private ThreadPool threadPool;

    @RequestMapping("/queryTest")
    @ResponseBody
    String queryTest() {
        String result = null;
        try {
             stockService.query();
            result = "success";
        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }
        return result;
    }



    @RequestMapping("/saveStockData")
    @ResponseBody
    String saveStockData() {
        String result = null;
        try {
            result = stockService.putRecentData2StockData();
        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }
        return result;
    }

    @RequestMapping("/saveStockDataAna")
    @ResponseBody
    String saveStockDataAna() {
        String result = null;
        try {


            String dealZSDataTimeKey = "STOCK_DEAL_TIME_STOCK_DAY_ANA";
            StockDayBean bean = new StockDayBean();
            String lastDealDayStr =  (String) bsStaticDataService.selectOne(ConstantsInfo.SQLID.GET_CODEVALUE ,dealZSDataTimeKey) ;
            if (lastDealDayStr != null) {
                try {
                    Timestamp lastDealDay = DateUtil.convertStringToTimestamp(lastDealDayStr, "yyyy-MM-dd");
                    if (lastDealDay.before(DateUtil.getCurrentDate())) {

                    File[] files = new File("E:\\stock\\data\\dayadd\\").listFiles();
                    ThreadPoolExecutor executor = threadPool.getThreadPoolExecutor("STOCKMINDEAL");

                    for (File file : files) {
                        SaveStockDataAnaThread thread = new       SaveStockDataAnaThread(file.getAbsolutePath(),lastDealDay,stockService);
                        executor.execute(thread);
                     }
                        bsStaticDataService.updateDealTime(dealZSDataTimeKey);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }
        return result;
    }


    @RequestMapping("/saveStockDayInflectionNew")
    @ResponseBody
    String saveStockDayInflectionNew() {
        String result = null;
        try {


            String dayInflectionDealDataTimeKey = "STOCK_DEAL_TIME_STOCK_DAY_INFLECTION_NEW";
            StockDayBean bean = new StockDayBean();
            String lastDealDayStr =  (String) bsStaticDataService.selectOne(ConstantsInfo.SQLID.GET_CODEVALUE ,dayInflectionDealDataTimeKey) ;
            if (lastDealDayStr != null) {
                try {
                    Timestamp lastDealDay = DateUtil.convertStringToTimestamp(lastDealDayStr, "yyyy-MM-dd");
                    if (lastDealDay.before(DateUtil.getCurrentDate())) {
                        File[] files = new File("E:\\stock\\data\\dayadd\\").listFiles();
                       Map currStockDayInflectionMap =    stockService.queryCurrStockDayInflection();

                        ThreadPoolExecutor executor = threadPool.getThreadPoolExecutor("STOCKMINDEAL");

                        for (File file : files) {

                            String fileName = file.getName();
                            String stockCode = fileName.substring(3,9);
                            SaveStockDayInfllectionThread thread = new       SaveStockDayInfllectionThread(file.getAbsolutePath(),lastDealDay,stockService,(StockDayInflectionBean)currStockDayInflectionMap.get(stockCode));
                            executor.execute(thread);
                        }
                        bsStaticDataService.updateDealTime(dayInflectionDealDataTimeKey);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }
        return result;
    }


    @RequestMapping("/saveStockMinuteData")
    @ResponseBody
    String saveStockMinuteData(String stockCode) {
        String result = null;
        try {
            if(valiStockCode (stockCode)){
                if("ALL".equals(stockCode)){
                    String[] files = new File("E:\\stock\\data\\minute\\").list();
                    if(files != null){
                        ThreadPoolExecutor executor = threadPool.getThreadPoolExecutor("STOCKMINDEAL");

                        for (String file : files) {
                            SaveStockMinuteThread thread = new       SaveStockMinuteThread(file.substring(0,9).replace("#",""),stockService);
                            //SH#600008.txt
                            executor.execute(thread);
//                            stockService.saveStockMinuteDataByStockCode(file.substring(0,9).replace("#",""));
                        }
                    }

                }else{
                    stockService.saveStockMinuteDataByStockCode(stockCode);
            }
                result = "success";

            }else{
                logger.error("stockCode is wrong "+stockCode);
            }
        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }

        return result;
    }

    private boolean valiStockCode(String stockCode){
         boolean result = false;

         if("ALL".equals(stockCode)){
             result = true;
         }
         else if(stockCode !=null && stockCode.length()==8 && (stockCode.startsWith("SH")||stockCode.startsWith("SZ"))){
             result = true;
         }
         return result;
    }


    @RequestMapping("/saveStockZSData")
    @ResponseBody
    String saveStockZSData() {
        String result = null;
        try {
            result = stockService.saveRecentZSData2StockData();
        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }

        return result;
    }

    @RequestMapping("/saveStockZSMinuteData")
    @ResponseBody
    String saveStockZSMinuteData() {
        String result = null;
        try {
            result = stockService.saveStockZSMinuteData();
        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }

        return result;
    }

}
