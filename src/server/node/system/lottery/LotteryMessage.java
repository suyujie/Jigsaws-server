package server.node.system.lottery;

import gamecore.trigger.TopicMessage;

import java.util.ArrayList;

import server.node.system.player.Player;
import server.node.system.robotPart.Part;

/**
 * 抽奖消息
 */
public final class LotteryMessage extends TopicMessage {

	public static final String LOTTERY_PARTS = "lottery_parts";
	public static final String LOTTERY_FREE = "lottery_free";

	private Player player;

	private ArrayList<Part> partPOs;

	public LotteryMessage(String name, Player player, ArrayList<Part> partPOs) {
		super(name);
		this.player = player;
		this.partPOs = partPOs;
	}

	public LotteryMessage(String name, Player player) {
		super(name);
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public ArrayList<Part> getPartPOs() {
		return partPOs;
	}

	public void setPartPOs(ArrayList<Part> partPOs) {
		this.partPOs = partPOs;
	}

}
