package com.guoxc.info.web.common;

import com.guoxc.info.utils.DateUtil;

import java.io.File;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.*;

public class StockCalucate {
    private static String space =" ";

    public static String detailCompare(String lastStr, String currStr,Map detailInfo){

      String result = null;

        String[] lastInfos = lastStr.split("~");
        String[] stockInfoCols = currStr.split("~");

        String stockCode = stockInfoCols[0];
        float currPirce = Float.valueOf(stockInfoCols[1]);
        float lasePirce = Float.valueOf(lastInfos[1]);
        Long realVol =  Long.valueOf(stockInfoCols[4])-Long.valueOf(lastInfos[4]);
        Long diffBuyVol = Long.valueOf(stockInfoCols[5])- Long.valueOf(lastInfos[5]);

        Long diffSellVol = Long.valueOf(stockInfoCols[6]) - Long.valueOf(lastInfos[6]);
        long careVol =  Math.round(1000/ Float.valueOf(stockInfoCols[2]))  ; //10万转化为多少手
        long warnLittleVol =  Math.round(2000/ Float.valueOf(stockInfoCols[2]))  ; //20万转化为多少手
        long warnVol =  Math.round(5000/ Float.valueOf(stockInfoCols[2]))  ; //50万转化为多少手

        Map<Float,Long> lastBuyInfo = new HashMap();
        Map<Float,Long> lastSellInfo = new HashMap();
        Map<Float,Long> currBuyInfo = new HashMap();
        Map<Float,Long> currSellInfo = new HashMap();

        // 记录成不同价位交量变化，跟成交量比较
        float[] lastBuyPrices = new  float[5];
        float[] lastSellPrices = new  float[5];
        long[] lastBuyVols = new  long[5];
        long[] lastSellVols = new  long[5];


        float[] buyPrices = new  float[5];
        float[] sellPrices = new  float[5];
        long[] buyVols = new  long[5];
        long[] sellVols = new  long[5];

        initPriceAndVols(lastBuyPrices,lastSellPrices,lastBuyVols,lastSellVols,buyPrices,sellPrices,buyVols,sellVols,lastInfos,stockInfoCols);
        initPriceAndVolsMap(lastBuyInfo,lastSellInfo,currBuyInfo,currSellInfo,lastInfos,stockInfoCols);
        long mayDiffBuyVol = 0;
        long mayDiffSellVol=0;

        long tmpShowDiffBuyVol = 0;
        long tmpShowDiffSellVol=0;

        String upDownType  = null;
        if(currPirce > lasePirce){
            upDownType = "up "+(new BigDecimal(Float.toString(currPirce)).subtract(new BigDecimal(Float.toString(lasePirce))) );
        }else if(currPirce == lasePirce){
            upDownType = "same 0.00";
        }else{
            upDownType = "down "+ (new BigDecimal(Float.toString(lasePirce)).subtract(new BigDecimal(Float.toString(currPirce))) );
        }


      Map warnInfo =   getWarnMsg(stockInfoCols, currPirce, realVol, diffBuyVol, diffSellVol, careVol, warnVol, currBuyInfo, currSellInfo, lastBuyPrices, lastSellPrices, lastBuyVols, lastSellVols, mayDiffBuyVol, mayDiffSellVol, upDownType);

        Long hiddenVol =  (Long)warnInfo.get("hiddenVol");
        Integer priceOverNum = (Integer)warnInfo.get("priceOverNum");



        StringBuffer sbf = new StringBuffer();
        //时间

        String tempRealVol=  realVol>= careVol ? "("+realVol+")" : realVol+"";

        sbf.append(stockInfoCols[28]).append(space).append(currPirce).append(space).append(upDownType).append(space).append(" exchange ").append(tempRealVol).append(space)
                .append("hidden_exchange ").append(hiddenVol);
        if(priceOverNum>2 || priceOverNum<-2){
            sbf.append(space).append("price_throw").append(space).append(priceOverNum).append(" proices");
        }
       String warnMessgae = sbf.toString();
        if(priceOverNum>2 || priceOverNum<-2 || realVol>warnVol ||hiddenVol>careVol || hiddenVol<-careVol ){
            result = sbf.toString();
        }


       String changeMessge =  getVolChangeMsg( stockInfoCols, currPirce, realVol, diffBuyVol, diffSellVol, careVol, warnLittleVol,warnVol, currBuyInfo, currSellInfo,lastBuyInfo,lastSellInfo,  buyPrices,sellPrices,lastBuyPrices, lastSellPrices,lastBuyVols, lastSellVols, mayDiffBuyVol, mayDiffSellVol, upDownType,detailInfo);

        if( realVol >0 ){

            writeDetailChange(stockCode,warnMessgae+space+changeMessge);
        }

     return result;

    }

