package com.guoxc.info.web.control;

import com.guoxc.info.utils.FileUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.guoxc.info.web.services.StockPredictService;
import java.io.File;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/stockPredict")
public class StockPredictControl {

    @Autowired
    private StockPredictService stockPredictService;
    @Autowired
    private Environment env;
    @RequestMapping("/predict")
    @ResponseBody
    String saveStockMinuteData(String stockCode,String operTime){


        try {
            if("ALL".equals(stockCode)){
                List  list = FileUtil.readFileXml(new File("E:\\stock\\data\\monitor\\predictStock.txt"),"gbk");
               for(int i=0;i<list.size();i++){
                   stockPredictService.predict((String)list.get(i),operTime);
               }
            }else{

                stockPredictService.predict(stockCode,operTime);
            }



        } catch (ParseException e) {
            e.printStackTrace();
        }


        return "ok";


    }


}
