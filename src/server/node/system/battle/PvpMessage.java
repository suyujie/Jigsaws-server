package server.node.system.battle;

import server.node.system.player.Player;
import gamecore.trigger.TopicMessage;

/**
 * pvp消息。
 */
public final class PvpMessage extends TopicMessage {

	public static final String PVP_EXIT = "pvp_exit";
	public static final String PVP_EXIT_NPC = "pvp_exit_npc";

	private Player attacker;
	private Player defender;
	private PvpBattleResult pvpBattleResult;

	public PvpMessage(String name, Player attacker, Player defender, PvpBattleResult pvpBattleResult) {
		super(name);
		this.attacker = attacker;
		this.defender = defender;
		this.pvpBattleResult = pvpBattleResult;
	}

	public Player getAttacker() {
		return attacker;
	}

	public void setAttacker(Player attacker) {
		this.attacker = attacker;
	}

	public Player getDefender() {
		return defender;
	}

	public void setDefender(Player defender) {
		this.defender = defender;
	}

	public PvpBattleResult getPvpBattleResult() {
		return pvpBattleResult;
	}

	public void setPvpBattleResult(PvpBattleResult pvpBattleResult) {
		this.pvpBattleResult = pvpBattleResult;
	}

}
