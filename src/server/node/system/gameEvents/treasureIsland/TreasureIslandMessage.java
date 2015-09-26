package server.node.system.gameEvents.treasureIsland;

import server.node.system.player.Player;
import gamecore.trigger.TopicMessage;

/**
 * 金银岛消息。
 */
public final class TreasureIslandMessage extends TopicMessage {

	public static final String TreasureIsland_End_Cash = "treasureIsland_end_cash";
	public static final String TreasureIsland_End_Exp = "treasureIsland_end_exp";

	private Player player;

	public TreasureIslandMessage(String name, Player player) {
		super(name);
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
