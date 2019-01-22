package com.guoxc.info.web.control;

//import org.apache.rocketmq.client.exception.MQClientException;
//import org.apache.rocketmq.client.producer.DefaultMQProducer;
//import org.apache.rocketmq.common.message.Message;
//import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.guoxc.info.iservice.IInfoManagerSV;
import com.guoxc.info.bean.info.StudentInfo;

@RestController
@RequestMapping("/info")
public class InfoManagerControl {
 private final static Logger logger = LoggerFactory.getLogger(InfoManagerControl.class);
	
 @Reference
   private IInfoManagerSV infoManagerSV;
//   @Autowired
//    private DefaultMQProducer defaultMQProducer;
  
	@RequestMapping("/hello")
    @ResponseBody
    String hello() {
      return "Hello World!";
    }
	
	
	@RequestMapping("/query")
    @ResponseBody
    String queryInfo() {
	  
//		StudentInfo stu = new StudentInfo ();
//		stu.setId(1000L);
//		stu.setName("zhansan");
//		stu.setPhone("15838377578");
//		logger.info("result:"+JSON.toJSONString(stu));
		StudentInfo queryStu=	infoManagerSV.queryStudentInfo(1000L);
//		  MQSendCallBack  sendCallBack = new MQSendCallBack();
//		   Message message = new Message("topic-test","tag",JSON.toJSONString(queryStu).getBytes());   
//	
//		try {
//            defaultMQProducer.send(message,sendCallBack );
//        } catch (MQClientException | RemotingException | InterruptedException e) {
//            logger.error("" ,e);
//        }
//		
		logger.info("queryStu:"+JSON.toJSONString(queryStu));
		
      return JSON.toJSONString(queryStu);
    }

}
