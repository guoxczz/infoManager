package com.guoxc.info.web.services;

import com.guoxc.info.bean.info.StockDayInflectionBean;
import com.guoxc.info.bean.info.StockDayInflectionRecentBean;
import com.guoxc.info.web.control.StockControl;
import com.guoxc.info.web.dao.BaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockDayInflectionRecentService {

    private final static Logger logger = LoggerFactory.getLogger(StockControl.class);

    @Autowired
    private BsStaticDataService bsStaticDataService;
    @Autowired
    private BaseDao baseDao;

    public String updateStockDayInflectionRecent(){

        baseDao.update("com.guoxc.info.dao.stockInflection.truncateTable","T_STOCK_DAY_INFLECTION_RECENT");

      List list =  baseDao.queryForList("com.guoxc.info.dao.stockInflection.getRecentInflectionNewInfo", null);

      List recentList = new ArrayList();
      if(list != null)  {
          int j =0 ;
          String tmpstockCode = "";
          StockDayInflectionRecentBean  recentBean = new StockDayInflectionRecentBean();
          for(int i=0;i<list.size();i++){
              StockDayInflectionBean bean = (StockDayInflectionBean) list.get(i);
              String stockCode = bean.getStockCode();
              if(stockCode.equals(tmpstockCode)){
                     j++;
                   if(j==1){
                       recentBean.setR2ClosePrice(bean.getClosePrice());
                       recentBean.setR2IntervalDay(bean.getIntervalDay());
                       recentBean.setR2SwingRate(bean.getSwingRate());
                       recentBean.setR2UpdDownType(bean.getUpDownType());
                   }else if(j==2){
                       recentBean.setR3SwingRate(bean.getSwingRate());
                       recentBean.setR3IntervalDay(bean.getIntervalDay());
                       recentBean.setR3ClosePrice(bean.getClosePrice());
                   }else if(j==3){
                       recentBean.setR4SwingRate(bean.getSwingRate());
                       recentBean.setR4IntervalDay(bean.getIntervalDay());
                       recentBean.setR4ClosePrice(bean.getClosePrice());
                   }else if(j==4){
                       recentBean.setR5SwingRate(bean.getSwingRate());
                       recentBean.setR5IntervalDay(bean.getIntervalDay());
                       recentBean.setR5ClosePrice(bean.getClosePrice());
                   }else if(j==5){
                       recentBean.setR6SwingRate(bean.getSwingRate());
                       recentBean.setR6IntervalDay(bean.getIntervalDay());
                       recentBean.setR6ClosePrice(bean.getClosePrice());
                   }
              }else{
                  j=0;
                  tmpstockCode = stockCode;
                   recentBean = new StockDayInflectionRecentBean();
                   recentList.add(recentBean);
                  recentBean.setStockCode(stockCode);
                  recentBean.setLastTime(bean.getOperTime());
                  recentBean.setR1ClosePrice(bean.getClosePrice());
                  recentBean.setR1IntervalDay(bean.getIntervalDay());
                  recentBean.setR1SwingRate(bean.getSwingRate());
                  recentBean.setR1UpdDownType(bean.getUpDownType());
              }
          }

          logger.info("recentList size="+recentList.size());
          if(recentList.size()>0){

              baseDao.insert("com.guoxc.info.dao.stockInflection.insertStockInflectionRecentList" ,recentList);
          }
      }

        return "success";
    }

}
