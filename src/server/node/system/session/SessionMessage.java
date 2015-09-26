package server.node.system.session;

import server.node.system.player.Player;
import gamecore.trigger.TopicMessage;

/**
 * session消息。
 */
public final class SessionMessage extends TopicMessage {

	//掉线
	public static final String SignOut = "session_signOut";

	private Player player;

	public SessionMessage(String name, Player player) {
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
