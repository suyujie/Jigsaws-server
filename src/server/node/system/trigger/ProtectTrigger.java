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

/**
 * pvp 触发器
 */
public final class ProtectTrigger implements Trigger, TopicSubscriber {

	private final static Logger logger = LogManager.getLogger(ProtectTrigger.class.getName());

	public ProtectTrigger() {
	}

	public boolean start() {
		Root.pvpSystem.subscribe(PvpMessage.PVP_EXIT, this);
		return true;
	}

	public void stop() {
		Root.pvpSystem.unsubscribe(PvpMessage.PVP_EXIT, this);
	}

	@Override
	public void onMessage(TopicPublisher publisher, TopicMessage message) {
		//pvp系统发来的消息，
		if (publisher instanceof PvpSystem) {
			if (message instanceof PvpMessage) {
				PvpMessage pvpMessage = (PvpMessage) message;
				Player defender = pvpMessage.getDefender();

				//pvp战斗结束
				if (message.getName() == PvpMessage.PVP_EXIT) {
					//attacker win,defender will be protected,if defender online,not protect
					if (pvpMessage.getPvpBattleResult().isWin()) {
						//重新拿到defender,可能上线了
						try {
							defender = Root.playerSystem.getPlayer(defender.getId());
							if (!defender.checkProtect() && !defender.checkOnLine()) {
								int expPartNum = 0;
								expPartNum += pvpMessage.getPvpBattleResult().getLootExpId() == null ? 0 : 1;
								expPartNum += pvpMessage.getPvpBattleResult().getLootChipName() == null ? 0 : 1;
								expPartNum += pvpMessage.getPvpBattleResult().getLootBergId() == null ? 0 : 1;
								Root.protectSystem.checkProtect(defender, expPartNum, pvpMessage.getPvpBattleResult().defenderLoseCash());
							}
						} catch (Exception e) {
							logger.error(e.getMessage());
						}
					}
					//if attacker have protect,cancel it
					if (pvpMessage.getAttacker().checkProtect()) {
						Root.protectSystem.cancelProtect(pvpMessage.getAttacker());
					}
				}
			}
		}

	}
}
