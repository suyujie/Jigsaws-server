package server.node.system;

import java.util.List;

import gamecore.cache.redis.JedisUtilJson;
import server.node.system.friend.FriendBag;
import server.node.system.gift.GiftBag;
import server.node.system.notice.NoticeBag;
import server.node.system.player.Player;
import server.node.system.session.Session;
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

	public static FriendBag getFriendBag(Long id) {
		return (FriendBag) JedisUtilJson.getInstance().get(FriendBag.generateCacheKey(id), FriendBag.class);
	}

	public static GiftBag getGiftBag(Long id) {
		return (GiftBag) JedisUtilJson.getInstance().get(GiftBag.generateCacheKey(id), GiftBag.class);
	}

	public static NoticeBag getNoticeBag(Long id) {
		return (NoticeBag) JedisUtilJson.getInstance().get(NoticeBag.generateCacheKey(id), NoticeBag.class);
	}

	public static Toturial getToturial(Long id) {
		return (Toturial) JedisUtilJson.getInstance().get(Toturial.generateCacheKey(id), Toturial.class);
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

}
