package com.guoxc.info.web.task;

import com.guoxc.info.utils.DateUtil;
import com.guoxc.info.web.control.StockControl;
import com.guoxc.info.web.services.StockMoniterService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
@EnableScheduling
public class ScheduledTask {
    private final static Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    private StockMoniterService stockMoniterService;

    @Scheduled(cron = "0,3,6,9,12,15,18,21,24,27,30,33,36,39,42,45,48,51,54,57 * 10,13 * * *") //每10秒执行一次
    public void scheduledTaskByCorn9() {
        if(isWeek()){
            return;
          }
        stockMoniterService.moniterStock();
//        scheduledTask();
    }


    @Scheduled(cron = "0,3,6,9,12,15,18,21,24,27,30,33,36,39,42,45,48,51,54,57 0-56 14 * * *") //每10秒执行一次
    public void scheduledTaskByCorn14() {
        if(isWeek()){
            return;
        }
        stockMoniterService.moniterStock();
//        scheduledTask();
    }



    @Scheduled(cron = "6 0 15 * * *") //每10秒执行一次
    public void scheduledTaskByCorn15() {
        if(isWeek()){
            return;
        }
        stockMoniterService.moniterStock();
//        scheduledTask();
    }



    @Scheduled(cron = "0,3,6,9,12,15,18,21,24,27,30,33,36,39,42,45,48,51,54,57 30-59 9 * * *") // 9点 每10秒执行一次
    public void scheduledTaskByCorn() {
        if(isWeek()){
            return;
        }
        stockMoniterService.moniterStock();
//        scheduledTask();
    }


    @Scheduled(cron = "0 20 9 * * *") // 9点 每10秒执行一次
    public void scheduledTaskByCorn20() {
        if(isWeek()){
            return;
        }
        stockMoniterService.moniterStock();
//        scheduledTask();
    }

    @Scheduled(cron = "30 25 9 * * *") // 9点 每10秒执行一次
    public void scheduledTaskByCorn25() {
        if(isWeek()){
            return;
        }
        stockMoniterService.moniterStock();
//        scheduledTask();
    }

    @Scheduled(cron = "0,3,6,9,12,15,18,21,24,27,30,33,36,39,42,45,48,51,54,57 0-30 11 * * *") //每10秒执行一次
    public void scheduledTaskByCorn11() {
        if(isWeek()){
            return;
        }
        stockMoniterService.moniterStock();
//        scheduledTask();
    }



//    @Scheduled(cron = "0 0/1 10,13-14 * * *") //每10秒执行一次
    public void moniterTransUp() {
        if(isWeek()){
            return;
        }
        stockMoniterService.moniterStockTranservesUp();
//        scheduledTask();
    }



//    @Scheduled(cron = "0 30/1 9 * * *") // 9点 每10秒执行一次
    public void moniterTransUp9() {
        if(isWeek()){
            return;
        }
        stockMoniterService.moniterStockTranservesUp();
//        scheduledTask();
    }

    @Scheduled(cron = "0 0-30 11 * * *") //每10秒执行一次
    public void moniterTransUp11() {
        if(isWeek()){
            return;
        }
        stockMoniterService.moniterStockTranservesUp();
//        scheduledTask();
    }

  //  @Scheduled(cron = "30 45 15-20 * * *")
    public void saveStockFenbiDetailStock() {
        stockMoniterService.saveStockFenbiDetail();
//        scheduledTask();
    }




//    @Scheduled(cron = "0/10 0 10,13,14,15 * * ?") //每10秒执行一次
//    public void scheduledTaskByCorn2() {
//        scheduledTask();
//    }

//    @Scheduled(fixedRate = 10000) //每10秒执行一次
//    public void scheduledTaskByFixedRate() {
//
//    }

    private void scheduledTask() {
        try {
            logger.info(DateUtil.getCurrDate("yyyy-MM-dd HH:mm:ss") +" ok");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isWeek(){
        boolean result = false ;
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek ==1 || dayOfWeek ==7){
            result =true;
        }
        return result;

    }

}
