package server.node.system.robotPart;

import java.util.List;

import gamecore.trigger.TopicMessage;
import server.node.system.player.Player;

/**
 * 关卡消息。
 */
public final class PartMessage extends TopicMessage {

	//通关,胜利一场战斗
	public static final String PART_GET = "part_get";

	private Player player;
	private Part part;
	private List<Part> parts;

	public PartMessage(String name, Player player, Part part) {
		super(name);
		this.player = player;
		this.part = part;
	}

	public PartMessage(String name, Player player, List<Part> parts) {
		super(name);
		this.player = player;
		this.parts = parts;
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

	public List<Part> getParts() {
		return parts;
	}

	public void setParts(List<Part> parts) {
		this.parts = parts;
	}

}
