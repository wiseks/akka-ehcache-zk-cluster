package com.framework.akka;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.framework.utils.MessageUtil;
import com.framework.utils.PropertiesUtils;
import com.google.protobuf.GeneratedMessageLite;

import akka.actor.ActorRef;
import akka.actor.UntypedAbstractActor;

/**
 * ClassName: MasterActor <br/>
 * Function: 接受client消息 <br/>
 *
 * @author wdj
 * @version 
 * @since JDK 1.7
 */
public class AkkaMasterActor extends UntypedAbstractActor {

	private static final Log logger = LogFactory.getLog(AkkaMasterActor.class);
	
	
	private ExecutorService service = Executors.newFixedThreadPool(10);

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof String){
			logger.info(PropertiesUtils.get("serverId")+">>>>>>>>>>"+message);
		}else if(message instanceof GeneratedMessageLite){
			service.submit(new DispatchThread((GeneratedMessageLite)message,getSender(),getSelf()));
		}
	}
	
	class DispatchThread implements Runnable {
		private GeneratedMessageLite command;
		private ActorRef senderRef;
		private ActorRef selfRef;

		DispatchThread(GeneratedMessageLite command, ActorRef senderRef,ActorRef selfRef) {
			this.command = command;
			this.senderRef = senderRef;
			this.selfRef = selfRef;
		}

		public void run() {
			try {
				Object response = MessageUtil.dispatcher(command);
				if(response!=null){
					senderRef.tell(response, selfRef);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
