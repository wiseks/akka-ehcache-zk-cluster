package com.framework.akka;

import org.apache.log4j.Logger;

import com.framework.utils.BeanUtils;
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

	Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof String){
			logger.info(PropertiesUtils.get("serverId")+">>>>>>>>>>"+message);
		}else if(message instanceof GeneratedMessageLite){
			Object response = BeanUtils.commandDispatcher.dispatch((GeneratedMessageLite)message);
			if(response!=null){
				getSender().tell(response, getSelf());
			}
		}
	}
}
