package server.node.system.trigger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gamecore.trigger.TopicMessage;
import gamecore.trigger.TopicPublisher;
import gamecore.trigger.TopicSubscriber;
import gamecore.trigger.Trigger;

/**
 * 推送触发器
 */
public final class PushTrigger implements Trigger, TopicSubscriber {

	@SuppressWarnings("unused")
	private final static Logger logger = LogManager.getLogger(PushTrigger.class.getName());

	public PushTrigger() {
	}

	public boolean start() {
		return true;
	}

	public void stop() {
	}

	@Override
	public void onMessage(TopicPublisher publisher, TopicMessage message) {

		// // robotsystem系统发来的消息，
		// if (publisher instanceof RobotSystem) {
		// if (message instanceof RobotMessage) {
		// RobotMessage robotMessage = (RobotMessage) message;
		// if (message.getName() == RobotMessage.ROBOT_REPAIRE) {
		// if (robotMessage.getRepairAllEndTime() - Clock.currentTimeSecond() >
		// 100) {// 修理时间挺长的时候才发送
		// PushRepaired pushRepaired = new
		// PushRepaired(robotMessage.getPlayer().getId(),
		// robotMessage.getRepairAllEndTime());
		// Root.pushSystem.addPushRepaired(pushRepaired);
		// }
		// }
		//
		// // 用钻石修理,取消掉修理完成的推送消息
		// if (message.getName() ==
		// RobotMessage.ROBOT_REPAIREOK_WITH_GOLD_HAVE_BEGIN) {
		// Player player = robotMessage.getPlayer();
		// Root.pushSystem.cancelPushRepaired(player);
		// }
		// }
		// }
		//
		// // 关卡收钱
		// if (publisher instanceof MissionSystem) {
		// if (message instanceof MissionMessage) {
		// MissionMessage missionMessage = (MissionMessage) message;
		// // 收钱
		// if (missionMessage.getName() == MissionMessage.GainCash) {
		// Mission mission = missionMessage.getMission();
		// MissionGainCashMaking missionGainMaking =
		// MissionGainCashLoadData.getInstance()
		// .getMissionGainMaking(mission.getMakingId());
		// PushCashFull pushCashFull = new
		// PushCashFull(missionMessage.getPlayer().getId(),
		// Clock.currentTimeSecond() + missionGainMaking.getMaxTime() * 60);
		// Root.pushSystem.addPushCashFull(pushCashFull);
		// }
		// // 损失收钱时间,满仓后延了.
		// if (missionMessage.getName() == MissionMessage.LoseTime) {
		// Mission mission = missionMessage.getMission();
		// MissionGainCashMaking missionGainMaking =
		// MissionGainCashLoadData.getInstance()
		// .getMissionGainMaking(mission.getMakingId());
		// PushCashFull pushCashFull = new
		// PushCashFull(missionMessage.getPlayer().getId(),
		// missionGainMaking.getMaxTime() * 60 - mission.getLastGainCashTime());
		// Root.pushSystem.addPushCashFull(pushCashFull);
		// }
		// }
		// }

		// 免费抽奖//去掉了
		// if (publisher instanceof LotterySystem) {
		// if (message instanceof LotteryMessage) {
		// LotteryMessage lotteryMessage = (LotteryMessage) message;
		// if (lotteryMessage.getName() == LotteryMessage.LOTTERY_FREE) {
		// PushLotteryFree pushLotteryFree = new
		// PushLotteryFree(lotteryMessage.getPlayer().getId(),
		// Clock.currentTimeSecond() + Content.FreeLotteryTime);
		// Root.pushSystem.addPushLotteryFree(pushLotteryFree);
		// }
		// }
		// }

	}
}
