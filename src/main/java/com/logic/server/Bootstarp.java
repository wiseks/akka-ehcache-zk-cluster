package com.logic.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.framework.utils.MessageUtil;
import com.message.MessageMsg.UserInfoMsg_23001;

@Configuration
@ImportResource(locations={"classpath:application-bean.xml"})
@SpringBootApplication
@ComponentScan(basePackages={"com"})  
public class Bootstarp {

	public static void main(String[] args) {
		SpringApplication.run(Bootstarp.class, args);
		UserInfoMsg_23001.Builder userInfo = UserInfoMsg_23001.newBuilder();
		userInfo.setId(1);
		userInfo.setName("ssss 1 send to sssss 2");
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
