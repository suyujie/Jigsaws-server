package server.node.system;

import gamecore.cache.redis.JedisUtilJson;
import server.node.system.gameImage.GameImage;
import server.node.system.player.Player;
import server.node.system.session.Session;

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

	public static Session getSession(String sessionId) {
		return (Session) JedisUtilJson.getInstance().get(Session.generateCacheKey(sessionId), Session.class);
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

	public static GameImage getGameImage(Long id) {
		return (GameImage) JedisUtilJson.getInstance().get(GameImage.generateCacheKey(id), GameImage.class);
	}

	public static Long getWaitImageIdSet(Integer tag) {
		return (Long) JedisUtilJson.getInstance().setRandGet("image_id_" + tag, Long.class);
	}

	public static void addWaitImageIdSet(Integer tag, Long id) {
		JedisUtilJson.getInstance().setAdd("image_id_" + tag, id);
	}

	public static void removeWaitImageIdSet(Integer tag, Long id) {
		JedisUtilJson.getInstance().setRemove("image_id_" + tag, id);
	}

}
