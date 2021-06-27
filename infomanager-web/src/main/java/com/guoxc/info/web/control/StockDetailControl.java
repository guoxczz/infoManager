package com.guoxc.info.web.control;

import com.alibaba.fastjson.JSON;
import com.guoxc.info.bean.info.StockDetailBean;
import com.guoxc.info.dao.StockDao;
import com.guoxc.info.utils.DateUtil;
import com.guoxc.info.utils.FileUtil;
import com.guoxc.info.web.dao.BaseDao;
import com.guoxc.info.web.services.StockMoniterService;
import com.guoxc.info.web.services.StockPredictService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stockDetail")
public class StockDetailControl {

    private final static Logger logger = LoggerFactory.getLogger(StockDetailControl.class);
    @Autowired
    private StockPredictService stockPredictService;
    @Autowired
    private Environment env;

    String careStockCode = ",002779,603929";

    private static Map  detailInfo = new HashMap();

    @Autowired
    private BaseDao baseDao;

    @RequestMapping("/insert")
    @ResponseBody
    String saveStockMinuteData(String stockCode,String operTime){


        try {
            if("ALL".equals(stockCode)){
                List  list = FileUtil.readFileXml(new File("E:\\stock\\data\\dayDetail"),"gbk");

               for(int i=0;i<list.size();i++){
                   stockPredictService.predict((String)list.get(i),operTime);
               }
            }else{
              File[] files =   new File("E:\\stock\\data\\dayDetail").listFiles();
              for(File file : files){
                  File stockDetailFile = new File(file.getAbsolutePath()+"/"+stockCode+".txt");
                 if(stockDetailFile.exists() ){
                     List  list = FileUtil.readFileXml(stockDetailFile,"gbk");
                     insertStockListList(list);
                     File destFile = new File("E:\\stock\\data\\dayDetailBak\\"+file.getName());
                     if(!destFile.exists()){
                         destFile.mkdirs();
                     }
//                     stockDetailFile.renameTo( new File("E:\\stock\\data\\dayDetailBak\\"+file.getName()+"\\"+stockCode+".txt"));
                     System.out.println(file.getName()+"\\"+stockCode+".txt deal over");
                 }

              }

            }



        } catch (ParseException e) {
            e.printStackTrace();
        }


        return "ok";


    }

//2: 股票代码; 3: 当前价格; 4: 昨收; 5: 今开; 6: 成交量（手）; 7: 外盘; 8: 内盘; 9: 买一;10: 买一量（手）;11-18: 买二 买五;19: 卖一;20: 卖一量;21-28: 卖二 卖五;
    private void insertStockListList(List <String> list) throws ParseException {

       if(list != null && list.size()>0){
          List detailList = new ArrayList();
          long lastVol = 0;
          int timeseq = 1;
          String lastTime = null;
          long lastBuyVol = 0L;
          long lastSelVol = 0L;
           for(String str : list){


               StockDetailBean bean = new  StockDetailBean();
               String[] details = str.split("~");
               if(details[28].equals(lastTime) ){
                   continue ;
               }else if(details[28].substring(8,12).compareTo("0925")<0){
                   continue ;
               }else{
                   lastTime =  details[28];
               }

               Long buyVol = Long.valueOf(details[5]);
               Long selVol =  Long.valueOf(details[6]);
               bean.setStockCode(details[0]);
               bean.setOperTime(DateUtil.convertStringToTimestamp(details[28], DateUtil.DATE_PATTERN.YYYYMMDDHHMMSS));
               bean.setCurrPrice(Float.valueOf(details[1]));
               bean.setPreClosePrice(Float.valueOf(details[2]));
               bean.setOpenPrice(Float.valueOf(details[3]));
               bean.setVolume(Long.valueOf(details[4]));
               bean.setDiffBuyVolume(buyVol);
               bean.setDiffSellVolume(selVol);
               bean.setBuy1P(Float.valueOf(details[7]));
               bean.setBuy1v(Long.valueOf(details[8]));
               bean.setBuy2P(Float.valueOf(details[9]));
               bean.setBuy2v(Long.valueOf(details[10]));
               bean.setBuy3P(Float.valueOf(details[11]));
               bean.setBuy3v(Long.valueOf(details[12]));
               bean.setBuy4P(Float.valueOf(details[13]));
               bean.setBuy4v(Long.valueOf(details[14]));
               bean.setBuy5P(Float.valueOf(details[15]));
               bean.setBuy5v(Long.valueOf(details[16]));
               bean.setSell1P(Float.valueOf(details[17]));
               bean.setSell1v(Long.valueOf(details[18]));
               bean.setSell2P(Float.valueOf(details[19]));
               bean.setSell2v(Long.valueOf(details[20]));
               bean.setSell3P(Float.valueOf(details[21]));
               bean.setSell3v(Long.valueOf(details[22]));
               bean.setSell4P(Float.valueOf(details[23]));
               bean.setSell4v(Long.valueOf(details[24]));
               bean.setSell5P(Float.valueOf(details[25]));
               bean.setSell5v(Long.valueOf(details[26]));
               if(bean.getDiffSellVolume()>0){
                   bean.setBuySellRate(  bean.getDiffBuyVolume()/(bean.getDiffSellVolume()) );
               }

               if(lastVol >0){
                   bean.setDiffVolume(bean.getVolume()-lastVol );
               }
              long buyDiffVol =  buyVol-lastBuyVol;
               long dellDiffVol =  selVol - lastSelVol;
               bean.setDiffBuySelVol(buyDiffVol-dellDiffVol);
               if(buyDiffVol>0 &&  dellDiffVol>0 ){
                   bean.setDiffVolDesc("b "+buyDiffVol+",s "+dellDiffVol);
               }
               lastVol =bean.getVolume();
               lastBuyVol = buyVol;
               lastSelVol = selVol;
               bean.setTimeSeq(timeseq++);
               detailList.add(bean);
               dealDetail(details[0],detailInfo,str);

               if(detailList.size()/500 ==0){
                   try{
                       baseDao.insert("com.guoxc.info.dao.StockDetail.insertStockDetailList",detailList);
                       detailList.clear();
                   }catch(Exception e){
                       System.out.println("******"+ JSON.toJSONString(detailList));

                       throw e;
                   }
               }


               if(details[28].substring(8,10).equals("15")){ //15点只有一条记录
                   break;
               }

           }


           try{
               if(detailList.size()>0){
                   baseDao.insert("com.guoxc.info.dao.StockDetail.insertStockDetailList",detailList);
               }
           }catch(Exception e){
               System.out.println("******"+ JSON.toJSONString(detailList));

               throw e;
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

                if(stockInfoCols[28].substring(8,10).equals("15")){ //15点后不记录
                    int priceRate =  getPriceRate(lastInfos[1],stockInfoCols[1]);
                    if(priceRate>10 || priceRate<-10){
                        writeDetail(code,"尾盘变化 "  +priceRate+"‰");
                    }
                    detailInfo.put(code+"lastInfo", null);
                    return ;
                }


                Map lastBuyMap = new HashMap();
                Map lastSellMap = new HashMap();

                // 记录成不同价位交量变化，跟成交量比较
                float[] lastBuyPrices = new  float[5];
                float[] lastSellPrices = new  float[5];
                long[] lastBuyVols = new  long[5];
                long[] lastSellVols = new  long[5];


                float[] buyPrices = new  float[5];
                float[] sellPrices = new  float[5];
                long[] buyVols = new  long[5];
                long[] sellVols = new  long[5];


                for(int i=7;i<26;i++){

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
                }

                for(int i=9; i<28;i++){

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

                if(buyPrices[0]<=lastBuyPrices[3] ){  // 卖方砸盘4个价位
                    Long tmpDiffSellVol = 0L;
                    int i=0;
                    for(;i<lastBuyPrices.length;i++){
                        if(buyPrices[0] <lastBuyPrices[i]){
                            tmpDiffSellVol = tmpDiffSellVol+lastBuyVols[i];
                        }else if(buyPrices[0] ==lastBuyPrices[i]){
                            tmpDiffSellVol = tmpDiffSellVol+ lastBuyVols[i]-buyVols[i];
                            break;
                        }else{
                            break;
                        }
                        if(i>2){
                            logger.info("*********************"+code+"sell throw over "+i + " price, vol  "+tmpDiffSellVol);
                            writeDetail(code,stockInfoCols[28]+"sell throw over "+i + " price, vol  "+tmpDiffSellVol);
                        }
                    }

                }

                if(sellPrices[0]>=lastSellPrices[3] ){  // 买方上攻4个价位
                    Long tmpDiffSellVol = 0L;
                    int i=0;
                    for(;i<lastSellPrices.length;i++){
                        if(sellPrices[0] <lastSellPrices[i]){
                            tmpDiffSellVol = tmpDiffSellVol+lastSellVols[0];
                        }else if(buyPrices[0] ==lastBuyPrices[i]){
                            tmpDiffSellVol = tmpDiffSellVol+ lastSellVols[i]-sellVols[i];
                            break;
                        }else{
                            break;
                        }
                    }
                    if(i>2){
                        logger.info("*********************"+code+"buyer throw  "+i + " proices, vol  "+tmpDiffSellVol);
                        writeDetail(code,stockInfoCols[28]+"buyer throw  "+i + " proices, vol  "+tmpDiffSellVol);
                    }

                }


// 非盘面成交量    大单变化量

                Long tmpDiffBuyVol = 0L;

                for(int i=0;i<lastBuyPrices.length;i++){
                    int j= 0;
                    for(;j<buyPrices.length;j++){
                        if(lastBuyPrices[i] == buyPrices[j]){
                            if( lastBuyVols[i] >buyVols[j] ){
                                tmpDiffBuyVol = lastBuyVols[i]-buyVols[j];
                            }
                            break;
                        }
                    }
                    if(j==5){
                        if(lastBuyPrices[i] >buyPrices[0]){ //已成交 或者撤单
                            tmpDiffBuyVol = lastBuyVols[i];
                        }else if(lastBuyPrices[i] <buyPrices[4] ) {  //有新的卖盘，或者下跌，该价格已经不显示了

                        }else{  //撤单了
                            tmpDiffBuyVol = tmpDiffBuyVol+ lastBuyVols[i];
                            if(tmpDiffBuyVol>100){
                                writeDetail(code,stockInfoCols[28]+" "+lastBuyPrices[i]+ " buy cancel "+ tmpDiffBuyVol);
                            }
                        }
                    }
                }

                if(diffBuyVol-tmpDiffBuyVol>100){  //成交量 钓大鱼变化量 ，说明隐藏成交
                    logger.error(code +"care  buy hidden  exchange "+  ( diffBuyVol-tmpDiffBuyVol));
                    writeDetail(code,stockInfoCols[28]+" buy hidden exchange  "+ ( diffBuyVol-tmpDiffBuyVol));
                }else if(diffBuyVol-tmpDiffBuyVol <-100){ //成交量大于变化量，说明盘面没有显示，快速成交
                    logger.error(code +" care  buy hidden cancel "+  ( tmpDiffBuyVol-diffBuyVol));
                    writeDetail(code,stockInfoCols[28]+" buy hidden cancel "+ ( tmpDiffBuyVol-diffBuyVol));
                }



                Long tmpDiffSellVol = 0L;

                for(int i=0;i<lastSellPrices.length;i++){
                    int j= 0;
                    for(;j<sellPrices.length;j++){
                        if(lastSellPrices[i] == sellPrices[j]){
                            if( lastSellVols[i] >sellVols[j] ){
                                tmpDiffSellVol = lastSellVols[i]-sellVols[j];
                            }
                            break;
                        }
                    }
                    if(j==5){
                        if(lastSellPrices[i] < sellPrices[0]){ //  可能成交或者撤单
                            tmpDiffSellVol = lastSellVols[i];
                        }else if(lastSellPrices[i] >sellPrices[4] ) {  //有新的买盘,或者上攻，该价格已经不显示了

                        }else{  //撤单了
                            tmpDiffSellVol = tmpDiffSellVol+ lastSellVols[i];
                            if(tmpDiffSellVol>90){
                                writeDetail(code,stockInfoCols[28]+" "+lastBuyPrices[i]+ " sell cancel "+ tmpDiffSellVol+"");
                            }

                        }
                    }
                }


                if(diffSellVol-tmpDiffSellVol>100){  //变化量 大于成交量，说明撤单
                    logger.error(code +"care sell hidden exchange "+  ( diffSellVol-tmpDiffSellVol));
                    writeDetail(code,stockInfoCols[28]+" sell hidden exchange "+ ( diffSellVol-tmpDiffSellVol));
                }else if(diffSellVol-tmpDiffSellVol <-100){ //成交量小于变化量 -100，说明盘面没有成交，而是取消了
                    logger.error(code +" care  sell hidden cacel "+  ( tmpDiffSellVol-diffSellVol));
                    writeDetail(code,stockInfoCols[28]+" sell hidden cacel "+ ( tmpDiffSellVol-diffSellVol));
                }
            }

            if(!stockInfoCols[28].substring(8,10).equals("15") && !stockInfoCols[28].substring(8,12).equals("0925") ){
                detailInfo.put(code+"lastInfo",stockeDetaiStr);
            }


        }
    }



    private void writeDetail(String code,String msg){
        try {

            File file = new File("E:\\stock\\data\\dayDetailwarn\\" +DateUtil.getCurrDate("yyyyMMdd"));
            if(!file.exists()){
                file.mkdir();
            }

            RandomAccessFile randomAccessFile = new RandomAccessFile(file.getAbsoluteFile()+"\\"+code+"test.txt","rw");
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.writeBytes(msg+"\n");
            randomAccessFile.close();
        }  catch (Exception e) {
            e.printStackTrace();
        }


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


}
