package com.guoxc.info.web.control;

import com.alibaba.fastjson.JSON;
import com.guoxc.info.bean.info.StockDayBean;
import com.guoxc.info.bean.info.StockDayInflectionBean;
import com.guoxc.info.utils.DateUtil;
import com.guoxc.info.web.common.ConstantsInfo;
import com.guoxc.info.web.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.applet.Applet;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("/stock")
public class StockControl {
 private final static Logger logger = LoggerFactory.getLogger(StockControl.class);
	private final static String NOT_DEAL_STOCKCODE = "002188,600301,600228,002289,000752,000707,002336,600781,600891,002427,002420,600556,000953,600319,002323,600608,000611,002692,600698,000410,002175,600654,000972,600701,600807,600666,600423,000571,002356,002656,600275,600112,600265,002569,600385,600817,002072,000995,600421,600234,002290,603779,600696,600767,002629,002190,000409,600091,600209,600238,600732,000816,600856,002113,600725,600652,002604,000010,600462,002086,600526,000737,600289,600634,603188,600396,600687,600321,600614,600726,002711,600186,600084,002501,603996,600721,600119,000504,002220,600179,002021,000806,002359,600074,002499,000868,600408,002210,002200,000981,002684,600877,600870,002005,600595,000982,600485,600399,000939,002450,600518,601558,601258,000422,000897,002445,002642,000670,000585,002263,000792,600193,002122,002147,000760,002207,600677,000820,000572,600145,000971,002872,002766,002259,002333,601798,600610,600149,002260,002716,002102,600539,002586,002306,600290,000911,600247,600815,601113,002089,600265,000693,600234,000029,600401,600747,002477,002070,600680,000418,600240,002680,000018,002509,002143,002018,002470,";

	@Autowired
    private StockService stockService;

    @Autowired
    private BsStaticDataService bsStaticDataService;

   @Autowired
    private ThreadPool threadPool;

    @Autowired
   private  StockDayInflectionRecentService stockDayInflectionRecentService;

    @RequestMapping("/queryTest")
    @ResponseBody
    String queryTest() {
        String result = null;
        try {
             stockService.query();
            result = "success";
        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }
        return result;
    }



    @RequestMapping("/updateStockInfo")
    @ResponseBody
    String updateStockInfo() {
        String result = null;
        try {
            result = stockService.updateStockInfo();
        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }
        return result;
    }


    @RequestMapping("/saveStockData")
    @ResponseBody
    String saveStockData() {
        String result = null;
        try {
            result = stockService.putRecentData2StockData();
        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }
        return result;
    }

