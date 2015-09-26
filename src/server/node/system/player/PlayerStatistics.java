package server.node.system.player;

import java.io.Serializable;

/**
 * 玩家的部分统计和状态
 */
public class PlayerStatistics implements Serializable {

	private static final long serialVersionUID = 7582153125956861246L;

	private int cupNum;//奖杯数
	private int pvpAttackWinCount;//进攻胜利
	private int pvpDefenceWinCount;//防守胜利
	private int pvpBeatRobotCount;//击败机器人数量
	private int letCount;//出租成功次数

	public PlayerStatistics() {

	}

	public PlayerStatistics(int cupNum, int pvpAttackWinCount, int pvpDefenceWinCount, int pvpBeatRobotCount, int letCount) {
		super();
		this.cupNum = cupNum;
		this.pvpAttackWinCount = pvpAttackWinCount;
		this.pvpDefenceWinCount = pvpDefenceWinCount;
		this.pvpBeatRobotCount = pvpBeatRobotCount;
		this.letCount = letCount;
	}

	public int getCupNum() {
		return cupNum;
	}

	public void setCupNum(int cupNum) {
		this.cupNum = cupNum;
	}

	public int getPvpAttackWinCount() {
		return pvpAttackWinCount;
	}

	public void setPvpAttackWinCount(int pvpAttackWinCount) {
		this.pvpAttackWinCount = pvpAttackWinCount;
	}

	public int getPvpDefenceWinCount() {
		return pvpDefenceWinCount;
	}

	public void setPvpDefenceWinCount(int pvpDefenceWinCount) {
		this.pvpDefenceWinCount = pvpDefenceWinCount;
	}

	public int getPvpBeatRobotCount() {
		return pvpBeatRobotCount;
	}

	public void setPvpBeatRobotCount(int pvpBeatRobotCount) {
		this.pvpBeatRobotCount = pvpBeatRobotCount;
	}

	public int getLetCount() {
		return letCount;
	}

	public void setLetCount(int letCount) {
		this.letCount = letCount;
	}

}
