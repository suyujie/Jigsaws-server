package server.node.system.dailyJob;

import gamecore.trigger.TopicMessage;
import server.node.system.player.Player;

/**
 * 每日任务消息
 */
public final class DailyJobMessage extends TopicMessage {

	public static final String NewJob = "new_daily_job";

	private Player player;

	public DailyJobMessage(String name, Player player) {
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
