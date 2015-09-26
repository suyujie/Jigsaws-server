package gamecore.cache.redis;

import java.util.List;

import server.node.system.battle.PveBattle;
import server.node.system.battle.PvpBattle;
import server.node.system.berg.BergBag;
import server.node.system.blackMarket.BlackMarket;
import server.node.system.chip.ChipBag;
import server.node.system.color.ColorBag;
import server.node.system.dailyJob.JobBag;
import server.node.system.egg.EggPartBag;
import server.node.system.expPart.ExpPartBag;
import server.node.system.friend.FriendBag;
import server.node.system.gameEvents.bergWheel.BergWheel;
import server.node.system.gameEvents.chipDeathWheel.DeathWheel;
import server.node.system.gameEvents.chipDeathWheel.SelectChipsInDeathWheel;
import server.node.system.gameEvents.treasureIsland.TreasureIsland;
import server.node.system.gift.GiftBag;
import server.node.system.handbook.HandbookBag;
import server.node.system.lottery.LotteryBag;
import server.node.system.mission.MissionBag;
import server.node.system.mission.PointFlushBag;
import server.node.system.monthCard.MonthCard;
import server.node.system.notice.NoticeBag;
import server.node.system.npc.NpcPlayer;
import server.node.system.opponent.PvpLoseBag;
import server.node.system.opponent.RepeatOpponentBag;
import server.node.system.player.Player;
import server.node.system.player.PlayerChangeBean;
import server.node.system.rechargePackage.RechargePackageBag;
import server.node.system.record.RecordBag;
import server.node.system.rent.RentOrder;
import server.node.system.rent.RentOrderBag;
import server.node.system.robot.RobotBag;
import server.node.system.robotPart.PartBag;
import server.node.system.session.Session;
import server.node.system.task.TaskBag;
import server.node.system.toturial.Toturial;

/** 
 * 缓存redis 帮助类
 */
public final class RedisHelperJson {

	private RedisHelperJson() {
	}

	public static void setBytes(String key, byte[] bytes, int sec) {
		JedisUtilJson.getInstance().setBytes(key, bytes, sec);
	}

	public static byte[] getBytes(String key) {
		return JedisUtilJson.getInstance().getBytes(key);
	}

	public static <T> T get(String key, Class<T> t) {
		return (T) JedisUtilJson.getInstance().get(key, t);
	}

	public static void removeRentOrder(String key) {
		JedisUtilJson.getInstance().del(key);
	}

	public static void removeEntity(String key) {
		JedisUtilJson.getInstance().del(key);
	}

	public static Session getSession(String mobileId) {
		return (Session) JedisUtilJson.getInstance().get(Session.generateCacheKey(mobileId), Session.class);
	}

	public static void removeSession(String mobileId) {
		JedisUtilJson.getInstance().del(Session.generateCacheKey(mobileId));
	}

	public static Player getPlayer(Long id) {
		return (Player) JedisUtilJson.getInstance().get(Player.generateCacheKey(id), Player.class);
	}

	public static void removePlayer(Long id) {
		JedisUtilJson.getInstance().del(Player.generateCacheKey(id));
	}

	public static RobotBag getRobotBag(Long id) {
		return (RobotBag) JedisUtilJson.getInstance().get(RobotBag.generateCacheKey(id), RobotBag.class);
	}

	public static ExpPartBag getExpPartBag(Long id) {
		return (ExpPartBag) JedisUtilJson.getInstance().get(ExpPartBag.generateCacheKey(id), ExpPartBag.class);
	}

	public static PartBag getPartBag(Long id) {
		return (PartBag) JedisUtilJson.getInstance().get(PartBag.generateCacheKey(id), PartBag.class);
	}

	public static BergBag getBergBag(Long id) {
		return (BergBag) JedisUtilJson.getInstance().get(BergBag.generateCacheKey(id), BergBag.class);
	}

	public static RechargePackageBag getRechargePackageBag(Long id) {
		return (RechargePackageBag) JedisUtilJson.getInstance().get(RechargePackageBag.generateCacheKey(id), RechargePackageBag.class);
	}

