package com.guoxc.info.web.services;

import com.alibaba.dubbo.common.json.JSON;
import com.guoxc.info.bean.info.StockDayBean;
import com.guoxc.info.dao.StockDao;
import com.guoxc.info.exception.GeneralException;
import com.guoxc.info.utils.DateUtil;
import com.guoxc.info.utils.FileUtil;
import com.guoxc.info.utils.StringUtil;
import com.guoxc.info.web.common.ConstantsInfo;
import com.guoxc.info.web.common.StockCalucate;
import com.guoxc.info.web.dao.BaseDao;
import com.guoxc.info.web.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockMoniterService {
    private final static Logger logger = LoggerFactory.getLogger(StockMoniterService.class);
    private String lastDayPrice =   "{\"stockCodes\":\"sh603929,sz002779,sh600171,sh603893,sz002153,sz002931,sz002835,sh603690\",\"sh600171_avg20\":105380,\"sh603929_avg20\":28947,\"sz002050_avg20\":383850,\"sz002153_avg20\":29943,\"sh601999_lastPrice\":\"1\",\"sh603893_avg20\":603893,\"sz002731_lastPrice\":\"1\",\"sz002906_avg20\":57800,\"sz002464_lastPrice\":\"1\",\"sz002464_avg20\":562744}";
    String careStockCode = ",002779,603929";
    private Map stockCodeMap = new HashMap();
    private Map alertMap = new HashMap();
    private Map<String , List> hisMap = new HashMap();
    private Map<String , Map> staticMap = new HashMap();
    private static Map  lastMap = new HashMap();
    private static Map  detailInfo = new HashMap();
    SimpleDateFormat sdf = new SimpleDateFormat("HH");
    @Autowired
    private StockDao stockDao  ;
    @Autowired
    private BaseDao baseDao;

    @Autowired
    private BsStaticDataService bsStaticDataService;

    private  Map cfgParam = null;
    private  Map transUpCfgParam = null;

   public  String moniterStock() {
        String result = null;
        try {
            init();
           String stockCodes = (String) cfgParam.get("stockCodes"); //sz000858,sh603919
//            if(stockCodes.indexOf(",")>0){
//                String[] stockCodeArray =stockCodes.split(",");
//               for(String stockCode: stockCodeArray){
//
//
//               }
//
//            }
            if(StringUtil.isNotBlank(stockCodes)){
                if(stockCodes.indexOf(",")>-1){
                    String[] stockCodeAndTypeArray =stockCodes.split(",");
                    for(String tockCodeAndType:stockCodeAndTypeArray){
                        moniterStock(tockCodeAndType);
                    }
                    logger.info("-------------");
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



    public  String moniterStockTranservesUp() {
        String result = null;
        try {

           List list = getTransUpMonitorStockList();
            if(list.size()>0){
                init(list);
            }
            logger.info("*****************minute*************begin");
           for(int i=0;i<list.size();i++){
                String stockCode = (String)list.get(i);
               moniterTransUpStock(stockCode);
           }
            logger.info("*****************minute*************end");
        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }
        return result;
    }


    private boolean moniterStock(String stockCodes) throws GeneralException, ParseException {

       try{

           //0: 未知; 1: 股票名字; 2: 股票代码; 3: 当前价格; 4: 昨收; 5: 今开; 6: 成交量（手）; 7: 外盘; 8: 内盘; 9: 买一;10: 买一量（手）;11-18: 买二 买五;19: 卖一;20: 卖一量;21-28: 卖二 卖五;29: 最近逐笔成交;30: 时间;31: 涨跌;32: 涨跌%;33: 最高;34: 最低;35: 价格/成交量（手）/成交额;36: 成交量（手）;37: 成交额（万）;38: 换手率;39: 市盈率;40:;41: 最高;42: 最低;43: 振幅;44: 流通市值;45: 总市值;46: 市净率;47: 涨停价;48: 跌停价;
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

                   putChangeDetailInfo(stockCodes,detailInfo ,stockInfoCols);
                   if(StringUtils.isBlank(detailStr)){
                       Integer priceRate = getPriceRate(stockInfoCols[4], currPrice);
                       String priInfo = careStockCode.indexOf(stockCode)>-1 ?"get ":"";
                      Integer volRate6s =  getVolRateRecent6s(stockCodes,Integer.parseInt(stockInfoCols[6]));
                      String tip = "";
                      if( volRate6s != null && volRate6s >6){
                          tip = "****";
                      }

                      logger.info(priInfo+stockInfoCols[30].substring(8)+" "+stockCode+" "+currPrice + " "+priceRate+"‰"+" "+getVolRate(stockCodes,Integer.parseInt(stockInfoCols[6]) ) +" 6s "+tip+volRate6s+" " +getDifPriceRate6s(stockCode,priceRate) );
                       return true;
                   }

//                   detailAlert(stockCodes, stockInfoCols, detailStr, stockCode, currPrice);
               }

           }

       }catch (Exception e) {
           logger.info("err1", e);
           return false;
       }
        return false;
    }

    private void putChangeDetailInfo(String code ,Map detailInfo, String[] stockInfoCols){

        List list = (List) detailInfo.get(code);
        if(list == null){
            list = new ArrayList();
            detailInfo.put(code,list);
        }
        String time = (String) detailInfo.get(code+"lastTime");

        if(time== null){
            time = stockInfoCols[30];
            detailInfo.put(code+"lastTime",time);
        }else{
            if(time.equals(stockInfoCols[30])){
                return ;
            }
        }

       if(stockInfoCols.length>31){
           StringBuilder sb = new StringBuilder();
           for(int i=2;i<31;i++){
               sb.append(stockInfoCols[i]).append("~");
           }
           dealDetail(code.substring(2), detailInfo, sb.toString());

           detailInfo.put(code+"lastInfo",sb.toString());
           list.add(sb.toString());


       }

       if(list.size()>0){
            if(list.size() %10 ==0  || "15".equals(sdf.format(System.currentTimeMillis())) ){
                writeDetail(code, list);
            }

       }

    }



    private void dealDetail(String code, Map detailInfo,String stockeDetaiStr) {


        if(careStockCode.indexOf(code)>-1){


            String[] stockInfoCols = stockeDetaiStr.split("~");
            Long buyVol = Long.valueOf(stockInfoCols[5]);
            Long selVol =  Long.valueOf(stockInfoCols[6]);


            String lastInfoStr = (String) detailInfo.get(code+"lastInfo");
            if(lastInfoStr != null){
                String[] lastInfos = lastInfoStr.split("~");
                Long diffBuyVol = buyVol- Long.valueOf(lastInfos[5]);
                long diffSellVol = selVol - Long.valueOf(lastInfos[6]);


                if(stockInfoCols[28].substring(8,11).equals("092")){ //9点20到9点30之间 ，不计算
                    detailInfo.put(code+"lastInfo", stockeDetaiStr);
                    return ;
                }

                if(stockInfoCols[28].substring(8,10).equals("15")){ //15点后不记录
                    int priceRate =  getPriceRate(lastInfos[1],stockInfoCols[1]);
                    if(priceRate>10 || priceRate<-10){
                        writeDetailWarn(code,"尾盘变化 "  +priceRate+"‰");
                    }
                    detailInfo.put(code+"lastInfo", null);
                    return ;
                }


               String result =  StockCalucate.detailCompare(lastInfoStr,stockeDetaiStr, detailInfo);
                if(StringUtils.isNotBlank(result)){
                    writeDetailWarn(code,result);
                }
            }

            if(!stockInfoCols[28].substring(8,10).equals("15") && !stockInfoCols[28].substring(8,12).equals("0925") ){
                detailInfo.put(code+"lastInfo",stockeDetaiStr);
            }
        }
    }



    private void writeDetail(String code, List <String>list){
        try {

            File file = new File("E:\\stock\\data\\dayDetail\\" +DateUtil.getCurrDate("yyyyMMdd"));
            if(!file.exists()){
                file.mkdir();
            }

            RandomAccessFile randomAccessFile = new RandomAccessFile(file.getAbsoluteFile()+"\\"+code+".txt","rw");
            randomAccessFile.seek(randomAccessFile.length());
            for(String line : list){
                randomAccessFile.writeBytes(line+"\n");
            }
            randomAccessFile.close();
            list.clear();
        }  catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void writeDetailWarn(String code, String msg){
        try {

            File file = new File("E:\\stock\\data\\dayDetail\\" +DateUtil.getCurrDate("yyyyMMdd"));
            if(!file.exists()){
                file.mkdir();
            }

            RandomAccessFile randomAccessFile = new RandomAccessFile(file.getAbsoluteFile()+"\\"+code+"warn.txt","rw");
            randomAccessFile.seek(randomAccessFile.length());
             randomAccessFile.writeBytes(msg+"\n");
            randomAccessFile.close();
        }  catch (Exception e) {
            e.printStackTrace();
        }


    }


    private boolean moniterTransUpStock(String stockCodes) throws GeneralException, ParseException {

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
                    if(StringUtils.isBlank(detailStr)){
                        Integer priceRate = getPriceRate(stockInfoCols[4], currPrice);
                        String priInfo = careStockCode.indexOf(stockCode)>-1 ?"get ":"";
                        Integer volRate6s =  getTransUpVolRateRecent6s(stockCodes,Integer.parseInt(stockInfoCols[6]));
                        String tip = "";
                        if( volRate6s != null && volRate6s >6){
                            tip = "****";
                        }
                        if(priceRate>60 ||priceRate<-60){
                            logger.info(priInfo+stockInfoCols[30].substring(8)+" "+stockCode+" "+currPrice + " "+priceRate+"‰"+" "+getTransUpVolRate(stockCodes,Integer.parseInt(stockInfoCols[6]) ) +" 1min "+tip+volRate6s );
                        }
                        return true;
                    }

//                   detailAlert(stockCodes, stockInfoCols, detailStr, stockCode, currPrice);
                }

            }

        }catch (Exception e) {
            logger.info("err1 "+stockCodes, e);
            return false;
        }
        return false;
    }

//    private void detailAlert(String stockCodes, String[] stockInfoCols, String detailStr, String stockCode, Float currPrice) throws ParseException, GeneralException {
//        String[] details =   detailStr.split("\\|");
//        List list = hisMap.get(stockCode);
//        if(list == null){
//            list =  new ArrayList();
//            hisMap.put(stockCode,list);
//        }
//
//        Map stockStaticMap = staticMap.get(stockCode);
//        if(stockStaticMap == null){
//            stockStaticMap = new HashMap();
//            staticMap.put(stockCode,stockStaticMap);
//        }
//        Long stockMoniterVol1 =  (Long)cfgParam.get(stockCode+"MONITER_VOL1");
//        Long stockMoniterVol2 =  (Long)cfgParam.get(stockCode+"MONITER_VOL2");
//        Long stockMoniterBigVol =  (Long)cfgParam.get(stockCode+"BIG_VOL");
//        List addList = new ArrayList();
//
//        for(int i=details.length-1;i>=0;i--){
//            String[] detail = details[i].split("/");
//            StockDayBean bean = new StockDayBean();
//            bean.setStockName(stockInfoCols[1]);
//            bean.setStockCode(stockInfoCols[2]);
//            bean.setV5Avg( Long.parseLong(stockInfoCols[6])); // 当前成交量保存在5日均量
//            String day =  stockInfoCols[30].substring(0,8);
//            String time =stockInfoCols[30].substring(0,8)+""+detail[0].replace(":","");
//            bean.setOperTime(DateUtil.convertStringToTimestamp(time,"yyyyMMddHHmmss"));
//            bean.setClosePrice(Float.parseFloat(detail[1]));
//            Long vol =  Long.parseLong(detail[2]);
//            bean.setVolume(vol);
//
//            if(list.size()>0){
//                StockDayBean tmp = (StockDayBean) list.get(list.size()-1);
//                bean.setvDif(tmp.getClosePrice()-bean.getClosePrice());
//                if(bean.getClosePrice()==tmp.getClosePrice()){
//                    bean.setSwing(  Math.round(   (bean.getClosePrice()-tmp.getClosePrice())*10000/bean.getClosePrice()));
//                }
//            }
//            String sellType = detail[3];
//            bean.setMacdInfo(sellType);
//            if(!stockCodeMap.containsKey(bean.getStockCode()+time)){
//                Integer priceRate = getPriceRate(stockInfoCols[4], bean.getClosePrice());
//                logger.info(bean.getStockCode()+" "+ DateUtil.getFormattedDate(bean.getOperTime(),"yyyy-MM-dd HH:mm:ss")+" "+bean.getClosePrice()+" "+bean.getVolume()+" "+bean.getMacdInfo() +" "+priceRate+"‰"+" "+getVolRate(stockCodes,Integer.parseInt(stockInfoCols[6])));
//                stockCodeMap.put(bean.getStockCode()+time,list.size());
//                list.add(bean);
//                addList.add(bean);
//
//                if(vol>=stockMoniterVol1){
//                    Long moniterNum = getFloatFromMap(stockStaticMap,stockCode+"_"+day+"V1_"+sellType+"_NUM");
//                    Long sumVol =  getFloatFromMap(stockStaticMap,stockCode+"_"+day+"V1_"+sellType+"_SUMVOL");
//                    moniterNum ++;
//                    stockStaticMap.put(stockCode+"_"+day+"V1_"+sellType+"_NUM" , moniterNum );
//                    stockStaticMap.put(stockCode+"_"+day+"V1_"+sellType+"_SUMVOL" ,sumVol+vol);
//                }else{
//                    if(vol>=stockMoniterVol2 ){
//                        Long moniterNum =  getFloatFromMap(stockStaticMap,stockCode+"_"+day+"V2_"+sellType+"_NUM");
//                        Long sumVol =  getFloatFromMap(stockStaticMap,stockCode+"_"+day+"V2_"+sellType+"_SUMVOL");
//                        moniterNum ++;
//                        stockStaticMap.put(stockCode+"_"+day+"V2_"+sellType+"_NUM" , moniterNum );
//                        stockStaticMap.put(stockCode+"_"+day+"V2_"+sellType+"_SUMVOL" ,sumVol+vol);
//                    }
//                }
//
//                if(vol>=stockMoniterBigVol){
//                    logger.warn("*********大单成交 " +stockCode+" "+ DateUtil.getFormattedDate(bean.getOperTime(),"yyyy-MM-dd HH:mm:ss")+" "+bean.getClosePrice()+" "+bean.getVolume()+" "+bean.getMacdInfo()+" "+bean.getDif());
//                }
//
//            }
//
//
//        }
//        if(addList.size()>0){
//            baseDao.insert("com.guoxc.info.dao.StockMinute.insertStockTmpMinuteDetList",addList);
//            addList.clear();
//
//            detailAlert(list,stockCode);
//            detailStaticAlert(list,stockCode,stockStaticMap);
//            moniterLineAlert( stockCode, currPrice);
//        }
//    }

    private Integer getPriceRate(String lastPriceStr, Float currPrice) {
        Integer priceRate = null;
        if( lastPriceStr != null){
           Float lastPrice = Float.parseFloat(lastPriceStr);
            priceRate = Math.round( (currPrice-  lastPrice)*1000/lastPrice);
        }
        return priceRate;
    }

    private Integer getPriceRate(String lastPriceStr, String currPriceStr) {
        Integer priceRate = null;
        if( lastPriceStr != null && currPriceStr!= null ){
            Float currPrice = Float.parseFloat(currPriceStr);
            Float lastPrice = Float.parseFloat(lastPriceStr);
            priceRate = Math.round( (currPrice-  lastPrice)*1000/lastPrice);
        }
        return priceRate;
    }


    private Integer getPriceRateFromCfg(String stockCodes, Float currPrice) {
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
        if( lastvol != null && lastvol>0){
            volRate = Math.round( vol*100/lastvol);
        }
        return volRate;
    }

    private Integer getVolRateRecent6s(String stockCodes, Integer vol) {
        Long avg6sVol =(Long)cfgParam.get(stockCodes+"_avg6s");
        Integer volRate = null;
        if(avg6sVol !=0 ){
            Integer last6sVol =  (Integer) lastMap.get("Last6sVol"+stockCodes);
            lastMap.put("Last6sVol"+stockCodes,vol);
            if( last6sVol != null && last6sVol>0 ){
                volRate = Math.round( (vol-last6sVol)/avg6sVol);
            }
        }
        return volRate;
    }

    private String getDifPriceRate6s(String stockCodes, Integer priceRate) {
        Integer priceRate6s =(Integer)cfgParam.get(stockCodes+"_priceRate6s");
        String diffPriceRateStr = "";
        if(priceRate6s != null   ){
           Integer diffPriceRate =    priceRate-priceRate6s;

           if(diffPriceRate>5||diffPriceRate<-5){
               logger.info(stockCodes+"diffPriceRate:"+diffPriceRate);
               diffPriceRateStr = "diffPriceRate6s:"+diffPriceRate;

           }
            try {
                if(careStockCode.indexOf(stockCodes)>0){
                    if(diffPriceRate>3||diffPriceRate<-3){
                        logger.info(stockCodes+"diffPriceRate:"+diffPriceRate);
                        diffPriceRateStr = "diffPriceRate6s:"+diffPriceRate;
                        playSound(stockCodes,diffPriceRate>0?"up":"down");
                    }

                }
            } catch (GeneralException e) {
                e.printStackTrace();
            }

        }
        cfgParam.put(stockCodes+"_priceRate6s",priceRate);
        return diffPriceRateStr;
    }


    private Integer getTransUpVolRate(String stockCodes, Integer vol) {
        Long lastvol =(Long)transUpCfgParam.get(stockCodes+"_avg20");

        Integer volRate = null;
        if( lastvol != null && lastvol>0){
            volRate = Math.round( vol*100/lastvol);
        }
        return volRate;
    }
    private Integer getTransUpVolRateRecent6s(String stockCodes, Integer vol) {
        Long avg6sVol =(Long)transUpCfgParam.get(stockCodes+"_avg6s");
        Integer volRate = null;
        if(avg6sVol !=0 ){
            Integer last6sVol =  (Integer) lastMap.get("Last6sVolTransUp"+stockCodes);
            lastMap.put("Last6sVolTransUp"+stockCodes,vol);
            if( last6sVol != null && last6sVol>0 ){
                volRate = Math.round( (vol-last6sVol)/avg6sVol);
            }
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
//            p.wait(10l);
//            p.destroy();
        } catch (Exception e) {
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
                   cfgParam.putAll( JSON.parse(lastDayPrice, new HashMap().getClass()));

               }
               String stockCodes = (String) cfgParam.get("stockCodes");
               if(StringUtil.isNotBlank(stockCodes)){
                   if(stockCodes.indexOf(",")>-1){
                       String[] stockCodeAndTypeArray =stockCodes.split(",");
                       for(String tockCodeAndType:stockCodeAndTypeArray){
                         long v20Avg =  getV20avg(tockCodeAndType.substring(2));
                           cfgParam.put(tockCodeAndType+"_avg20",v20Avg);
                           cfgParam.put(tockCodeAndType+"_avg6s",v20Avg/(4*60*20) ); // 平均6秒成交量
                       }
                       logger.info("-------------");
                   }else{
                       long v20Avg =  getV20avg(stockCodes.substring(2));
                       cfgParam.put(stockCodes+"_avg20",v20Avg);
                       cfgParam.put(stockCodes+"_avg6s",v20Avg/(4*60*20) ); // 平均6秒成交量
                   }
               }



           }
       }catch(Exception e){
            logger.error("init moniterStock error",e);
       }

    }


    private  void init(List list){

        try {
            if(transUpCfgParam == null){
                transUpCfgParam = new HashMap();
                for(int i=0;i<list.size();i++){
                    String stockCode = (String) list.get(i);
                    long v20Avg =  getV20avg(stockCode.substring(2));
                    transUpCfgParam.put(stockCode+"_avg20",v20Avg);
                    transUpCfgParam.put(stockCode+"_avg6s",v20Avg/(120) ); // 平均6秒成交量
                }
            }
        }catch(Exception e){
            logger.error("init list moniterStock error",e);
        }

    }


    private long getV20avg(String stockCode){
        long result = 0l ;
        try {
            Map param = new HashMap();
            Timestamp date =   DateUtil.addDay(DateUtil.getCurrentDate(),-15);
            param.put("operTime",date);
            param.put("stockCode",stockCode);
            List stockDayList =   baseDao.queryForList("com.guoxc.info.dao.StockDao.selectStockDay",param);
            long sumV20avg = 0;
           for(int i=0;i<stockDayList.size();i++){
               sumV20avg = sumV20avg+   ((StockDayBean)stockDayList.get(i)).getV20Avg();
           }
           if(stockDayList.size()>0){
               result =  sumV20avg/stockDayList.size();
           }


        } catch (Exception e) {
            e.printStackTrace();
        }



        return result;
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


    private List getTransUpMonitorStockList(){

       List result = new ArrayList();
        List  list = FileUtil.readFileXml(new File("E:\\stock\\data\\monitor\\transervesUp.txt"),"gbk");
        if(list != null){
            for(int i=0;i<list.size();i++){
                String stockCode = (String) list.get(i);
                if(stockCode.startsWith("6")){
                    result.add("sh"+stockCode);
                }else if("0".equals(stockCode)) {
                    result.add("sz"+stockCode);
                }
            }
        }
       return result;
    }




}
