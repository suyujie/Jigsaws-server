package server.node.system;

import gamecore.task.TaskCenter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.account.AccountSystem;
import server.node.system.battle.PveSystem;
import server.node.system.battle.PvpSystem;
import server.node.system.berg.BergSystem;
import server.node.system.blackList.BlackListSystem;
import server.node.system.blackMarket.BlackMarketSystem;
import server.node.system.buff.BuffSystem;
import server.node.system.chip.ChipSystem;
import server.node.system.color.ColorSystem;
import server.node.system.dailyJob.JobSystem;
import server.node.system.expPart.ExpPartSystem;
import server.node.system.feedback.FeedbackSystem;
import server.node.system.friend.FriendSystem;
import server.node.system.gameEvents.bergWheel.BergWheelSystem;
import server.node.system.gameEvents.chipDeathWheel.DeathWheelSystem;
import server.node.system.gameEvents.treasureIsland.TreasureIslandSystem;
import server.node.system.gamePrice.GamePriceSystem;
import server.node.system.gift.GiftSystem;
import server.node.system.handbook.HandbookSystem;
import server.node.system.ids.IdsSystem;
import server.node.system.lang.LangSystem;
import server.node.system.log.LogSystem;
import server.node.system.lottery.LotterySystem;
import server.node.system.mission.MissionSystem;
import server.node.system.monthCard.MonthCardSystem;
import server.node.system.notice.NoticeSystem;
import server.node.system.npc.NpcSystem;
import server.node.system.opponent.DefenderSystem;
import server.node.system.opponent.OpponentSystem;
import server.node.system.opponent.ProtectSystem;
import server.node.system.pay.PaySystem;
import server.node.system.player.PlayerSystem;
import server.node.system.push.PushSystem;
import server.node.system.ranking.RankingSystem;
import server.node.system.rechargePackage.RechargePackageSystem;
import server.node.system.record.RecordSystem;
import server.node.system.rent.RentOrderSystem;
import server.node.system.robot.RobotSystem;
import server.node.system.robotPart.PartSystem;
import server.node.system.session.SessionSystem;
import server.node.system.task.TaskSystem;
import server.node.system.toturial.ToturialSystem;
import server.node.system.trigger.TriggerSystem;
import server.node.system.version.VersionSystem;

/**
 * 系统根，所有子系统引导类。
 */
public final class Root {

	private final static Logger logger = LogManager.getLogger(Root.class.getName());

	private final static Root instance = new Root();

	private boolean run = true;

	public static VersionSystem versionSystem = null;
	public static IdsSystem idsSystem = null;
	public static LangSystem langSystem = null;
	public static GamePriceSystem gamePriceSystem = null;
	public static BlackListSystem blackListSystem = null;
	public static SessionSystem sessionSystem = null;
	public static AccountSystem accountSystem = null;
	public static PlayerSystem playerSystem = null;
	public static BuffSystem buffSystem = null;
	public static PartSystem partSystem = null;
	public static ExpPartSystem expPartSystem = null;
	public static RobotSystem robotSystem = null;
	public static RechargePackageSystem rechargePackageSystem = null;
	public static MonthCardSystem monthCardSystem = null;
	public static RentOrderSystem rentOrderSystem = null;
	public static MissionSystem missionSystem = null;
	public static PveSystem pveSystem = null;
	public static ColorSystem colorSystem = null;
	public static ChipSystem chipSystem = null;
	public static BergSystem bergSystem = null;
	public static DeathWheelSystem deathWheelSystem = null;
	public static BergWheelSystem bergWheelSystem = null;
	public static TreasureIslandSystem treasureIslandSystem = null;
	public static PvpSystem pvpSystem = null;
	public static RecordSystem recordSystem = null;
	public static LotterySystem lotterySystem = null;
	public static OpponentSystem opponentSystem = null;
	public static DefenderSystem defenderSystem = null;
	public static ProtectSystem protectSystem = null;
	public static NpcSystem npcSystem = null;
	public static TaskSystem taskSystem = null;
	public static JobSystem jobSystem = null;
	public static ToturialSystem toturialSystem = null;
	public static LogSystem logSystem = null;
	public static FriendSystem friendSystem = null;
	public static GiftSystem giftSystem = null;
	public static PushSystem pushSystem = null;
	public static FeedbackSystem feedbackSystem = null;
	public static NoticeSystem noticeSystem = null;
	public static HandbookSystem handbookSystem = null;
	public static RankingSystem rankingSystem = null;
	public static BlackMarketSystem blackMarketSystem = null;

