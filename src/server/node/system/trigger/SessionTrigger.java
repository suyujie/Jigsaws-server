package server.node.system.trigger;

import gamecore.trigger.TopicMessage;
import gamecore.trigger.TopicPublisher;
import gamecore.trigger.TopicSubscriber;
import gamecore.trigger.Trigger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Root;
import server.node.system.player.Player;
import server.node.system.player.PlayerMessage;
import server.node.system.player.PlayerSystem;

/**
 * session 触发器
 */
public final class SessionTrigger implements Trigger, TopicSubscriber {

	private final static Logger logger = LogManager.getLogger(SessionTrigger.class.getName());

	public SessionTrigger() {
	}

	public boolean start() {
		Root.playerSystem.subscribe(PlayerMessage.Registe, this);
		Root.playerSystem.subscribe(PlayerMessage.SignIn, this);
		return true;
	}

	public void stop() {
		Root.playerSystem.unsubscribe(PlayerMessage.Registe, this);
		Root.playerSystem.unsubscribe(PlayerMessage.SignIn, this);
	}

	@Override
	public void onMessage(TopicPublisher publisher, TopicMessage message) {

		// 用户系统发来的消息，
		if (publisher instanceof PlayerSystem) {
			if (message instanceof PlayerMessage) {
				PlayerMessage playerMsg = (PlayerMessage) message;
				Player player = playerMsg.getPlayer();
				String sessionId = playerMsg.getSessionId();
				if (player != null) {
					// 新注册,创建session,加入在线列表
					if (message.getName() == PlayerMessage.Registe) {
						Root.sessionSystem.updateOrSaveSession(player, sessionId);
						Root.sessionSystem.saveOnlinePlayerList(player);
					}
					// 刚登录,创建session 加入在线列表
					if (message.getName() == PlayerMessage.SignIn) {
						Root.sessionSystem.updateOrSaveSession(player, sessionId);
						Root.sessionSystem.saveOnlinePlayerList(player);
					}
				}
			}
		}
	}

}
