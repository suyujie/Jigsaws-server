package server.node.system.battle;

import gamecore.entity.AbstractEntity;
import gamecore.util.Clock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import server.node.system.player.Player;
import server.node.system.robot.Robot;

/**
 * 战斗
 */
public final class ChipBattle extends AbstractEntity {

	private static final long serialVersionUID = 2051313654117794209L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "chip_battle_";

	private long enterTime;//进战斗的时间

	private Map<Long, Robot> attackRobots;//参战机器人-自己的

	public ChipBattle() {
	}

	public ChipBattle(Player player) {
		super(ChipBattle.generateCacheKey(player.getId()));
		this.enterTime = Clock.currentTimeSecond();
		this.attackRobots = new HashMap<Long, Robot>();
	}

	public long getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(long enterTime) {
		this.enterTime = enterTime;
	}

	public void setAttackRobots(Map<Long, Robot> attackRobots) {
		this.attackRobots = attackRobots;
	}

	public Map<Long, Robot> getAttackRobots() {
		return attackRobots;
	}

	public void addAttackRobot(Robot robot) {
		if (!attackRobots.containsKey(robot.getId())) {
			attackRobots.put(robot.getId(), robot);
		}
	}

	public List<Robot> readAttackRobotArray() {
		return new ArrayList<Robot>(attackRobots.values());
	}

	public static String generateCacheKey(Long playerId) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(ChipBattle.CKPrefix).append(playerId).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

}