    private static Map getWarnMsg(String[] stockInfoCols, float currPirce, Long realVol, Long diffBuyVol, Long diffSellVol, long careVol,  long warnVol,Map<Float, Long> currBuyInfo, Map<Float, Long> currSellInfo, float[] lastBuyPrices, float[] lastSellPrices, long[] lastBuyVols, long[] lastSellVols, long mayDiffBuyVol, long mayDiffSellVol, String upDownType) {
        Map result = new HashMap();
        Long hiddenVol = 0l;
        int priceOverNum = 0;

        if(currPirce <= lastBuyPrices[0]){ //股价降低

            for(int i=0;i<lastBuyPrices.length;i++){
                if(lastBuyPrices[i]>currPirce){
                    mayDiffBuyVol = mayDiffBuyVol+ lastBuyVols[i];
                    priceOverNum--;
                }else if(lastBuyPrices[i] ==currPirce){
                    if(currBuyInfo.containsKey(lastBuyPrices[i])){  //买盘依然存在， 做代表未成交完，做差值
                        Long tmpVol =  currBuyInfo.get(lastBuyPrices[i]);
                        //买一价位和当前价位一样，成交量相减
                        if(lastBuyVols[i] >tmpVol){  //比较成交量增减
                            mayDiffBuyVol = mayDiffBuyVol+ lastBuyVols[i]-tmpVol;
                        }
                    }else{
                        mayDiffBuyVol = mayDiffBuyVol+ lastBuyVols[i];
                    }
                }
            }
            hiddenVol = diffSellVol - mayDiffBuyVol;

        }else if(currPirce>=lastSellPrices[0]){
            for(int i=0;i<lastSellPrices.length;i++){
                if(lastSellPrices[i]<currPirce){
                    priceOverNum++;
                    mayDiffSellVol = mayDiffSellVol+ lastSellVols[i];
                }else if(lastSellPrices[i] ==currPirce){
                    if(currSellInfo.containsKey(lastSellPrices[i])){
                        Long tmpVol =  currSellInfo.get(lastSellPrices[i]);
                        if(lastSellVols[i] >tmpVol){  //比较成交量增减
                            mayDiffSellVol = mayDiffSellVol+ lastSellVols[i]-tmpVol;
                        }
                    }else{
                        mayDiffSellVol = mayDiffSellVol+ lastSellVols[i];
                    }
                }
            }

            hiddenVol = diffBuyVol- mayDiffSellVol;

        }

        result.put("priceOverNum",priceOverNum);
        result.put("hiddenVol",hiddenVol);


        return result;
    }



