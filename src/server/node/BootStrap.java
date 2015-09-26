package server.node;

import gamecore.action.ActionFactory;
import gamecore.cache.redis.JedisUtilJson;
import gamecore.db.DBManager;
import gamecore.task.TaskCenter;
import gamecore.util.Clock;
import server.node.system.ConfigManager;
import server.node.system.Root;
import server.node.system.StorageManager;

/**
 * 初始化系统
 * @author SYJ
 */
public class BootStrap {

	public BootStrap() {

	}

	public static void main(String[] args) {

		//加载服务配置文件
		if (!ConfigManager.getInstance().load()) {
			System.exit(1);
		} else {
			System.out.println("**load node config OK");
		}

		//加载服务配置文件
//		if (!StorageManager.getInstance().load()) {
//			System.exit(1);
//		} else {
//			System.out.println("**load storage config OK");
//		}

		//部分配置
		TaskCenter.getInstance().setMaxThread(ConfigManager.getInstance().maxTasks);
		TaskCenter.getInstance().setWindowTPS(ConfigManager.getInstance().tasksPerSecond);

		// 报告标签
		System.out.println("**Server TAG : " + ConfigManager.getInstance().tag);

		//初始化redis缓存
//		if (!JedisUtilJson.getInstance().init()) {
//			System.out.println("!!!!!redisJson init error");
//			Clock.stop();
//			System.exit(1);
//		} else {
//			System.out.println("**init redisJson is OK");
//		}

		//初始化数据库连接池
//		if (!DBManager.getInstance().initConnPools()) {
//			System.out.println("!!!!!dbmanager init error");
//			Clock.stop();
//			System.exit(1);
//		} else {
//			System.out.println("**init mysql OK");
//		}

		//启动子系统
//		try {
//			System.out.println("child system start...");
//			boolean b = Root.getInstance().initAndStartSystem();
//			if (!b) {
//				System.out.println("!!!!!child system start failed");
//				System.exit(1);
//				throw new Exception();
//			}
//		} catch (Exception e) {
//			System.out.println("!!!!! child system start error");
//			e.printStackTrace();
//		}
		System.out.println("**child system start success");

		//初始化action
		int actionNum = ActionFactory.getInstance().registerActions("server.node.action");
		System.out.println("**game action num is : " + actionNum);

		//初始化管理action
		int actionManagerNum = ActionFactory.getInstance().registerManagerActions("server.node.managerAction");
		System.out.println("**manager action num is : " + actionManagerNum);

		//启动服务
		NodeServer.getInstance().startup();

	}

}
