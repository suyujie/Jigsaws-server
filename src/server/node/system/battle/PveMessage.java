package server.node.system.battle;

import gamecore.trigger.TopicMessage;

import server.node.system.player.Player;

/**
 * 关卡消息。
 */
public final class PveMessage extends TopicMessage {

	//通关,胜利一场战斗
	public static final String PVE_EXIT = "pass_point";

	private Player player;

	private PveBattle pveBattle;

	private PveBattleResult pveBattleResult;

	public PveMessage(String name) {
		super(name);
	}

	public PveMessage(String name, Player player) {
		super(name);
		this.player = player;
	}

	public PveMessage(String name, Player player, PveBattle pveBattle, PveBattleResult pveBattleResult) {
		super(name);
		this.player = player;
		this.pveBattle = pveBattle;
		this.pveBattleResult = pveBattleResult;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public PveBattleResult getPveBattleResult() {
		return pveBattleResult;
	}

	public void setPveBattleResult(PveBattleResult pveBattleResult) {
		this.pveBattleResult = pveBattleResult;
	}

	public PveBattle getPveBattle() {
		return pveBattle;
	}

	public void setPveBattle(PveBattle pveBattle) {
		this.pveBattle = pveBattle;
	}

}
