package server.node.system.trigger.trigger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gamecore.trigger.TopicMessage;
import gamecore.trigger.TopicPublisher;
import gamecore.trigger.TopicSubscriber;
import gamecore.trigger.Trigger;
import server.node.system.Root;
import server.node.system.evaluate.EvaluateSystem;
import server.node.system.jigsaw.Jigsaw;
import server.node.system.jigsaw.JigsawMessage;
import server.node.system.player.Player;

/**
 * 拼图触发器
 */
public final class JigsawTrigger implements Trigger, TopicSubscriber {

	private final static Logger logger = LogManager.getLogger(JigsawTrigger.class.getName());

	public JigsawTrigger() {
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
				Long jigsawId = msg.getJigsawId();

				if (jigsaw != null) {
					Root.jigsawSystem.playedJigsaw(player, jigsaw);
				}
				// 官方拼图，只给id
				if (jigsawId != null) {
					Root.jigsawSystem.playedJigsaw(player, jigsawId);
				}

				player.synchronize();
			}
		}

	}

}
