package com.guoxc.info.web.services;

import com.alibaba.fastjson.JSON;
import com.guoxc.info.bean.info.StockBean;
import com.guoxc.info.bean.info.StockDayBean;
import com.guoxc.info.bean.info.StockDayInflectionBean;
import com.guoxc.info.bean.info.StockTransverseBean;
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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

@Service
public class StockService {
    private final static Logger logger = LoggerFactory.getLogger(StockControl.class);
    @Autowired
    private StockDao stockDao  ;

    @Autowired
    private BsStaticDataDao bsStaticDataDao;

    @Autowired
    private BsStaticDataService bsStaticDataService;

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



    public Map queryCurrStockDayInflection(){
        Map result = new HashMap();
       List  currStockDayInflectionList =  baseDao.queryForList("com.guoxc.info.dao.stockInflection.getCurrInflectionNew",null);
       if(currStockDayInflectionList != null){
           for(int i=0; i<currStockDayInflectionList.size();i++){
               StockDayInflectionBean  tmpStockDayInflectionBean = (StockDayInflectionBean) currStockDayInflectionList.get(i);
               result.put(tmpStockDayInflectionBean.getStockCode(),tmpStockDayInflectionBean);
           }
       }
      return result;
    }




    public String updateStockInfo(){

        try {
            File[] files = new File("E:\\stock\\data\\stockInfo").listFiles();
            for(File file : files){
                String fileName = file.getName();
                if(fileName.startsWith("沪深Ａ股")){
                    updateStockFinanceInfo(file);
                  boolean bool =  file.renameTo(new File(file.getParent()+"/bak/"+file.getName()));
                }else{
                    if(fileName.indexOf("202")>0){//2020年的前三位
                        updateStockHangyeInfo(file);
                        file.renameTo(new File(file.getParent()+"/bak/"+file.getName()));
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }
// 更新财务数据
    private  void updateStockFinanceInfo(File file) throws ParseException {

       List StockBeanList =  baseDao.queryForList("com.guoxc.info.dao.StockDao.selectStockInfo",null);
       Map paramMap = new HashMap();
       if(StockBeanList.size()>0){
           for(int i=0;i<StockBeanList.size();i++){
               StockBean bean =  (StockBean) StockBeanList.get(i);
               paramMap.put(bean.getStockCode(),bean);
           }
       }
       List updateList = new  ArrayList();
        List insertList = new  ArrayList();

        paramMap.put("updateList",updateList);
        paramMap.put("insertList",insertList);
        List  list = FileUtil.readFileXml(file,"gbk");
        String line = (String) list.get(0);
        Map<String,Integer> keyOffsetMap =  getStockKeyOffsetMap(line);
        for(int i=1;i<list.size()-1;i++){ //除去最后一行
            line  = (String) list.get(i);
            try{
                getStockKeyOffsetMap(line,keyOffsetMap,paramMap);
            }catch(Exception e){
                logger.error(i+"line",e);
            }

        }
        if(insertList.size()>0){
            baseDao.insert("com.guoxc.info.dao.StockDao.insertStockInfoList",insertList);
        }
        if(updateList.size()>0){
            for(int i=0; i<updateList.size();i++){
                StockBean bean =  (StockBean)updateList.get(i);
                baseDao.update("com.guoxc.info.dao.StockDao.updateStockInfo",bean);
            }
        }

    }


    // 更新概念分类（行业分类2）
    private  void updateStockHangyeInfo(File file) throws ParseException {

        String fileName = file.getName();
        String hangye = fileName.substring(0,fileName.indexOf("202"));//2020年的前三位
        Map paramMap = new HashMap();
        List updateList = new  ArrayList();;
        List  list = FileUtil.readFileXml(file,"gbk");
        String line = (String) list.get(0);
        for(int i=1;i<list.size()-1;i++){ //除去最后一行
            line  = (String) list.get(i);
            String[] cols = line.split("\t");
            paramMap.put("hangye2",hangye+",");
            paramMap.put("stockCode",cols[0]);
            baseDao.update("com.guoxc.info.dao.StockDao.updateStockInfoHangye2",paramMap);
        }


    }



    private void getStockKeyOffsetMap(String line,  Map<String,Integer> offsetMap, Map stockInfoMap) throws ParseException {


        // 标题  日期	    开盘	    最高	    最低	    收盘	    成交量	    成交额
        String[] cols = line.split("\t");
        StockBean bean = null ;
        if(offsetMap.get("stockCode")!= null){

           String stockCode =  cols[offsetMap.get("stockCode")];
            bean = (StockBean) stockInfoMap.get(stockCode);
            if(bean == null){
                bean = new StockBean();
               List insertList =  (List) stockInfoMap.get("insertList");
                insertList.add(bean);
                bean.setHangye2(",");
                bean.setStockCode(stockCode);
            }else{
                List updateList =  (List) stockInfoMap.get("updateList");
                updateList.add(bean);
            }
        }else{

        }
        if(offsetMap.get("stockName")!= null){
            bean.setStockName(cols[offsetMap.get("stockName")]);
        }
        if(offsetMap.get("hangye")!= null){

            bean.setHangYe(cols[offsetMap.get("hangye")]);
        }
        if(offsetMap.get("prov")!= null){
            bean.setProv(cols[offsetMap.get("prov")]);
        }
        if(offsetMap.get("zgb")!= null){
            String  zgb =  cols[offsetMap.get("zgb")];
            if(!"--".equals(zgb.trim())){
                bean.setZgb(Float.valueOf(zgb));
            }
        }
        if(offsetMap.get("ltgb")!= null){
            String  ltgb = cols[offsetMap.get("ltgb")];
            if(!"--".equals(ltgb.trim())){
                bean.setLtgb(Float.valueOf(ltgb));
            }
        }
        if(offsetMap.get("priceRate3D")!= null){
            String priceRate3D = cols[offsetMap.get("priceRate3D")];
            if(!"--".equals(priceRate3D.trim())){
                bean.setPriceRate3D(Float.valueOf(priceRate3D));
            }
        }
        if(offsetMap.get("priceRate20D")!= null){
            String priceRate20D = cols[offsetMap.get("priceRate20D")];
            if(!"--".equals(priceRate20D.trim())){
                bean.setPriceRate20D(Float.valueOf(priceRate20D));
            }
        }
        if(offsetMap.get("priceRate60D")!= null){
            String priceRate60D = cols[offsetMap.get("priceRate60D")];
            if(!"--".equals(priceRate60D.trim())){
                bean.setPriceRate60D(Float.valueOf(priceRate60D));
            }
        }
        if(offsetMap.get("recentTips")!= null){
            bean.setRecentTips(cols[offsetMap.get("recentTips")]);
        }
        if(offsetMap.get("financeUpdateDay")!= null){
            bean.setFinanceUpdateDay(DateUtil.getTimestamp(cols[offsetMap.get("financeUpdateDay")],"yyyyMMdd"));
        }
        if(offsetMap.get("onMarketDay")!= null){
            String onMarketDay = cols[offsetMap.get("onMarketDay")];
            if(!"--".equals(onMarketDay.trim())){
                bean.setOnMarketDay(DateUtil.getTimestamp(onMarketDay,"yyyyMMdd"));
            }

        }
        if(offsetMap.get("debtRatio")!= null){
            bean.setDebtRatio(Float.valueOf(cols[offsetMap.get("debtRatio")]));
        }
        if(offsetMap.get("netProfitRate")!= null){

           String netProfitRate =   cols[offsetMap.get("netProfitRate")];
            netProfitRate= netProfitRate.replace("㈢","").replace("㈣","").replace("㈡","").replace("㈠","");
            if(!"--".equals(netProfitRate.trim())){
                bean.setNetProfitRate(Float.valueOf(netProfitRate));
            }

        }
        if(offsetMap.get("profitRateBefore")!= null){
                bean.setProfitRateBefore(Float.valueOf(cols[offsetMap.get("profitRateBefore")]));


        }
        if(offsetMap.get("stockerNum")!= null){
            Float stockerNum = Float.valueOf(cols[offsetMap.get("stockerNum")]);

            if(StringUtils.isBlank(bean.getStockerNum()) ){//为空代表第一次
                bean.setStockerNum("，"+cols[offsetMap.get("stockerNum")]+ ",");
            }else if(!bean.getStockerNum().endsWith("，"+cols[offsetMap.get("stockerNum")]+ ",")){
                //和上一次人数不一样，代表有变动，更新
                String [] stokcerNum =bean.getStockerNum().split(",");
                bean.setStockerNum(bean.getStockerNum()+cols[offsetMap.get("stockerNum")]+ ",");
//                bean.setStockerNumRate( Long.valueOf(cols[offsetMap.get("stockerNum")])*100/Long.valueOf(stokcerNum[stokcerNum.length-1]));
            }
        }
    }


    private Map<String,Integer> getStockKeyOffsetMap(String line){
        // 标题  日期	    开盘	    最高	    最低	    收盘	    成交量	    成交额
        String[] cols = line.split("\t");
        Map<String , Integer> offsetMap = new HashMap();
        for(int i=0 ;i <cols.length;i++){
            if("代码".endsWith(cols[i])){
                offsetMap.put("stockCode",i);
            }else if("名称".endsWith(cols[i])){
                offsetMap.put("stockName",i);
            } else if("细分行业".endsWith(cols[i])){
                offsetMap.put("hangye",i);
            }else if("地区".endsWith(cols[i])){
                offsetMap.put("prov",i);
            }else if("流通股(亿)".endsWith(cols[i])){
                offsetMap.put("ltgb",i);
            }else if("3日涨幅%".endsWith(cols[i])){
                offsetMap.put("priceRate3D",i);
            }else if("20日涨幅%".endsWith(cols[i])){
                offsetMap.put("priceRate20D",i);
            } else if("60日涨幅%".endsWith(cols[i])){
                offsetMap.put("priceRate60D",i);
            } else if("近日指标提示".endsWith(cols[i])){
                offsetMap.put("recentTips",i);
            }else if("财务更新".endsWith(cols[i])){
                offsetMap.put("financeUpdateDay",i);
            }else if("上市日期".endsWith(cols[i])){
                offsetMap.put("onMarketDay",i);
            }else if("资产负债率%".endsWith(cols[i])){
                offsetMap.put("debtRatio" ,i);
            }else if("净益率%".endsWith(cols[i])){
                offsetMap.put("netProfitRate"  ,i);
            }else if("股东人数".endsWith(cols[i])){
                offsetMap.put("stockerNum" ,i);
            }else if("利润同比%".endsWith(cols[i])){
                offsetMap.put("profitRateBefore" ,i);
            }

        }
        return offsetMap;
    }



    public String saveStockMinuteDataByStockCode(String stockCode){

        String last6StockCode =stockCode.substring(2);
        String dealZSDataTimeKey = "STOCK_MINUTE_DEAL_TIME_"+last6StockCode;
        String lastDealDayStr =  (String) baseDao.selectOne(ConstantsInfo.SQLID.GET_CODEVALUE ,dealZSDataTimeKey) ;
        boolean exist = true;
        if(lastDealDayStr == null){
            lastDealDayStr ="2020-1-1";
            exist= false;
            createStockDayTable(stockCode);
        }

        try {
            Timestamp lastDealDay = DateUtil.convertStringToTimestamp(lastDealDayStr, "yyyy-MM-dd");
            if (DateUtil.getCurrentDate().after(lastDealDay) ) {
                File file = new File("E:\\stock\\data\\minute\\"+stockCode.substring(0,2)+"#"+stockCode.substring(2)+".txt");
                putStockDataMinuteFile2DB(file, lastDealDay,"");
                if(exist){
                    bsStaticDataService.updateDealTime(dealZSDataTimeKey);
                }else{
                    bsStaticDataService.insertCodeInfo(dealZSDataTimeKey,DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN.YYYY_MM_DD));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("deal_voer:"+stockCode);
        return "success";
    }


    public String saveStockTmpDay(File  file){
        List  list = FileUtil.readFileXml(file,"gbk");
        List tmpList = new ArrayList();

        baseDao.update("com.guoxc.info.dao.StockDao.clearStockTmpDay",null);
        for(int i=1;i<list.size()-1;i++){
            String line = (String)list.get(i);
            String[] cols = line.split("\t");

            try{

                StockDayBean bean = new  StockDayBean();

                bean.setStockCode(cols[0]);
                bean.setStockName(cols[1]);
                if(line.startsWith("000029")){
                    logger.error(line);
                }

                 if("--".equals(cols[2].trim())){
                     continue;
                 }

                bean.setLowPrice(Float.parseFloat(cols[2]));//涨幅
                bean.setClosePrice(Float.parseFloat(cols[3]));//当前价格
                bean.setHighPrice( Float.parseFloat(cols[7]));  //量比
                tmpList.add(bean);
            }catch (Exception e){

                logger.error("error line="+line,e);
            }

        }

        baseDao.insert("com.guoxc.info.dao.StockDao.insertStockTmpDayList",tmpList);
        return "success";
    }




    private void  createStockDayTable(String stockCode){

        Map param = new HashMap();
        param.put("tableName","t_st_min_"+stockCode.substring(2));
        baseDao.update("com.guoxc.info.dao.StockDao.createStockDayTable",param);

    }


    public String putRecentData2StockData() {

        String dealZSDataTimeKey = "STOCK_DEAL_TIME_STOCK_DAY";
        StockDayBean bean = new StockDayBean();
        String lastDealDayStr =  (String) baseDao.selectOne(ConstantsInfo.SQLID.GET_CODEVALUE ,dealZSDataTimeKey) ;
        if (lastDealDayStr != null) {
            try {
                Timestamp lastDealDay = DateUtil.convertStringToTimestamp(lastDealDayStr, "yyyy-MM-dd");
                if (lastDealDay.before(DateUtil.getCurrentDate())) {

                    File[] files = new File("E:\\stock\\data\\dayadd\\").listFiles();
                    for (File file : files) {
                        putStockZSDataFile2DDB(file, lastDealDay,"");
                    }
                    bsStaticDataService.updateDealTime(dealZSDataTimeKey);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //603682 sz002951 002952 002958
        return "success";
    }




    public String putRecentData2StockDataAnaly(File file) {

        String dealZSDataTimeKey = "STOCK_DEAL_TIME_STOCK_DAY_ANA";
        StockDayBean bean = new StockDayBean();
        String lastDealDayStr =  (String) baseDao.selectOne(ConstantsInfo.SQLID.GET_CODEVALUE ,dealZSDataTimeKey) ;
        if (lastDealDayStr != null) {
            try {
                Timestamp lastDealDay = DateUtil.convertStringToTimestamp(lastDealDayStr, "yyyy-MM-dd");
                if (lastDealDay.before(DateUtil.getCurrentDate())) {

//                    File[] files = new File("E:\\stock\\data\\dayadd\\").listFiles();
////                    for (File file : files) {
////                        putStockZSDataFile2DBAnaly(file, lastDealDay,"");
////                    }
                    putStockZSDataFile2DBAnaly(file, lastDealDay,"");
                    bsStaticDataService.updateDealTime(dealZSDataTimeKey);
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
        String lastDealDayStr =  (String) baseDao.selectOne(ConstantsInfo.SQLID.GET_CODEVALUE ,dealZSDataTimeKey) ;
//        bsStaticDataDao.getCodeValue(dealZSDataTimeKey);
        if(StringUtil.isBlank(lastDealDayStr) ){
            lastDealDayStr = "2018-8-1";
        }

            try {
                Timestamp lastDealDay = DateUtil.convertStringToTimestamp(lastDealDayStr, "yyyy-MM-dd");
                if (lastDealDay.before(DateUtil.getCurrentDate())) {
                    File file = new File(filePath);
                    if(file.exists()){

                        putStockZSDataFile2DDB(file, lastDealDay,"ZS");

                        bsStaticDataService.updateDealTime(dealZSDataTimeKey);
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


    public String saveStockZSMinuteData() {
        String result = "error";
        String filePath = "E:\\stock\\data\\zhushu_minute\\SH#999999.txt";
        String dealZSDataMinuteDealTimeKey = "STOCK_DEAL_TIME_STOCK_MINUTE_ZS";
        String lastDealDayStr =  (String) baseDao.selectOne(ConstantsInfo.SQLID.GET_CODEVALUE ,dealZSDataMinuteDealTimeKey) ;

        if (lastDealDayStr != null) {
            try {
                Timestamp lastDealDay = DateUtil.convertStringToTimestamp(lastDealDayStr, "yyyy-MM-dd");
                if (lastDealDay.before(DateUtil.getCurrentDate())) {
                    File file = new File(filePath);
                    if(file.exists()){
                        putStockDataMinuteFile2DB(file, lastDealDay,"ZS");

                        bsStaticDataService.updateDealTime(dealZSDataMinuteDealTimeKey);
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


//usegetana
    public void  putStockZSDataFile2DBAnaly(File file,Timestamp lastDealDay,String type) {
        try{
//            Timestamp beginTime = DateUtil.addMonth(lastDealDay,3);
            List stockList = new ArrayList();
            List stockSwingList = new ArrayList();
            //file 示例 SH#600000.txt
            String sqlId = null;
            sqlId = "com.guoxc.info.dao.StockDao.insertStockDataAnaBatch" ;
            long currTime = System.currentTimeMillis();
            String fileName = file.getName();
            String stockCode = fileName.substring(3,9);
            List  list = FileUtil.readFileXml(file,"gbk");
            Map hisVolAndClosePrice = new HashMap();
            List <Float>hisVolList = new ArrayList();
            List<Float> hisClosePriceList = new ArrayList();
            List<Float> hisSwingList = new ArrayList();
            logger.info("*****"+stockCode+" readFile cost "+ ( System.currentTimeMillis()-currTime));
            currTime=System.currentTimeMillis();
            hisVolAndClosePrice.put("hisVolList",hisVolList);
            hisVolAndClosePrice.put("hisClosePriceList",hisClosePriceList);
            hisVolAndClosePrice.put("hisSwingList",hisSwingList);

            String getMaxSeqSql= "com.guoxc.info.dao.StockDao.getMaxSeqByStockCode";
            StockDayBean lastStockDayean  = null ;
                     lastStockDayean  =  (StockDayBean) baseDao.selectOne(getMaxSeqSql ,stockCode) ;
            hisVolAndClosePrice.put("lastInfo",lastStockDayean);
            logger.info("*****"+stockCode+" readDBinfo1 cost "+ ( System.currentTimeMillis()-currTime));
            currTime=System.currentTimeMillis();
            String getLastStockSwingSql= "com.guoxc.info.dao.StockSwingDao.getLastStockSwingByStockCode";
            StockTransverseBean lastStockSwingean = null;
                     lastStockSwingean =  (StockTransverseBean) baseDao.selectOne(getLastStockSwingSql ,stockCode) ;

            hisVolAndClosePrice.put("lastStockSwingInfo",lastStockSwingean);
            logger.info("*****"+stockCode+" readDBinfo2 cost "+ ( System.currentTimeMillis()-currTime));
            currTime=System.currentTimeMillis();

            String getCurrInflectionSql= "com.guoxc.info.dao.stockInflection.getCurrInflection";
            StockDayInflectionBean stockDayInflection = null;
             stockDayInflection =  (StockDayInflectionBean) baseDao.selectOne(getCurrInflectionSql ,stockCode) ;
            hisVolAndClosePrice.put("currStockDayInflection",stockDayInflection);
            logger.info("*****"+stockCode+" readDBinfo3 cost "+ ( System.currentTimeMillis()-currTime));
            currTime=System.currentTimeMillis();
            for(int i=2; i<list.size();i++){
                String line = (String) list.get(i);
                if(line.contains("日期")){
                    continue; //标题跳过.
                }
                // 标题  日期	    开盘	    最高	    最低	    收盘	    成交量	    成交额
                //2010/01/04	6.69	6.70	6.41	6.42	66191338	1419984128.00

                String[] cols = line.split("\t");
                if(cols.length>=7){
                    if(DateUtil.convertStringToTimestamp(cols[0],"yyyy/MM/dd").after(lastDealDay)){
                        StockDayBean bean =  getStockBeanForDayFromLine(hisVolAndClosePrice,list,i,stockCode);
                        bean.setStockCode(stockCode);
                        if(bean.getVolume()!=0){
                            stockList.add(bean);
                            if(bean.getSeq()>60){
                                setRecentSwingInfo(bean,stockSwingList,hisVolAndClosePrice);
                            }
                        }
                    }
                }
            }

            logger.info("*****"+stockCode+" deal data cost "+ ( System.currentTimeMillis()-currTime));
            currTime=System.currentTimeMillis();
            logger.info(stockCode+"stockList size="+stockList.size());
            if(stockList.size()>0){
//                stockDao.insertStockDayZSList(stockList);
                baseDao.insert(sqlId,stockList);
            }
            logger.info("*****"+stockCode+" save DBdata1 cost "+ ( System.currentTimeMillis()-currTime));
            currTime=System.currentTimeMillis();
// save stockSwing
            if(stockSwingList.size()>0){
                  if(lastStockSwingean != null ){
                      StockTransverseBean tmpStockSwingean  =  (StockTransverseBean) stockSwingList.get(0);
                       if(tmpStockSwingean.getSwingSeq() ==  lastStockSwingean.getSwingSeq()  ){
                           stockSwingList.remove(0);
                           baseDao.update("com.guoxc.info.dao.StockSwingDao.updateStockSwing",tmpStockSwingean);
                       }
                  }
                  if(stockSwingList.size()>0){
                      baseDao.insert("com.guoxc.info.dao.StockSwingDao.insertStockSwingList",stockSwingList);
                  }
            }

            logger.info("*****"+stockCode+" save DBdata2 cost "+ ( System.currentTimeMillis()-currTime));
//            currTime=System.currentTimeMillis();
//            // save stockDayInflection
//            if(stockDayInflection ==null){
//                StockDayInflectionBean tmpStockDayCurrInflection = (StockDayInflectionBean) hisVolAndClosePrice.get("currStockDayInflection");
//                if(tmpStockDayCurrInflection != null){
//                    baseDao.insert("com.guoxc.info.dao.stockInflection.insertStockInflection",tmpStockDayCurrInflection);
//                }
//            }else{
//                StockDayInflectionBean tmpStockDayCurrInflection = (StockDayInflectionBean) hisVolAndClosePrice.get("currStockDayInflection");
//               if( tmpStockDayCurrInflection!= null && stockDayInflection.getSeq()<tmpStockDayCurrInflection.getSeq()){
//                   baseDao.insert("com.guoxc.info.dao.stockInflection.updateStockInflection",tmpStockDayCurrInflection);
//               }
//            }
//
//            logger.info("*****"+stockCode+" save DBdata3 cost "+ ( System.currentTimeMillis()-currTime));

        }catch (Exception e){
            logger.error("putStockDataFile2DDB_err",e);
        }
    }


    private void  putStockZSDataFile2DDB(File file,Timestamp lastDealDay,String type) {
        try{
            Timestamp beginTime = DateUtil.addMonth(lastDealDay,3);
            List stockList = new ArrayList();
            List stockSwingList = new ArrayList();
            //file 示例 SH#600000.txt
            String sqlId = null;
           if("ZS".equals(type)){
               sqlId = "com.guoxc.info.dao.StockDao.insertStockDayZSList";
           }else{
               sqlId = "com.guoxc.info.dao.StockDao.insertStockDayList" ;
           }

            String fileName = file.getName();
            String stockCode = fileName.substring(3,9);
            List  list = FileUtil.readFileXml(file,"gbk");
            Map hisVolAndClosePrice = new HashMap();
            List <Float>hisVolList = new ArrayList();
            List<Float> hisClosePriceList = new ArrayList();
            List<Float> hisSwingList = new ArrayList();
            hisVolAndClosePrice.put("hisVolList",hisVolList);
            hisVolAndClosePrice.put("hisClosePriceList",hisClosePriceList);
            hisVolAndClosePrice.put("hisSwingList",hisSwingList);


            for(int i=2; i<list.size();i++){
                String line = (String) list.get(i);
                if(line.contains("日期")){
                    continue; //标题跳过.
                }
                // 标题  日期	    开盘	    最高	    最低	    收盘	    成交量	    成交额
                //2010/01/04	6.69	6.70	6.41	6.42	66191338	1419984128.00

                String[] cols = line.split("\t");
                if(cols.length>=7){
                    if(DateUtil.convertStringToTimestamp(cols[0],"yyyy/MM/dd").after(lastDealDay)){
                        StockDayBean bean =  getStockBeanForDayFromLine(hisVolAndClosePrice,list,i, stockCode);
                        bean.setStockCode(stockCode);

                        stockList.add(bean);
                    }
                }
            }
            logger.info(stockCode+"stockList size="+stockList.size());
            if(stockList.size()>0){
//                stockDao.insertStockDayZSList(stockList);
                baseDao.insert(sqlId,stockList);
            }

        }catch (Exception e){
            logger.error("putStockDataFile2DDB_err",e);
        }
    }


    private   StockDayBean   getBaseStockDayBean(List list ,int index)throws ParseException {
        StockDayBean bean = new StockDayBean();
        String line = (String) list.get(index);
        String[] cols = line.split("\t");
        if(cols.length>=7){
                bean.setOperTime(DateUtil.convertStringToTimestamp(cols[0],"yyyy/MM/dd"));
                bean.setOpenPrice(Float.valueOf(cols[1]));
                bean.setHighPrice(Float.valueOf(cols[2]));
                bean.setLowPrice(Float.valueOf(cols[3]));
                bean.setClosePrice(Float.valueOf(cols[4]));
                bean.setVolume(Long.valueOf(cols[5])/100); //单位 ：百
                bean.setTurnover( Long.valueOf(cols[6].substring(0,cols[6].length()-3))/10000);//单位万
            }

      return bean;
    }


    private   StockDayBean   getBaseStockDayBeanForMinute(List list ,int index)throws ParseException {
        StockDayBean bean = new StockDayBean();
        String line = (String) list.get(index);
        String[] cols = line.split("\t");
        if(cols.length>=8){
            bean.setOperTime(DateUtil.convertStringToTimestamp(cols[0]+" "+cols[1],"yyyy/MM/dd HHmm"));
            bean.setOpenPrice(Float.valueOf(cols[2]));
            bean.setHighPrice(Float.valueOf(cols[3]));
            bean.setLowPrice(Float.valueOf(cols[4]));
            bean.setClosePrice(Float.valueOf(cols[5]));
            bean.setVolume(Long.valueOf(cols[6])/100);
            bean.setTurnover( Long.valueOf(cols[7].substring(0,cols[7].length()-3))/10000);
        }
        return bean;
    }



    private  StockDayBean getStockBeanForMinuteFromLine(Map hisVol, List list, int index) throws ParseException {
        StockDayBean result = new StockDayBean();
        StockDayBean bean =   getBaseStockDayBeanForMinute(list,index);
       String dayStr =   DateUtil.getFormattedDate(bean.getOperTime(),DateUtil.DATE_PATTERN.YYYY_MM_DD);
        Float dayVol = (Float)  hisVol.get(dayStr);
        if(dayVol == null || dayVol == 0d){  //如果不存在，则认为是第一条，从该条查240条
            dayVol = getDayVol(list,index);
            hisVol.put(dayStr,dayVol) ;
        }
        BeanUtils.copyProperties(bean,result);
        result.setPriceRate( Math.round(   (result.getClosePrice()-result.getOpenPrice())*1000/result.getOpenPrice()) );
        result.setSwing(  Math.round((result.getHighPrice()- result.getLowPrice())*1000/result.getOpenPrice()));
        result.setV20Rate(  Math.round ((result.getVolume() *100/dayVol)) ) ;
         return result;
    }


    private Float getDayVol(List list, int index) throws ParseException {
        Float result = 0f;
        for( int j= index-240+1;j<=index;j++){
            StockDayBean bean =   getBaseStockDayBeanForMinute(list,j);
            result = result+bean.getVolume();
        }
        return result;
    }



    private  StockDayBean getStockBeanForDayFromLine(Map hisVolAndPrice, List list, int index,String stockCode) throws ParseException {
        StockDayBean result = new StockDayBean();
        List <Long>hisVol = (List) hisVolAndPrice.get("hisVolList");
        List<Float> hisClosePrice = (List) hisVolAndPrice.get("hisClosePriceList");
        List<Long> hisSwing = (List) hisVolAndPrice.get("hisSwingList");
        Float preClosePrice = null;
        if(hisVol.size()==0){
            int beginNum = index-60>2? index-60:2;
            for(;beginNum<index;beginNum++){
                String line = (String) list.get(beginNum);
                String[] cols = line.split("\t");
                if(cols.length>5){
                    hisVol.add(Long.valueOf(cols[5])/100);
                    hisClosePrice.add(Float.valueOf(cols[4]));
                       if(preClosePrice != null){
                           hisSwing.add((long) Math.round( (Float.valueOf(cols[2])-Float.valueOf(cols[3]))*1000/preClosePrice));
                       }

                    preClosePrice = Float.valueOf(cols[4]);
                }
            }
        }
        StockDayBean bean =   getBaseStockDayBean(list,index);
        BeanUtils.copyProperties(bean,result);
        result.setStockCode(stockCode);
        if(index!=2){
            result.setPreClosePrice(getPreClosePrice(hisClosePrice));
            result.setC5Avg( getlastPriceAvg(hisClosePrice,5) );
            result.setC20Avg( getlastPriceAvg(hisClosePrice,20));
            result.setC60Avg( getlastPriceAvg(hisClosePrice,60));
            result.setV5Avg(getlastVolAvg(hisVol,5));
            result.setV20Avg(getlastVolAvg(hisVol,20));
            result.setV60Avg(getlastVolAvg(hisVol,60));

            Map <String,Float> c5MaxMinPriceMap =   getRecentMaxMinPrice(hisClosePrice,5);
            Map <String,Float> c10MaxMinPriceMap =   getRecentMaxMinPrice(hisClosePrice,10);
            Map <String,Float> c20MaxMinPriceMap =   getRecentMaxMinPrice(hisClosePrice,20);
            Map <String,Float> c60MaxMinPriceMap =   getRecentMaxMinPrice(hisClosePrice,60);
            result.setMaxC5Price(c5MaxMinPriceMap.get("maxPrice"));
            result.setMinC5Price(c5MaxMinPriceMap.get("minPrice"));
            result.setMaxC10Price(c10MaxMinPriceMap.get("maxPrice"));
            result.setMinC10Price(c10MaxMinPriceMap.get("minPrice"));
            result.setMaxC20Price(c20MaxMinPriceMap.get("maxPrice"));
            result.setMinC20Price(c20MaxMinPriceMap.get("minPrice"));
            result.setMaxC60Price(c60MaxMinPriceMap.get("maxPrice"));
            result.setMinC60Price(c60MaxMinPriceMap.get("minPrice"));


            Map <String,Long> c5MaxMinVolMap =   getRecentMaxMinVol(hisVol,5);
            Map <String,Long> c10MaxMinVolMap =   getRecentMaxMinVol(hisVol,10);
            Map <String,Long> c20MaxMinVolMap =   getRecentMaxMinVol(hisVol,20);
            Map <String,Long> c60MaxMinVolMap =   getRecentMaxMinVol(hisVol,60);
            result.setMaxV5Vol(c5MaxMinVolMap.get("maxVol"));
            result.setMinV5Vol(c5MaxMinVolMap.get("minVol"));
            result.setMaxV10Vol(c10MaxMinVolMap.get("maxVol"));
            result.setMinV10Vol(c10MaxMinVolMap.get("minVol"));
            result.setMaxV20Vol(c20MaxMinVolMap.get("maxVol"));
            result.setMinV20Vol(c20MaxMinVolMap.get("minVol"));
            result.setMaxV60Vol(c60MaxMinVolMap.get("maxVol"));
            result.setMinV60Vol(c60MaxMinVolMap.get("minVol"));
            result.setS5Avg( getlastVolAvg(hisSwing,5));
            result.setS10Avg( getlastVolAvg(hisSwing,10));
            result.setS20Avg( getlastVolAvg(hisSwing,20));

        }else{
            Float  closePrice  = Float.parseFloat( String.valueOf(result.getClosePrice()) ); //Float转换double 数据会变化
            result.setPreClosePrice(closePrice);
            result.setC5Avg(  closePrice );
            result.setC20Avg( closePrice);
            result.setC60Avg( closePrice);
            result.setV5Avg(result.getVolume());
            result.setV20Avg(result.getVolume());
            result.setV60Avg(result.getVolume());
            result.setS20Avg(  Math.round((result.getHighPrice()- result.getLowPrice())*1000/result.getPreClosePrice()));
        }
        result.setPriceRate( Math.round(   (result.getClosePrice()-result.getPreClosePrice())*1000/result.getPreClosePrice())  );
        result.setSwing(  Math.round((result.getHighPrice()- result.getLowPrice())*1000/result.getPreClosePrice()));
        if(result.getS20Avg() ==0){
            result.setS20Rate( 1);
        }else{
            result.setS20Rate( Math.round ( result.getSwing()*100/result.getS20Avg()));
        }


        result.setV20Rate(  Math.round ((result.getVolume()-result.getV20Avg() )*100/result.getV20Avg()) ) ;
        result.setV5Rate(  Math.round ((result.getVolume()-result.getV5Avg() )*100/result.getV5Avg()) ) ;
        result.setPeriod(getPeriod(result.getPriceRate()));
        result.setHighLow(getHighLowType(result.getClosePrice(),result.getC60Avg()));

        result.setOpenRate( Math.round(   (result.getOpenPrice()-result.getPreClosePrice())*1000/result.getPreClosePrice())  );
        result.setHighRate( Math.round(   (result.getHighPrice()-result.getOpenPrice())*1000/result.getPreClosePrice())  );
        result.setLowRate( Math.round(   (result.getLowPrice()-result.getOpenPrice())*1000/result.getPreClosePrice())  );
        result.setCloseRate( Math.round(   (result.getClosePrice()-result.getOpenPrice())*1000/result.getPreClosePrice())  );


        StockDayBean lastStockDayean= (StockDayBean)  hisVolAndPrice.get("lastInfo");
        Long maxSeq = 0L;
        String lastMacdInfo  = null;
        if(lastStockDayean != null){
            maxSeq =  lastStockDayean.getSeq();
        }
        result.setSeq(maxSeq+1);
        setMacdInfo(result,lastStockDayean);
        int jztd = 0;
        float bodyTail = result.getClosePrice()>result.getOpenPrice()?result.getOpenPrice():result.getClosePrice();
        if( result.getPreClosePrice() >0){
            int  jzTail=  Math.round( (bodyTail - result.getLowPrice())*1000/ result.getPreClosePrice());
            if(jzTail>10){
                result.setJzTail(jzTail);
                int  jzBody=  Math.round(  Math.abs(result.getOpenPrice() - result.getClosePrice())*100/ result.getPreClosePrice());
                result.setJzBody(jzBody);
            }

        }

        if(hisVol.size()>=60){
            hisVol.remove(0);
        }

        hisVol.add(result.getVolume());
        if(hisClosePrice.size()>60){
            hisClosePrice.remove(0);
        }
        hisClosePrice.add(Float.parseFloat(String.valueOf(result.getClosePrice())));

        if(hisSwing.size()>60){
            hisSwing.remove(0);
        }
        hisSwing.add(result.getSwing());

        hisVolAndPrice.put("lastInfo",result);

        saveStockInflection(hisVolAndPrice,result);

        return result;
    }


    /**
     * @param bean
     *  recentHorizon
     *        horOperTime
     *        horSeq
     *        horMaxPrice
     *        horMinPrice
     *        horMaxMinRate
     *        horLasDay   横盘持续天数
     *        horType   10  代表 10日 horMaxMinRate<6; 20 代表 20日  horMaxMinRate<10 ，优先判断10
     *        horPrice  最高价和最低价的平均值
     * @param stockSwinglist
     */

    private void setRecentSwingInfo(StockDayBean bean,  List stockSwinglist, Map hisVolAndPrice){
        StockTransverseBean lastStockSwingean  = (StockTransverseBean)  hisVolAndPrice.get("lastStockSwingInfo");
//        float tmpMaxPrice =  lastStockSwingean.getMaxPrice();
//        float tmpMinPrice = lastStockSwingean.getMinPrice();
        int price10Rate =   Math.round( (bean.getMaxC10Price()-bean.getMinC10Price())*1000/bean.getMaxC10Price() );
        int price20Rate =   Math.round( (bean.getMaxC20Price()-bean.getMinC20Price())*1000/bean.getMaxC20Price() );
        int price60Rate =   Math.round( (bean.getMaxC60Price()-bean.getMinC20Price())*1000/bean.getMaxC60Price() );
        String swingType ="";
        int lastDayNum =0 ;//持续时间
        if(price10Rate <60  ){
            swingType = "H";
            lastDayNum = 10;
        }else if( price20Rate<80){
            swingType = "H";
            lastDayNum = 20;
        }
//        if(price60Rate<200 &&  !"H".equals(swingType)){
//            swingType = "Z";
//            lastDayNum = 60;DBdata1
//        }

        if( StringUtils.isNotBlank(swingType) ){
           if(lastStockSwingean !=null && lastStockSwingean.getStockSeq()+2 ==  bean.getSeq()  ){
              boolean updateVol = false;
               boolean updatePrice = false;
               if (bean.getClosePrice() >  lastStockSwingean.getMaxPrice()) {
                   lastStockSwingean.setMaxPrice(bean.getClosePrice());
                   updatePrice= true;
               } else if (bean.getClosePrice() < lastStockSwingean.getMinPrice()) {
                   lastStockSwingean.setMinPrice(bean.getClosePrice());
                   updatePrice= true;
               }
               if(updatePrice){
                   float tmpAvgPrice =  (lastStockSwingean.getMaxPrice()+lastStockSwingean.getMinPrice())/2;
                   lastStockSwingean.setAvgPrice(  (float)(Math.round(tmpAvgPrice*100))/100 );
                   lastStockSwingean.setPriceRate(   Math.round( (lastStockSwingean.getMaxPrice()-lastStockSwingean.getMinPrice())*1000/lastStockSwingean.getMaxPrice() ) );
               }
              lastStockSwingean.setOperTime(bean.getOperTime());
              lastStockSwingean.setStockSeq(bean.getSeq()-1);
              lastStockSwingean.setLastDay(lastStockSwingean.getLastDay()+1);

               if(bean.getVolume()> lastStockSwingean.getMaxVol() ){
                   lastStockSwingean.setMaxVol(bean.getVolume());
                   updateVol =true;
               }else if(bean.getVolume()<lastStockSwingean.getMinVol()){
                   lastStockSwingean.setMinVol(bean.getVolume());
                   updateVol= true;
               }
               if(updateVol){
                   lastStockSwingean.setAvgVol( (lastStockSwingean.getMaxVol()+lastStockSwingean.getMinVol())/2 );
                   lastStockSwingean.setVolRate(   Math.round( (lastStockSwingean.getMaxVol()-lastStockSwingean.getMinVol())*100/lastStockSwingean.getMinVol() )   );
               }
               if(stockSwinglist.size()==0) {  //如果本次横盘信息和以前横盘信息连起来，则代表已经添加到里面，不在更新。
                   stockSwinglist.add(lastStockSwingean);
                   hisVolAndPrice.put("lastStockSwingInfo",lastStockSwingean);
               }
         }else {
               //走到这 代表没有初始的信息，或者 跟上一次信息断开，重新插入新记录
               long swingSeq = 1;
               StockTransverseBean tmpLastStockSwingean  = new StockTransverseBean();
               if(lastStockSwingean!= null){
                   swingSeq = lastStockSwingean.getSwingSeq()+1;
               }
               tmpLastStockSwingean.setSwingSeq(swingSeq);
               tmpLastStockSwingean.setStockCode(bean.getStockCode());
               tmpLastStockSwingean.setOperTime(bean.getOperTime());
               tmpLastStockSwingean.setStockSeq(bean.getSeq()-1);
               tmpLastStockSwingean.setLastDay(lastDayNum);
               tmpLastStockSwingean.setSwingType(swingType);
               if(lastDayNum ==10){
                   tmpLastStockSwingean.setMaxPrice(bean.getMaxC10Price());
                   tmpLastStockSwingean.setMinPrice(bean.getMinC10Price());
                   tmpLastStockSwingean.setMaxVol(bean.getMaxV10Vol());
                   tmpLastStockSwingean.setMinVol(bean.getMinV10Vol());
               }else if(lastDayNum == 20){
                   tmpLastStockSwingean.setMaxPrice(bean.getMaxC20Price());
                   tmpLastStockSwingean.setMinPrice(bean.getMinC20Price());
                   tmpLastStockSwingean.setMaxVol(bean.getMaxV20Vol());
                   tmpLastStockSwingean.setMinVol(bean.getMinV20Vol());
               }else if(lastDayNum == 60){
                   tmpLastStockSwingean.setMaxPrice(bean.getMaxC60Price());
                   tmpLastStockSwingean.setMinPrice(bean.getMinC60Price());
                   tmpLastStockSwingean.setMaxVol(bean.getMaxV60Vol());
                   tmpLastStockSwingean.setMinVol(bean.getMinV60Vol());
               }
               tmpLastStockSwingean.setAvgVol( (tmpLastStockSwingean.getMaxVol()+tmpLastStockSwingean.getMinVol())/2);
               float tmpAvgPrice =  (tmpLastStockSwingean.getMaxPrice()+tmpLastStockSwingean.getMinPrice())/2;
               tmpLastStockSwingean.setAvgPrice(  (float)(Math.round(tmpAvgPrice*100))/100 );
               tmpLastStockSwingean.setPriceRate(Math.round( (tmpLastStockSwingean.getMaxPrice()-tmpLastStockSwingean.getMinPrice())*1000/tmpLastStockSwingean.getMaxPrice() ));
               tmpLastStockSwingean.setVolRate( Math.round( (tmpLastStockSwingean.getMaxVol()-tmpLastStockSwingean.getMinVol())*100/tmpLastStockSwingean.getMinVol() )   );
               stockSwinglist.add(tmpLastStockSwingean);
               hisVolAndPrice.put("lastStockSwingInfo",tmpLastStockSwingean);
           }
        }
    }




    private void putPriceDesc(  StockDayBean bean){
        StringBuffer sbf = new StringBuffer();
        long openRate =  bean.getOpenRate();
//        if(bean.getOpenPrice())

    }


    private void saveStockInflection( Map hisVolAndPrice ,StockDayBean result ){

        StockDayInflectionBean stockDayInflection = (StockDayInflectionBean) hisVolAndPrice.get("currStockDayInflection");
         long priceRate = result.getPriceRate();
         float closePrice = result.getClosePrice();
            if(stockDayInflection == null ){
                stockDayInflection = new StockDayInflectionBean();
                setStockDayInflectionFromStockDay(result, stockDayInflection);
                stockDayInflection.setPreClosePrice(result.getPreClosePrice());
                stockDayInflection.setUpDownType( priceRate>0?1:2); //拐点类型 1上升 ； 2 下降；
                stockDayInflection.setIntervalDay(-1);
                stockDayInflection.setSeq(result.getSeq());
                stockDayInflection.setPreSeq(result.getSeq());
                hisVolAndPrice.put("currStockDayInflection",stockDayInflection);
            }else{

               int swingRate =  Math.round( (closePrice-stockDayInflection.getClosePrice())*1000/stockDayInflection.getClosePrice());

                if(stockDayInflection.getUpDownType() ==1){ //上升
                    if(priceRate>0){
                        if(stockDayInflection.getClosePrice()<closePrice){
                            setStockDayInflectionFromStockDay(result, stockDayInflection);
                        }
                    }else if(priceRate<-20 ||swingRate<-30 ){  //翻转后，出现拐点，  插入拐点入表，更新当前拐点信息


                        stockDayInflection.setIntervalDay( stockDayInflection.getSeq()-stockDayInflection.getPreSeq());
                        stockDayInflection.setSwingRate(Math.round( (stockDayInflection.getClosePrice()-stockDayInflection.getPreClosePrice())*1000/stockDayInflection.getPreClosePrice()));
                        baseDao.insert("com.guoxc.info.dao.stockInflection.insertStockInflection",stockDayInflection);
                           //翻转后，出现拐点，插入表中
                            StockDayInflectionBean  tmpStockDayInflectionBean = new StockDayInflectionBean();
                            setStockDayInflectionFromStockDay(result, tmpStockDayInflectionBean);
                            tmpStockDayInflectionBean.setPreClosePrice(stockDayInflection.getClosePrice());
                            tmpStockDayInflectionBean.setUpDownType(2);
                            tmpStockDayInflectionBean.setIntervalDay(-1);
                            tmpStockDayInflectionBean.setPreSeq(stockDayInflection.getSeq());
                           hisVolAndPrice.put("currStockDayInflection",tmpStockDayInflectionBean);
                    }
                }else{ //原来为下降
                    if(priceRate>20 ||swingRate>30){

                        stockDayInflection.setIntervalDay(stockDayInflection.getSeq()-stockDayInflection.getPreSeq());
                        stockDayInflection.setSwingRate(Math.round( (stockDayInflection.getClosePrice()-stockDayInflection.getPreClosePrice())*1000/stockDayInflection.getPreClosePrice()));
                        baseDao.insert("com.guoxc.info.dao.stockInflection.insertStockInflection",stockDayInflection);

                        StockDayInflectionBean  tmpStockDayInflectionBean = new StockDayInflectionBean();
                        setStockDayInflectionFromStockDay(result, tmpStockDayInflectionBean);
                        tmpStockDayInflectionBean.setPreClosePrice(stockDayInflection.getClosePrice());
                        tmpStockDayInflectionBean.setUpDownType(1);
                        tmpStockDayInflectionBean.setIntervalDay(-1);
                        tmpStockDayInflectionBean.setPreSeq(stockDayInflection.getSeq());
                        hisVolAndPrice.put("currStockDayInflection",tmpStockDayInflectionBean);


                    }else if(priceRate<0){
                        if(closePrice<stockDayInflection.getClosePrice()){
                            setStockDayInflectionFromStockDay(result, stockDayInflection);
                        }
                    }
                }
            }
    }


    public void  putStockDayInflectionNew2DB(File file,Timestamp lastDealDay,StockDayInflectionBean currStockDayInflection) {
        try{
//            Timestamp beginTime = DateUtil.addMonth(lastDealDay,3);
            List dayInflectionList = new ArrayList();
            long  lastConfirmDayInflectionSeq = 0;
            List stockList = new ArrayList();
            //file 示例 SH#600000.txt
            String sqlId =  "com.guoxc.info.dao.stockInflection.insertStockInflectionNewList" ;
            boolean first = false;
            String fileName = file.getName();
            String stockCode = fileName.substring(3,9);
            List  list = FileUtil.readFileXml(file,"gbk");




            for(int i=2; i<list.size();i++){
                String line = (String) list.get(i);
                if(line.contains("日期")){
                    continue; //标题跳过.
                }
                // 标题  日期	    开盘	    最高	    最低	    收盘	    成交量	    成交额
                //2010/01/04	6.69	6.70	6.41	6.42	66191338	1419984128.00

                String[] cols = line.split("\t");
                if(cols.length>=7){
                    if(DateUtil.convertStringToTimestamp(cols[0],"yyyy/MM/dd").after(lastDealDay)){
                        StockDayBean bean =  getBaseStockDayBean(list,i) ;
                        bean.setStockCode(stockCode);
                        stockList.add(bean);

                    }
                }
            }
             if(stockList.size()==0){
                return ;
             }
            if(currStockDayInflection==null && stockList.size()<=6){ //新股前6天，直接跳过。
                return ;
            }

            Map lastDayInflectionMap = new HashMap();
            StockDayInflectionBean lastConfirmDayInflection = null ;
            if(currStockDayInflection !=null){
//                hisDayInflection.put("currStockDayInflection",stockDayInflection);
                Map param = new HashMap();
                param.put("seq",currStockDayInflection.getPreSeq());
                param.put("stockCode",currStockDayInflection.getStockCode());
                String getLastConfirmInflectionSql= "com.guoxc.info.dao.stockInflection.getInflectionNewInfo";
                lastConfirmDayInflection =  (StockDayInflectionBean) baseDao.selectOne(getLastConfirmInflectionSql ,param) ;
                if(lastConfirmDayInflection !=null){
                    lastDayInflectionMap.put("lastConfirmDayInflection",lastConfirmDayInflection);
                    lastDayInflectionMap.put("lastConfirmOffset",0-lastConfirmDayInflection.getIntervalDay());
                    lastDayInflectionMap.put("lastConfirmClosePrice",lastConfirmDayInflection.getClosePrice());
                    lastConfirmDayInflectionSeq = lastConfirmDayInflection.getSeq();

                }
            }
            if(currStockDayInflection==null ){
                StockDayBean bean = (StockDayBean) stockList.get(0);
                currStockDayInflection = new StockDayInflectionBean();
                currStockDayInflection.setStockCode(bean.getStockCode());
                currStockDayInflection.setSeq(1);
                currStockDayInflection.setOperTime(bean.getOperTime());
                currStockDayInflection.setUpDownType(0);
                currStockDayInflection.setIntervalDay(0);
                currStockDayInflection.setClosePrice(bean.getClosePrice());
                lastConfirmDayInflection = new StockDayInflectionBean();
                BeanUtils.copyProperties(currStockDayInflection,lastConfirmDayInflection);
                dayInflectionList.add(lastConfirmDayInflection);
                lastDayInflectionMap.put("lastConfirmDayInflection",lastConfirmDayInflection);
                lastDayInflectionMap.put("lastConfirmOffset",0L);
                lastDayInflectionMap.put("lastConfirmClosePrice",lastConfirmDayInflection.getClosePrice());
                first = true;
            }

            StockDayBean currStockDayBean = new StockDayBean();
            currStockDayBean.setStockCode(currStockDayInflection.getStockCode());
            currStockDayBean.setOperTime(currStockDayInflection.getOperTime());
            currStockDayBean.setClosePrice(currStockDayInflection.getClosePrice());
            stockList.add(0,currStockDayBean);//把当前记录加入最前面，进行统一计算

//            float lastConfirmClosePrice = lastConfirmDayInflection.getClosePrice();
//            int upDownType = lastConfirmDayInflection.getUpDownType(); // 拐点类型 1上升 ； 2 下降；

            for(int i=0;i<stockList.size();i=i+5){
                Map tmpMaxMin = getMaxMinPrice(stockList,i,5);
                putDayInflection2List(tmpMaxMin,stockList,dayInflectionList,lastDayInflectionMap);
           }

            StockDayInflectionBean  finalStockDayInflection = getFinalStockDayInflection(lastDayInflectionMap,stockList);
            if(first){
                dayInflectionList.add(finalStockDayInflection);
            }

            logger.info(stockCode+"dayInflectionList size="+dayInflectionList.size());
            if(dayInflectionList.size()>0){
                StockDayInflectionBean   firstBean =  ((StockDayInflectionBean)dayInflectionList.get(0));
                if(firstBean.getSeq()== lastConfirmDayInflectionSeq){
                    baseDao.update( "com.guoxc.info.dao.stockInflection.updateStockInflectionNew",firstBean);
                    dayInflectionList.remove(0);
                    if(dayInflectionList.size() !=0){
                        baseDao.insert(sqlId,dayInflectionList);
                    }
                }else{
                    baseDao.insert(sqlId,dayInflectionList);
                }
            }

           if(!first){
               baseDao.update( "com.guoxc.info.dao.stockInflection.updateStockInflectionNew",finalStockDayInflection);
           }




//            if(first){
//                StockDayInflectionBean tmpStockDayCurrInflection = (StockDayInflectionBean) hisDayInflection.get("currStockDayInflection");
//                if(tmpStockDayCurrInflection != null){
//                    baseDao.insert("com.guoxc.info.dao.stockInflection.insertStockInflection",tmpStockDayCurrInflection);
//                }
//            }else{
//                StockDayInflectionBean tmpStockDayCurrInflection = (StockDayInflectionBean) hisDayInflection.get("currStockDayInflection");
//                if( tmpStockDayCurrInflection!= null){
//                    baseDao.insert("com.guoxc.info.dao.stockInflection.updateStockInflection",tmpStockDayCurrInflection);
//                }
//            }

        }catch (Exception e){
            logger.error("putStockDataFile2DDB_err",e);
        }
    }



    private StockDayInflectionBean getFinalStockDayInflection(Map lastDayInflectionMap, List stockList){

        StockDayInflectionBean lastConfirmDayInflection = (StockDayInflectionBean) lastDayInflectionMap.get("lastConfirmDayInflection");
        StockDayBean lastStockDay = (StockDayBean) stockList.get(stockList.size() -1);
        StockDayInflectionBean stockDayInflection = new   StockDayInflectionBean();
        stockDayInflection.setStockCode(lastStockDay.getStockCode());
        stockDayInflection.setOperTime(lastStockDay.getOperTime());
        stockDayInflection.setClosePrice(lastStockDay.getClosePrice());

        int upDownType = lastStockDay.getClosePrice()> lastConfirmDayInflection.getClosePrice()?1:2;
        stockDayInflection.setUpDownType(upDownType);
        long lastconfirmOffset = (long) lastDayInflectionMap.get("lastConfirmOffset");
        stockDayInflection.setIntervalDay(stockList.size()-lastconfirmOffset);
        stockDayInflection.setSwingRate(getPriceRate(lastDayInflectionMap,lastStockDay.getClosePrice()));
        stockDayInflection.setPreClosePrice(lastConfirmDayInflection.getClosePrice());
        stockDayInflection.setSeq(9999);
        stockDayInflection.setPreSeq(lastConfirmDayInflection.getSeq());

        return stockDayInflection;
    }



    private void putDayInflection2List( Map tmpMaxMin , List stockList , List stockDayInflectionList, Map lastDayInflectionMap){
        StockDayInflectionBean lastConfirmDayInflection = (StockDayInflectionBean) lastDayInflectionMap.get("lastConfirmDayInflection");
        long lastConfirmOffset = (long) lastDayInflectionMap.get("lastConfirmOffset");
        float lastConfirmClosePrice = lastConfirmDayInflection.getClosePrice();
        int upDownType = lastConfirmDayInflection.getUpDownType();
        int maxOffset = (int) tmpMaxMin.get("maxOffset");
        int minOffset = (int) tmpMaxMin.get("minOffset");
        if(maxOffset != minOffset ){ //如果最高价和最低价是同一天，一定是最后一天。因为价格一样时，maxOffset和minOffset都会后移。 暂不考虑5天价格一样。  出现这种情况则是最后一天,则什么都不处理。只更新当前拐点信息
            float maxPrice = (float) tmpMaxMin.get("maxPrice");
            float minPrice = (float) tmpMaxMin.get("minPrice");
            if(maxOffset>minOffset){  //minoffset 在前， maxOffset 在后
                int minRate = getPriceRate(lastDayInflectionMap,minPrice);
                if(upDownType==0){
                    if(minRate<=-80){ //在上升过程中，低点向下低于8个点，加入到拐点中,同时返回最新的确认拐点。
                         putDayInflection2List(stockList,  lastDayInflectionMap, stockDayInflectionList, minOffset, minRate,2);
                    }
                     //如果上个if满足，也执行该代码，所以该部分不用else
                    int maxRate = getPriceRate(lastDayInflectionMap,maxPrice);
                    if(maxRate>80){
                        putDayInflection2List(stockList,  lastDayInflectionMap, stockDayInflectionList, maxOffset, maxRate,1);
                    }

                }
                else if(upDownType==1){
                    if(minRate<=-80){ //在上升过程中，低点向下低于8个点，加入到拐点中,同时返回最新的确认拐点。
                        putDayInflection2List(stockList,  lastDayInflectionMap, stockDayInflectionList, minOffset, minRate,2);
                        //确认上涨点是否
                        int maxRate = getPriceRate(lastDayInflectionMap,maxPrice);
                        if(maxRate>=80){
                          putDayInflection2List(stockList,  lastDayInflectionMap, stockDayInflectionList, maxOffset, maxRate,1);
                        }
                    }else{
                        int maxRate = getPriceRate(lastDayInflectionMap,maxPrice);
                        if(maxRate>0){
                             updateDayInflection2List(stockList,lastDayInflectionMap,stockDayInflectionList,maxOffset);
                        }
                    }
                }else if(upDownType ==2){
                    if(minRate<0){
                         updateDayInflection2List(stockList,lastDayInflectionMap,stockDayInflectionList,minOffset);
                    }else{
                        int maxRate = getPriceRate(lastDayInflectionMap,maxPrice);
                        if( maxRate>80){
                             putDayInflection2List(stockList,  lastDayInflectionMap, stockDayInflectionList, maxOffset, maxRate,1);
                        }
                    }
                }
            }else { // maxOffset 在前， minoffset 在后 ****************************************8
                int maxRate =  getPriceRate(lastDayInflectionMap,maxPrice);
                if(upDownType==0){
                    if(maxRate>=80){ //在上升过程中，低点向下低于8个点，加入到拐点中,同时返回最新的确认拐点。
                        putDayInflection2List(stockList,  lastDayInflectionMap, stockDayInflectionList, maxOffset, maxRate,1);
                    }
                    //如果上个if满足，也执行该代码，所以该部分不用else
                    int minRate =  getPriceRate(lastDayInflectionMap,minPrice);
                    if(minRate<=-80){
                         putDayInflection2List(stockList,  lastDayInflectionMap, stockDayInflectionList, minOffset, minRate,2);
                    }
                }
                else if(upDownType==1){
                    if(maxRate>0){
                        updateDayInflection2List(stockList,lastDayInflectionMap,stockDayInflectionList,maxOffset);

                    }
                    int minRate =  getPriceRate(lastDayInflectionMap,minPrice);
                    if(minRate<= -80){
                         putDayInflection2List(stockList,  lastDayInflectionMap, stockDayInflectionList, minOffset, minRate,2);
                    }
                }else if(upDownType ==2){
                    if(maxRate>= 80){
                         putDayInflection2List(stockList,  lastDayInflectionMap, stockDayInflectionList, maxOffset, maxRate,1);
                        int minRate  =  getPriceRate(lastDayInflectionMap,minPrice);
                        if(minRate<= -80){
                             putDayInflection2List(stockList,  lastDayInflectionMap, stockDayInflectionList, minOffset, minRate,2);
                        }
                    }else{
                        int minRate = getPriceRate(lastDayInflectionMap,minPrice);
                        if( minRate<0){
                            updateDayInflection2List(stockList,lastDayInflectionMap,stockDayInflectionList,minOffset);
                        }
                    }
                }
            }
        }
    }




    private int getPriceRate( Map lastDayInflectionMap, float price){
       return   Math.round((price- (float) lastDayInflectionMap.get("lastConfirmClosePrice") )*1000/(float) lastDayInflectionMap.get("lastConfirmClosePrice"));
    }


    private void putDayInflection2List(List stockList, Map lastDayInflectionMap,
                                                         List stockDayInflectionList, int offset, int priceRate,int upDownType) {
        StockDayInflectionBean lastConfirmDayInflection = (StockDayInflectionBean) lastDayInflectionMap.get("lastConfirmDayInflection");
        long   lastconfirmOffset = (long) lastDayInflectionMap.get("lastConfirmOffset");
        StockDayBean lastStockDayBean = (StockDayBean) stockList.get(offset);
        StockDayInflectionBean stockDayInflection = new StockDayInflectionBean();
        stockDayInflection.setStockCode(lastStockDayBean.getStockCode());
        stockDayInflection.setOperTime(lastStockDayBean.getOperTime());
        stockDayInflection.setClosePrice(lastStockDayBean.getClosePrice());
        stockDayInflection.setUpDownType(upDownType);
        stockDayInflection.setIntervalDay(offset-lastconfirmOffset);
        stockDayInflection.setSwingRate(priceRate);
        stockDayInflection.setPreClosePrice(lastConfirmDayInflection.getClosePrice());
        stockDayInflection.setSeq(lastConfirmDayInflection.getSeq()+1);
        stockDayInflectionList.add(stockDayInflection);
        lastDayInflectionMap.put("lastConfirmDayInflection",stockDayInflection);
        lastDayInflectionMap.put("lastConfirmClosePrice",lastStockDayBean.getClosePrice());
        lastDayInflectionMap.put("lastConfirmOffset", Long.parseLong(String.valueOf(offset) ));
    }


    private void updateDayInflection2List(List stockList, Map lastDayInflectionMap, List stockDayInflectionList, int offset) {

        StockDayInflectionBean lastConfirmDayInflection = (StockDayInflectionBean) lastDayInflectionMap.get("lastConfirmDayInflection");
        long   lastconfirmOffset = (long) lastDayInflectionMap.get("lastConfirmOffset");

        StockDayInflectionBean tmpLastConfirmDayInflection = null ;
        if(stockDayInflectionList.size()>0){
             tmpLastConfirmDayInflection = (StockDayInflectionBean) stockDayInflectionList.get(stockDayInflectionList.size()-1);
        }else{ //如果 为空，则修改的是数据库查询出来的第一条记录。保存的时候，判断第一条记录的seq，如何和原来一致，则修改。否则，就插入
            tmpLastConfirmDayInflection = lastConfirmDayInflection;
            stockDayInflectionList.add(tmpLastConfirmDayInflection);
        }
        StockDayBean lastStockDayBean = (StockDayBean) stockList.get(offset);
        tmpLastConfirmDayInflection.setOperTime(lastStockDayBean.getOperTime());
        tmpLastConfirmDayInflection.setClosePrice(lastStockDayBean.getClosePrice());

        tmpLastConfirmDayInflection.setIntervalDay(tmpLastConfirmDayInflection.getIntervalDay()+offset-lastconfirmOffset);
        tmpLastConfirmDayInflection.setSwingRate(Math.round((lastStockDayBean.getClosePrice()- tmpLastConfirmDayInflection.getPreClosePrice())*1000/tmpLastConfirmDayInflection.getPreClosePrice()));

        lastDayInflectionMap.put("lastConfirmDayInflection",tmpLastConfirmDayInflection);
        lastDayInflectionMap.put("lastConfirmClosePrice",lastStockDayBean.getClosePrice());
        lastDayInflectionMap.put("lastConfirmOffset",Long.parseLong(String.valueOf(offset)) );
    }


    private  Map   getMaxMinPrice(List stockList, int offset,int  num ){
       Map result  = new HashMap();
         if(stockList!=null&& stockList.size()>0){
               int endNum = stockList.size()>offset+num?offset+num:stockList.size();
               int max = offset;
               int min = offset;
             StockDayBean bean = (StockDayBean) stockList.get(offset);
              float maxPrice = bean.getClosePrice();
              float minPrice = bean.getClosePrice();

               for(int i=offset+1;i<endNum;i++){
                   StockDayBean tmpBean = (StockDayBean) stockList.get(i);
                   if(tmpBean.getClosePrice()>=maxPrice){
                       max = i;
                       maxPrice = tmpBean.getClosePrice();
                   }
                   if(tmpBean.getClosePrice()<=minPrice){
                       min = i;
                       minPrice = tmpBean.getClosePrice();
                   }
               }
             result.put("minOffset",min);
             result.put("maxOffset",max);
             result.put("minPrice",minPrice);
             result.put("maxPrice",maxPrice);

         }

       return result ;
    }


    private void setStockDayInflectionFromStockDay(StockDayBean result, StockDayInflectionBean stockDayInflection) {
        stockDayInflection.setSeq(result.getSeq());
        stockDayInflection.setStockCode(result.getStockCode());
        stockDayInflection.setClosePrice(result.getClosePrice());
        stockDayInflection.setOperTime(result.getOperTime());
    }


    private int getHighLowType(Float closePrice, Float c60Avg ){
        //1 相对低位; 2 相对高位， 相对于60日均线，超过10%为高位，低于10%为低位
        int result =0 ;
       int rate  =   Math.round(   (closePrice-c60Avg)*1000/c60Avg);
        if(rate <-7 ){
           result =1;
       }else if (rate <7) {
           result = 0;
       }else{
            result =2 ;
        }
        return result;

    }

    private int getPeriod(long priceRate ){
       // 1大幅下跌 70以上 ，2，下跌 69-40 ， 3 震荡下帖39-15    4 横盘  -15 到15， 5 震荡 上升15-39     ；6 上升 40-69； 7  大幅上升 70 以上
        int result =4 ;
        if(priceRate<=-70){
            result = 1;
        }else if(priceRate <=-40){
            result =2 ;
        }else if(priceRate <=-15){
            result =3 ;
        }else if(priceRate <=15){
            result =4 ;
        }else if(priceRate <40) {
            result = 5;
        }else if(priceRate <70) {
            result =6;
        }else {
            result = 7;
        }
        return result;

    }


    private void setMacdInfo(StockDayBean bean, StockDayBean lastStockDayean){
        // EMA(c,9), EMA(c,20), EMA(c,9), EMA( EMA(c,9)-EMA(c,20),7)
        float closePrice = bean.getClosePrice();
      String macdInfo = "";
        float diff = 0L;
        float dea = 0L;
        int macdCross = 0;
        if(lastStockDayean == null){
            diff = 0L;
            dea = 0L;
            macdInfo=closePrice+","+closePrice+","+"0.00";
            macdCross = 0 ;
        }else{
           String[]  macdCols = lastStockDayean.getMacdInfo().split(",");

            float emaShort = (float) (Math.round(  ( 2*closePrice/10 + 8*Float.valueOf(macdCols[0])/10 )*10000 )/10000.0);
            float emaLong = (float) (Math.round(  (2*closePrice/21+ 19*Float.valueOf(macdCols[1])/21 )*10000 )/10000.0);
             diff =  emaShort -  emaLong;
             dea = (float) (Math.round(  ( 2*diff/8 + 6*Float.valueOf(macdCols[2])/8 )*10000 )/10000.0);
             macdInfo = emaShort+","+emaLong+","+dea;
            diff = (float) (Math.round ((diff)*100)/100.0);
            dea = (float) (Math.round ((dea)*100)/100.0);
             if( lastStockDayean.getDif()>lastStockDayean.getDea() &&  diff<dea ){
                 macdCross = 1;
             }else if( lastStockDayean.getDif()< lastStockDayean.getDea() &&  diff>dea ){
                 macdCross = 3;
             }else if(diff == dea){
                 macdCross = 2;
             }
        }
        bean.setDif(diff);
        bean.setDea(dea);
        bean.setMacdCross(macdCross);
        bean.setMacdInfo(macdInfo);


    }

    private Long getlastVolAvg(List <Long>list, int num ){
        int i =list.size()-1;
        long tmp = 0l;
        int realNum = 0;
        for(int j=0; j <num;j++){
            if( (i-j) >=0){
                tmp = tmp+ list.get(i-j);
                realNum++;
            }
        }
        return  Long.parseLong( String.valueOf(Math.round(tmp/realNum)));
    }

    private Map<String,Float> getRecentMaxMinPrice(List<Float> hisClosePriceList, int num ){
        Map result = new HashMap();
        int i =hisClosePriceList.size()-1;
        float tmp = 0l;
        int realNum = 0;
        float maxPrice = hisClosePriceList.get(i);
        float minPrice = maxPrice;


        for(int j=1; j <num;j++){
            if( (i-j) >=0){
                tmp =  hisClosePriceList.get(i-j);
                if(tmp>maxPrice){
                    maxPrice = tmp;
                }else if(tmp<minPrice){
                    minPrice = tmp;
                }
                realNum++;
            }
        }
        result.put("maxPrice",maxPrice);
        result.put("minPrice",minPrice);
        return result;
    }


    private Map<String,Long> getRecentMaxMinVol(List <Long>hisVolList, int num ){
        Map result = new HashMap();
        int i =hisVolList.size()-1;
        Long tmp = 0l;
        int realNum = 0;
        Long maxVol = hisVolList.get(i);
        Long minVol = maxVol;


        for(int j=1; j <num;j++){
            if( (i-j) >=0){
                tmp =  hisVolList.get(i-j);
                if(tmp>maxVol){
                    maxVol = tmp;
                }else if(tmp<minVol){
                    minVol = tmp;
                }
                realNum++;
            }
        }
        result.put("maxVol",maxVol);
        result.put("minVol",minVol);
        return result;
    }

    private Long getMinRecentPrice(List <Long>list, int num ){
        int i =list.size()-1;
        long tmp = 0l;
        int realNum = 0;
        for(int j=0; j <num;j++){
            if( (i-j) >=0){
                tmp = tmp+ list.get(i-j);
                realNum++;
            }
        }
        return  Long.parseLong( String.valueOf(Math.round(tmp/realNum)));
    }


    private Float getlastPriceAvg(List <Float>list, int num ){
        int i =list.size()-1;
        Float tmp = 0f;
        int realNum = 0;
        for(int j=0; j <num;j++){
            if( (i-j) >=0){
                tmp = tmp+ list.get(i-j);
                realNum++;
            }
        }
        return   (Float.parseFloat( String.valueOf (Math.round(tmp*100/realNum))))/100;
    }


    private Float getPreClosePrice(List <Float>list ){

        return list.size()>0?  list.get(list.size()-1):0f;
    }



    private void  putStockDataMinuteFile2DB(File file,Timestamp lastDealDay,String type ) {
        try{


            List stockList = new ArrayList();
            //file 示例 SH#600000.txt
            String fileName = file.getName();
            String stockCode = fileName.substring(3,9);
            Map daoVolMap = new HashMap();

            List  list = FileUtil.readFileXml(file,"gbk");
            int number = 0;
            for(int i=list.size()-1; i>1;i--){
                String line = (String) list.get(i);
                if(line.contains("日期")){
                    continue; //标题跳过.
                }
                // 标题  日期	    开盘	    最高	    最低	    收盘	    成交量	    成交额
                //2010/01/04	6.69	6.70	6.41	6.42	66191338	1419984128.00
                String[] cols = line.split("\t");
                if(cols.length>5){
                    if(DateUtil.convertStringToTimestamp(cols[0],"yyyy/MM/dd").after(lastDealDay)){
                        StockDayBean bean =  getStockBeanForMinuteFromLine(daoVolMap,list,i);
                        bean.setStockCode(stockCode);
                        stockList.add(bean);
                    }
                }
                if(stockList.size()>=2000){
                    number= number+stockList.size();
                    logger.info(stockCode+"stockCode size="+number);
                    insertStockMinuteList2DB(stockList,stockCode,type);
//                    baseDao.insert(sqlId,stockList);
//                    stockDao.insertStockMinuteList(stockList);
                    stockList.clear();
                }
            }

            if(stockList.size()>0){
                number= number+stockList.size();
                logger.info(stockCode+"stockList size="+number);
                insertStockMinuteList2DB(stockList,stockCode,type);
//                stockDao.insertStockMinuteList(stockList);
            }
        }catch (Exception e){
            logger.error("putStockDataFile2DDB_err",e);
        }

    }


    private void insertStockMinuteList2DB(List stockList, String stockCode,String type){
        String sqlId = null;
        if("ZS".equals(type)){
            sqlId = "com.guoxc.info.dao.StockDao.insertStockMinuteZSList";
            baseDao.insert(sqlId,stockList);
        }else{
            sqlId= "com.guoxc.info.dao.StockDao.insertStockMinuteList";
            Map param = new HashMap();
            param.put("tableName","t_st_min_"+stockCode);
            param.put("list",stockList);
            baseDao.insert(sqlId,param);

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
                if(cols.length>=8){
                    if(DateUtil.convertStringToTimestamp(cols[0],"yyyy/MM/dd").after(lastDealDay)){
                        StockDayBean bean = new StockDayBean();
                        bean.setStockCode(stockCode);
                        bean.setOperTime(DateUtil.convertStringToTimestamp(cols[0]+" "+cols[1],"yyyy/MM/dd HHmm"));
                        bean.setOpenPrice(Float.valueOf(cols[2]));
                        bean.setHighPrice(Float.valueOf(cols[3]));
                        bean.setLowPrice(Float.valueOf(cols[4]));
                        bean.setClosePrice(Float.valueOf(cols[5]));
                        bean.setVolume(Long.valueOf(cols[6])/100);
                        bean.setTurnover( Long.valueOf(cols[7].substring(0,cols[7].length()-3))/10000);
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
