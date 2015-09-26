package server.node.system.battle;

import gamecore.entity.AbstractEntity;
import gamecore.util.Clock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import server.node.system.mission.Point;
import server.node.system.npc.NpcRobot;
import server.node.system.player.Player;
import server.node.system.robot.Robot;

/**
 * 战斗
 */
public final class PveBattle extends AbstractEntity {

	private static final long serialVersionUID = 7036966400796112407L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "pve_";

	private int missionId;
	private Point pointPO;
	private long enterTime;//进战斗的时间

	private Map<Long, Robot> attackRobots;//参战机器人-自己的
	private Robot attackHireRobot;//参战机器人-租借的
	private NpcRobot attackNpcRobot;//参战机器人-租借的npc
	private boolean consumeWear;//是否扣过耐久

	public PveBattle() {
	}

	public PveBattle(Player player, int missionId, Point pointPO) {
		super(PveBattle.generateCacheKey(player.getId()));
		this.missionId = missionId;
		this.pointPO = pointPO;
		this.enterTime = Clock.currentTimeSecond();
		this.attackRobots = new HashMap<Long, Robot>();
	}

	public int getMissionId() {
		return missionId;
	}

	public void setMissionId(int missionId) {
		this.missionId = missionId;
	}

	public Point getPointPO() {
		return pointPO;
	}

	public void setPointPO(Point pointPO) {
		this.pointPO = pointPO;
	}

	public long getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(long enterTime) {
		this.enterTime = enterTime;
	}

	public Robot getAttackHireRobot() {
		return attackHireRobot;
	}

	public void setAttackHireRobot(Robot attackHireRobot) {
		this.attackHireRobot = attackHireRobot;
	}

	public NpcRobot getAttackNpcRobot() {
		return attackNpcRobot;
	}

	public void setAttackNpcRobot(NpcRobot attackNpcRobot) {
		this.attackNpcRobot = attackNpcRobot;
	}

	public boolean isConsumeWear() {
		return consumeWear;
	}

	public void setConsumeWear(boolean consumeWear) {
		this.consumeWear = consumeWear;
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

	public void addAttackRobot(NpcRobot npcRobot) {
		attackNpcRobot = npcRobot;
	}

	public List<Robot> readAttackRobotArray() {
		return new ArrayList<Robot>(attackRobots.values());
	}

	public List<Robot> readAttackRobotArrayAll() {
		List<Robot> robots = new ArrayList<Robot>(attackRobots.values());
		if (attackHireRobot != null) {
			robots.add(attackHireRobot);
		}
		return robots;
	}

	/**
	 * 生成存储键。玩家id为key
	 */
	public static String generateCacheKey(Long playerId) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(PveBattle.CKPrefix).append(playerId).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

}
