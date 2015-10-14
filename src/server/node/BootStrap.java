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
 * 鍒濆鍖栫郴缁�
 * 
 * @author SYJ
 */
public class BootStrap {

	public BootStrap() {

	}

	public static void main(String[] args) {

		if (!ConfigManager.getInstance().load()) {
			System.exit(1);
		} else {
			System.out.println("**load node config OK");
		}

		// if (!StorageManager.getInstance().load()) {
		// System.exit(1);
		// } else {
		// System.out.println("**load storage config OK");
		// }

		TaskCenter.getInstance().setMaxThread(ConfigManager.getInstance().maxTasks);
		TaskCenter.getInstance().setWindowTPS(ConfigManager.getInstance().tasksPerSecond);

		System.out.println("**Server TAG : " + ConfigManager.getInstance().tag);

		// if (!JedisUtilJson.getInstance().init()) {
		// System.out.println("!!!!!redisJson init error");
		// Clock.stop();
		// System.exit(1);
		// } else {
		// System.out.println("**init redisJson is OK");
		// }

		// if (!DBManager.getInstance().initConnPools()) {
		// System.out.println("!!!!!dbmanager init error");
		// Clock.stop();
		// System.exit(1);
		// } else {
		// System.out.println("**init mysql OK");
		// }

		try {
			System.out.println("child system start...");
			boolean b = Root.getInstance().initAndStartSystem();
			if (!b) {
				System.out.println("!!!!!child system start failed");
				System.exit(1);
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("!!!!! child system start error");
			e.printStackTrace();
		}
		System.out.println("**child system start success");

		int actionNum = ActionFactory.getInstance().registerActions("server.node.action");
		System.out.println("**game action num is : " + actionNum);

		int actionManagerNum = ActionFactory.getInstance().registerManagerActions("server.node.managerAction");
		System.out.println("**manager action num is : " + actionManagerNum);

		NodeServer.getInstance().startup();

	}

}
