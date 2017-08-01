package com.framework.ehcache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.framework.interfaces.IAddressClusterManager;
import com.framework.interfaces.ServerInfo;
import com.framework.utils.MessageUtil;
import com.framework.utils.GsonUtil;
import com.typesafe.config.Config;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class EhcacheAddressClusterManger implements IAddressClusterManager {
	

	private Cache serverCache;
	
	private static final Map<String,ServerInfo> MAP = new ConcurrentHashMap<>();
	
	@Override
	public void init() {
		// 缓存管理器
		CacheManager manager = CacheManager.newInstance(EhcacheAddressClusterManger.class.getResourceAsStream("/ehcache-rmi.xml"));// new
		System.out.println(manager.getActiveConfigurationText());
		// 取出所有的cacheName
		String names[] = manager.getCacheNames();
		for (String name : names) {
			System.out.println("config cacheName:" + name);// 输出所有Cache名称
		}
		serverCache = manager.getCache("serverCache");
		Config childConfig = MessageUtil.CONFIG.getConfig("ServerSys");
		Config addressConfig = childConfig.getConfig("akka").getConfig("remote").getConfig("netty.tcp");
		String hostname = addressConfig.getString("hostname");
		int port = addressConfig.getInt("port");
		String serverId = MessageUtil.CONFIG.getString("serverId");
		String serverAddress = "akka.tcp://ServerSystem@" + hostname + ":" + port + "/user/serverActor";
		String serverIp = "ServerSys.akka.remote.netty.tcp.hostname=" + hostname;
		ServerInfo info = new ServerInfo();
		info.setAddress(serverAddress);
		info.setIp(serverIp);
		info.setServerId(serverId);
		info.setPort(port);
		String json = GsonUtil.beanToJson(info);
		serverCache.put(new Element(info.getServerId(), json));
	}

	@Override
	public ServerInfo getServerInfo(String serverName) {
		ServerInfo info = MAP.get(serverName);
		if(info==null){
			Element element = serverCache.get(serverName);
			if(element!=null){
				info = GsonUtil.jsonToBean(element.getObjectValue().toString(), ServerInfo.class);
				MAP.put(serverName, info);
			}
		}
		return info;
	}

}
