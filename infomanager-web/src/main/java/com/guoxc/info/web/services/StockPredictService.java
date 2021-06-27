package com.guoxc.info.web.services;

import com.guoxc.info.bean.info.StockDayBean;
import com.guoxc.info.bean.info.StockDayInflectionBean;
import com.guoxc.info.bean.info.StockPredictBean;
import com.guoxc.info.bean.info.StockTransverseBean;
import com.guoxc.info.dao.BsStaticDataDao;
import com.guoxc.info.dao.StockDao;
import com.guoxc.info.utils.DateUtil;
import com.guoxc.info.utils.FileUtil;
import com.guoxc.info.web.control.StockControl;
import com.guoxc.info.web.dao.BaseDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockPredictService {
    private final static Logger logger = LoggerFactory.getLogger(StockControl.class);
    @Autowired
    private StockDao stockDao  ;

    @Autowired
    private BsStaticDataDao bsStaticDataDao;

    @Autowired
    private BsStaticDataService bsStaticDataService;

    @Autowired
    private BaseDao baseDao;

    private int UPDAY = 10;

    /**
     * 价格低位买入点： 1、地位缩量买进（成交量低）
     *                 2、横盘后 下跌： 横盘后  下跌15%，   成交量变小。 （价格历史最低点， 60日最低点）     卖出点： 横盘价格抛50%，横盘价格上涨10% 后抛50%, 达到横盘价格附近后，下跌10，继续买进。
     *                 3、 横盘持续50天后  上涨，涨幅比较大，8%以上。次日开盘价低于2% 买入（ 实际买入参考，无此数据 开盘成交量低于20日均线的3%）
     *  价格中位       1、  震动缩量下跌： 成交量处于60日最小量附近，价格处于震荡范围低位。买入，  成交量放大时，上涨则卖出一部分，下跌则次日加仓。
     *
     * 价格高位        1、 首次大幅上涨后，回调到开始大幅上涨（超过4%）的价格时，开始买入。
     *
     *
     *特殊选股：
     *       股价在中位， 成交量低，且连续大跌，跌幅为前20日平均振幅的60%以上。
     *
     * @param stockCode
     * @param date
     * @throws ParseException
     */


    public void  predict(String stockCode,String date) throws ParseException {

        List predictList = new ArrayList();
         Map param = new HashMap();

         if(StringUtils.isBlank(date)){
            date = DateUtil.getCurrDate("yyyy-MM-dd");
         }
        param.put("operTime",DateUtil.convertStringToTimestamp(date,"yyyy-MM-dd") );
        param.put("stockCode",stockCode);

        Map stockHisMap = new HashMap();

        List stockDayList =   baseDao.queryForList("com.guoxc.info.dao.StockDao.selectStockDay",param);
        List stockTransverseList = baseDao.queryForList("com.guoxc.info.dao.StockSwingDao.queryRecentStockSwing",param);  //横盘信息
        List stockInflectionList = baseDao.queryForList("com.guoxc.info.dao.stockInflection.getInflectionNewInfoByOrder",param);



        for(int i=0; i<stockDayList.size();i++ ){
            StockDayBean stockDayBean = (StockDayBean) stockDayList.get(i);
            if(stockDayBean.getSeq()<60){
                continue;
            }
            if(stockDayBean.getHighPrice()== stockDayBean.getLowPrice()){
                if(stockDayBean.getMaxC60Price()/stockDayBean.getMinC60Price()<1.15 &&  stockDayBean.getV5Rate()<400){
                    logger.info("次日买进,长期横盘大涨");
                    saleStock(predictList,stockDayList, i ,"长期横盘大涨",stockDayBean.getMaxC60Price() ,0);
                }
                continue;
            }
             float closePrice =  stockDayBean.getClosePrice();
            float minC60Price = stockDayBean.getMinC60Price();
            float maxC60Price = stockDayBean.getMaxC60Price();
            float diffPriceRate =  (closePrice-minC60Price)*100/minC60Price;
            List recentInflectionList =  getRecentInflectionList(stockInflectionList,stockDayList,i);
            if(recentInflectionList.size()<3){
                logger.error("recentInflectionList size<3 "+stockCode);
                continue;
            }
            Map swingMap  =  getInflectionPriceSwing(recentInflectionList);
            Float maxPrice = null;
            try{
                maxPrice =  (Float)swingMap.get("maxPrice");
            }catch(Exception e){
                e.printStackTrace();;
            }

            String priceSwingDesc = (String) swingMap.get("swingDesc");
            List recentTransverseList=  getRecentTransverseList(stockTransverseList,stockDayList,i);
            StockDayInflectionBean lastDayInflectionBean = (StockDayInflectionBean) recentInflectionList.get(0);
            long minVol = getstockMinVol(recentInflectionList,stockCode,stockDayBean);
            String currPriceDesc = getCurrPosition(stockDayBean,swingMap,lastDayInflectionBean);


            if(priceSwingDesc.indexOf("巨涨") >-1 || priceSwingDesc.indexOf("巨跌") >-1){
                  if( "低位横盘".equals(currPriceDesc) ||  "低位下降".equals(currPriceDesc) ||  "低位新低".equals(currPriceDesc)){
                        if(stockDayBean.getVolume()<minVol*1.5 &&  stockDayBean.getV20Rate()<-30  && stockDayBean.getVolume()/stockDayBean.getV5Avg()-1<-0.20 ){
                            logger.info("次日买进,巨幅变动");
                            saleStock(predictList,stockDayList, i ,"巨变低位",maxPrice,0);
                        }
                  }
            }else{
                if(currPriceDesc.startsWith("低位")||currPriceDesc.startsWith("箱低") ){
                    if(stockDayBean.getVolume()<minVol*1.5 &&  stockDayBean.getV20Rate()<-40  && stockDayBean.getV5Rate()<-30 ){
                        saleStock(predictList,stockDayList, i ,currPriceDesc.substring(0,2),maxPrice,0);
                    }
                }
            }

            if(recentTransverseList.size()>0){
                StockTransverseBean stockTransverseBean = (StockTransverseBean) recentTransverseList.get(0);
                if(stockTransverseBean.getLastDay()>20 ){
                    if( stockDayBean.getSeq()-  stockTransverseBean.getStockSeq()<=6 ){
                          if(stockDayBean.getClosePrice()/stockTransverseBean.getMinPrice()<0.85 && stockDayBean.getV5Rate()<-40 && stockDayBean.getV20Rate()>-20){
                              logger.info("次日买进，横盘首跌"+currPriceDesc.substring(0,2));
                              saleStock(predictList,stockDayList, i ,currPriceDesc.substring(0,2),maxPrice,stockTransverseBean.getLastDay());

                          }
                    }
                }
            }
            if( stockDayBean.getMaxC60Price()/stockDayBean.getMinC60Price()<1.15 && stockDayBean.getPriceRate()>80 &&  stockDayBean.getV5Rate()<400){
                logger.info("次日买进,长期横盘大涨");
                saleStock(predictList,stockDayList, i ,"长期横盘大涨",stockDayBean.getMaxC60Price(),0);
            }

        }

        if(predictList.size()>0){
            baseDao.insert("com.guoxc.info.dao.StockPredictDao.insertStockPredictList",predictList);
        }
        logger.info(stockCode+" 检测结束");

//        getUpdayAndNormalVolList(stockDayList);





    }

    /**
     *
     * @param stockDayList
     * @param offset
     * @param type  长期横盘大涨   巨变低位  箱低  低位  横盘首跌
     */

    private void saleStock(List predictList, List stockDayList, int offset,String type,float maxPrice,long lastDayNum){

        StockDayBean tmpStockDayBean = (StockDayBean) stockDayList.get(offset);
        StockPredictBean stockPredictBean = new StockPredictBean();
        BeanUtils.copyProperties(tmpStockDayBean,stockPredictBean);
        stockPredictBean.setBuyDesc(type);
        stockPredictBean.setBuyMaxPrice(maxPrice);
        stockPredictBean.setBuyLastDayNum(lastDayNum);

         if("长期横盘大涨".equals(type)){
               for(int i=offset+1;i<stockDayList.size();i++){
                   StockDayBean stockDayBean = (StockDayBean) stockDayList.get(i);
                   float upClosePrice = stockDayBean.getClosePrice()>stockDayBean.getOpenPrice()?stockDayBean.getClosePrice():stockDayBean.getOpenPrice();
                   float upLineRate =  (stockDayBean.getHighPrice()-upClosePrice)/stockDayBean.getPreClosePrice() ;
                   if( ( stockDayBean.getPriceRate()<-20 || upLineRate>0.03 )   && stockDayBean.getV5Rate()>0 ){
                        //sale 卖出
                       setSellInfo(stockPredictBean,stockDayBean);
                       break;
                   }
               }
         }else if("巨变低位".endsWith(type) ||"低位".endsWith(type) ){
             for(int i=offset+1;i<stockDayList.size();i++){
                 StockDayBean stockDayBean = (StockDayBean) stockDayList.get(i);
                 float upClosePrice = stockDayBean.getClosePrice()>stockDayBean.getOpenPrice()?stockDayBean.getClosePrice():stockDayBean.getOpenPrice();
                 float upLineRate =  (stockDayBean.getHighPrice()-upClosePrice)/stockDayBean.getPreClosePrice() ;
                 if( ( stockDayBean.getPriceRate()<-30 || upLineRate>0.03 )   && stockDayBean.getV5Rate()>30 ){
                     //sale 卖出
                     setSellInfo(stockPredictBean,stockDayBean);
                     break;
                 }
             }
         }else if("箱低".endsWith(type) ){
             for(int i=offset+1;i<stockDayList.size();i++){
                 StockDayBean stockDayBean = (StockDayBean) stockDayList.get(i);
                 float upClosePrice = stockDayBean.getClosePrice()>stockDayBean.getOpenPrice()?stockDayBean.getClosePrice():stockDayBean.getOpenPrice();
                 float upLineRate =  (stockDayBean.getHighPrice()-upClosePrice)/stockDayBean.getPreClosePrice() ;
                 if(  ( stockDayBean.getPriceRate()<-30    && stockDayBean.getV5Rate()>50 )   ||  stockDayBean.getClosePrice()/maxPrice>0.96  ){
                     //sale 卖出
                     setSellInfo(stockPredictBean,stockDayBean);
                     break;
                 }
             }
         }else if("横盘首跌".endsWith(type) ){

             for(int i=offset+1;i<stockDayList.size();i++){
                 StockDayBean stockDayBean = (StockDayBean) stockDayList.get(i);
                 float upClosePrice = stockDayBean.getClosePrice()>stockDayBean.getOpenPrice()?stockDayBean.getClosePrice():stockDayBean.getOpenPrice();
                 float upLineRate =  (stockDayBean.getHighPrice()-upClosePrice)/stockDayBean.getPreClosePrice() ;
                 if(lastDayNum <30){
                     if(  ( stockDayBean.getPriceRate()<-30    && stockDayBean.getV5Rate()>60 )   ||  stockDayBean.getClosePrice()/maxPrice>0.96  ){
                         //sale 卖出
                         setSellInfo(stockPredictBean,stockDayBean);
                         break;
                     }
                 }else if(lastDayNum<70){
                     if(  ( stockDayBean.getPriceRate()<-40    && stockDayBean.getV5Rate()>80 )   ||  stockDayBean.getClosePrice()/maxPrice>1.10  ){
                         //sale 卖出
                         setSellInfo(stockPredictBean,stockDayBean);
                         break;
                     }
                 }else{
                     if(  ( stockDayBean.getPriceRate()<-50    && stockDayBean.getV5Rate()>100 )   ||  stockDayBean.getClosePrice()/maxPrice>1.30  ){
                         //sale 卖出
                         setSellInfo(stockPredictBean,stockDayBean);
                         break;
                     }
                 }
             }
         }

        predictList.add(stockPredictBean);
    }


    private void setSellInfo(StockPredictBean stockPredictBean,StockDayBean stockDayBean){
        stockPredictBean.setSellOperTime(stockDayBean.getOperTime());
        stockPredictBean.setSellVol(stockDayBean.getVolume());
        stockPredictBean.setSellClosePrice(stockDayBean.getClosePrice());
        stockPredictBean.setSellV5Avg(stockDayBean.getV5Avg());
        stockPredictBean.setSellV20Avg(stockDayBean.getV20Avg());
        stockPredictBean.setSellPriceRate(stockDayBean.getPriceRate());
        stockPredictBean.setLastDayNum( stockDayBean.getSeq()-stockPredictBean.getSeq());
        stockPredictBean.setProfit(Math.round ((stockPredictBean.getSellClosePrice()/stockPredictBean.getClosePrice()-1)*1000));
        stockPredictBean.setSellV5Rate(stockDayBean.getV5Rate());
        stockPredictBean.setSellV20Rate(stockDayBean.getV20Rate());
    }



    private long getstockMinVol( List stockInflectionList,String stockCode,StockDayBean stockDayBean){
        Map param = new HashMap();

        param.put("stockCode",stockCode);
        List tmpList = new ArrayList();
        for(int i=0;i<stockInflectionList.size();i++){
            tmpList.add( ((StockDayInflectionBean)stockInflectionList.get(i)).getOperTime());
        }
        param.put("operTimes",tmpList );
        List list = baseDao.queryForList("com.guoxc.info.dao.StockDao.getStockDayByOperTime",param);
        for(int i=0;i<list.size();){
            StockDayBean tmpStockDayBean = (StockDayBean)list.get(i);
            if(stockDayBean.getSeq()-tmpStockDayBean.getSeq()>250){
                list.remove(i);
            }else{
                i++;
            }
        }
       long minVol = 0l;
        for(int i=0;i<list.size();i++){
            StockDayBean tmpStockDayBean = (StockDayBean)list.get(i);
            if(i==0){
                minVol = tmpStockDayBean.getVolume();
            }
            if(tmpStockDayBean.getPriceRate()<0){
                 if(tmpStockDayBean.getHighPrice()>tmpStockDayBean.getLowPrice()){
                       if(minVol > tmpStockDayBean.getVolume()){
                           minVol = tmpStockDayBean.getVolume();
                       }
                 }
                 if(minVol > tmpStockDayBean.getV5Avg()){
                     minVol = tmpStockDayBean.getV5Avg();
                 }
            }
        }
        if(minVol > stockDayBean.getMinV60Vol()){
            minVol = stockDayBean.getMinV60Vol();
        }
      return minVol;
    }




      private String getCurrPosition(StockDayBean stockDayBean, Map swingMap,StockDayInflectionBean inflectionBean){
          String result = null;
          float maxcC60price = stockDayBean.getMaxC60Price();
          float minC60Price = stockDayBean.getMinC60Price();
          float maxInflecPrice= (float) swingMap.get("maxPrice");
          float minInflecPrice= (float) swingMap.get("minPrice");
          float priceRate60 = maxcC60price/minC60Price -1 ;
          float priceRateInflection = maxInflecPrice/minInflecPrice -1 ;

          float currPriceInflecRate = stockDayBean.getClosePrice()/minInflecPrice -1 ;

          if(priceRateInflection>0.7){
              if(currPriceInflecRate<0.20 ){
                  result= "低位";
              }else if(currPriceInflecRate<0.5){
                  result= "中位";
              }else {
                  result= "高位";
              }
          }else if(priceRateInflection>0.3){
              if(currPriceInflecRate<0.15 ){
                  result= "低位";
              }else if(currPriceInflecRate<0.3 ){
                  result= "中位";
              }else{
                  result= "高位";
              }
          }else {
              if(currPriceInflecRate<0.06 ){
                  result= "箱低";
              }else if(currPriceInflecRate<0.24){
                  result= "箱中";
              }else{
                  result= "箱高";
              }

          }

          if(priceRate60<0.15 ){  //横盘
              result = result+"横盘";
          }else {
              if(inflectionBean.getSwingRate()>0 ){
                 if( stockDayBean.getClosePrice()>inflectionBean.getClosePrice()){
                     result = result+"新高";
                 }else{
                     result = result+"下降";
                 }
              }else {
                  if( stockDayBean.getClosePrice()>inflectionBean.getClosePrice()){
                      result = result+"上升";
                  }else{
                      result = result+"新低";
                  }
              }
          }
          return result;
      }



    private Map   getInflectionPriceSwing(List stockInflectionList){
        Map result = new  HashMap();
        String priceSwingStr = "";
        for(int i=0;i<stockInflectionList.size();i++){
            StockDayInflectionBean stockDayInflection =   (StockDayInflectionBean)stockInflectionList.get(i);
            String desc=  getInflectionSwingDesc(stockDayInflection.getSwingRate());
            priceSwingStr = priceSwingStr + desc+",";
        }
        result.put("swingDesc",priceSwingStr);
         long allIntervalDay = 0;
        if(stockInflectionList.size()>=3 ){
            Float[] inflectionPrices = new Float[stockInflectionList.size()];

            for(int i=0;i<stockInflectionList.size();i++){
                StockDayInflectionBean inflectionBean = (StockDayInflectionBean) stockInflectionList.get(i);
                inflectionPrices[i]=inflectionBean.getClosePrice();
                allIntervalDay= allIntervalDay+inflectionBean.getIntervalDay();
            }

            Map<String,Float> oddnumberMap =   getMaxMin(inflectionPrices,"oddnumber");
            Map<String,Float>  evennumberMap =   getMaxMin(inflectionPrices,"evennumber");
            float oddRate = oddnumberMap.get("maxPrice")/oddnumberMap.get("minPrice")-1;
            float evenRate = evennumberMap.get("maxPrice")/evennumberMap.get("minPrice")-1;
            Map<String,Float> allMap =   getMaxMin(inflectionPrices,"all");
            result.putAll(allMap);
            if(oddRate<0.05 && evenRate<0.05 ){
                float allRate = allMap.get("maxPrice")/allMap.get("minPrice")-1;
                result.put("swingRate",allRate);
            }
        }
        result.put("allIntervalDay",allIntervalDay);

        return result;
    }

   private Map getMaxMin( Float[] inflectionPrices,String type){
       Map  result = new HashMap();
       float maxPrice = 0l;
       float minPrice = 0l ;
       int step = 1;
       if("all".equals(type)){
           maxPrice = inflectionPrices[0];
           minPrice = inflectionPrices[0];
           step =1;
       }else if("oddnumber".equals(type)){
           maxPrice = inflectionPrices[0];
           minPrice = inflectionPrices[0];
           step =2;
       }else if("evennumber".endsWith(type)){
           maxPrice = inflectionPrices[1];
           minPrice = inflectionPrices[1];
           step =2;
       }

       for(int i=step-1;i<inflectionPrices.length;i=i+step){
           if(inflectionPrices[i]>maxPrice){
               maxPrice =  inflectionPrices[i];
           }else if(inflectionPrices[i] <minPrice){
               minPrice = inflectionPrices[i];
           }
       }
       result.put("maxPrice",maxPrice);
       result.put("minPrice",minPrice);

       return result;

   }






    private Map getTransverseInfo(List stockDayList,List recentTransverseList,int dealOffset){
        Map result = new HashMap();
        StockDayBean stockDayBean  = (StockDayBean) stockDayList.get(dealOffset);
        int count =0;
        long stockSeq = stockDayBean.getSeq();
           for(int i=0;i<recentTransverseList.size();i++){
               StockTransverseBean bean = (StockTransverseBean)  recentTransverseList.get(i);
               for(int j=0;j<stockDayList.size();j++){
                   StockDayBean tmpStockDayBean = (StockDayBean) stockDayList.get(i);
                   if(bean.getOperTime().equals(tmpStockDayBean.getOperTime())){
                       if((stockSeq-tmpStockDayBean.getSeq())<120){
                           count ++;
                           result.put("transversePrice"+count,bean.getMinPrice());
                           result.put("transverseLast"+count,bean.getLastDay());
                           result.put("transverse"+count,bean);
                       }
                   }
               }

           }
         return result;
    }

    private List  getRecentInflectionList(List stockInflectionList, List stockDayList, int dealOffset) {

        int offset = 0;
        StockDayBean stockDayBean  = (StockDayBean) stockDayList.get(dealOffset);
        Timestamp operTime = stockDayBean.getOperTime();
        for(int i=0;i<stockInflectionList.size();i++){
            StockDayInflectionBean inflectionBean = (StockDayInflectionBean) stockInflectionList.get(i);
            if(inflectionBean.getOperTime().before(operTime)){
                 boolean isConfirm = isConfirmInflection(inflectionBean,stockDayList,dealOffset);
                 if(isConfirm){
                     offset = i;
                 }else{
                     offset = i+1;
                 }
                break;
            }else{
                continue;
            }
        }
        List list = new ArrayList();
        long intervalNum = 0;
        if(stockInflectionList.size() -9-offset>0){
            for(int i=0;i<8 ;i++){
              StockDayInflectionBean  stockDayInflectionBean =null;
              if(offset+i<stockInflectionList.size()){
                  stockDayInflectionBean =  (StockDayInflectionBean)stockInflectionList.get(offset+i);
                  intervalNum = intervalNum+ stockDayInflectionBean.getIntervalDay();
                  list.add(stockDayInflectionBean);
                  if(intervalNum>250){
                      break;
                  }
              }

            }
        }


       return list;
    }


    private List  getRecentTransverseList(List stockTransverseList, List stockDayList, int dealOffset) {

        int offset = 0;
        boolean isFind = false;
        StockDayBean stockDayBean  = (StockDayBean) stockDayList.get(dealOffset);
        Timestamp operTime = stockDayBean.getOperTime();
        for(int i=0;i<stockTransverseList.size();i++){
            StockTransverseBean swingBean = (StockTransverseBean) stockTransverseList.get(i);
            if(swingBean.getOperTime().before(operTime)){
                offset = i;
                isFind =true;
                break;
            }else{
                continue;
            }
        }
        List list = new ArrayList();
        if(isFind){
            list.add(((StockTransverseBean)stockTransverseList.get(offset)));
        }
        return list;
    }






//    private String getSwingDesc(float priceRate){
//        String result = "";
//        if(priceRate>1.5 ){
//            result ="巨涨";
//        }else if(priceRate>1.25){
//            result ="大涨";
//        }else if(priceRate>1.08){
//            result ="上涨";
//        }else if(priceRate>1){
//            result ="微涨";
//        }else  if(priceRate>-0.92){
//            result ="微跌";
//        }else  if(priceRate>0.85){
//            result ="下跌";
//        }else  if(priceRate>0.65){
//            result ="大跌";
//        }else {
//            result ="巨跌";
//        }
//        return result+",";
//    }

    private String getInflectionSwingDesc(float priceRate){
        String result = "";
        if(priceRate>500 ){
            result ="巨涨";
        }else if(priceRate>250){
            result ="大涨";
        }else if(priceRate>150){
            result ="上涨";
        }else if(priceRate>1){
            result ="微涨";
        }else  if(priceRate>-130){
            result ="微跌";
        }else  if(priceRate>-200){
            result ="下跌";
        }else  if(priceRate>-330){
            result ="大跌";
        }else {
            result ="巨跌";
        }
        return result;
    }



    private  boolean isConfirmInflection(StockDayInflectionBean inflectionBean,  List stockDayList,int dealOffset){
        StockDayBean stockDayBean  = (StockDayBean) stockDayList.get(dealOffset);
        StockDayBean tmpStockDayBean = null;
          if(inflectionBean.getSwingRate()>0){
              float minPrice = stockDayBean.getClosePrice();
              for(;;){
                  dealOffset -- ;
                  if(dealOffset<0){
                      break;
                  }
                  tmpStockDayBean = (StockDayBean) stockDayList.get(dealOffset);
                  if(tmpStockDayBean.getOperTime().after(inflectionBean.getOperTime())){
                      if(tmpStockDayBean.getClosePrice()<minPrice){
                          minPrice = tmpStockDayBean.getClosePrice();
                      }
                  }else{
                      break;
                  }
              }
              if( (minPrice-inflectionBean.getClosePrice())/inflectionBean.getClosePrice() <-0.08){
                  return true;
              }else{
                  return false;
              }
          }else{
              float maxPrice = stockDayBean.getClosePrice();
              for(;;){
                  dealOffset -- ;
                  if(dealOffset<0){
                      break;
                  }
                  tmpStockDayBean = (StockDayBean) stockDayList.get(dealOffset);
                  if(tmpStockDayBean.getOperTime().after(inflectionBean.getOperTime())){
                      if(tmpStockDayBean.getClosePrice()>maxPrice){
                          maxPrice = tmpStockDayBean.getClosePrice();
                      }
                  }else{
                      break;
                  }
              }
              if( (maxPrice-inflectionBean.getClosePrice())/inflectionBean.getClosePrice() >0.08){
                  return true;
              }else{
                  return false;
              }
          }
    }


    private void getStockRecentInfo(String stockCode){
        Map param = new HashMap();
        param.put("stockCode",stockCode);
        List stockInflectionList =   baseDao.queryForList("com.guoxc.info.dao.stockInflection.getInflectionNewInfoByOrder",param);
       for(int i=0;i<stockInflectionList.size();i++){

       }



    }

    private Float getMax(Float f1,Float f2){
        return f1> f2? f1:f2;
    }
    private Float getMin(Float f1,Float f2){
        return f1> f2? f2:f1;
    }

    private void saveHengPan(StockDayBean stockDayBean,Map stockHisMap){
        int price20Rate =   Math.round( (stockDayBean.getMaxC20Price()-stockDayBean.getMinC20Price())/stockDayBean.getMaxC20Price() );
        if(price20Rate<0.12){ //代表横盘
            if(stockHisMap.get("h_seq")==null){
                stockHisMap.put("h_seq",stockDayBean.getSeq());
                stockHisMap.put("h_eseq",stockDayBean.getSeq()-20);
                stockHisMap.put("h_maxprice",stockDayBean.getMaxC20Price());
                stockHisMap.put("h_minPrice",stockDayBean.getMinC20Price());
            }else{
                stockHisMap.put("h_eseq",(Long)stockHisMap.get("h_eseq") +1);
                stockHisMap.put("h_maxprice",getMax(stockDayBean.getClosePrice() , (float) stockHisMap.get("h_maxprice")));
                stockHisMap.put("h_minPrice",getMin(stockDayBean.getClosePrice() , (float) stockHisMap.get("h_minPrice")));
            }
        }
    }


    private  List getUpdayAndNormalVolList(List stockDayList){
        List result = new ArrayList();
        int count =0;
        long priceRate = 0;
        for(int i=0;i<stockDayList.size();i++){
            StockDayBean bean = (StockDayBean) stockDayList.get(i);
            //量比20日均量 小10% 且价格下降2%
            if(bean.getV20Rate()<-30 && bean.getPriceRate()<-20 && bean.getHighPrice()>bean.getLowPrice() && bean.getClosePrice()>4){
                priceRate = priceRate+bean.getPriceRate();
                count++;
            }else{
                priceRate =0;
                count = 0;
            }
            //联系
            if(count == 3){
                bean.setSmallInP(priceRate);
                result.add(bean);
                priceRate =0;
                count = 0;
            }
        }

        if(result.size()>0){
            baseDao.insert("com.guoxc.info.dao.StockDao.insertStockPredictBatch",result);
        }
        return result;

    }





    private void predict( StockDayBean bean,  List stockInflectionList ){
        Timestamp operTime = bean.getOperTime();
        List recentStockInflectionList = getRecentStockInflection(stockInflectionList, operTime);






    }

    private List getRecentStockInflection(List stockInflectionList, Timestamp operTime) {
        List recentStockInflectionList = new ArrayList();
        for(int i=stockInflectionList.size()-1; i>0; i--){
          StockDayInflectionBean tmpDayInflection =  (StockDayInflectionBean)  stockInflectionList.get(i);
          if(tmpDayInflection.getOperTime().before(operTime)){
              if(recentStockInflectionList.size()<8){
                  recentStockInflectionList.add(tmpDayInflection);
              }else{
                  break;
              }
          }
        }
        return  recentStockInflectionList;
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
                param.put("seq",currStockDayInflection.getSeq()-1);
                param.put("stockCode",currStockDayInflection.getStockCode());
                String getLastConfirmInflectionSql= "com.guoxc.info.dao.stockInflection.getLastConfirmInflectionNewInfo";
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
