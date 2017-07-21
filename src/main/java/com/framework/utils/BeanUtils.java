package com.framework.utils;

import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.framework.akka.AkkaMasterActor;
import com.framework.dispatch.CommandDispatcher;
import com.framework.interfaces.IFutureProcesser;
import com.framework.interfaces.ServerInfo;
import com.google.protobuf.GeneratedMessageLite;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;


public class BeanUtils {

	public static CommandDispatcher commandDispatcher = new CommandDispatcher();
	
	public static final ApplicationContext APPLICATION_CONTEXT = new ClassPathXmlApplicationContext("application.xml");
	public static final Config CONFIG = ConfigFactory.load("master-application.conf");
	private static final ActorSystem ACTORSYSTEM = ActorSystem.create("ServerSystem", CONFIG.getConfig("ServerSys"));
	
	
	public static void init(){
		commandDispatcher.init(APPLICATION_CONTEXT);
		ACTORSYSTEM.actorOf(Props.create(AkkaMasterActor.class), "serverActor");
	}
	
	public static void sendToServer(GeneratedMessageLite message,ServerInfo serverInfo){
		ActorSelection remoteActor = ACTORSYSTEM.actorSelection(serverInfo.getAddress());
		remoteActor.tell(message, ActorRef.noSender());
	}
	
	public static Future<Object> askToServer(GeneratedMessageLite message,ServerInfo serverInfo){
		ActorSelection remoteActor = ACTORSYSTEM.actorSelection(serverInfo.getAddress());
		Future<Object> future = Patterns.ask(remoteActor, message,new Timeout(FiniteDuration.create(5, TimeUnit.SECONDS)));
		return future;
	}
	
	public static void processFuture(IFutureProcesser processer,Future<Object> future){
		ExecutionContext ec = ACTORSYSTEM.dispatcher();
		future.onSuccess(new OnSuccess<Object>() {
			@Override
			public void onSuccess(Object obj) throws Throwable {
				processer.process(obj);
			}
		}, ec);
	}
	
	
}
