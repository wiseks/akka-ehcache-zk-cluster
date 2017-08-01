package com.logic.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.framework.interfaces.IAddressClusterManager;
import com.framework.interfaces.ServerInfo;
import com.framework.utils.BeanUtils;
import com.framework.zk.ZKClusterManager;
import com.message.MessageMsg.UserInfoMsg_23001;

@SpringBootApplication
@ComponentScan(basePackages={"com"})  
public class ZKBootstarp1 {

	public static void main(String[] args) {
		SpringApplication.run(ZKBootstarp1.class, args);
		BeanUtils.init();
		IAddressClusterManager manager = new ZKClusterManager();
		manager.init();
		UserInfoMsg_23001.Builder userInfo = UserInfoMsg_23001.newBuilder();
		userInfo.setId(1);
		userInfo.setName("server 1 send to server 2");
		for(int i=0;i<100;i++){
			ServerInfo serverInfo = manager.getServerInfo("2");
			BeanUtils.sendToServer(userInfo.build(), serverInfo);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
