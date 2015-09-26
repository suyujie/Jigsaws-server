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
import server.node.system.dailyJob.DailyJobMessage;
import server.node.system.dailyJob.JobSystem;
import server.node.system.player.Player;

/**
 * 公告触发器
 */
public final class NoticeTrigger implements Trigger, TopicSubscriber {

	private final static Logger logger = LogManager.getLogger(NoticeTrigger.class.getName());

	public NoticeTrigger() {
	}

	public boolean start() {
		Root.pvpSystem.subscribe(PvpMessage.PVP_EXIT, this);
		Root.jobSystem.subscribe(DailyJobMessage.NewJob, this);
		return true;
	}

	public void stop() {
		Root.pvpSystem.unsubscribe(PvpMessage.PVP_EXIT, this);
		Root.jobSystem.unsubscribe(DailyJobMessage.NewJob, this);
	}

	@Override
	public void onMessage(TopicPublisher publisher, TopicMessage message) {

		//session系统发来的消息，
		if (publisher instanceof PvpSystem) {
			try {
				if (message instanceof PvpMessage) {
					PvpMessage pvpMessage = (PvpMessage) message;
					//pvp结束,被打了
					if (message.getName() == PvpMessage.PVP_EXIT) {
						Root.noticeSystem.addPrivateNoticeBeBeat(pvpMessage.getDefender());
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		//每日任务系统发来的消息，
		if (publisher instanceof JobSystem) {
			try {
				if (message instanceof DailyJobMessage) {
					DailyJobMessage dailyJobMessage = (DailyJobMessage) message;
					Player player = dailyJobMessage.getPlayer();
					//分配了新任务
					if (message.getName() == DailyJobMessage.NewJob) {
						Root.noticeSystem.addPrivateNoticeNewJob(player);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

	}

}
