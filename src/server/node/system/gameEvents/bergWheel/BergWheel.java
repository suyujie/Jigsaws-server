package server.node.system.gameEvents.bergWheel;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;
import gamecore.util.Clock;
import gamecore.util.DateUtils;

import java.util.HashMap;
import java.util.List;

import server.node.system.player.Player;

public final class BergWheel extends AbstractEntity {

	private static final long serialVersionUID = 3062582914109954725L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "berg_w_";

	private long id;
	private long createTime;//生成时间,秒
	private Integer battleId;//战斗场次
	private int resetNum;//重置过的次数
	private int reliveNum;//复活次数
	private int completedNum;//完成次数,考虑将来可能一天可以多次..战斗真正的结束后,才+1

	private BergWheelBattle battle;//战斗数据

	private List<HashMap<String, Integer>> bergAwardFirst6;

	public BergWheel() {
	}

	public BergWheel(long id, Player player, long createTime, Integer battleId) {
		super(generateCacheKey(player.getId()));
		this.id = id;
		this.createTime = createTime;
		this.battleId = battleId;
		this.resetNum = 0;
		this.reliveNum = 0;
		this.completedNum = 0;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public Integer getBattleId() {
		return battleId;
	}

	public void setBattleId(Integer battleId) {
		this.battleId = battleId;
	}

	public int getReliveNum() {
		return reliveNum;
	}

	public void setReliveNum(int reliveNum) {
		this.reliveNum = reliveNum;
	}

	public int getResetNum() {
		return resetNum;
	}

	public void setResetNum(int resetNum) {
		this.resetNum = resetNum;
	}

	public BergWheelBattle getBattle() {
		return battle;
	}

	public void setBattle(BergWheelBattle battle) {
		this.battle = battle;
	}

	public List<HashMap<String, Integer>> getBergAwardFirst6() {
		return bergAwardFirst6;
	}

	public void setBergAwardFirst6(List<HashMap<String, Integer>> bergAwardFirst6) {
		this.bergAwardFirst6 = bergAwardFirst6;
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
			String ret = ckBuf.append(BergWheel.CKPrefix).append(playerId).toString();
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
