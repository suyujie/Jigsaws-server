package server.node.system.gameEvents.treasureIsland;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;
import gamecore.util.Clock;
import gamecore.util.DateUtils;
import server.node.system.player.Player;

public final class TreasureIsland extends AbstractEntity {

	private static final long serialVersionUID = 3062582914109954725L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "treasure_island_";

	private long id;

	private long createTime;//生成时间,秒
	private int completedNum;//完成次数,考虑将来可能一天可以多次..战斗真正的结束后,才+1
	private TreasureIslandType type;//0 金钱   1expId
	private int resetNum;//重置次数
	private int bossLevel;

	private Integer allCash;
	private Integer expId;
	private Integer expNum;

	public TreasureIsland() {
	}

	public TreasureIsland(long id, Player player, int bossLevel, long createTime, int completedNum, int resetNum) {
		super(generateCacheKey(player.getId()));
		this.id = id;
		this.bossLevel = bossLevel;
		this.createTime = createTime;
		this.completedNum = completedNum;
		this.resetNum = resetNum;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TreasureIslandType getType() {
		return type;
	}

	public void setType(TreasureIslandType type) {
		this.type = type;
	}

	public int getBossLevel() {
		return bossLevel;
	}

	public void setBossLevel(int bossLevel) {
		this.bossLevel = bossLevel;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getCompletedNum() {
		return completedNum;
	}

	public void setCompletedNum(int completedNum) {
		this.completedNum = completedNum;
	}

	public int getResetNum() {
		return resetNum;
	}

	public void setResetNum(int resetNum) {
		this.resetNum = resetNum;
	}

	public Integer getAllCash() {
		return allCash;
	}

	public void setAllCash(Integer allCash) {
		this.allCash = allCash;
	}

	public Integer getExpId() {
		return expId;
	}

	public void setExpId(Integer expId) {
		this.expId = expId;
	}

	public Integer getExpNum() {
		return expNum;
	}

	public void setExpNum(Integer expNum) {
		this.expNum = expNum;
	}

	public boolean checkTimeOut() {
		if (DateUtils.isSameDay(createTime * 1000, Clock.currentTimeMillis())) {//同一天,没过期
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 生成存储键。玩家id为key
	 */
	public static String generateCacheKey(Long playerId) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(TreasureIsland.CKPrefix).append(playerId).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	/** 实例数据写入缓存。
	 */
	public void synchronize() {
		synchronized (this) {
			JedisUtilJson.getInstance().setForHour(getCacheKey(), this, 24);
		}
	}

}
