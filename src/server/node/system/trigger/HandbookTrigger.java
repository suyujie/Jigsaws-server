package server.node.system.trigger;

import gamecore.trigger.TopicMessage;
import gamecore.trigger.TopicPublisher;
import gamecore.trigger.TopicSubscriber;
import gamecore.trigger.Trigger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Root;
import server.node.system.robot.RobotMessage;
import server.node.system.robot.RobotSystem;
import server.node.system.robotPart.PartMessage;
import server.node.system.robotPart.PartSystem;

/**
 * 日常任务触发器
 */
public final class HandbookTrigger implements Trigger, TopicSubscriber {

	private final static Logger logger = LogManager.getLogger(HandbookTrigger.class.getName());

	public HandbookTrigger() {
	}

	public boolean start() {
		Root.partSystem.subscribe(PartMessage.PART_GET, this);
		Root.robotSystem.subscribe(RobotMessage.ROBOT_GET_NEW, this);
		Root.robotSystem.subscribe(RobotMessage.ROBOT_PART_EVOLUTION, this);
		return true;
	}

	public void stop() {
		Root.partSystem.unsubscribe(PartMessage.PART_GET, this);
		Root.robotSystem.unsubscribe(RobotMessage.ROBOT_GET_NEW, this);
		Root.robotSystem.unsubscribe(RobotMessage.ROBOT_PART_EVOLUTION, this);
	}

	@Override
	public void onMessage(TopicPublisher publisher, TopicMessage message) {
		//获得新部件
		if (publisher instanceof PartSystem) {

			try {
				if (message instanceof PartMessage) {//零件消息
					PartMessage partMessage = (PartMessage) message;
					if (partMessage.getName() == PartMessage.PART_GET) {
						if (partMessage.getPart() != null) {//获得一个部件
							Root.handbookSystem.addPart(partMessage.getPlayer(), partMessage.getPart());
						}
						if (partMessage.getParts() != null) {//获得多个部件
							Root.handbookSystem.addParts(partMessage.getPlayer(), partMessage.getParts());
						}
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		}

		if (publisher instanceof RobotSystem) {
			try {
				if (message instanceof RobotMessage) {//获得新部件
					RobotMessage robotMessage = (RobotMessage) message;
					if (robotMessage.getName() == RobotMessage.ROBOT_GET_NEW) {
						Root.handbookSystem.addParts(robotMessage.getPlayer(), robotMessage.getRobot().readParts());
					}
				}

				if (message instanceof RobotMessage) {//进化得到新部件
					RobotMessage robotMessage = (RobotMessage) message;
					if (robotMessage.getName() == RobotMessage.ROBOT_PART_EVOLUTION) {
						Root.handbookSystem.addPart(robotMessage.getPlayer(), robotMessage.getPart());
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

	}
}
