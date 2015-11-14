package server.node.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gamecore.task.TaskCenter;
import server.node.system.account.AccountSystem;
import server.node.system.ids.IdsSystem;
import server.node.system.player.PlayerSystem;
import server.node.system.session.SessionSystem;
import server.node.system.trigger.TriggerSystem;

/**
 * 系统根，所有子系统引导类。
 */
public final class Root {

	private final static Logger logger = LogManager.getLogger(Root.class.getName());

	private final static Root instance = new Root();

	private boolean run = true;

	public static IdsSystem idsSystem = null;
	public static SessionSystem sessionSystem = null;
	public static AccountSystem accountSystem = null;
	public static PlayerSystem playerSystem = null;

	public static TriggerSystem triggerSystem = null;

	private Root() {
	}

	public static Root getInstance() {
		return Root.instance;
	}

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	public boolean initAndStartSystem() {

		System.out.println("all child system startup ....");
		idsSystem = new IdsSystem();
		sessionSystem = new SessionSystem();
		accountSystem = new AccountSystem();
		playerSystem = new PlayerSystem();
		triggerSystem = new TriggerSystem();

		// 启动子系统
		if (!idsSystem.startup()) {
			logger.error("idsSystem startup failed");
			return false;
		}
		if (!accountSystem.startup()) {
			logger.error("accountSystem startup failed");
			return false;
		}
		if (!playerSystem.startup()) {
			logger.error("playerSystem startup failed");
			return false;
		}
		if (!sessionSystem.startup()) {
			logger.error("sessionSystem startup failed");
			return false;
		}
		if (!triggerSystem.startup()) {
			logger.error("triggerSystem startup failed");
			return false;
		}

		System.out.println("all child system startup ....OK");

		return true;
	}

	public void shutdown() {

		if (null != idsSystem) {
			idsSystem.shutdown();
		}
		if (null != sessionSystem) {
			sessionSystem.shutdown();
		}
		if (null != accountSystem) {
			accountSystem.shutdown();
		}
		if (null != playerSystem) {
			playerSystem.shutdown();
		}
		if (null != triggerSystem) {
			triggerSystem.shutdown();
		}

		// 关闭任务中心
		TaskCenter.getInstance().close();
	}

}
