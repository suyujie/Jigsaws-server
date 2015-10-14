package server.node.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gamecore.task.TaskCenter;
import server.node.system.account.AccountSystem;
import server.node.system.blackList.BlackListSystem;
import server.node.system.feedback.FeedbackSystem;
import server.node.system.friend.FriendSystem;
import server.node.system.gift.GiftSystem;
import server.node.system.ids.IdsSystem;
import server.node.system.lang.LangSystem;
import server.node.system.log.LogSystem;
import server.node.system.notice.NoticeSystem;
import server.node.system.player.PlayerSystem;
import server.node.system.push.PushSystem;
import server.node.system.session.SessionSystem;
import server.node.system.toturial.ToturialSystem;
import server.node.system.trigger.TriggerSystem;

/**
 * 系统根，所有子系统引导类。
 */
public final class Root {

	private final static Logger logger = LogManager.getLogger(Root.class.getName());

	private final static Root instance = new Root();

	private boolean run = true;

	public static IdsSystem idsSystem = null;
	public static LangSystem langSystem = null;
	public static BlackListSystem blackListSystem = null;
	public static SessionSystem sessionSystem = null;
	public static AccountSystem accountSystem = null;
	public static PlayerSystem playerSystem = null;
	public static ToturialSystem toturialSystem = null;
	public static LogSystem logSystem = null;
	public static FriendSystem friendSystem = null;
	public static GiftSystem giftSystem = null;
	public static PushSystem pushSystem = null;
	public static FeedbackSystem feedbackSystem = null;
	public static NoticeSystem noticeSystem = null;

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
		langSystem = new LangSystem();
		blackListSystem = new BlackListSystem();
		sessionSystem = new SessionSystem();
		accountSystem = new AccountSystem();
		playerSystem = new PlayerSystem();
		toturialSystem = new ToturialSystem();
		friendSystem = new FriendSystem();
		triggerSystem = new TriggerSystem();
		giftSystem = new GiftSystem();
		pushSystem = new PushSystem();
		feedbackSystem = new FeedbackSystem();
		logSystem = new LogSystem();
		noticeSystem = new NoticeSystem();

		// 启动子系统
		if (!idsSystem.startup()) {
			logger.error("idsSystem startup failed");
			return false;
		}
		if (!langSystem.startup()) {
			logger.error("versionSystem startup failed");
			return false;
		}
		if (!blackListSystem.startup()) {
			logger.error("blackListSystem startup failed");
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
		if (!toturialSystem.startup()) {
			logger.error("toturialSystem startup failed");
			return false;
		}
		if (!logSystem.startup()) {
			logger.error("logSystem startup failed");
			return false;
		}
		if (!friendSystem.startup()) {
			logger.error("friendSystem startup failed");
			return false;
		}
		if (!giftSystem.startup()) {
			logger.error("giftSystem startup failed");
			return false;
		}
		if (!pushSystem.startup()) {
			logger.error("pushSystem startup failed");
			return false;
		}
		if (!feedbackSystem.startup()) {
			logger.error("pushSystem startup failed");
			return false;
		}
		if (!noticeSystem.startup()) {
			logger.error("noticeSystem startup failed");
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

		if (null != langSystem) {
			langSystem.shutdown();
		}
		if (null != blackListSystem) {
			blackListSystem.shutdown();
		}
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
		if (null != toturialSystem) {
			toturialSystem.shutdown();
		}
		if (null != logSystem) {
			logSystem.shutdown();
		}
		if (null != friendSystem) {
			friendSystem.shutdown();
		}
		if (null != giftSystem) {
			giftSystem.shutdown();
		}
		if (null != pushSystem) {
			pushSystem.shutdown();
		}
		if (null != feedbackSystem) {
			feedbackSystem.shutdown();
		}
		if (null != triggerSystem) {
			triggerSystem.shutdown();
		}
		if (null != noticeSystem) {
			noticeSystem.shutdown();
		}

		// 关闭任务中心
		TaskCenter.getInstance().close();
	}

}
