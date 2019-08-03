package com.guoxc.info.web.services;

import com.alibaba.fastjson.JSON;
import com.guoxc.info.bean.info.BsStaticDataBean;
import com.guoxc.info.bean.info.StockDayBean;
import com.guoxc.info.dao.BsStaticDataDao;
import com.guoxc.info.dao.StockDao;
import com.guoxc.info.utils.DateUtil;
import com.guoxc.info.utils.FileUtil;
import com.guoxc.info.utils.StringUtil;
import com.guoxc.info.web.common.ConstantsInfo;
import com.guoxc.info.web.control.StockControl;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.guoxc.info.web.dao.BaseDao;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockService {
    private final static Logger logger = LoggerFactory.getLogger(StockControl.class);
    @Autowired
    private StockDao stockDao  ;

    @Autowired
    private BsStaticDataDao bsStaticDataDao;


    @Autowired
    private BaseDao baseDao;


    @Autowired
    private SqlSession sqlSession;



    public void query(){

         Map param = new HashMap<String,String>();
        param.put("codeName","STOCK_MINUTE_DEAL_TIME_601181");

        Object object1 =   sqlSession.selectOne("com.guoxc.info.dao.BsStaticDataDao.getCodeValue","STOCK_MINUTE_DEAL_TIME_601181");

        Object object = baseDao.queryForObject("com.guoxc.info.dao.BsStaticDataDao.getCodeValue",param);

        logger.error(JSON.toJSONString(object));


    }


    public String putRecentData2StockData() {

        StockDayBean bean = new StockDayBean();
        String lastDealDayStr = bsStaticDataDao.getCodeValue("STOCK_DEAL_TIME_STOCK_DAY");
        if (lastDealDayStr != null) {
            try {
                Timestamp lastDealDay = DateUtil.convertStringToTimestamp(lastDealDayStr, "yyyy-MM-dd");
                if (lastDealDay.before(DateUtil.getCurrentDate())) {

                    File[] files = new File("E:\\stock\\data\\dayadd\\").listFiles();
                    for (File file : files) {
                        putStockDataFile2DDB(file, lastDealDay);
                    }

                    BsStaticDataBean bsStaticDataBean = new BsStaticDataBean();
                    bsStaticDataBean.setCodeName("STOCK_DEAL_TIME_STOCK_DAY");
                    bsStaticDataBean.setCodeValue(DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN.YYYY_MM_DD));
                    bsStaticDataDao.updateCodeValue(bsStaticDataBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //603682 sz002951 002952 002958
        return "success";
    }




    public String saveStockMinuteData() {

        StockDayBean bean = new StockDayBean();
        String lastDealDayStr = bsStaticDataDao.getCodeValue("STOCK_DEAL_TIME_STOCK_MINUTE");
        if (lastDealDayStr != null) {
            try {
                Timestamp lastDealDay = DateUtil.convertStringToTimestamp(lastDealDayStr, "yyyy-MM-dd");
                if (lastDealDay.before(DateUtil.getCurrentDate())) {

                    File[] files = new File("E:\\stock\\data\\minute\\").listFiles();
                    for (File file : files) {
                        putStockDataMinuteFile2DB(file, lastDealDay);
                    }

                    BsStaticDataBean bsStaticDataBean = new BsStaticDataBean();
                    bsStaticDataBean.setCodeName("STOCK_DEAL_TIME_STOCK_MINUTE");
                    bsStaticDataBean.setCodeValue(DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN.YYYY_MM_DD));
                    bsStaticDataDao.updateCodeValue(bsStaticDataBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //603682 sz002951 002952 002958
        return "success";
    }




    public String saveRecentZSData2StockData() {

        String filePath = "E:\\stock\\data\\zhishu\\SH#999999.txt";
        String dealZSDataTimeKey = "STOCK_DEAL_TIME_STOCK_DAY_ZS";
        String lastDealDayStr =  (String) baseDao.selectOne(ConstantsInfo.SQLID.GET_CODEVALUE ,dealZSDataTimeKey) ;  bsStaticDataDao.getCodeValue(dealZSDataTimeKey);
        if(StringUtil.isBlank(lastDealDayStr) ){
            lastDealDayStr = "2018-5-1";
        }

            try {
                Timestamp lastDealDay = DateUtil.convertStringToTimestamp(lastDealDayStr, "yyyy-MM-dd");
                if (lastDealDay.before(DateUtil.getCurrentDate())) {
                    File file = new File(filePath);
                    if(file.exists()){

                        putStockZSDataFile2DDB(file, lastDealDay,"ZS");

                        updateDealTime(dealZSDataTimeKey);
                    }
                    }else{
                        logger.error(filePath+" 不存在");
                    }

            } catch (Exception e) {
                e.printStackTrace();
            }

        //603682 sz002951 002952 002958
        return "success";
    }


    private  void updateDealTime(String codeName){
         Map param = new HashMap();
         param.put("codeName",key);
         param.put("codeValue",DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN.YYYY_MM_DD));
         int result = baseDao.update("com.guoxc.info.dao.BsStaticDataDao.updateCodeValue",param);
         if(result == 0){
             logger.error("updateDealTime fail");
         }

    }



    public String saveStockZSMinuteData() {
        String result = "error";
        String filePath = "E:\\stock\\data\\zhushu_minute\\SH#999999.txt";
        String dealZSDataMinuteDealTimeKey = "STOCK_DEAL_TIME_STOCK_MINUTE_ZS";
        StockDayBean bean = new StockDayBean();
        String lastDealDayStr = bsStaticDataDao.getCodeValue(dealZSDataMinuteDealTimeKey);
        if (lastDealDayStr != null) {
            try {
                Timestamp lastDealDay = DateUtil.convertStringToTimestamp(lastDealDayStr, "yyyy-MM-dd");
                if (lastDealDay.before(DateUtil.getCurrentDate())) {


                    File file = new File(filePath);
                    if(file.exists()){
                        putStockZSDataMinuteFile2DB(file, lastDealDay);
                        BsStaticDataBean bsStaticDataBean = new BsStaticDataBean();
                        bsStaticDataBean.setCodeName(dealZSDataMinuteDealTimeKey);
                        bsStaticDataBean.setCodeValue(DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN.YYYY_MM_DD));
                        bsStaticDataDao.updateCodeValue(bsStaticDataBean);
                        result= "success";
                    }else{
                        logger.error(filePath+" 不存在");
                        result = "error";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //603682 sz002951 002952 002958
        return result;
    }





    public String  putRecentZhiShu2StockData(){

        StockDayBean bean = new StockDayBean();
        File[] files = new File("E:\\stock\\data\\zhishu\\").listFiles();
        for(File file :files ){

        }



        //603682 sz002951 002952 002958

        return "success";
    }





    private void  putStockDataFile2DDB(File file,Timestamp lastDealDay ) {
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
                 if(DateUtil.convertStringToTimestamp(cols[0],"yyyy/MM/dd").after(lastDealDay)){
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
         }
        logger.info(stockCode+"stockList size="+stockList.size());
         if(stockList.size()>0){
             stockDao.insertStockDayList(stockList);
         }
    }catch (Exception e){
        logger.error("putStockDataFile2DDB_err",e);
    }

    }




    private void  putStockZSDataFile2DDB(File file,Timestamp lastDealDay,String type ) {
        try{
            List stockList = new ArrayList();
            int volMinusNum = 100;
            int turnoverMinusNum = 10000;
            if("ZS".equals(type)){
                volMinusNum = 10000;
                turnoverMinusNum = 100000000;
            }

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
                    if(DateUtil.convertStringToTimestamp(cols[0],"yyyy/MM/dd").after(lastDealDay)){
                        StockDayBean bean = new StockDayBean();
                        bean.setStockCode(stockCode);
                        bean.setOperTime(DateUtil.convertStringToTimestamp(cols[0],"yyyy/MM/dd"));
                        bean.setOpenPrice(Float.valueOf(cols[1]));
                        bean.setHighPrice(Float.valueOf(cols[2]));
                        bean.setLowPrice(Float.valueOf(cols[3]));
                        bean.setClosePrice(Float.valueOf(cols[4]));
                        bean.setVolume(Double.valueOf(cols[5])/volMinusNum); //单位 ：万
                        bean.setTurnover( Double.valueOf(cols[6].substring(0,cols[6].length()-3))/turnoverMinusNum);//单位亿
                    }
                }
            }
            logger.info(stockCode+"stockList size="+stockList.size());
            if(stockList.size()>0){
                stockDao.insertStockDayZSList(stockList);
            }
        }catch (Exception e){
            logger.error("putStockDataFile2DDB_err",e);
        }

    }




    private void  putStockDataMinuteFile2DB(File file,Timestamp lastDealDay ) {
        try{
            List stockList = new ArrayList();
            //file 示例 SH#600000.txt
            String fileName = file.getName();
            String stockCode = fileName.substring(3,9);
            List  list = FileUtil.readFileXml(file,"gbk");
            int number = 0;
            for(int i=1; i<list.size();i++){
                String line = (String) list.get(i);
                if(line.contains("日期")){
                    continue; //标题跳过.
                }
                // 标题  日期	    开盘	    最高	    最低	    收盘	    成交量	    成交额
                //2010/01/04	6.69	6.70	6.41	6.42	66191338	1419984128.00
                String[] cols = line.split("\t");
                if(cols.length>5){
                    if(DateUtil.convertStringToTimestamp(cols[0],"yyyy/MM/dd").after(lastDealDay)){
                        StockDayBean bean = new StockDayBean();
                        bean.setStockCode(stockCode);
                        bean.setOperTime(DateUtil.convertStringToTimestamp(cols[0]+" "+cols[1],"yyyy/MM/dd HHmm"));
                        bean.setOpenPrice(Float.valueOf(cols[2]));
                        bean.setHighPrice(Float.valueOf(cols[3]));
                        bean.setLowPrice(Float.valueOf(cols[4]));
                        bean.setClosePrice(Float.valueOf(cols[5]));
                        bean.setVolume(Double.valueOf(cols[6])/100);
                        bean.setTurnover( Double.valueOf(cols[7].substring(0,cols[7].length()-3))/10000);
                        stockList.add(bean);
                    }
                }

                if(stockList.size()>=2000){
                    number= number+stockList.size();
                    logger.info(stockCode+"stockList size="+number);
                    stockDao.insertStockMinuteList(stockList);
                    stockList.clear();
                }
            }

            if(stockList.size()>0){
                number= number+stockList.size();
                logger.info(stockCode+"stockList size="+number);
                stockDao.insertStockMinuteList(stockList);
            }
        }catch (Exception e){
            logger.error("putStockDataFile2DDB_err",e);
        }

    }




    private void  putStockZSDataMinuteFile2DB(File file,Timestamp lastDealDay ) {
        try{
            List stockList = new ArrayList();
            //file 示例 SH#600000.txt
            String fileName = file.getName();
            String stockCode = fileName.substring(3,9);
            List  list = FileUtil.readFileXml(file,"gbk");
            int number = 0;
            for(int i=1; i<list.size();i++){
                String line = (String) list.get(i);
                if(line.contains("日期")){
                    continue; //标题跳过.
                }
                // 标题  日期	    开盘	    最高	    最低	    收盘	    成交量	    成交额
                //2010/01/04	6.69	6.70	6.41	6.42	66191338	1419984128.00
                String[] cols = line.split("\t");
                if(cols.length>5){
                    if(DateUtil.convertStringToTimestamp(cols[0],"yyyy/MM/dd").after(lastDealDay)){
                        StockDayBean bean = new StockDayBean();
                        bean.setStockCode(stockCode);
                        bean.setOperTime(DateUtil.convertStringToTimestamp(cols[0]+" "+cols[1],"yyyy/MM/dd HHmm"));
                        bean.setOpenPrice(Float.valueOf(cols[2]));
                        bean.setHighPrice(Float.valueOf(cols[3]));
                        bean.setLowPrice(Float.valueOf(cols[4]));
                        bean.setClosePrice(Float.valueOf(cols[5]));
                        bean.setVolume(Double.valueOf(cols[6])/100);
                        bean.setTurnover( Double.valueOf(cols[7].substring(0,cols[7].length()-3))/10000);
                        stockList.add(bean);
                    }
                }

                if(stockList.size()>=2000){
                    number= number+stockList.size();
                    logger.info(stockCode+"stockList size="+number);
                    stockDao.insertStockMinuteZSList(stockList);
                    stockList.clear();
                }
            }

            if(stockList.size()>0){
                number= number+stockList.size();
                logger.info(stockCode+"stockList size="+number);
                stockDao.insertStockMinuteZSList(stockList);
            }
        }catch (Exception e){
            logger.error("putStockDataFile2DDB_err",e);
        }

    }




}
