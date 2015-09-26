package server.node.system.trigger;

import gamecore.trigger.TopicMessage;
import gamecore.trigger.TopicPublisher;
import gamecore.trigger.TopicSubscriber;
import gamecore.trigger.Trigger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Root;
import server.node.system.battle.PvpMessage;
import server.node.system.battle.PvpSystem;

/**
 * log 触发器
 */
public final class ToturialTrigger implements Trigger, TopicSubscriber {

	@SuppressWarnings("unused")
	private final static Logger logger = LogManager.getLogger(ToturialTrigger.class.getName());

	public ToturialTrigger() {
	}

	public boolean start() {
		Root.pvpSystem.subscribe(PvpMessage.PVP_EXIT, this);
		Root.pvpSystem.subscribe(PvpMessage.PVP_EXIT_NPC, this);
		return true;
	}

	public void stop() {
		Root.pvpSystem.unsubscribe(PvpMessage.PVP_EXIT, this);
		Root.pvpSystem.unsubscribe(PvpMessage.PVP_EXIT_NPC, this);
	}

	@Override
	public void onMessage(TopicPublisher publisher, TopicMessage message) {

		//pvp
		if (publisher instanceof PvpSystem && message instanceof PvpMessage) {
			PvpMessage pvpMessage = (PvpMessage) message;
			if (pvpMessage.getName() == PvpMessage.PVP_EXIT || pvpMessage.getName() == PvpMessage.PVP_EXIT_NPC) {
				if (pvpMessage.getPvpBattleResult().isWin()) {
					try {
						Root.toturialSystem.updatePvpWinOne(pvpMessage.getAttacker());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
}
