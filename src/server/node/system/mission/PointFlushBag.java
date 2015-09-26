package server.node.system.mission;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;
import gamecore.util.Clock;
import gamecore.util.DateUtils;

import java.util.HashMap;

import server.node.system.Content;

/**
 * 大关卡
 */
public final class PointFlushBag extends AbstractEntity {

	private static final long serialVersionUID = 6591995804593877875L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "point_flush_bag_";

	private HashMap<Integer, PointFlush> pointFlushes = new HashMap<Integer, PointFlush>();

	public PointFlushBag() {
	}

	public PointFlushBag(Long playerId) {
		super(generateCacheKey(playerId));
	}

	public HashMap<Integer, PointFlush> getPointFlushes() {
		return pointFlushes;
	}

	public void setPointFlushes(HashMap<Integer, PointFlush> pointFlushes) {
		this.pointFlushes = pointFlushes;
	}

	//刷关卡
	public void flushPoint(Integer pointMakingId, boolean sync) {
		PointFlush pointFlush = pointFlushes.get(pointMakingId);

		if (pointFlush == null) {
			pointFlush = new PointFlush();
			pointFlush.setFlushNum(1);
			pointFlush.setTime(Clock.currentTimeSecond());
		} else {
			//是否同一天
			if (DateUtils.isSameDay(Clock.currentTimeMillis(), pointFlush.getTime() * 1000)) {//同一天
				pointFlush.setFlushNum(pointFlush.getFlushNum() + 1);//次数+1
			} else {//不是同一天
				pointFlush.setFlushNum(1);
				pointFlush.setTime(Clock.currentTimeSecond());
			}
		}
		pointFlushes.put(pointMakingId, pointFlush);

		if (sync) {
			this.synchronize();
		}
	}

	public PointFlush readPointFlush(Integer pointMakingId) {
		PointFlush pointFlush = pointFlushes.get(pointMakingId);
		if (pointFlush == null) {
			return null;
		} else {
			if (DateUtils.isSameDay(Clock.currentTimeMillis(), pointFlush.getTime() * 1000)) {//同一天
				return pointFlush;//
			} else {//不是同一天
				return null;
			}
		}
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long playerId) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(PointFlushBag.CKPrefix).append(playerId).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

}
