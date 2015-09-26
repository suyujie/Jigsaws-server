package server.node.system.friend;

import gamecore.trigger.TopicMessage;
import server.node.system.player.Player;

/**
 * gift messsage
 */
public final class FriendMessage extends TopicMessage {

	public static final String SYNC_FRIENDS = "sync_friends";//同步好友

	private Player player;
	private FriendBag friendBag;

	public FriendMessage(String name, Player player, FriendBag friendBag) {
		super(name);
		this.player = player;
		this.friendBag = friendBag;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public FriendBag getFriendBag() {
		return friendBag;
	}

	public void setFriendBag(FriendBag friendBag) {
		this.friendBag = friendBag;
	}

}