    private static String getVolChangeMsg( String[] stockInfoCols, float currPirce, Long realVol, Long diffBuyVol, Long diffSellVol, long careVol,long warnLittleVol, long warnVol,Map<Float, Long> currBuyInfo, Map<Float, Long> currSellInfo,Map<Float, Long> lastBuyInfo, Map<Float, Long> lastSellInfo,float[] buyPrices, float[] sellPrices, float[] lastBuyPrices, float[] lastSellPrices, long[] lastBuyVols, long[] lastSellVols, long mayDiffBuyVol, long mayDiffSellVol, String upDownType,Map detailInfo) {

        StringBuffer sbf = new StringBuffer();
        Long hiddenVol = 0l;
        int priceOverNum = 0;
        String stockCode = stockInfoCols[0];
        String currDay = stockInfoCols[28].substring(0,8);

//        //时间
//        sbf.append(stockInfoCols[28]).append(space).append(currPirce).append(space).append(upDownType).append(space).append(" exchange ").append(realVol).append(space);



        // 计算 连续上涨和下跌次数，  连续大单上涨或者下跌次数
        if(realVol >0){
            if(upDownType.startsWith("up")){
                setChangeInfo(detailInfo, stockCode+currDay, "uptime","downtime");
                if(realVol>=careVol){
                    setChangeInfo(detailInfo, stockCode+currDay, "bigExchangeUptime","bigExchangeDowntime");
                }
            }else if(upDownType.startsWith("down")){
                setChangeInfo(detailInfo, stockCode+currDay, "downtime","uptime");
                if(realVol>=careVol){
                    setChangeInfo(detailInfo, stockCode+currDay, "bigExchangeDowntime","bigExchangeUptime");
                }
            }else { //股价不变时， 成交量小，则不计算，成交量大，则按照原方向计算
                String change = null;
                if(realVol>careVol){
                    Integer  upTime=  (Integer)detailInfo.get("uptime"+stockCode+currDay);
                    Integer  downTime=  (Integer)detailInfo.get("downtime"+stockCode+currDay);
                    change = getChangeString(currPirce, buyPrices, sellPrices, lastBuyPrices, lastSellPrices, change, upTime, downTime);
                    if(upTime>0 ){
                        if("sameup".equals(change)){
                            setChangeInfo(detailInfo, stockCode+currDay, "uptime","downtime");
                            if(realVol>=careVol){
                                setChangeInfo(detailInfo, stockCode+currDay, "bigExchangeUptime","bigExchangeDowntime");
                            }
                        }else if("reverse".equals(change)){
                            setChangeInfo(detailInfo, stockCode+currDay, "downtime","uptime");
                            if(realVol>=careVol){
                                setChangeInfo(detailInfo, stockCode+currDay, "bigExchangeDowntime","bigExchangeUptime");
                            }
                        }
                    }else if(downTime>0){
                        if("samedown".equals(change)){
                            setChangeInfo(detailInfo, stockCode+currDay, "downtime","uptime");
                            if(realVol>=careVol){
                                setChangeInfo(detailInfo, stockCode+currDay, "bigExchangeDowntime","bigExchangeUptime");
                            }
                        }else if("reverse".equals(change)){
                            setChangeInfo(detailInfo, stockCode+currDay, "uptime","downtime");
                            if(realVol>=careVol){
                                setChangeInfo(detailInfo, stockCode+currDay, "bigExchangeUptime","bigExchangeDowntime");
                            }
                        }
                    }
                }
            }

            if(realVol<careVol){
                detailInfo.put("bigExchangeUptime"+stockCode+currDay,0);
                detailInfo.put("bigExchangeDowntime"+stockCode+currDay,0);
            }


            Integer  upTime=  (Integer)detailInfo.get("uptime"+stockCode+currDay);
            Integer  downTime=  (Integer)detailInfo.get("downtime"+stockCode+currDay);
            Integer  bigExchangeUptime =  (Integer)detailInfo.get("bigExchangeUptime"+stockCode+currDay);
            Integer  bigExchangeDowntime =  (Integer)detailInfo.get("bigExchangeDowntime"+stockCode+currDay);

            if(upTime>=6){
                sbf.append("uptime").append(space).append(upTime).append(space);
            }
            if(downTime >= 6){
                sbf.append("downtime").append(space).append(downTime).append(space);
            }
            if(bigExchangeUptime>=3){
                sbf.append("bigExchangeUptime").append(space).append(bigExchangeUptime).append(space);
            }
            if(bigExchangeDowntime>=3){
                sbf.append("bigExchangeDowntime").append(space).append(bigExchangeDowntime).append(space);
            }

        }






    //计算夹板 挂单量
        //todo

        Float bigBorderB1P =   getAsFloat(detailInfo,"bigBorderB1P_"+stockCode+currDay) ;
        Long bigBorderB1V =   getAsLong(detailInfo,"bigBorderB1V_"+stockCode+currDay) ;
        if(bigBorderB1P >0){
            if(bigBorderB1P> buyPrices[0] ){  //在买盘 之上
                if(realVol>bigBorderB1V){
                    sbf.append("exchange bigBorderB1P").append(space).append(bigBorderB1P).append("(").append( bigBorderB1V).append(")");
                }else{
                    sbf.append("cancel bigBorderB1P").append(space).append(bigBorderB1P).append("(").append( bigBorderB1V).append(")");
                }
            }else if(bigBorderB1P>=buyPrices[4] ){ //在显示买盘之内
                Long vol = getAsLong(currBuyInfo,bigBorderB1P);
                if(vol ==0){
                    sbf.append("bigBorderB1P_C").append(space).append(bigBorderB1P).append("(").append( detailInfo.get("bigBorderB1V_"+stockCode+currDay)).append(")");
                }else{
                  if(bigBorderB1P == buyPrices[0]){
                       if(vol >= bigBorderB1V){
                           detailInfo.put("bigBorderB1V_"+stockCode+currDay,vol);
                       }else {
                           sbf.append("bigBorderB1P_M").append(space).append(bigBorderB1P).append("(").append(vol).append(space).append(bigBorderB1V- vol).append(")");
                           detailInfo.put("bigBorderB1V_"+stockCode+currDay,null);
                           detailInfo.put("bigBorderB1P_"+stockCode+currDay,null);
                       }
                  }else { //
                      if(vol >= bigBorderB1V){
                          detailInfo.put("bigBorderB1V_"+stockCode+currDay,vol);
                      }else {
                          sbf.append("bigBorderB1P_M").append(space).append(bigBorderB1P).append("(").append(vol).append(space).append(bigBorderB1V- vol).append(")");
                          detailInfo.put("bigBorderB1V_"+stockCode+currDay,null);
                          detailInfo.put("bigBorderB1P_"+stockCode+currDay,null);
                      }
                  }
                }
            }
        }


        Float bigBorderS1P =   getAsFloat(detailInfo,"bigBorderS1P_"+stockCode+currDay) ;
        Long bigBorderS1V =  getAsLong(detailInfo,"bigBorderS1V_"+stockCode+currDay);
        if(bigBorderS1P >0){
            if(bigBorderS1P < sellPrices[0] ){  //在买盘 之上
                if(realVol>bigBorderB1V){
                    sbf.append(" exchange bigBorderS1P").append(space).append(bigBorderS1P).append("(").append( bigBorderS1V).append(")");
                }else{
                    sbf.append(" cancel bigBorderS1P").append(space).append(bigBorderS1P).append("(").append( bigBorderS1V).append(")");
                }
            }else if(bigBorderS1P<=sellPrices[4] ){ //在显示买盘之内
                Long vol =   getAsLong(currBuyInfo,bigBorderS1P);
                if(vol ==0){
                    sbf.append("bigBorderS1P_C").append(space).append(bigBorderS1P).append("(").append( bigBorderS1V).append(")");
                }else{
                    if(bigBorderS1P == sellPrices[0]){
                        if(vol >= bigBorderS1P){
                            detailInfo.put("bigBorderS1V_"+stockCode+currDay,vol);
                        }else {
                            sbf.append("bigBorderS1P").append(space).append(bigBorderS1P).append("(").append(vol).append(space).append(bigBorderS1V- vol).append(")");
                            detailInfo.put("bigBorderS1V_"+stockCode+currDay,null);
                            detailInfo.put("bigBorderS1P_"+stockCode+currDay,null);
                        }
                    }else { //
                        if(vol >= bigBorderS1V){
                            detailInfo.put("bigBorderS1V_"+stockCode+currDay,vol);
                        }else {
                            sbf.append("  bigBorderS1P_M").append(space).append(bigBorderS1P).append("(").append(vol).append(space).append(bigBorderS1V- vol).append(")");
                            detailInfo.put("bigBorderS1V_"+stockCode+currDay,null);
                            detailInfo.put("bigBorderS1P_"+stockCode+currDay,null);
                        }
                    }
                }
            }
        }


        for(int i=0; i<buyPrices.length;i++){
            Long tmpVol = currBuyInfo.get(buyPrices[i]);
            if(tmpVol >= warnVol){
                sbf.append("B_ban").append(space).append(i+1).append("(").append(buyPrices[i]).append(space).append(tmpVol).append(")").append(space);
                setBigChangeBorderInfo(detailInfo,stockCode+currDay,"B",tmpVol,buyPrices[i]);

            }else if(tmpVol>=warnLittleVol){
                sbf.append("B_lban(").append(buyPrices[i]).append(space).append(tmpVol).append(")").append(space);
            }
        }

        for(int i=0;i<sellPrices.length;i++){
            long tmpVol = currSellInfo.get(sellPrices[i]);
            if(tmpVol >= warnVol){
                sbf.append("S_ban").append(space).append(i+1).append("(").append(sellPrices[i]).append(space).append(tmpVol).append(")").append(space);
                setBigChangeBorderInfo(detailInfo,stockCode+currDay,"S",tmpVol,sellPrices[i]);
            }else if(tmpVol>=warnLittleVol){
                sbf.append("S_lban(").append(sellPrices[i]).append(space).append(tmpVol).append(")").append(space);
            }
        }

        //显示 未成交的 盘面挂单量 变动情况
        if(currPirce <= lastBuyPrices[0]){ //股价降低
             List<Float> tmpPrices = new ArrayList();
            for(int i=0;i<lastBuyPrices.length;i++){
                if(lastBuyPrices[i]<currPirce){
                    if(!currBuyInfo.containsKey(lastBuyPrices[i])){  //当前买盘依然存在， 做代表已取消
                        tmpPrices.add(lastBuyPrices[i]);
                    }
                }
            }

            for(int i=0; i<buyPrices.length;i++){
                tmpPrices.add(buyPrices[i]);
            }
            Collections.sort(tmpPrices);

            for(int i=0;i<tmpPrices.size();i++){
              Float tmpPrice = tmpPrices.get(i);
              if(tmpPrice>= lastBuyPrices[4]){
                   if(currBuyInfo.containsKey(tmpPrice)){
                       if(lastBuyInfo.containsKey(tmpPrice)){
                           sbf.append("BK"+(i+1)+"").append(space).append(currBuyInfo.get(tmpPrice)).append("(").append(  currBuyInfo.get(tmpPrice)-lastBuyInfo.get(tmpPrice) ).append(");");
                       }else{
                           sbf.append("BA"+(i+1)+"").append(space).append(  currBuyInfo.get(tmpPrice) ).append(";");
                       }
                   }else{ //卖盘取消
                       tmpPrices.remove(tmpPrice);
                       i--;
                       sbf.append("BC"+(i+1)+"").append(space).append(  lastBuyInfo.get(tmpPrice) ).append(";");
                   }
              }
            }
            sbf.append("----");
            tmpPrices.clear();
            for(int i=0;i<sellPrices.length;i++){
                tmpPrices.add(sellPrices[i]);
            }
            for(int i=0;i<lastSellPrices.length; i++){
                if(lastSellPrices[i]<=sellPrices[4]){
                    if(!tmpPrices.contains(lastSellPrices[i])){
                        tmpPrices.add(lastSellPrices[i]);
                    }
                }
            }

            Collections.sort(tmpPrices);

           for(int i=0;i<tmpPrices.size();i++){
               Float tmpPrice = tmpPrices.get(i);
                if(currSellInfo.containsKey(tmpPrice)){
                      if(lastSellInfo.containsKey(tmpPrice)){
                          sbf.append("SK"+(i+1)+"").append(space).append(currSellInfo.get(tmpPrice)).append("(").append(  currSellInfo.get(tmpPrice)- lastSellInfo.get(tmpPrice) ).append(");");
                      }else{
                          sbf.append("SA"+(i+1)+"").append(space).append(  currSellInfo.get(tmpPrice) ).append(";");
                      }

                }else{
                    tmpPrices.remove(tmpPrice);
                    i--;
                    sbf.append("SC"+(i+1)+"").append(space).append(  lastSellInfo.get(tmpPrice) ).append(";");
                }
           }

        }else if(currPirce>=lastSellPrices[0]){ //股价升高

            List<Float> tmpPrices = new ArrayList();
            for(int i=0;i<lastBuyPrices.length;i++){
                if(lastBuyPrices[i]>buyPrices[4]){
                    if(!currBuyInfo.containsKey(lastBuyPrices[i])){  //当前买盘不存在，代表已取消
                        tmpPrices.add(lastBuyPrices[i]);
                    }
                }
            }

            for(int i=0;i<buyPrices.length;i++){
                tmpPrices.add(buyPrices[i]);
            }
            Collections.sort(tmpPrices);

            for(int i=0;i<tmpPrices.size();i++){
                Float tmpPrice = tmpPrices.get(i);
                if(currBuyInfo.containsKey(tmpPrice)){
                    if(lastBuyInfo.containsKey(tmpPrice)){
                        sbf.append("BK"+(i+1)+"").append(space).append(currBuyInfo.get(tmpPrice)).append("(").append(  currBuyInfo.get(tmpPrice)-lastBuyInfo.get(tmpPrice) ).append(");");
                    }else{
                        sbf.append("BA"+(i+1)+"").append(space).append(  currBuyInfo.get(tmpPrice) ).append(";");
                    }
                }else{ //卖盘取消
                    tmpPrices.remove(tmpPrice);
                    i--;
                    sbf.append("BC"+(i+1)+"").append(space).append(  lastBuyInfo.get(tmpPrice) ).append(";");
                }
            }
            sbf.append("----");
            tmpPrices.clear();
            for(int i=0;i<sellPrices.length;i++){
                tmpPrices.add(sellPrices[i]);
            }
            for(int i=0;i<lastSellPrices.length; i++){
                if(lastSellPrices[i]>= currPirce){
                    if(!tmpPrices.contains(lastSellPrices[i])){
                        tmpPrices.add(lastSellPrices[i]);
                    }

                }
            }
            Collections.sort(tmpPrices);
            for(int i=0;i<tmpPrices.size();i++){
                Float tmpPrice = tmpPrices.get(i);
                if(currSellInfo.containsKey(tmpPrice)){
                    if(lastSellInfo.containsKey(tmpPrice)){
                        sbf.append("SK"+(i+1)+"").append(space).append(currSellInfo.get(tmpPrice)).append("(").append(  currSellInfo.get(tmpPrice)- lastSellInfo.get(tmpPrice) ).append(");");
                    }else{
                        sbf.append("SA"+(i+1)+"").append(space).append(  currSellInfo.get(tmpPrice) ).append(";");
                    }

                }else{
                    tmpPrices.remove(tmpPrice);
                    i--;
                    sbf.append("SC"+(i+1)+"").append(space).append(  lastSellInfo.get(tmpPrice) ).append(";");
                }
            }

        }
       return sbf.toString();
    }

