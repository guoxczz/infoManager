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


	
	
	@RequestMapping("/putStockData")
    @ResponseBody
    String putStockData() {



        String result = null;
    try{
          result =    stockService.putRecentData2StockData();

        }catch (Exception e){

        logger.info("err1",e);
        result="error";
     }

      return result;
}

}
