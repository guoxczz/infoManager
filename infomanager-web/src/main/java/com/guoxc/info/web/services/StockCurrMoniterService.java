package com.guoxc.info.web.services;

import com.alibaba.dubbo.common.json.JSON;
import com.guoxc.info.bean.info.StockDayBean;
import com.guoxc.info.dao.StockDao;
import com.guoxc.info.exception.GeneralException;
import com.guoxc.info.utils.DateUtil;
import com.guoxc.info.utils.StringUtil;
import com.guoxc.info.web.common.ConstantsInfo;
import com.guoxc.info.web.dao.BaseDao;
import com.guoxc.info.web.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockCurrMoniterService {
    private final static Logger logger = LoggerFactory.getLogger(StockCurrMoniterService.class);
//    private String lastDayPrice =   "{\"sz002726_lastPrice\":\"10.40\",\"sz002726_avg20\":379855,\"sz002464_lastPrice\":\"5.97\",\"sz002464_avg20\":562744}";
    String getStockCode = ",002726,";
    String currMoniterStockCode = "sz000016,sz000021,sz000030,sz000056,sz000155,sz000158,sz000510,sz000555,sz000563,sz000564,sz000609,sz000613,sz000650,sz000676,sz000700,sz000715,sz000728,sz000786,sz000793,sz000802,sz000851,sz000905,sz000913,sz000917,sz000955,sz000988,sz002062,sz002079,sz002103,sz002112,sz002126,sz002131,sz002182,sz002185,sz002205,sz002222,sz002236,sz002238,sz002282,sz002307,sz002314,sz002326,sz002328,sz002331,sz002334,sz002351,sz002387,sz002388,sz002390,sz002397,sz002400,sz002402,sz002432,sz002464,sz002481,sz002510,sz002532,sz002551,sz002565,sz002600,sz002607,sz002613,sz002614,sz002618,sz002635,sz002640,sz002666,sz002725,sz002740,sz002753,sz002782,sz002813,sz002832,sz002839,sz002856,sz002862,sz002888,sz002913,sz002915,sz002939,sz002945,sz002950,sz002951,sz002953,sz002965,sz002970,sz002971,sz002979,sz002981,sz002983,sz002987,sh600026,sh600037,sh600053,sh600059,sh600075,sh600095,sh600107,sh600109,sh600120,sh600129,sh600155,sh600191,sh600197,sh600198,sh600207,sh600268,sh600318,sh600327,sh600360,sh600446,sh600532,sh600552,sh600559,sh600571,sh600604,sh600621,sh600641,sh600657,sh600667,sh600695,sh600754,sh600789,sh600834,sh600838,sh600864,sh600884,sh600909,sh600918,sh600986,sh601101,sh601236,sh601377,sh601696,sh601789,sh601990,sh603003,sh603005,sh603012,sh603013,sh603093,sh603108,sh603109,sh603138,sh603197,sh603212,sh603283,sh603297,sh603383,sh603439,sh603559,sh603595,sh603598,sh603608,sh603665,sh603682,sh603685,sh603687,sh603690,sh603696,sh603697,sh603757,sh603787,sh603815,sh603825,sh603866,sh603893,sh603917,sh603919,sh603929,sh603936,sh603977,sh605168";
    private Map stockCodeMap = new HashMap();
    private Map alertMap = new HashMap();
    private Map<String , List> hisMap = new HashMap();
    private Map<String , Map> staticMap = new HashMap();
    @Autowired
    private StockDao stockDao  ;
    @Autowired
    private BaseDao baseDao;

    @Autowired
    private BsStaticDataService bsStaticDataService;

    private  Map cfgParam = null;

   public  String moniterStock() {
        String result = null;
        try {
            init();
           String stockCodes = currMoniterStockCode; //sz000858,sh603919

            if(StringUtil.isNotBlank(stockCodes)){
                if(stockCodes.indexOf(",")>-1){
                    String[] stockCodeAndTypeArray =stockCodes.split(",");
                    for(String tockCodeAndType:stockCodeAndTypeArray)
                        moniterStock(tockCodeAndType);
                }else{
                    moniterStock(stockCodes);
                }
            }

        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }
        return result;
    }

    private boolean moniterStock(String stockCodes) throws GeneralException, ParseException {

       try{

           String response =    HttpUtil.doHttpPost("http://qt.gtimg.cn/q="+stockCodes,"");
           //v_sh603919="1~金徽酒~603919~14.31~14.40~14.41~36290~20105~16144~14.31~218~14.30~185~14.29~37~14.28~66~14.27~20~14.32~68~14.34~75~14.35~15~14.36~51~14.39~20~15:00:03/14.31/376/S/538628/22775|14:56:53/14.31/2/S/2862/22461|14:56:49/14.31/8/B/11448/22456|14:56:47/14.31/2/B/2862/22452|14:56:45/14.31/4/S/5724/22448|14:56:40/14.31/13/B/18601/22442~20200410153003~-0.09~-0.62~14.72~14.28~14.31/36290/52452445~36290~5245~1.00~20.63~~14.72~14.28~3.06~52.09~55.84~2.20~15.84~12.96~0.73~297~14.45~20.63~20.63~~~1.13~5245.2445~0.0000~0~ ~GP-A~-19.20~~1.58~10.65~8.51"
           if(StringUtil.isNotBlank(response)){
               String[] stockInfos = response.split(";\n");
               for(String stockInfoStr : stockInfos){
//                  stockInfoStr = stockInfoStr.replace("\"","");
                   String[] stockInfoCols = stockInfoStr.split("~");
                   //15:00:03/14.31/376/S/538628/22775|14:56:53/14.31/2/S/2862/22461|14:56:49/14.31/8/B/11448/22456|14:56:47/14.31/2/B/2862/22452|14:56:45/14.31/4/S/5724/22448|14:56:40/14.31/13/B/18601/22442~20200410153003
                   String detailStr = stockInfoCols[29];
                   String stockCode = stockInfoCols[2];
                   Float currPrice = Float.parseFloat(stockInfoCols[3]);
                   String   priceRate =   stockInfoCols[32];
                  Float   priceRateFloat =   Float.parseFloat(stockInfoCols[32]);
                   if(priceRateFloat <-2){
                       logger.info(" "+stockCode+" "+stockInfoCols[1]+" "+currPrice + " "+priceRate+"‰"+" " );
                   }

               }

           }

       }catch (Exception e) {
           logger.info("err1", e);
           return false;
       }

        return false;
    }

    private Integer getPriceRate(String stockCodes, Float currPrice) {
        String lastPriceStr =(String)cfgParam.get(stockCodes+"_lastPrice");
        Integer priceRate = null;
        if( lastPriceStr != null){
           Float lastPrice = Float.parseFloat(lastPriceStr);
            priceRate = Math.round( (currPrice-  lastPrice)*1000/lastPrice);
        }
        return priceRate;
    }


    private Integer getVolRate(String stockCodes, Integer vol) {
        Long lastvol =(Long)cfgParam.get(stockCodes+"_avg20");
        Integer volRate = null;
        if( lastvol != null){
            volRate = Math.round( vol*100/lastvol);
        }
        return volRate;
    }

    private void moniterLineAlert(String stockCode, Float currPrice) throws GeneralException {
        String upAlertPriceStr = (String) cfgParam.get("UPALERT"+ stockCode);
        if(StringUtil.isNotBlank( upAlertPriceStr)){
            Timestamp alertTime =   (Timestamp) cfgParam.get(stockCode+ upAlertPriceStr.toString());
            if(alertTime == null){
                Float upAlertPrice = Float.parseFloat(upAlertPriceStr);
                if(upAlertPrice<=currPrice){
                    logger.warn("*********到达告警上线 " +stockCode+" "+currPrice);
                    playSound(stockCode,"up");
                    cfgParam.put(stockCode+ upAlertPriceStr.toString(), DateUtil.getCurrentDate());
                }
            }
        }

        String downAlertPriceStr = (String) cfgParam.get("DOWNALERT"+ stockCode);
        if(StringUtil.isNotBlank( downAlertPriceStr)){
            Timestamp  alertTime =   (Timestamp) cfgParam.get(stockCode+ downAlertPriceStr.toString());
            if(alertTime == null){
                Float downAlertPrice = Float.parseFloat(downAlertPriceStr);
                if(downAlertPrice>=currPrice){
                    logger.warn("*********到达告警下线 " +stockCode+" "+currPrice);
                    playSound(stockCode,"down");
                    cfgParam.put(stockCode+ downAlertPriceStr.toString(), DateUtil.getCurrentDate());
                }
            }
        }
    }


    private void detailAlert( List list,String stockCode) throws GeneralException{

        Timestamp  alertTime = (Timestamp) cfgParam.get("ALERTTIME"+stockCode);
        Timestamp currTime =  DateUtil.getCurrentDate();

        if(alertTime!= null && alertTime.after(currTime)){
            return ;
        }
         int size = list.size();
        if(list.size()<5){
            return ;
        }
        int length = list.size()>20?20:list.size();
        int maxPriceOffset=list.size()-1;
        int minPriceOffset = list.size()-1;

          StockDayBean maxBean =  (StockDayBean) list.get(size-1);
         StockDayBean minBean = (StockDayBean) list.get(size-1);
         Float currPrice = maxBean.getClosePrice();
        for(int i=1; i<length-1;i++){
            StockDayBean tmpBean  = (StockDayBean) list.get(size-1-i);
            if(tmpBean.getClosePrice()>maxBean.getClosePrice()){
                maxBean =  tmpBean;
                maxPriceOffset = size-1-i;
            }
            if(tmpBean.getClosePrice() < minBean.getClosePrice()){
                minBean = tmpBean;
                minPriceOffset= size-1-i;
            }
        }

        logger.info("swingrate="+(maxBean.getClosePrice()-minBean.getClosePrice())*100 /minBean.getClosePrice()+" "+currPrice);

        if( (maxBean.getClosePrice()-minBean.getClosePrice())*100 /minBean.getClosePrice() >0.5){

                 if(maxPriceOffset > minPriceOffset){
                     logger.info("*********突增 " +stockCode+" "+minBean.getClosePrice()+" "+maxBean.getClosePrice()+" "+currPrice);
                     playSound(stockCode,"up");
                 }else{
                     logger.info("*********突降 " +stockCode+" "+maxBean.getClosePrice()+" "+minBean.getClosePrice()+" "+currPrice);
                     playSound(stockCode,"down");
                 }

            try {
                cfgParam.put("ALERTTIME"+stockCode,DateUtil.addMinute(currTime,1));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    private void detailStaticAlert( List list,String stockCode,Map stockStaticMap) throws GeneralException{

        Timestamp  alertTime = (Timestamp) cfgParam.get("STATICTIME"+stockCode);
        Timestamp currTime =  DateUtil.getCurrentDate();
        if(alertTime!= null && alertTime.after(currTime)){
            return ;
        }
        int size = list.size();
        if(list.size()<20){
            return ;
        }
        int length = list.size()>20?20:list.size();
        int sellCount = 0;
        long sellVolSum = 0;
        int buyCount = 0;
        long buyVolSum = 0;
        for(int i=1; i<length-1;i++){
            StockDayBean tmpBean  = (StockDayBean) list.get(size-1-i);
            if("B".equals(tmpBean.getMacdInfo())){
                buyCount++;
                buyVolSum = buyVolSum + tmpBean.getVolume();
            }else if("S".equals(tmpBean.getMacdInfo())){
                sellCount++;
                sellVolSum = sellVolSum + tmpBean.getVolume();
            }
        }

        String day = DateUtil.getFormattedDate(currTime,"yyyyMMdd");
        Long v1SellCount =  getFloatFromMap(stockStaticMap,stockCode+"_"+day+"V1_S_NUM");
        Long v1SellSumVol = getFloatFromMap(stockStaticMap,stockCode+"_"+day+"V1_S_SUMVOL");
        Long v1BuyCount =  getFloatFromMap(stockStaticMap,stockCode+"_"+day+"V1_B_NUM");
        Long v1BuySumVol =  getFloatFromMap(stockStaticMap,stockCode+"_"+day+"V1_B_SUMVOL");

        Long v2SellCount =  getFloatFromMap(stockStaticMap,stockCode+"_"+day+"V2_S_NUM");
        Long v2SellSumVol =  getFloatFromMap(stockStaticMap,stockCode+"_"+day+"V2_S_SUMVOL");
        Long v2BuyCount =  getFloatFromMap(stockStaticMap,stockCode+"_"+day+"V2_B_NUM");
        Long v2BuySumVol =  getFloatFromMap(stockStaticMap,stockCode+"_"+day+"V2_B_SUMVOL");



        logger.info("#####static \n" +stockCode+" recent b：s="+ buyCount+"："+sellCount+"  vol:"+buyVolSum+":"+sellVolSum+" "+"\n "+
                "tolstatic v1 b:s="+v1BuyCount+":"+v1SellCount+"  vol:"+v1BuySumVol+":"+v1SellSumVol+" "+"\n"+
                "tolstatic v2 b:s="+v2BuyCount+":"+v2SellCount+" vol:"+v2BuySumVol+":"+v2SellSumVol+" ");

        try {
            cfgParam.put("STATICTIME"+stockCode,DateUtil.addMinute(currTime,1));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private Long getFloatFromMap(Map stockStaticMap, String key){
        Long result = null;
       if(stockStaticMap != null){
           result =  (Long)stockStaticMap.get(key);
       }
       if(result == null){
           result = 0l;
       }
        return result;
    }


    private void playSound(String stockCode,String type) throws GeneralException {

       String fileName = "D:/ffmpeg/file/smsdown.mp3";
       if("up".equals(type)){
           fileName ="D:/ffmpeg/file/smsup.mp3";
       }
        Runtime rt = Runtime.getRuntime();
        Process p = null;
        try {
            p = rt.exec(" cmd.exe  /c start wmplayer "+fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private  void init(){

       try {
           if (cfgParam == null) {
               String moniterCfg = "STOCK_MONITER_CFG";

               String moniterCfgStr = (String) bsStaticDataService.selectOne(ConstantsInfo.SQLID.GET_CODEVALUE, moniterCfg);
               if (StringUtil.isNotBlank(moniterCfgStr)) {
                   cfgParam = JSON.parse(moniterCfgStr, new HashMap().getClass());

               }
           }
       }catch(Exception e){
            logger.error("init moniterStock error",e);
       }

    }


     private List getAddDetailInfos(String stockCodeAndType,float lastClosePrice) throws ParseException {
       List list = new ArrayList();
         try {
             String response =    HttpUtil.doHttpPost("http://qt.gtimg.cn/q="+stockCodeAndType,"");
             if(StringUtil.isNotBlank(response)){
                 String[] stockInfoCols = response.split("~");
                 String detailStr = stockInfoCols[29];
                 String stockCode = stockInfoCols[2];
                 Float currPrice = Float.parseFloat(stockInfoCols[3]);

                 String[] details =   detailStr.split("\\|");
                 for(int i=details.length-1;i>=0;i--) {
                     String[] detail = details[i].split("/");
                     StockDayBean bean = new StockDayBean();
                     bean.setStockName(stockInfoCols[1]);
                     bean.setStockCode(stockInfoCols[2]);
                     bean.setV5Avg( Long.parseLong(stockInfoCols[6])); // 当前成交量保存在5日均量
                     String day =  stockInfoCols[30].substring(0,8);
                     String time =stockInfoCols[30].substring(0,8)+""+detail[0].replace(":","");
                     bean.setOperTime(DateUtil.convertStringToTimestamp(time,"yyyyMMddHHmmss"));
                     bean.setClosePrice(Float.parseFloat(detail[1]));
                     Long vol =  Long.parseLong(detail[2]);
                     bean.setVolume(vol);

                     String sellType = detail[3];
                     bean.setMacdInfo(sellType);
                     if(lastClosePrice >0){
                         bean.setvDif(bean.getClosePrice()-lastClosePrice);
                         bean.setSwing(  Math.round(   (bean.getClosePrice()-lastClosePrice)*1000/bean.getClosePrice()));
                     }
                     list.add(bean);
                 }

             }


         } catch (GeneralException e) {
             logger.error("getAddDetailInfos error",e);
         }


         return  list;
     }




    public  void saveStockFenbiDetail()  {
        String dealTimeKey = "STOCK_DEAL_TIME_STOCK_FENBI";
        try{



        String lastDealDayStr =  (String) bsStaticDataService.selectOne(ConstantsInfo.SQLID.GET_CODEVALUE ,dealTimeKey) ;
        if (lastDealDayStr != null){
            Timestamp lastDealDay = DateUtil.convertStringToTimestamp(lastDealDayStr, "yyyy-MM-dd");
            if (DateUtil.getCurrentDate().after(lastDealDay)){

                String fenbiStockCodeStr =  (String) bsStaticDataService.selectOne(ConstantsInfo.SQLID.GET_CODEVALUE ,"GET_FENBI_STOCKCODE") ;
                if(StringUtil.isNotBlank(fenbiStockCodeStr)){
                    String[] fenbiStockCodes = fenbiStockCodeStr.split(",");
                    for(String stockCode  :fenbiStockCodes){
                    List list =    getAddDetailInfos(stockCode, null);
                    if(list.size()>0){
                        baseDao.insert("com.guoxc.info.dao.StockMinute.insertStockMinuteDetList",list);
                      }
                    }
                }

            }

            bsStaticDataService.updateDealTime(dealTimeKey);
        }

        }catch (Exception e){
            logger.error("saveStockFenbiDetail error",e);
        }
        logger.info("saveStockFenbiDetail success");

    }




    private List getAddDetailInfos(String stockCodeAndType, Float preClosePrice ) throws ParseException {
        List list = new ArrayList();
        try {
            String url = "https://vip.stock.finance.sina.com.cn/quotes_service/view/CN_TransListV2.php?symbol="+stockCodeAndType+"&rn="+System.currentTimeMillis();
           String dateTime = DateUtil.getCurrDate("yyyy-MM-dd");
            Timestamp currDay = DateUtil.getTimestamp(dateTime,"yyyy-MM-dd");
            String response =    HttpUtil.doHttpPost(url,"");
            //var trade_item_list = new Array(); trade_item_list[0] = new Array('15:00:01', '50300', '34.920', 'UP'); trade_item_list[1] = new Array('14:56:57', '4800', '34.900', 'DOWN'); trade_item_list[2] = new Array('14:56:54', '700', '34.920', 'UP'); trade_item_list[3] = new Array('14:56:51', '2700', '34.920', 'UP'); var trade_INVOL_OUTVOL=[2570792.5,1961653.5];
            if(StringUtil.isNotBlank(response)){
                String[] tmpStockInfoCols = response.split(";");
                for(int i=tmpStockInfoCols.length-3; i>0;i--){  //去掉第一个和最后2个， 倒着处理
                   String tockInfoColStr = tmpStockInfoCols[i].substring( tmpStockInfoCols[i].indexOf("(")+1).replace(")","").replace("'","").replace(" ","");
                   String[] stockInfoCols =tockInfoColStr.split(",");
                    StockDayBean bean = new StockDayBean();
                    bean.setStockCode(stockCodeAndType.substring(2)); //sh600001 截取后6位
                    bean.setOperTime(DateUtil.getTimestamp(dateTime+" "+stockInfoCols[0],"yyyy-MM-dd HH:mm:ss"));
                    bean.setVolume(Long.parseLong(stockInfoCols[1])/100);
                    bean.setClosePrice(Float.parseFloat(stockInfoCols[2]));
                    String sellType = "";
                    if("UP".equals(stockInfoCols[3])){
                        sellType = "B";
                    }else if("DOWN".equals(stockInfoCols[3])){
                        sellType = "S";
                    }
                    bean.setMacdInfo(sellType);
                    if(preClosePrice != null){
                        bean.setDif(bean.getClosePrice()-preClosePrice);
                        bean.setSwing(  Math.round((bean.getClosePrice()-preClosePrice)*1000/preClosePrice ) );
                    }
                    preClosePrice = bean.getClosePrice();
                    list.add(bean);

                }
            }
        } catch (GeneralException e) {
            logger.error("getAddDetailInfos error",e);
        }


        return  list;
    }







}
