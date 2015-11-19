package server.node.system.player;

import gamecore.trigger.TopicMessage;

/**
 * 玩家消息。
 */
public final class PlayerMessage extends TopicMessage {

	// 升级
	public static final String LEVEL_UP = "player_levelUp";
	// 注册
	public static final String Registe = "player_registe";
	// 刚登录
	public static final String SignIn = "player_signIn";
	// 有访问
	public static final String PlayerActive = "player_active";

	private String sessionId;
	private Player player;
	private int oldLevel;
	private int currentLevel;

	public PlayerMessage(String name, Player player, int oldLevel, int currentLevel) {
		super(name);
		this.player = player;
		this.oldLevel = oldLevel;
		this.currentLevel = currentLevel;
	}

	public PlayerMessage(String name, Player player, String sessionId) {
		super(name);
		this.player = player;
		this.sessionId = sessionId;
	}

	public PlayerMessage(String name, Player player) {
		super(name);
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getOldLevel() {
		return oldLevel;
	}

	public void setOldLevel(int oldLevel) {
		this.oldLevel = oldLevel;
	}

	public int getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}

}