	public static TriggerSystem triggerSystem = null;
	public static PaySystem paySystem = null;

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
		//初始化子系统
		versionSystem = new VersionSystem();
		idsSystem = new IdsSystem();
		langSystem = new LangSystem();
		blackListSystem = new BlackListSystem();
		gamePriceSystem = new GamePriceSystem();
		sessionSystem = new SessionSystem();
		accountSystem = new AccountSystem();
		playerSystem = new PlayerSystem();
		buffSystem = new BuffSystem();
		partSystem = new PartSystem();
		expPartSystem = new ExpPartSystem();
		robotSystem = new RobotSystem();
		rechargePackageSystem = new RechargePackageSystem();
		monthCardSystem = new MonthCardSystem();
		rentOrderSystem = new RentOrderSystem();
		missionSystem = new MissionSystem();
		pveSystem = new PveSystem();
		colorSystem = new ColorSystem();
		chipSystem = new ChipSystem();
		bergSystem = new BergSystem();
		deathWheelSystem = new DeathWheelSystem();
		bergWheelSystem = new BergWheelSystem();
		treasureIslandSystem = new TreasureIslandSystem();
		opponentSystem = new OpponentSystem();
		protectSystem = new ProtectSystem();
		defenderSystem = new DefenderSystem();
		pvpSystem = new PvpSystem();
		recordSystem = new RecordSystem();
		lotterySystem = new LotterySystem();
		npcSystem = new NpcSystem();
		taskSystem = new TaskSystem();
		jobSystem = new JobSystem();
		toturialSystem = new ToturialSystem();
		friendSystem = new FriendSystem();
		triggerSystem = new TriggerSystem();
		giftSystem = new GiftSystem();
		pushSystem = new PushSystem();
		feedbackSystem = new FeedbackSystem();
		logSystem = new LogSystem();
		noticeSystem = new NoticeSystem();
		rankingSystem = new RankingSystem();
		handbookSystem = new HandbookSystem();
		blackMarketSystem = new BlackMarketSystem();
		paySystem = new PaySystem();

