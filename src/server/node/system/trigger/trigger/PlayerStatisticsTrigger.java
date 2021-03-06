package server.node.system.trigger.trigger;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gamecore.trigger.TopicMessage;
import gamecore.trigger.TopicPublisher;
import gamecore.trigger.TopicSubscriber;
import gamecore.trigger.Trigger;
import server.node.system.Root;
import server.node.system.evaluate.EvaluateSystem;
import server.node.system.evaluate.EvaluateType;
import server.node.system.jigsaw.Jigsaw;
import server.node.system.jigsaw.JigsawMessage;
import server.node.system.player.Player;

/**
 * 统计 触发器
 */
public final class PlayerStatisticsTrigger implements Trigger, TopicSubscriber {

	@SuppressWarnings("unused")
	private final static Logger logger = LogManager.getLogger(PlayerStatisticsTrigger.class.getName());

	public PlayerStatisticsTrigger() {
	}

	public boolean start() {
		Root.evaluateSystem.subscribe(JigsawMessage.Evaluate, this);
		return true;
	}

	public void stop() {
		Root.evaluateSystem.unsubscribe(JigsawMessage.Evaluate, this);
	}

	@Override
	public void onMessage(TopicPublisher publisher, TopicMessage message) {

		// 评价系统发来的消息
		if (publisher instanceof EvaluateSystem) {
			if (message instanceof JigsawMessage) {
				JigsawMessage msg = (JigsawMessage) message;
				Player player = msg.getPlayer();
				Jigsaw jigsaw = msg.getJigsaw();
				EvaluateType type = msg.getEvaluateType();

				try {
					Root.playerSystem.updatePlayerStatisticsAsPlayer(player, type);// 玩这个游戏的人
																					// 统计
					if (jigsaw != null) {
						Root.playerSystem.updatePlayerStatisticsAsOwner(jigsaw.getPlayerId(), type);// 提供这个游戏的人统计
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				player.synchronize();
			}
		}

	}

}
