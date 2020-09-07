package com.guoxc.info.web.control;

import com.alibaba.dubbo.common.json.JSON;
import com.guoxc.info.bean.info.StockDayBean;
import com.guoxc.info.utils.StringUtil;
import com.guoxc.info.web.common.ConstantsInfo;
import com.guoxc.info.web.services.BsStaticDataService;
import com.guoxc.info.web.services.StockService;
import com.guoxc.info.web.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stockStat")
public class StockStatControl {

    private final static Logger logger = LoggerFactory.getLogger(StockStatControl.class);
    @Autowired
    private StockService stockService;

    @Autowired
    private BsStaticDataService bsStaticDataService;


    @RequestMapping("/stockInflection")
    @ResponseBody
    String saveStockDataAna() {
        String result = null;
        try {


            String moniterCfg = "STOCK_MONITER_CFG";
            StockDayBean bean = new StockDayBean();
            String moniterCfgStr =  (String) bsStaticDataService.selectOne(ConstantsInfo.SQLID.GET_CODEVALUE ,moniterCfg) ;
            Map cfgParam = new HashMap();
            if(StringUtil.isNotBlank(moniterCfgStr)){
                cfgParam = JSON.parse(moniterCfgStr, cfgParam.getClass());
            }
            HttpUtil.doHttpPost("http://qt.gtimg.cn/q=sz000858","");




        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }
        return result;
    }



}
