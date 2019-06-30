package com.guoxc.info.web.services;

import com.guoxc.info.bean.info.BsStaticDataBean;
import com.guoxc.info.bean.info.StockDayBean;
import com.guoxc.info.dao.BsStaticDataDao;
import com.guoxc.info.dao.StockDao;
import com.guoxc.info.mapper.BsStaticDataMapper;
import com.guoxc.info.utils.DateUtil;
import com.guoxc.info.utils.FileUtil;
import com.guoxc.info.web.control.StockControl;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class StockService {
    private final static Logger logger = LoggerFactory.getLogger(StockControl.class);
    @Autowired
    private StockDao stockDao  ;

    @Autowired
    private BsStaticDataDao bsStaticDataDao;


    public String  putRecentData2StockData(){

        StockDayBean bean = new StockDayBean();
        String lastDealDayStr = bsStaticDataDao.getCodeValue("STOCK_DEAL_TIME_STOCK_DAY");
        if(lastDealDayStr != null){
            try {
                Timestamp lastDealDay = DateUtil.convertStringToTimestamp(lastDealDayStr,"yyyy-MM-dd");
                if(lastDealDay.before(DateUtil.getCurrentDate())){

                    File[] files = new File("E:\\stock\\data\\dayadd\\").listFiles();
                    for(File file :files ){
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



}
