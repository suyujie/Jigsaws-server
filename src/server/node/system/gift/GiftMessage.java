package server.node.system.gift;

import gamecore.trigger.TopicMessage;
import server.node.system.player.Player;

/**
 * gift messsage
 */
public final class GiftMessage extends TopicMessage {

	public static final String SEND_GIFT = "send_gift";//送礼物

	private Player player;

	public GiftMessage(String name, Player player) {
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
