package server.node.system.battle;

import gamecore.trigger.TopicMessage;
import server.node.system.npc.NpcRobot;
import server.node.system.player.Player;
import server.node.system.robot.Robot;

/**
 * pvp消息。
 */
public final class BeatRobotMessage extends TopicMessage {

	public static final String PVP_BEAT_ROBOT = "pvp_beat_robot";
	public static final String PVP_BEAT_ROBOT_NPC = "pvp_beat_robot_npc";

	private Player attacker;
	private Robot beatedRobot;
	private NpcRobot beatedNpcRobot;

	public BeatRobotMessage(String name, Player player, Robot beatedRobot) {
		super(name);
		this.attacker = player;
		this.beatedRobot = beatedRobot;
	}

	public BeatRobotMessage(String name, Player player, NpcRobot beatedNpcRobot) {
		super(name);
		this.attacker = player;
		this.beatedNpcRobot = beatedNpcRobot;
	}

	public Player getAttacker() {
		return attacker;
	}

	public void setAttacker(Player attacker) {
		this.attacker = attacker;
	}

	public Robot getBeatedRobot() {
		return beatedRobot;
	}

	public void setBeatedRobot(Robot beatedRobot) {
		this.beatedRobot = beatedRobot;
	}

	public NpcRobot getBeatedNpcRobot() {
		return beatedNpcRobot;
	}

	public void setBeatedNpcRobot(NpcRobot beatedNpcRobot) {
		this.beatedNpcRobot = beatedNpcRobot;
	}

}
