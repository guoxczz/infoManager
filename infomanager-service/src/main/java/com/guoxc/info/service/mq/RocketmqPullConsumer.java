package com.guoxc.info.service.mq;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//@Component
public class RocketmqPullConsumer {
	
	 private static final Logger LOGGER = LoggerFactory.getLogger(RocketmqConsumer.class);
	    
	    @Value("${rocketmq.consumerGroup}")

	    private String consumerGroup;
	    
	    @Value("${rocketmq.namesrvAddr}")
	    private String namesrvAddr;
	    private final DefaultMQPullConsumer consumer = new DefaultMQPullConsumer(consumerGroup);
	    private static final Map<MessageQueue, Long> offsetTable = new HashMap<MessageQueue, Long>();

	    
	    /**
	     * 初始化
	     *
	     * @throws MQClientException
	     */
	    @PostConstruct
    public void start() throws MQClientException {

        consumer.setNamesrvAddr(namesrvAddr);
        consumer.start();
        Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues("PushTopic");
        for (MessageQueue mq : mqs) {
            System.out.println("Consume from the queue: " + mq);

            SINGLE_MQ: while (true) {
                try {
                    PullResult pullResult = consumer.pullBlockIfNotFound(mq, null, getMessageQueueOffset(mq), 32);
                    System.out.println(pullResult);
                    putMessageQueueOffset(mq, pullResult.getNextBeginOffset());
                    switch (pullResult.getPullStatus()) {
                    case FOUND:
                        // TODO
                        break;
                    case NO_MATCHED_MSG:
                        break;
                    case NO_NEW_MSG:
                        break SINGLE_MQ;
                    case OFFSET_ILLEGAL:
                        break;
                    default:
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        consumer.shutdown();

    }
	    
	    
	    
    private void putMessageQueueOffset(MessageQueue mq, long offset) {
        offsetTable.put(mq, offset);
    }

    private long getMessageQueueOffset(MessageQueue mq) {
        Long offset = offsetTable.get(mq);
        if (offset != null) return offset;
        return 0;
    }
	    

}
