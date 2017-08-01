package com.logic.server;

import com.framework.ehcache.EhcacheAddressClusterManger;
import com.framework.interfaces.IAddressClusterManager;
import com.framework.interfaces.ServerInfo;
import com.framework.utils.MessageUtil;
import com.message.MessageMsg.UserInfoMsg_23001;

public class Bootstarp {

	public static void main(String[] args) {
		IAddressClusterManager manager = new EhcacheAddressClusterManger();
		manager.init();
		UserInfoMsg_23001.Builder userInfo = UserInfoMsg_23001.newBuilder();
		userInfo.setId(1);
		userInfo.setName("tom");
		for(int i=0;i<100;i++){
			ServerInfo serverInfo = manager.getServerInfo("1");
			MessageUtil.sendToServer(userInfo.build(), serverInfo);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