		//启动子系统
		if (!versionSystem.startup()) {
			logger.error("versionSystem startup failed");
			return false;
		}
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
		if (!gamePriceSystem.startup()) {
			logger.error("gamePriceSystem startup failed");
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
		if (!buffSystem.startup()) {
			logger.error("buffSystem startup failed");
			return false;
		}
		if (!partSystem.startup()) {
			logger.error("partSystem startup failed");
			return false;
		}
		if (!expPartSystem.startup()) {
			logger.error("expPartSystem startup failed");
			return false;
		}
		if (!robotSystem.startup()) {
			logger.error("robotSystem startup failed");
			return false;
		}
		if (!rechargePackageSystem.startup()) {
			logger.error("rechargePackageSystem startup failed");
			return false;
		}
		if (!monthCardSystem.startup()) {
			logger.error("monthCardSystem startup failed");
			return false;
		}
		if (!rentOrderSystem.startup()) {
			logger.error("rentOrderSystem startup failed");
			return false;
		}
		if (!missionSystem.startup()) {
			logger.error("pointSystem startup failed");
			return false;
		}
		if (!pveSystem.startup()) {
			logger.error("battleSystem startup failed");
			return false;
		}
		if (!colorSystem.startup()) {
			logger.error("colorSystem startup failed");
			return false;
		}
		if (!chipSystem.startup()) {
			logger.error("chipSystem startup failed");
			return false;
		}
		if (!bergSystem.startup()) {
			logger.error("bergSystem startup failed");
			return false;
		}
		if (!deathWheelSystem.startup()) {
			logger.error("deathWheelSystem startup failed");
			return false;
		}
		if (!bergWheelSystem.startup()) {
			logger.error("bergWheelSystem startup failed");
			return false;
		}
		if (!treasureIslandSystem.startup()) {
			logger.error("treasureIslandSystem startup failed");
			return false;
		}
		if (!pvpSystem.startup()) {
			logger.error("pvpSystem startup failed");
			return false;
		}
		if (!opponentSystem.startup()) {
			logger.error("opponentSystem startup failed");
			return false;
		}
		if (!defenderSystem.startup()) {
			logger.error("defenderSystem startup failed");
			return false;
		}
		if (!protectSystem.startup()) {
			logger.error("protectSystem startup failed");
			return false;
		}
		if (!recordSystem.startup()) {
			logger.error("recordSystem startup failed");
			return false;
		}
		if (!lotterySystem.startup()) {
			logger.error("lotterySystem startup failed");
			return false;
		}
		if (!npcSystem.startup()) {
			logger.error("npcSystem startup failed");
			return false;
		}
		if (!taskSystem.startup()) {
			logger.error("taskSystem startup failed");
			return false;
		}
		if (!jobSystem.startup()) {
			logger.error("jobSystem startup failed");
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
		if (!rankingSystem.startup()) {
			logger.error("rankingSystem startup failed");
			return false;
		}
		if (!handbookSystem.startup()) {
			logger.error("handbookSystem startup failed");
			return false;
		}
		if (!blackMarketSystem.startup()) {
			logger.error("blackMarketSystem startup failed");
			return false;
		}
		if (!paySystem.startup()) {
			logger.error("paySystem startup failed");
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

		if (null != versionSystem) {
			versionSystem.shutdown();
		}
		if (null != langSystem) {
			langSystem.shutdown();
		}
		if (null != blackListSystem) {
			blackListSystem.shutdown();
		}
		if (null != gamePriceSystem) {
			gamePriceSystem.shutdown();
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
		if (null != buffSystem) {
			buffSystem.shutdown();
		}
		if (null != partSystem) {
			partSystem.shutdown();
		}
		if (null != expPartSystem) {
			expPartSystem.shutdown();
		}
		if (null != robotSystem) {
			robotSystem.shutdown();
		}
		if (null != rechargePackageSystem) {
			rechargePackageSystem.shutdown();
		}
		if (null != monthCardSystem) {
			monthCardSystem.shutdown();
		}
		if (null != rentOrderSystem) {
			rentOrderSystem.shutdown();
		}
		if (null != missionSystem) {
			missionSystem.shutdown();
		}
		if (null != colorSystem) {
			colorSystem.shutdown();
		}
		if (null != chipSystem) {
			chipSystem.shutdown();
		}
		if (null != bergSystem) {
			bergSystem.shutdown();
		}
		if (null != deathWheelSystem) {
			deathWheelSystem.shutdown();
		}
		if (null != bergWheelSystem) {
			bergWheelSystem.shutdown();
		}
		if (null != treasureIslandSystem) {
			treasureIslandSystem.shutdown();
		}
		if (null != pvpSystem) {
			pvpSystem.shutdown();
		}
		if (null != opponentSystem) {
			opponentSystem.shutdown();
		}
		if (null != defenderSystem) {
			defenderSystem.shutdown();
		}
		if (null != protectSystem) {
			protectSystem.shutdown();
		}
		if (null != recordSystem) {
			recordSystem.shutdown();
		}
		if (null != lotterySystem) {
			lotterySystem.shutdown();
		}
		if (null != npcSystem) {
			npcSystem.shutdown();
		}
		if (null != taskSystem) {
			taskSystem.shutdown();
		}
		if (null != jobSystem) {
			jobSystem.shutdown();
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
		if (null != rankingSystem) {
			rankingSystem.shutdown();
		}
		if (null != handbookSystem) {
			handbookSystem.shutdown();
		}
		if (null != paySystem) {
			paySystem.shutdown();
		}

		// 关闭任务中心
		TaskCenter.getInstance().close();
	}

}
