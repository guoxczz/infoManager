package com.guoxc.info.web.services;

import com.alibaba.fastjson.JSON;
import com.guoxc.info.bean.info.StockDayBean;
import com.guoxc.info.bean.info.StockDayInflectionBean;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Timestamp;
import java.text.ParseException;
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



    public String saveStockMinuteDataByStockCode(String stockCode){

        String last6StockCode =stockCode.substring(2);
        String dealZSDataTimeKey = "STOCK_MINUTE_DEAL_TIME_"+last6StockCode;
        String lastDealDayStr =  (String) baseDao.selectOne(ConstantsInfo.SQLID.GET_CODEVALUE ,dealZSDataTimeKey) ;
        boolean exist = true;
        if(lastDealDayStr == null){
            lastDealDayStr ="2018-1-1";
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



    public void  putStockZSDataFile2DBAnaly(File file,Timestamp lastDealDay,String type) {
        try{
//            Timestamp beginTime = DateUtil.addMonth(lastDealDay,3);
            List stockList = new ArrayList();
            //file 示例 SH#600000.txt
            String sqlId = null;
            sqlId = "com.guoxc.info.dao.StockDao.insertStockDataAnaBatch" ;

            String fileName = file.getName();
            String stockCode = fileName.substring(3,9);
            List  list = FileUtil.readFileXml(file,"gbk");
            Map hisVolAndClosePrice = new HashMap();
            List <Float>hisVolList = new ArrayList();
            List<Float> hisClosePriceList = new ArrayList();
            hisVolAndClosePrice.put("hisVolList",hisVolList);
            hisVolAndClosePrice.put("hisClosePriceList",hisClosePriceList);

            String getMaxSeqSql= "com.guoxc.info.dao.StockDao.getMaxSeqByStockCode";
            StockDayBean lastStockDayean =  (StockDayBean) baseDao.selectOne(getMaxSeqSql ,stockCode) ;
            hisVolAndClosePrice.put("lastInfo",lastStockDayean);


            String getCurrInflectionSql= "com.guoxc.info.dao.stockInflection.getCurrInflection";
            StockDayInflectionBean stockDayInflection =  (StockDayInflectionBean) baseDao.selectOne(getCurrInflectionSql ,stockCode) ;
            hisVolAndClosePrice.put("currStockDayInflection",stockDayInflection);

            for(int i=2; i<list.size();i++){
                String line = (String) list.get(i);
                if(line.contains("日期")){
                    continue; //标题跳过.
                }
                // 标题  日期	    开盘	    最高	    最低	    收盘	    成交量	    成交额
                //2010/01/04	6.69	6.70	6.41	6.42	66191338	1419984128.00

                String[] cols = line.split("\t");
                if(cols.length>=7){
                    if(DateUtil.convertStringToTimestamp(cols[0],"dd/MM/yyyy").after(lastDealDay)){
                        StockDayBean bean =  getStockBeanForDayFromLine(hisVolAndClosePrice,list,i,stockCode);
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


            if(stockDayInflection ==null){
                StockDayInflectionBean tmpStockDayCurrInflection = (StockDayInflectionBean) hisVolAndClosePrice.get("currStockDayInflection");
                if(tmpStockDayCurrInflection != null){
                    baseDao.insert("com.guoxc.info.dao.stockInflection.insertStockInflection",tmpStockDayCurrInflection);
                }
            }else{
                StockDayInflectionBean tmpStockDayCurrInflection = (StockDayInflectionBean) hisVolAndClosePrice.get("currStockDayInflection");
               if( tmpStockDayCurrInflection!= null && stockDayInflection.getSeq()<tmpStockDayCurrInflection.getSeq()){
                   baseDao.insert("com.guoxc.info.dao.stockInflection.updateStockInflection",tmpStockDayCurrInflection);
               }
            }

        }catch (Exception e){
            logger.error("putStockDataFile2DDB_err",e);
        }
    }



    private void  putStockZSDataFile2DDB(File file,Timestamp lastDealDay,String type) {
        try{
            Timestamp beginTime = DateUtil.addMonth(lastDealDay,3);
            List stockList = new ArrayList();
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
            hisVolAndClosePrice.put("hisVolList",hisVolList);
            hisVolAndClosePrice.put("hisClosePriceList",hisClosePriceList);


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
                bean.setOperTime(DateUtil.convertStringToTimestamp(cols[0],"dd/MM/yyyy"));
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
        if(hisVol.size()==0){
            int beginNum = index-60>2? index-60:2;
            for(;beginNum<index;beginNum++){
                String line = (String) list.get(beginNum);
                String[] cols = line.split("\t");
                if(cols.length>5){
                    hisVol.add(Long.valueOf(cols[5])/100);
                    hisClosePrice.add(Float.valueOf(cols[4]));
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
        }else{
            Float  closePrice  = Float.parseFloat( String.valueOf(result.getClosePrice()) ); //Float转换double 数据会变化
            result.setPreClosePrice(closePrice);
            result.setC5Avg(  closePrice );
            result.setC20Avg( closePrice);
            result.setC60Avg( closePrice);
            result.setV5Avg(result.getVolume());
            result.setV20Avg(result.getVolume());
            result.setV60Avg(result.getVolume());
        }
        result.setPriceRate( Math.round(   (result.getClosePrice()-result.getPreClosePrice())*1000/result.getPreClosePrice())  );
        result.setSwing(  Math.round((result.getHighPrice()- result.getLowPrice())*1000/result.getPreClosePrice()));
        result.setV20Rate(  Math.round ((result.getVolume()-result.getV20Avg() )*100/result.getV20Avg()) ) ;
        result.setPeriod(getPeriod(result.getPriceRate()));
        result.setHighLow(getHighLowType(result.getClosePrice(),result.getC60Avg()));

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

        if(hisVol.size()>0){
            hisVol.remove(0);
        }

        hisVol.add(result.getVolume());
        if(hisClosePrice.size()>0){
            hisClosePrice.remove(0);
        }
        hisClosePrice.add(Float.parseFloat(String.valueOf(result.getClosePrice())));

        hisVolAndPrice.put("lastInfo",result);

        saveStockInflection(hisVolAndPrice,result);

        return result;
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
                    if(DateUtil.convertStringToTimestamp(cols[0],"dd/MM/yyyy").after(lastDealDay)){
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
                param.put("seq",currStockDayInflection.getSeq()-1);
                param.put("stockCode",currStockDayInflection.getStockCode());
                String getLastConfirmInflectionSql= "com.guoxc.info.dao.stockInflection.getInflectionNewInfo";
                lastConfirmDayInflection =  (StockDayInflectionBean) baseDao.selectOne(getLastConfirmInflectionSql ,param) ;
                lastDayInflectionMap.put("lastConfirmDayInflection",lastConfirmDayInflection);
                lastDayInflectionMap.put("lastConfirmOffset",0-lastConfirmDayInflection.getIntervalDay());
                lastDayInflectionMap.put("lastConfirmClosePrice",lastConfirmDayInflection.getClosePrice());
                if(lastConfirmDayInflection !=null){
                    lastConfirmDayInflectionSeq = lastConfirmDayInflection.getSeq();

                }
            }


            if(currStockDayInflection==null ){
                StockDayBean bean = (StockDayBean) stockList.get(0);
                currStockDayInflection = new StockDayInflectionBean();
                currStockDayInflection.setSeq(1);
                currStockDayInflection.setOperTime(bean.getOperTime());
                currStockDayInflection.setUpDownType(0);
                currStockDayInflection.setIntervalDay(0);
                currStockDayInflection.setClosePrice(bean.getClosePrice());
                lastConfirmDayInflection = new StockDayInflectionBean();
                BeanUtils.copyProperties(currStockDayInflection,lastConfirmDayInflection);
                dayInflectionList.add(lastConfirmDayInflection);
                lastDayInflectionMap.put("lastConfirmDayInflection",lastConfirmDayInflection);
                lastDayInflectionMap.put("lastConfirmOffset",0);
                lastDayInflectionMap.put("lastConfirmClosePrice",lastConfirmDayInflection.getClosePrice());
                first = true;
            }

            StockDayBean currStockDayBean = new StockDayBean();
            currStockDayBean.setStockCode(currStockDayInflection.getStockCode());
            currStockDayBean.setOperTime(currStockDayInflection.getOperTime());
            currStockDayBean.setClosePrice(currStockDayInflection.getClosePrice());
            stockList.add(currStockDayBean);

            float lastConfirmClosePrice = lastConfirmDayInflection.getClosePrice();
            int upDownType = lastConfirmDayInflection.getUpDownType(); // 拐点类型 1上升 ； 2 下降；

            for(int i=0;i<stockList.size();i=i+5){
                Map tmpMaxMin = getMaxMinPrice(stockList,i,5);
                putDayInflection2List(tmpMaxMin,stockList,dayInflectionList,lastDayInflectionMap);
           }

            StockDayInflectionBean  finalStockDayInflection = getFinalStockDayInflection(lastDayInflectionMap,stockList);
            if(first){
                dayInflectionList.add(finalStockDayInflection);
            }

            logger.info("dayInflectionList size="+dayInflectionList.size());
            if(dayInflectionList.size()>0){
                StockDayInflectionBean   firstBean =  ((StockDayInflectionBean)dayInflectionList.get(0));
                if(firstBean.getSeq()== lastConfirmDayInflectionSeq){
                    baseDao.update( "com.guoxc.info.dao.stockInflection.updateStockInflectionNew",firstBean);
                    dayInflectionList.remove(0);
                    baseDao.insert(sqlId,dayInflectionList);
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

        int upDownType = lastStockDay.getClosePrice()> lastConfirmDayInflection.getClosePrice()?1:2
        stockDayInflection.setUpDownType(upDownType);
        long lastconfirmOffset = (long) lastDayInflectionMap.get("lastConfirmOffset");
        stockDayInflection.setIntervalDay(stockList.size()-lastconfirmOffset);
        stockDayInflection.setSwingRate(getPriceRate(lastDayInflectionMap,lastStockDay.getClosePrice()));
        stockDayInflection.setPreClosePrice(lastConfirmDayInflection.getClosePrice());
        stockDayInflection.setSeq(9999);

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
        lastDayInflectionMap.put("lastConfirmDayInflection",lastConfirmDayInflection);
        lastDayInflectionMap.put("lastConfirmClosePrice",lastStockDayBean.getClosePrice());
        lastDayInflectionMap.put("lastConfirmOffset",offset);
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
        lastDayInflectionMap.put("lastConfirmOffset",offset);
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
                    logger.info(stockCode+"stockList size="+number);
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
