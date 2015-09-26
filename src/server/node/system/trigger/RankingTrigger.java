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
import server.node.system.robot.Robot;
import server.node.system.robot.RobotMessage;
import server.node.system.robot.RobotSystem;

/**
 * 机器人触发器。
 */
public final class RankingTrigger implements Trigger, TopicSubscriber {

	@SuppressWarnings("unused")
	private final static Logger logger = LogManager.getLogger(RankingTrigger.class.getName());

	public RankingTrigger() {
	}

	public boolean start() {
		Root.robotSystem.subscribe(RobotMessage.ROBOT_PART_LEVELUP, this);
		Root.robotSystem.subscribe(RobotMessage.ROBOT_CHANGE_PART, this);
		Root.pvpSystem.subscribe(PvpMessage.PVP_EXIT, this);
		Root.pvpSystem.subscribe(PvpMessage.PVP_EXIT_NPC, this);
		return true;
	}

	public void stop() {
		Root.robotSystem.unsubscribe(RobotMessage.ROBOT_PART_LEVELUP, this);
		Root.robotSystem.unsubscribe(RobotMessage.ROBOT_CHANGE_PART, this);
		Root.pvpSystem.unsubscribe(PvpMessage.PVP_EXIT, this);
		Root.pvpSystem.unsubscribe(PvpMessage.PVP_EXIT_NPC, this);
	}

	@Override
	public void onMessage(TopicPublisher publisher, TopicMessage message) {
		//机器人系统发来信息
		if (publisher instanceof RobotSystem) {
			//玩家信息
			if (message instanceof RobotMessage) {
				RobotMessage robotMessage = (RobotMessage) message;
				Player player = robotMessage.getPlayer();

				//升级
				if (robotMessage.getName() == RobotMessage.ROBOT_PART_LEVELUP) {
					//机器人部件升级
					if (robotMessage.isLevelUp()) {//确定升级了
						Robot robot = robotMessage.getRobot();
						Root.rankingSystem.addRankingScore(robot.refreshFightProperty().getScore(), player, robot);
					}
				}
				//换装
				if (robotMessage.getName() == RobotMessage.ROBOT_CHANGE_PART) {
					//机器人换部件
					Robot robot = robotMessage.getRobot();
					Root.rankingSystem.addRankingScore(robot.refreshFightProperty().getScore(), player, robot);
				}

			}
		}

		//pvp系统发来信息
		if (publisher instanceof PvpSystem) {
			if (message instanceof PvpMessage) {
				PvpMessage pvpMessage = (PvpMessage) message;
				Player defender = pvpMessage.getDefender();
				Player attacker = pvpMessage.getAttacker();
				//pvp战斗结束
				if (message.getName() == PvpMessage.PVP_EXIT || message.getName() == PvpMessage.PVP_EXIT_NPC) {
					Root.rankingSystem.addRankingCup(attacker.getPlayerStatistics().getCupNum(), attacker);
					if (defender != null) {
						Root.rankingSystem.addRankingCup(defender.getPlayerStatistics().getCupNum(), defender);
					}
				}
			}
		}

	}
}