    private static void setBigChangeBorderInfo(Map detailInfo, String stockCodeAndDay, String type,Long vol, Float price) {
        if("B".equals(type)){
            Float bigBorderB1P =  (Float)   detailInfo.get("bigBorderB1P_"+stockCodeAndDay);
            if(bigBorderB1P == null || price>bigBorderB1P ){ //不存在，或者当前的买盘 比较大
                detailInfo.put("bigBorderB1P_"+stockCodeAndDay,price);
                detailInfo.put("bigBorderB1V_"+stockCodeAndDay,vol);
            }
        }else if("S".equals(type)){
            Float bigBorderS1P =  (Float)   detailInfo.get("bigBorderS1P_"+stockCodeAndDay);
            if(bigBorderS1P == null || price < bigBorderS1P) {
                detailInfo.put("bigBorderS1P_"+stockCodeAndDay,price);
                detailInfo.put("bigBorderS1V_"+stockCodeAndDay,vol);
            }
        }

    }


    private static void setChangeInfo(Map detailInfo, String stockCodeAndDay, String type,String clearKey) {
        Integer  time=  (Integer)detailInfo.get(type+stockCodeAndDay);
        if(time== null){
            time=0;
        }
        time ++;
        detailInfo.put(type+stockCodeAndDay,time);
        detailInfo.put(clearKey+stockCodeAndDay,0);
    }

