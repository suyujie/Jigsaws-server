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
import server.node.system.player.Player;
import server.node.system.rent.RentOrder;
import server.node.system.rent.RentOrderMessage;
import server.node.system.rent.RentOrderSystem;

/**
 * pvp 触发器
 */
public final class RecordTrigger implements Trigger, TopicSubscriber {

	@SuppressWarnings("unused")
	private final static Logger logger = LogManager.getLogger(RecordTrigger.class.getName());

	public RecordTrigger() {
	}

	public boolean start() {
		Root.pvpSystem.subscribe(PvpMessage.PVP_EXIT, this);
		Root.pvpSystem.subscribe(PvpMessage.PVP_EXIT_NPC, this);
		Root.rentOrderSystem.subscribe(RentOrderMessage.Close, this);
		return true;
	}

	public void stop() {
		Root.pvpSystem.unsubscribe(PvpMessage.PVP_EXIT, this);
		Root.pvpSystem.unsubscribe(PvpMessage.PVP_EXIT_NPC, this);
		Root.rentOrderSystem.unsubscribe(RentOrderMessage.Close, this);
	}

	@Override
	public void onMessage(TopicPublisher publisher, TopicMessage message) {
		//pvp系统发来的消息，
		if (publisher instanceof PvpSystem) {
			if (message instanceof PvpMessage) {
				PvpMessage pvpMessage = (PvpMessage) message;
				//战斗结算,加入战斗记录
				if (message.getName() == PvpMessage.PVP_EXIT) {
					Root.recordSystem.addPvpRecord(pvpMessage.getAttacker(), pvpMessage.getDefender(), pvpMessage.getPvpBattleResult());
				}
				//战斗结算,加入战斗记录
				if (message.getName() == PvpMessage.PVP_EXIT_NPC) {
					Root.recordSystem.addAttackRecordWithNpc(pvpMessage.getAttacker(), pvpMessage.getPvpBattleResult());
				}
			}
		}

		//rent系统发来的消息，
		if (publisher instanceof RentOrderSystem) {
			if (message instanceof RentOrderMessage) {
				RentOrderMessage rentOrderMessage = (RentOrderMessage) message;
				Player player = rentOrderMessage.getRenter();
				RentOrder rentOrder = rentOrderMessage.getOrder();

				//租赁结束
				if (message.getName() == RentOrderMessage.Close) {
					//加入记录
					Root.recordSystem.addRentRecord(player, rentOrder);
				}
			}
		}

	}
}