	public static EggPartBag getEggPartBag(Long id) {
		return (EggPartBag) JedisUtilJson.getInstance().get(EggPartBag.generateCacheKey(id), EggPartBag.class);
	}

	public static PointFlushBag getPointFlushBag(Long id) {
		return (PointFlushBag) JedisUtilJson.getInstance().get(PointFlushBag.generateCacheKey(id), PointFlushBag.class);
	}

	public static ColorBag getColorBag(Long id) {
		return (ColorBag) JedisUtilJson.getInstance().get(ColorBag.generateCacheKey(id), ColorBag.class);
	}

	public static ChipBag getChipBag(Long id) {
		return (ChipBag) JedisUtilJson.getInstance().get(ChipBag.generateCacheKey(id), ChipBag.class);
	}

	public static SelectChipsInDeathWheel getSelectChipsInDeathWheel(Long id) {
		return (SelectChipsInDeathWheel) JedisUtilJson.getInstance().get(SelectChipsInDeathWheel.generateCacheKey(id), SelectChipsInDeathWheel.class);
	}

	public static MissionBag getMissionBag(Long id) {
		return (MissionBag) JedisUtilJson.getInstance().get(MissionBag.generateCacheKey(id), MissionBag.class);
	}

	public static JobBag getJobBag(Long id) {
		return (JobBag) JedisUtilJson.getInstance().get(JobBag.generateCacheKey(id), JobBag.class);
	}

	public static DeathWheel getDeathWheel(Long id) {
		return (DeathWheel) JedisUtilJson.getInstance().get(DeathWheel.generateCacheKey(id), DeathWheel.class);
	}

	public static BergWheel getBergWheel(Long id) {
		return (BergWheel) JedisUtilJson.getInstance().get(BergWheel.generateCacheKey(id), BergWheel.class);
	}

	public static TreasureIsland getTreasureIsland(Long id) {
		return (TreasureIsland) JedisUtilJson.getInstance().get(TreasureIsland.generateCacheKey(id), TreasureIsland.class);
	}

	public static TaskBag getTaskBag(Long id) {
		return (TaskBag) JedisUtilJson.getInstance().get(TaskBag.generateCacheKey(id), TaskBag.class);
	}

	public static BlackMarket getBlackMarket(Long id) {
		return (BlackMarket) JedisUtilJson.getInstance().get(BlackMarket.generateCacheKey(id), BlackMarket.class);
	}

	public static FriendBag getFriendBag(Long id) {
		return (FriendBag) JedisUtilJson.getInstance().get(FriendBag.generateCacheKey(id), FriendBag.class);
	}

	public static GiftBag getGiftBag(Long id) {
		return (GiftBag) JedisUtilJson.getInstance().get(GiftBag.generateCacheKey(id), GiftBag.class);
	}

	public static PveBattle getPveBattle(Long id) {
		return (PveBattle) JedisUtilJson.getInstance().get(PveBattle.generateCacheKey(id), PveBattle.class);
	}

	public static LotteryBag getLotteryBag(Long id) {
		return (LotteryBag) JedisUtilJson.getInstance().get(LotteryBag.generateCacheKey(id), LotteryBag.class);
	}

	public static MonthCard getMonthCard(Long id) {
		return (MonthCard) JedisUtilJson.getInstance().get(MonthCard.generateCacheKey(id), MonthCard.class);
	}

	public static NoticeBag getNoticeBag(Long id) {
		return (NoticeBag) JedisUtilJson.getInstance().get(NoticeBag.generateCacheKey(id), NoticeBag.class);
	}

	public static PvpBattle getPvpBattle(Long id) {
		return (PvpBattle) JedisUtilJson.getInstance().get(PvpBattle.generateCacheKey(id), PvpBattle.class);
	}

	public static void removePvpBattle(Long id) {
		JedisUtilJson.getInstance().del(PvpBattle.generateCacheKey(id));
	}

	public static Toturial getToturial(Long id) {
		return (Toturial) JedisUtilJson.getInstance().get(Toturial.generateCacheKey(id), Toturial.class);
	}

