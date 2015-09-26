package server.node.system.mission;

import gamecore.trigger.TopicMessage;
import server.node.system.player.Player;

/**
 * 大关卡消息
 */
public final class MissionMessage extends TopicMessage {

	//收钱
	public static final String GainCash = "gain_cash";
	public static final String AddStar = "add_star";
	public static final String LoseTime = "lose_time";

	private Player player;
	private MissionBag missionBag;
	private Mission mission;
	private int cashNum;

	public MissionMessage(String name, Player player, Mission mission, int cashNum) {
		super(name);
		this.player = player;
		this.mission = mission;
		this.cashNum = cashNum;
	}

	public MissionMessage(String name, Player player, MissionBag missionBag) {
		super(name);
		this.player = player;
		this.missionBag = missionBag;
	}

	public MissionMessage(String name, Player player, Mission mission) {
		super(name);
		this.player = player;
		this.mission = mission;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Mission getMission() {
		return mission;
	}

	public void setMission(Mission mission) {
		this.mission = mission;
	}

	public int getCashNum() {
		return cashNum;
	}

	public void setCashNum(int cashNum) {
		this.cashNum = cashNum;
	}

	public MissionBag getMissionBag() {
		return missionBag;
	}

	public void setMissionBag(MissionBag missionBag) {
		this.missionBag = missionBag;
	}

}
