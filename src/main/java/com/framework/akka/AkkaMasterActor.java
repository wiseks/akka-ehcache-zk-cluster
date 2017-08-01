package com.framework.akka;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.framework.utils.MessageUtil;
import com.framework.utils.PropertiesUtils;
import com.google.protobuf.GeneratedMessageLite;

import akka.actor.UntypedAbstractActor;

/**
 * ClassName: MasterActor <br/>
 * Function: 接受client消息 <br/>
 * date: 2016年8月5日 下午5:33:40 <br/>
 *
 * @author siinger
 * @version 
 * @since JDK 1.7
 */
public class AkkaMasterActor extends UntypedAbstractActor {

	private static final Log logger = LogFactory.getLog(AkkaMasterActor.class);

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof String){
			logger.info(PropertiesUtils.get("serverId")+">>>>>>>>>>"+message);
		}else if(message instanceof GeneratedMessageLite){
			Object response = MessageUtil.dispatcher((GeneratedMessageLite)message);
			if(response!=null){
				getSender().tell(response, getSelf());
			}
		}
	}
}
