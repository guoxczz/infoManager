package com.guoxc.info.web.services;

import com.guoxc.info.utils.DateUtil;
import com.guoxc.info.web.control.StockControl;
import com.guoxc.info.web.dao.BaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BsStaticDataService {

    private final static Logger logger = LoggerFactory.getLogger(StockControl.class);
    @Autowired
    private BaseDao baseDao;


    public Object selectOne(String sqlId, Object obj){
        return baseDao.selectOne(sqlId,obj);

    }

    public  void updateDealTime(String codeName){
        Map param = new HashMap();
        param.put("codeName",codeName);
        param.put("codeValue",DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN.YYYY_MM_DD));
        int result = baseDao.update("com.guoxc.info.dao.BsStaticDataDao.updateCodeValue",param);
        if(result == 0){
            logger.error("updateDealTime fail");
        }

    }

    public  void insertCodeInfo(String codeName,String codeValue){
        Map param = new HashMap();
        param.put("codeName",codeName);
        param.put("codeValue",codeValue);
        int result = baseDao.insert("com.guoxc.info.dao.BsStaticDataDao.insertCodeInfo",param);
        if(result == 0){
            logger.error("insertCodeInfo fail");
        }

    }



}
