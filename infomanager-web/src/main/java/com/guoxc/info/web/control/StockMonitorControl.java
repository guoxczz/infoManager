package com.guoxc.info.web.control;

import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guoxc.info.bean.info.StockDayBean;
import com.guoxc.info.utils.DateUtil;
import com.guoxc.info.utils.StringUtil;
import com.guoxc.info.web.common.ConstantsInfo;
import com.guoxc.info.web.services.BsStaticDataService;
import com.guoxc.info.web.services.SaveStockDataAnaThread;
import com.guoxc.info.web.services.StockCurrMoniterService;
import com.guoxc.info.web.services.StockService;
import com.guoxc.info.web.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("/stockMonitor")
public class StockMonitorControl {

    private final static Logger logger = LoggerFactory.getLogger(StockMonitorControl.class);
    @Autowired
    private StockCurrMoniterService stockCurrMoniterService;

    @Autowired
    private BsStaticDataService bsStaticDataService;


    @RequestMapping("/moniter")
    @ResponseBody
    String saveStockDataAna() {
        String result = null;
        try {

            stockCurrMoniterService.moniterStock();
        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }
        return result;
    }



}
