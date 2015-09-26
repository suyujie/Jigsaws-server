package server.node.system.egg;

import gamecore.trigger.TopicMessage;
import server.node.system.player.Player;
import server.node.system.robotPart.Part;

/**
 * 抽蛋消息
 */
public final class EggPartMessage extends TopicMessage {

	//抽蛋
	public static final String GET_EGG_PART = "get_egg_part";

	private Player player;

	private Part part;

	public EggPartMessage(String name, Player player, Part part) {
		super(name);
		this.player = player;
		this.part = part;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Part getPart() {
		return part;
	}

	public void setPart(Part part) {
		this.part = part;
	}

}