	public static NpcPlayer getNpcPlayer(Long id) {
		return (NpcPlayer) JedisUtilJson.getInstance().get(NpcPlayer.generateCacheKey(id), NpcPlayer.class);
	}

	public static boolean existsOpponent(Integer cup) {
		return JedisUtilJson.getInstance().exists("opponent_" + cup);
	}

	public static Long getOpponent(Integer cup) {
		return (Long) JedisUtilJson.getInstance().setRandGet("opponent_" + cup, Long.class);
	}

	public static void addOpponent(Integer cup, Long id) {
		JedisUtilJson.getInstance().setAdd("opponent_" + cup, id);
	}

	public static void removeOpponent(Integer cup, Long id) {
		JedisUtilJson.getInstance().setRemove("opponent_" + cup, id);
	}

	public static void addWaitRentOrder(Integer cup, String rentOrderKey) {
		JedisUtilJson.getInstance().setAdd("waitOrder_" + cup, rentOrderKey);
	}

	public static void removeWaitRentOrder(Integer cup, String rentOrderKey) {
		JedisUtilJson.getInstance().setRemove("waitOrder_" + cup, rentOrderKey);
	}

	public static List<String> getWaitRentOrder(Integer cup, int num) {
		return JedisUtilJson.getInstance().setRandGet("waitOrder_" + cup, num, String.class);
	}

	public static void addRankingScore(String area, Integer score, long playerId, long robotId) {
		JedisUtilJson.getInstance().sortedSetAdd("rank_score_" + area, playerId + "_" + robotId, score);
	}

	public static List<String> getRankingScore(String area, int num) {
		return JedisUtilJson.getInstance().sortedSetGet("rank_score_" + area, 0, num, false, String.class);
	}

	public static void addRankingCup(String area, Integer cup, Long playerId) {
		JedisUtilJson.getInstance().sortedSetAdd("rank_cup_" + area, playerId, cup);
	}

	public static List<Long> getRankingCup(String area, int num) {
		return JedisUtilJson.getInstance().sortedSetGet("rank_cup_" + area, 0, num, false, Long.class);
	}

	public static RentOrderBag getRentOrderBag(Long id) {
		return (RentOrderBag) JedisUtilJson.getInstance().get(RentOrderBag.generateCacheKey(id), RentOrderBag.class);
	}

	public static RentOrder getRentOrder(Long id, int slot) {//直接从缓存中获取订单
		return (RentOrder) JedisUtilJson.getInstance().get(RentOrder.generateCacheKey(id, slot), RentOrder.class);
	}

	public static RentOrder getRentOrder(String key) {//直接从缓存中获取订单
		return (RentOrder) JedisUtilJson.getInstance().get(key, RentOrder.class);
	}

	public static RecordBag getRecordBag(Long id) {
		return (RecordBag) JedisUtilJson.getInstance().get(RecordBag.generateCacheKey(id), RecordBag.class);
	}

	public static PlayerChangeBean getPlayerChangeBean(Long playerId) {
		return (PlayerChangeBean) JedisUtilJson.getInstance().get(PlayerChangeBean.generateCacheKey(playerId), PlayerChangeBean.class);
	}

	public static void removePlayerChangeBean(Long playerId) {
		JedisUtilJson.getInstance().del(PlayerChangeBean.generateCacheKey(playerId));
	}

	public static RepeatOpponentBag getRepeatOpponentBag(Long id) {
		return (RepeatOpponentBag) JedisUtilJson.getInstance().get(RepeatOpponentBag.generateCacheKey(id), RepeatOpponentBag.class);
	}

	public static HandbookBag getHandbookBag(Long id) {
		return (HandbookBag) JedisUtilJson.getInstance().get(HandbookBag.generateCacheKey(id), HandbookBag.class);
	}

	public static PvpLoseBag getPvpLoseBag(Long id) {
		return (PvpLoseBag) JedisUtilJson.getInstance().get(PvpLoseBag.generateCacheKey(id), PvpLoseBag.class);
	}

	public static void removePvpLoseBag(Long id) {
		JedisUtilJson.getInstance().del(PvpLoseBag.generateCacheKey(id));
	}

}
