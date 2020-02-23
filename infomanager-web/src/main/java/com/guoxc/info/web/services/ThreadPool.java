package com.guoxc.info.web.services;

import com.guoxc.info.utils.StringUtil;
import com.guoxc.info.web.control.StockControl;
import com.guoxc.info.web.dao.BaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class ThreadPool {
    private final static Logger logger = LoggerFactory.getLogger(ThreadPool.class);
    @Autowired
    private BaseDao baseDao;

    private Map<String,ThreadPoolExecutor > executorPoolMap = new ConcurrentHashMap<String ,ThreadPoolExecutor>();



    public ThreadPoolExecutor getThreadPoolExecutor(String name){
        if(StringUtil.isNull(name)){
            name = "defaultPool";
        }
        ThreadPoolExecutor executorPool = executorPoolMap.get(name);
        if(executorPool == null){
           String poolCfg = (String) baseDao.selectOne("com.guoxc.info.dao.BsStaticDataDao.getCodeValue","THREAD_POOL_CFG_"+name);
           if( StringUtil.isNotBlank(poolCfg) && poolCfg.split("_").length>=3){
               String[] cfgs = poolCfg.split("_");
               int corePoolSize = Integer.parseInt(cfgs[0]);
               int maximumPoolSize = Integer.parseInt(cfgs[1]);
               long keepAliveTime = Long.parseLong(cfgs[2]);
               int queueCapacity = Integer.parseInt(cfgs[3]);
               executorPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,  TimeUnit.MILLISECONDS,
                       new LinkedBlockingQueue<Runnable>(queueCapacity));

           }

        }

        return  executorPool;
    }



}
