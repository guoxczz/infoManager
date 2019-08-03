package com.guoxc.info.web.control;

import com.alibaba.fastjson.JSON;
import com.guoxc.info.web.services.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stock")
public class StockControl {
 private final static Logger logger = LoggerFactory.getLogger(StockControl.class);
	
 @Autowired
   private StockService stockService;


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


    @RequestMapping("/saveStockMinuteData")
    @ResponseBody
    String saveStockMinuteData() {
        String result = null;
        try {
            result = stockService.saveStockMinuteData();
        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
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
