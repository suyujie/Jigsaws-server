package server.node.system.trigger;

import gamecore.trigger.TopicMessage;
import gamecore.trigger.TopicPublisher;
import gamecore.trigger.TopicSubscriber;
import gamecore.trigger.Trigger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Root;
import server.node.system.battle.PveMessage;
import server.node.system.player.Player;
import server.node.system.player.PlayerMessage;
import server.node.system.player.PlayerSystem;

/**
 * 机器人触发器。
 */
public final class RobotTrigger implements Trigger, TopicSubscriber {

	@SuppressWarnings("unused")
	private final static Logger logger = LogManager.getLogger(RobotTrigger.class.getName());

	public RobotTrigger() {
	}

	public boolean start() {
		Root.playerSystem.subscribe(PlayerMessage.NewPlayer, this);
		Root.playerSystem.subscribe(PlayerMessage.LEVEL_UP, this);
		Root.pveSystem.subscribe(PveMessage.PVE_EXIT, this);
		return true;
	}

	public void stop() {
		Root.playerSystem.unsubscribe(PlayerMessage.NewPlayer, this);
		Root.playerSystem.unsubscribe(PlayerMessage.LEVEL_UP, this);
		Root.pveSystem.unsubscribe(PveMessage.PVE_EXIT, this);
	}

	@Override
	public void onMessage(TopicPublisher publisher, TopicMessage message) {
		//玩家系统发来的信息
		if (publisher instanceof PlayerSystem) {
			//玩家信息
			if (message instanceof PlayerMessage) {
				PlayerMessage playerMessage = (PlayerMessage) message;
				Player player = playerMessage.getPlayer();

				//new player
				if (playerMessage.getName() == PlayerMessage.NewPlayer) {
					// 根据等级加入机器人(刚注册,第一个机器人)
					Root.robotSystem.createRobotByLevel(player, null);
					// 默认给一个仓库的机器人
					Root.robotSystem.createDefaultRobotInStorage(player, null);
				}

				//player level up
				if (playerMessage.getName() == PlayerMessage.LEVEL_UP) {
					//根据等级加入机器人
					Root.robotSystem.createRobotByLevel(player, null);
				}

				// 11 级的时候恢复全部耐久
				if (playerMessage.getName() == PlayerMessage.LEVEL_UP) {
					if (playerMessage.getCurrentLevel() == 11) {
						Root.robotSystem.recoverRobotWearAllFull(player, null);
					}
				}
			}
		}

	}
}
