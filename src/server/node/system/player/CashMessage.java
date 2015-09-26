package server.node.system.player;

import gamecore.trigger.TopicMessage;

/**
 * cash消息。
 */
public final class CashMessage extends TopicMessage {

	//cup 发生改变
	public static final String CashAdd = "cash_add";

	private Player player;
	private int changeCash;

	public CashMessage(String name, Player player, int changeCash) {
		super(name);
		this.player = player;
		this.changeCash = changeCash;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getChangeCash() {
		return changeCash;
	}

	public void setChangeCash(int changeCash) {
		this.changeCash = changeCash;
	}

}
