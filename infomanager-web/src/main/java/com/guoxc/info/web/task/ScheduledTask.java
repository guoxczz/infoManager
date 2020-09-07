package com.guoxc.info.web.task;

import com.guoxc.info.utils.DateUtil;
import com.guoxc.info.web.control.StockControl;
import com.guoxc.info.web.services.StockMoniterService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {
    private final static Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    private StockMoniterService stockMoniterService;

    @Scheduled(cron = "0,6,12,18,24,30,36,42,48,54 * 10,13-14 * * *") //每10秒执行一次
    public void scheduledTaskByCorn9() {
        stockMoniterService.moniterStock();
//        scheduledTask();
    }



    @Scheduled(cron = "0,6,12,18,24,30,36,42,48,54 30-59 9 * * *") // 9点 每10秒执行一次
    public void scheduledTaskByCorn() {
        stockMoniterService.moniterStock();
//        scheduledTask();
    }

    @Scheduled(cron = "0,6,12,18,24,30,36,42,48,54 0-30 11 * * *") //每10秒执行一次
    public void scheduledTaskByCorn11() {
        stockMoniterService.moniterStock();
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

}