    private static String getChangeString(float currPirce, float[] buyPrices, float[] sellPrices, float[] lastBuyPrices, float[] lastSellPrices, String change, Integer upTime, Integer downTime) {
        if(upTime>0){
            if(currPirce == buyPrices[0]){
                if( currPirce == lastBuyPrices[0]){ //同在买盘， 成交量为  卖出的
                    change = "reverse";
                }else if( currPirce == lastSellPrices[0]){ //由卖盘进入 买盘 代表上涨
                    change = "sameup";
                }
            }else if(currPirce == sellPrices[0]){
                if( currPirce == lastSellPrices[0]){  //同在卖盘， 成交量为买入的
                    change = "sameup";
                }else  if( currPirce == lastBuyPrices[0]){ //由买盘进入 卖盘 代表下降
                    change = "reverse";
                }
            }
        }else if(downTime>0){
            if(currPirce == buyPrices[0]){
                 if(currPirce == lastBuyPrices[0]){  //同在买盘， 成交量为  卖出的
                     change = "samedown";
                 }else if( currPirce == lastSellPrices[0]){ //由卖盘进入 买盘 代表上涨
                     change = "reverse";
                 }
            }else if(currPirce == sellPrices[0]){
                if( currPirce == lastSellPrices[0]){  //同在卖盘， 成交量为买入的
                    change = "reverse";
                }else  if( currPirce == lastBuyPrices[0]){ //由买盘进入 卖盘 代表下降
                    change = "samedown";
                }
            }
        }
        return change;
    }


