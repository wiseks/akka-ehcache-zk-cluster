package com.framework.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

import com.framework.interfaces.IAddressClusterManager;
import com.framework.interfaces.ServerInfo;
import com.framework.utils.BeanUtils;
import com.framework.utils.GsonUtil;
import com.framework.utils.Utils;
import com.typesafe.config.Config;

public class ZKClusterManager implements IAddressClusterManager {

	private CuratorFramework client;
	
	@Override
	public void init() {
		String connectString = "127.0.0.1:2181";  
	    // 连接时间 和重试次数  
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3); 
		client = CuratorFrameworkFactory.newClient(connectString, 5000, 5000, retryPolicy);
	    String PATH = "/1";  
	    
	    Config childConfig = BeanUtils.CONFIG.getConfig("ServerSys");
		Config addressConfig = childConfig.getConfig("akka").getConfig("remote").getConfig("netty.tcp");
		String hostname = addressConfig.getString("hostname");
		int port = addressConfig.getInt("port");
		String serverId = BeanUtils.CONFIG.getString("serverId");
		String serverAddress = "akka.tcp://ServerSystem@" + hostname + ":" + port + "/user/serverActor";
		String serverIp = "ServerSys.akka.remote.netty.tcp.hostname=" + hostname;
		ServerInfo info = new ServerInfo();
		info.setAddress(serverAddress);
		info.setIp(serverIp);
		info.setServerId(serverId);
		info.setPort(port);
		String string = GsonUtil.beanToJson(info);
	    client.start();  
        try {
        	Stat stat = client.checkExists().forPath(PATH);
        	if(stat!=null){
        		client.delete().forPath(PATH);
        	}
        	client.create().forPath(PATH, string.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}

	@Override
	public ServerInfo getServerInfo(String serverName) {
		try {
			String path = "/"+serverName;
			Stat stat = client.checkExists().forPath(path);
			if(stat!=null){
				byte[] bs = client.getData().forPath("/"+serverName);
				String str = new String(bs);
				ServerInfo info = GsonUtil.jsonToBean(str, ServerInfo.class);
				return info;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
