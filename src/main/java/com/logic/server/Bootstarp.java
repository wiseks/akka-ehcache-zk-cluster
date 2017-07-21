package com.logic.server;

import com.framework.ehcache.EhcacheAddressClusterManger;
import com.framework.interfaces.IAddressClusterManager;
import com.framework.utils.BeanUtils;

public class Bootstarp {

	public static void main(String[] args) {
		BeanUtils.init();
		IAddressClusterManager manager = new EhcacheAddressClusterManger();
		manager.init();
		
	}
}
