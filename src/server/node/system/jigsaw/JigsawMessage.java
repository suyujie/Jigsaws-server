package server.node.system.jigsaw;

import gamecore.trigger.TopicMessage;
import server.node.system.evaluate.EvaluateType;
import server.node.system.player.Player;

/**
 * 玩家消息。
 */
public final class JigsawMessage extends TopicMessage {

	// 评价
	public static final String Evaluate = "evaluate";
	private Player player;
	private Jigsaw jigsaw;
	private Long jigsawId;
	private EvaluateType evaluateType;

	public JigsawMessage(String name, Player player, Jigsaw jigsaw, EvaluateType evaluateType) {
		super(name);
		this.player = player;
		this.jigsaw = jigsaw;
		this.evaluateType = evaluateType;
	}

	public JigsawMessage(String name, Player player, Long jigsawId, EvaluateType evaluateType) {
		super(name);
		this.player = player;
		this.jigsawId = jigsawId;
		this.evaluateType = evaluateType;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Jigsaw getJigsaw() {
		return jigsaw;
	}

	public void setJigsaw(Jigsaw jigsaw) {
		this.jigsaw = jigsaw;
	}

	public Long getJigsawId() {
		return jigsawId;
	}

	public void setJigsawId(Long jigsawId) {
		this.jigsawId = jigsawId;
	}

	public EvaluateType getEvaluateType() {
		return evaluateType;
	}

	public void setEvaluateType(EvaluateType evaluateType) {
		this.evaluateType = evaluateType;
	}

}
