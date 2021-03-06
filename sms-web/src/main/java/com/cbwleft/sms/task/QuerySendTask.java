package com.cbwleft.sms.task;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cbwleft.sms.dao.model.Message;
import com.cbwleft.sms.service.IBatchQueryable;
import com.cbwleft.sms.service.IChannelSMSService;
import com.cbwleft.sms.service.IMessageService;

@Component
public class QuerySendTask {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IMessageService messageService;

	@Autowired
	private IChannelSMSService channelSMSService;

	@Scheduled(fixedDelay = 5 * 60 * 1000)
	public void querySend() {
		logger.debug("开始查询短信发送结果");
		if (channelSMSService instanceof IBatchQueryable) {
			((IBatchQueryable) channelSMSService).batchQueryAndUpdateSendStatus();
		}else {
			Date yesterday = Date.from(Instant.now().minus(Duration.ofDays(1)));
			List<Message> list = messageService.querySendingMessages(yesterday);
			list.forEach(messageService::queryAndUpdateSendStatus);
		}
	}

}
