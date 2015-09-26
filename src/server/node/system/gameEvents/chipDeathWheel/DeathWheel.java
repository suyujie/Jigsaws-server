package server.node.system.gameEvents.chipDeathWheel;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;
import gamecore.io.ByteArrayGameOutput;
import gamecore.util.Clock;
import gamecore.util.DateUtils;

import java.util.Map;

import server.node.system.player.Player;
import server.node.system.robot.Robot;

public final class DeathWheel extends AbstractEntity {

	private static final long serialVersionUID = 7766117118033197749L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "chip_w_";

	private long id;
	private Integer battleHardLevel;//正在战斗的这个难度
	private long createTime;//生成时间,秒
	private int resetNum;//重置过的次数
	private Map<Integer, Robot> attackRobots;

	private Map<Integer, DeathWheelBoss> bosses;//boss,5个难度的  0---4

	public DeathWheel() {
	}

	public DeathWheel(long id, Player player, long createTime, int resetNum, Map<Integer, DeathWheelBoss> bosses) {
		super(generateCacheKey(player.getId()));
		this.id = id;
		this.resetNum = resetNum;
		this.createTime = createTime;
		this.bosses = bosses;
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

	public int getResetNum() {
		return resetNum;
	}

	public void setResetNum(int resetNum) {
		this.resetNum = resetNum;
	}

	public Map<Integer, DeathWheelBoss> getBosses() {
		return bosses;
	}

	public void setBosses(Map<Integer, DeathWheelBoss> bosses) {
		this.bosses = bosses;
	}

	public void putBoss(DeathWheelBoss boss, boolean sync) {
		bosses.put(boss.getBossMakingId(), boss);
		if (sync) {
			this.synchronize();
		}
	}

	public Integer getBattleHardLevel() {
		return battleHardLevel;
	}

	public void setBattleHardLevel(Integer battleHardLevel) {
		this.battleHardLevel = battleHardLevel;
	}

	public Map<Integer, Robot> getAttackRobots() {
		return attackRobots;
	}

	public void setAttackRobots(Map<Integer, Robot> attackRobots) {
		this.attackRobots = attackRobots;
	}

	public DeathWheelBoss readCurrentBoss() {
		if (battleHardLevel == null) {
			return null;
		} else {
			return bosses.get(battleHardLevel);
		}
	}

	public boolean checkTimeOut() {
		if (DateUtils.isSameDay(createTime * 1000, Clock.currentTimeMillis())) {//同一天,没过期
			return false;
		} else {
			return true;
		}
	}

	public byte[] toByteArray(Player player) {
		ByteArrayGameOutput go = new ByteArrayGameOutput();
		try {
			go.putInt(getBosses().size());

			for (Integer hardLevel = 0; hardLevel < getBosses().size(); hardLevel++) {
				go.putBytesNoLength(getBosses().get(hardLevel).toByteArray(player));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return go.toByteArray();
	}

	/**
	 * 生成存储键。玩家id为key
	 */
	public static String generateCacheKey(Long playerId) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(DeathWheel.CKPrefix).append(playerId).toString();
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
