package com.guoxc.info.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.guoxc.info.iservice.IInfoManagerSV;
import com.gxc.info.bean.info.StudentInfo;

@Service
public class InfoManagerImpl implements IInfoManagerSV{
	
	 private final static Logger logger = LoggerFactory.getLogger(InfoManagerImpl.class);
	

	public StudentInfo queryStudentInfo(Long id){
		
		StudentInfo stu = new StudentInfo ();
		stu.setId(1000L);
		stu.setName("zhansan");
		stu.setPhone("15838377578");
		logger.info("result:"+JSON.toJSONString(stu));
		
		return stu;
		
	}

	
}