    public static void initPriceAndVols(float[] lastBuyPrices,float[] lastSellPrices,long[] lastBuyVols,long[] lastSellVols,
                                  float[] buyPrices, float[] sellPrices,long[] buyVols ,long[] sellVols,  String[] lastInfos,String[] stockInfoCols){
        lastBuyPrices[0]=Float.valueOf(lastInfos[7]); //买1
        lastBuyVols[0]= Long.valueOf(lastInfos[8]);
        lastBuyPrices[1]=Float.valueOf(lastInfos[9]);
        lastBuyVols[1]= Long.valueOf(lastInfos[10]);
        lastBuyPrices[2]=Float.valueOf(lastInfos[11]);
        lastBuyVols[2]= Long.valueOf(lastInfos[12]);
        lastBuyPrices[3]=Float.valueOf(lastInfos[13]);
        lastBuyVols[3]= Long.valueOf(lastInfos[14]);
        lastBuyPrices[4]=Float.valueOf(lastInfos[15]);
        lastBuyVols[4]= Long.valueOf(lastInfos[16]);

        lastSellPrices[0]=Float.valueOf(lastInfos[17]); //卖1
        lastSellVols[0]= Long.valueOf(lastInfos[18]);
        lastSellPrices[1]=Float.valueOf(lastInfos[19]);
        lastSellVols[1]= Long.valueOf(lastInfos[20]);
        lastSellPrices[2]=Float.valueOf(lastInfos[21]);
        lastSellVols[2]= Long.valueOf(lastInfos[22]);
        lastSellPrices[3]=Float.valueOf(lastInfos[23]);
        lastSellVols[3]= Long.valueOf(lastInfos[24]);
        lastSellPrices[4]=Float.valueOf(lastInfos[25]);
        lastSellVols[4]= Long.valueOf(lastInfos[26]);


        buyPrices[0]=Float.valueOf(stockInfoCols[7]); //买1
        buyVols[0]= Long.valueOf(stockInfoCols[8]);
        buyPrices[1]=Float.valueOf(stockInfoCols[9]);
        buyVols[1]= Long.valueOf(stockInfoCols[10]);
        buyPrices[2]=Float.valueOf(stockInfoCols[11]);
        buyVols[2]= Long.valueOf(stockInfoCols[12]);
        buyPrices[3]=Float.valueOf(stockInfoCols[13]);
        buyVols[3]= Long.valueOf(stockInfoCols[14]);
        buyPrices[4]=Float.valueOf(stockInfoCols[15]);
        buyVols[4]= Long.valueOf(stockInfoCols[16]);

        sellPrices[0]=Float.valueOf(stockInfoCols[17]); //卖1
        sellVols[0]= Long.valueOf(stockInfoCols[18]);
        sellPrices[1]=Float.valueOf(stockInfoCols[19]);
        sellVols[1]= Long.valueOf(stockInfoCols[20]);
        sellPrices[2]=Float.valueOf(stockInfoCols[21]);
        sellVols[2]= Long.valueOf(stockInfoCols[22]);
        sellPrices[3]=Float.valueOf(stockInfoCols[23]);
        sellVols[3]= Long.valueOf(stockInfoCols[24]);
        sellPrices[4]=Float.valueOf(stockInfoCols[25]);
        sellVols[4]= Long.valueOf(stockInfoCols[26]);
    }

