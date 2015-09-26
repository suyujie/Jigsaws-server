package server.node.system.trigger;

import gamecore.trigger.TopicMessage;
import gamecore.trigger.TopicPublisher;
import gamecore.trigger.TopicSubscriber;
import gamecore.trigger.Trigger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Content;
import server.node.system.Root;
import server.node.system.battle.PvpMessage;
import server.node.system.battle.PvpSystem;
import server.node.system.player.Player;
import server.node.system.player.PlayerMessage;
import server.node.system.player.PlayerSystem;
import server.node.system.session.SessionMessage;
import server.node.system.session.SessionSystem;

/**
 * pvp 触发器
 */
public final class OpponentTrigger implements Trigger, TopicSubscriber {

	@SuppressWarnings("unused")
	private final static Logger logger = LogManager.getLogger(OpponentTrigger.class.getName());

	public OpponentTrigger() {
	}

	public boolean start() {
		Root.playerSystem.subscribe(PlayerMessage.SignIn, this);
		Root.sessionSystem.subscribe(SessionMessage.SignOut, this);
		Root.pvpSystem.subscribe(PvpMessage.PVP_EXIT, this);
		return true;
	}

	public void stop() {
		Root.playerSystem.unsubscribe(PlayerMessage.SignIn, this);
		Root.sessionSystem.unsubscribe(SessionMessage.SignOut, this);
		Root.pvpSystem.unsubscribe(PvpMessage.PVP_EXIT, this);
	}

	@Override
	public void onMessage(TopicPublisher publisher, TopicMessage message) {

		//session系统发来的消息，
		if (publisher instanceof SessionSystem) {
			if (message instanceof SessionMessage) {
				SessionMessage sessionMessage = (SessionMessage) message;
				Player player = sessionMessage.getPlayer();
				//有玩家掉线
				if (message.getName() == SessionMessage.SignOut) {
					//加入pvp列表
					if (!player.checkProtect() && player.getLevel() >= Content.PvpMinLevel) {
						Root.opponentSystem.addOpponent(player.getPlayerStatistics().getCupNum(), player);
					}
				}
			}
		}
		//player系统发来的消息，
		if (publisher instanceof PlayerSystem) {
			if (message instanceof PlayerMessage) {
				PlayerMessage sessionMessage = (PlayerMessage) message;
				Player player = sessionMessage.getPlayer();
				//有玩家上线 ,从对手列表中删掉
				if (message.getName() == PlayerMessage.SignIn) {
					Root.opponentSystem.removeOpponent(player.getPlayerStatistics().getCupNum(), player.getId());
				}
			}
		}

		if (publisher instanceof PvpSystem) {
			if (message instanceof PvpMessage) {
				PvpMessage pvpMessage = (PvpMessage) message;
				Player defender = pvpMessage.getDefender();
				//pvp战斗结束
				if (message.getName() == PvpMessage.PVP_EXIT && defender != null) {
					if (!pvpMessage.getPvpBattleResult().isWin()) {//攻击者输了,那么defender继续在被打队列里,但是换了cup区间
						Root.opponentSystem.addOpponent(defender.getPlayerStatistics().getCupNum(), defender);
					}
				}
			}
		}

	}

}
