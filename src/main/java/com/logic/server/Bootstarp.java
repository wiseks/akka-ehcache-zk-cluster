package com.logic.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.framework.utils.MessageUtil;
import com.message.MessageMsg.UserInfoMsg_23001;

@SpringBootApplication
@ComponentScan(basePackages={"com"})  
public class Bootstarp {

	public static void main(String[] args) {
		SpringApplication.run(Bootstarp.class, args);
		UserInfoMsg_23001.Builder userInfo = UserInfoMsg_23001.newBuilder();
		userInfo.setId(1);
		userInfo.setName("server 1 send to server 2");
		for(int i=0;i<100;i++){
			MessageUtil.sendToServer(userInfo.build(), "2");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