    private static void initPriceAndVolsMap(Map lastBuyInfo,Map lastSellInfo, Map currBuyInfo,Map currSellInfo,String[] lastInfos,String[] stockInfoCols){
        lastBuyInfo.put(Float.valueOf(lastInfos[7]),Long.valueOf(lastInfos[8]));
        lastBuyInfo.put(Float.valueOf(lastInfos[9]),Long.valueOf(lastInfos[10]));
        lastBuyInfo.put(Float.valueOf(lastInfos[11]),Long.valueOf(lastInfos[12]));
        lastBuyInfo.put(Float.valueOf(lastInfos[13]),Long.valueOf(lastInfos[14]));
        lastBuyInfo.put(Float.valueOf(lastInfos[15]),Long.valueOf(lastInfos[16]));
        lastSellInfo.put(Float.valueOf(lastInfos[17]),Long.valueOf(lastInfos[18]));
        lastSellInfo.put(Float.valueOf(lastInfos[19]),Long.valueOf(lastInfos[20]));
        lastSellInfo.put(Float.valueOf(lastInfos[21]),Long.valueOf(lastInfos[22]));
        lastSellInfo.put(Float.valueOf(lastInfos[23]),Long.valueOf(lastInfos[24]));
        lastSellInfo.put(Float.valueOf(lastInfos[25]),Long.valueOf(lastInfos[26]));

        currBuyInfo.put(Float.valueOf(stockInfoCols[7]),Long.valueOf(stockInfoCols[8]));
        currBuyInfo.put(Float.valueOf(stockInfoCols[9]),Long.valueOf(stockInfoCols[10]));
        currBuyInfo.put(Float.valueOf(stockInfoCols[11]),Long.valueOf(stockInfoCols[12]));
        currBuyInfo.put(Float.valueOf(stockInfoCols[13]),Long.valueOf(stockInfoCols[14]));
        currBuyInfo.put(Float.valueOf(stockInfoCols[15]),Long.valueOf(stockInfoCols[16]));
        currSellInfo.put(Float.valueOf(stockInfoCols[17]),Long.valueOf(stockInfoCols[18]));
        currSellInfo.put(Float.valueOf(stockInfoCols[19]),Long.valueOf(stockInfoCols[20]));
        currSellInfo.put(Float.valueOf(stockInfoCols[21]),Long.valueOf(stockInfoCols[22]));
        currSellInfo.put(Float.valueOf(stockInfoCols[23]),Long.valueOf(stockInfoCols[24]));
        currSellInfo.put(Float.valueOf(stockInfoCols[25]),Long.valueOf(stockInfoCols[26]));

    }


    private static void writeDetailChange(String code, String msg){
        try {

            File file = new File("E:\\stock\\data\\dayDetail\\" +DateUtil.getCurrDate("yyyyMMdd"));
            if(!file.exists()){
                file.mkdir();
            }

            RandomAccessFile randomAccessFile = new RandomAccessFile(file.getAbsoluteFile()+"\\"+code+"change.txt","rw");
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.writeBytes(msg+"\n");
            randomAccessFile.close();
        }  catch (Exception e) {
            e.printStackTrace();
        }


    }


    private static long getAsLong(Map map,String key){
       Long result =  (Long) map.get(key);

     return result == null? 0l:result;
    }

    private static Float getAsFloat(Map map,String key){
        Float result =  (Float) map.get(key);

        return result == null? 0f:result;
    }


    private static long getAsLong(Map map,Float key){
        Long result =  (Long) map.get(key);

        return result == null? 0l:result;
    }


}