    @RequestMapping("/saveStockDataAna")
    @ResponseBody
    String saveStockDataAna() {
        String result = null;
        try {


            String dealZSDataTimeKey = "STOCK_DEAL_TIME_STOCK_DAY_ANA";
            StockDayBean bean = new StockDayBean();
            String lastDealDayStr =  (String) bsStaticDataService.selectOne(ConstantsInfo.SQLID.GET_CODEVALUE ,dealZSDataTimeKey) ;
            if (lastDealDayStr != null) {
                try {
                    Timestamp lastDealDay = DateUtil.convertStringToTimestamp(lastDealDayStr, "yyyy-MM-dd");  //lastDealDayStr
                    if (lastDealDay.before(DateUtil.getCurrentDate())) {

                    File[] files = new File("E:\\stock\\data\\dayadd\\").listFiles();
                    ThreadPoolExecutor executor = threadPool.getThreadPoolExecutor("STOCKMINDEAL");

                    for (File file : files) {

                        String fileName = file.getName();
                        String stockCode = fileName.substring(3,9);
                        if(!  (NOT_DEAL_STOCKCODE.contains(stockCode)|| stockCode.startsWith("300") || stockCode.startsWith("688"))){
                            SaveStockDataAnaThread thread = new       SaveStockDataAnaThread(file.getAbsolutePath(),lastDealDay,stockService);
                            executor.execute(thread);
                        }
                     }
                        bsStaticDataService.updateDealTime(dealZSDataTimeKey);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }
        return result;
    }


    @RequestMapping("/saveTmpStockDataAna")
    @ResponseBody
    String saveTmpStockDataAna() {
        String result = null;
        try {

            result =  stockService.saveStockTmpDay(new File("E:\\stock\\沪深Ａ股20200409.txt"));

        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }
        return result;
    }



    @RequestMapping("/moniterStock")
    @ResponseBody
    String moniterStock() {
        String result = null;
        try {

//            FileInputStream fileau=new FileInputStream("D:\\ffmpeg\\file\\11.mp3");
//
//            AudioStream as=new AudioStream(fileau);
//            AudioPlayer.player.start(as);


            File sound1=new File("D:\\ffmpeg\\file\\11.mp3");

            java.applet.AudioClip sound_choose=Applet.newAudioClip(sound1.toURL());
            sound_choose.loop();
            sound_choose.play();


        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }
        return result;
    }

    @RequestMapping("/saveStockDayInflectionNew")
    @ResponseBody
    String saveStockDayInflectionNew() {
        String result = null;
        try {


            String dayInflectionDealDataTimeKey = "STOCK_DEAL_TIME_STOCK_DAY_INFLECTION_NEW";
            StockDayBean bean = new StockDayBean();
            String lastDealDayStr =  (String) bsStaticDataService.selectOne(ConstantsInfo.SQLID.GET_CODEVALUE ,dayInflectionDealDataTimeKey) ;
            if (lastDealDayStr != null) {
                try {
                    Timestamp lastDealDay = DateUtil.convertStringToTimestamp(lastDealDayStr, "yyyy-MM-dd");
                    if (lastDealDay.before(DateUtil.getCurrentDate())) {
                        File[] files = new File("E:\\stock\\data\\dayadd\\").listFiles();
                       Map currStockDayInflectionMap =    stockService.queryCurrStockDayInflection();

                        ThreadPoolExecutor executor = threadPool.getThreadPoolExecutor("STOCKMINDEAL");

                        for (File file : files) {

                            String fileName = file.getName();
                            String stockCode = fileName.substring(3,9);

                            if(! (NOT_DEAL_STOCKCODE.contains(stockCode)|| stockCode.startsWith("300") || stockCode.startsWith("688"))){
                                SaveStockDayInfllectionThread thread = new       SaveStockDayInfllectionThread(file.getAbsolutePath(),lastDealDay,stockService,(StockDayInflectionBean)currStockDayInflectionMap.get(stockCode));
                                executor.execute(thread);
                            }
                        }
                        bsStaticDataService.updateDealTime(dayInflectionDealDataTimeKey);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }
        return result;
    }



    @RequestMapping("/saveStockDayInflectionRecent")
    @ResponseBody
    String saveStockDayInflectionRecent() {
        String result = null;
        try {
            stockDayInflectionRecentService.updateStockDayInflectionRecent();
        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }
        return result;
    }



    private boolean valiStockCode(String stockCode){
         boolean result = false;

         if("ALL".equals(stockCode)){
             result = true;
         }
         else if(stockCode !=null && stockCode.length()==8 && (stockCode.startsWith("SH")||stockCode.startsWith("SZ"))){
             result = true;
         }
         return result;
    }


    @RequestMapping("/saveStockZSData")
    @ResponseBody
    String saveStockZSData() {
        String result = null;
        try {
            result = stockService.saveRecentZSData2StockData();
        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }

        return result;
    }

    @RequestMapping("/saveStockZSMinuteData")
    @ResponseBody
    String saveStockZSMinuteData() {
        String result = null;
        try {
            result = stockService.saveStockZSMinuteData();
        } catch (Exception e) {
            logger.info("err1", e);
            result = "error";
        }

        return result;
    }

}
