package com.logic.server;

import com.framework.utils.MessageUtil;
import com.message.MessageMsg.UserInfoMsg_23001;

public class Bootstarp {

	public static void main(String[] args) {
		UserInfoMsg_23001.Builder userInfo = UserInfoMsg_23001.newBuilder();
		userInfo.setId(1);
		userInfo.setName("tom");
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
