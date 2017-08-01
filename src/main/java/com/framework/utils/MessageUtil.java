package com.framework.utils;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

@Service
public class MessageUtil {

	private static CommandDispatcher commandDispatcher;

	public static Config CONFIG = ConfigFactory.load("master-application.conf");

	private static ActorSystem ACTORSYSTEM = ActorSystem.create("ServerSystem", CONFIG.getConfig("ServerSys"));

	
	public static Object dispatcher(GeneratedMessageLite message){
		return commandDispatcher.dispatch(message);
	}
	
	public static void sendToServer(GeneratedMessageLite message, ServerInfo serverInfo) {
		if (serverInfo != null) {
			ActorSelection remoteActor = ACTORSYSTEM.actorSelection(serverInfo.getAddress());
			remoteActor.tell(message, ActorRef.noSender());
		} else {
			System.out.println("serverInfo is null");
		}
	}

	public static Future<Object> askToServer(GeneratedMessageLite message, ServerInfo serverInfo) {
		ActorSelection remoteActor = ACTORSYSTEM.actorSelection(serverInfo.getAddress());
		Future<Object> future = Patterns.ask(remoteActor, message,
				new Timeout(FiniteDuration.create(5, TimeUnit.SECONDS)));
		return future;
	}

	public static void processFuture(IFutureProcesser processer, Future<Object> future) {
		ExecutionContext ec = ACTORSYSTEM.dispatcher();
		future.onSuccess(new OnSuccess<Object>() {
			@Override
			public void onSuccess(Object obj) throws Throwable {
				processer.process(obj);
			}
		}, ec);
	}
	
	@PostConstruct
	public void init() {
		ACTORSYSTEM.actorOf(Props.create(AkkaMasterActor.class), "serverActor");
	}

	@Autowired
	private void setCommandDispatcher(CommandDispatcher dis) {
		commandDispatcher = dis;
	}
}
