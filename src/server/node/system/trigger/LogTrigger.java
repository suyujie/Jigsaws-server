package server.node.system.trigger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gamecore.trigger.TopicMessage;
import gamecore.trigger.TopicPublisher;
import gamecore.trigger.TopicSubscriber;
import gamecore.trigger.Trigger;
import gamecore.util.Clock;
import server.node.system.Root;
import server.node.system.player.Player;
import server.node.system.player.PlayerMessage;
import server.node.system.player.PlayerSystem;
import server.node.system.session.SessionMessage;
import server.node.system.session.SessionSystem;

/**
 * log 触发器
 */
public final class LogTrigger implements Trigger, TopicSubscriber {

	@SuppressWarnings("unused")
	private final static Logger logger = LogManager.getLogger(LogTrigger.class.getName());

	public LogTrigger() {
	}

	public boolean start() {
		Root.playerSystem.subscribe(PlayerMessage.NewPlayer, this);
		Root.playerSystem.subscribe(PlayerMessage.SignIn, this);
		Root.sessionSystem.subscribe(SessionMessage.SignOut, this);
		Root.playerSystem.subscribe(PlayerMessage.LEVEL_UP, this);
		return true;
	}

	public void stop() {
		Root.playerSystem.unsubscribe(PlayerMessage.NewPlayer, this);
		Root.playerSystem.unsubscribe(PlayerMessage.SignIn, this);
		Root.sessionSystem.unsubscribe(SessionMessage.SignOut, this);
		Root.playerSystem.unsubscribe(PlayerMessage.LEVEL_UP, this);
	}

	@Override
	public void onMessage(TopicPublisher publisher, TopicMessage message) {

		// 用户系统发来的消息，
		if (publisher instanceof PlayerSystem) {
			if (message instanceof PlayerMessage) {
				PlayerMessage playerMsg = (PlayerMessage) message;
				Player player = playerMsg.getPlayer();

				// 新注册,添加登陆日志
				if (message.getName() == PlayerMessage.NewPlayer) {
					Root.logSystem.addSignLog(player, false);
				}
				// 登录,更新掉之前的登陆日志,加入新的登陆日志
				if (message.getName() == PlayerMessage.SignIn) {
					if (player.getSignLogId() != null) {
						player.setOnLineTime(player.getOnLineTime() + (player.getLastSignT() == null ? 0
								: (Clock.currentTimeSecond() - player.getLastSignT())));
						Root.logSystem.updateSignLog(player, false);
					}
					Root.logSystem.addSignLog(player, false);
				}

				player.synchronize();
			}
		}

		// session系统发来的消息，
		if (publisher instanceof SessionSystem) {
			if (message instanceof SessionMessage) {
				SessionMessage sessionMessage = (SessionMessage) message;
				if (message.getName() == SessionMessage.SignOut) {
					Player player = sessionMessage.getPlayer();
					if (player.getSignLogId() != null) {
						Root.logSystem.updateSignLog(player, true);
					}
				}
			}
		}

	}

}
