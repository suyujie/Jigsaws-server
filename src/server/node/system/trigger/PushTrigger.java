package server.node.system.trigger;

import gamecore.trigger.TopicMessage;
import gamecore.trigger.TopicPublisher;
import gamecore.trigger.TopicSubscriber;
import gamecore.trigger.Trigger;
import gamecore.util.Clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Content;
import server.node.system.Root;
import server.node.system.battle.PvpMessage;
import server.node.system.battle.PvpSystem;
import server.node.system.lottery.LotteryMessage;
import server.node.system.lottery.LotterySystem;
import server.node.system.mission.Mission;
import server.node.system.mission.MissionGainCashLoadData;
import server.node.system.mission.MissionGainCashMaking;
import server.node.system.mission.MissionMessage;
import server.node.system.mission.MissionSystem;
import server.node.system.player.Player;
import server.node.system.push.pushMessage.PushCashFull;
import server.node.system.push.pushMessage.PushLotteryFree;
import server.node.system.push.pushMessage.PushPvpBeated;
import server.node.system.push.pushMessage.PushRentOrderHired;
import server.node.system.push.pushMessage.PushRepaired;
import server.node.system.rent.RentOrderMessage;
import server.node.system.rent.RentOrderSystem;
import server.node.system.robot.RobotMessage;
import server.node.system.robot.RobotSystem;

/**
 * 推送触发器
 */
public final class PushTrigger implements Trigger, TopicSubscriber {

	@SuppressWarnings("unused")
	private final static Logger logger = LogManager.getLogger(PushTrigger.class.getName());

	public PushTrigger() {
	}

	public boolean start() {
		Root.pvpSystem.subscribe(PvpMessage.PVP_EXIT, this);
		Root.rentOrderSystem.subscribe(RentOrderMessage.Hire, this);
		Root.robotSystem.subscribe(RobotMessage.ROBOT_REPAIRE, this);
		Root.robotSystem.subscribe(RobotMessage.ROBOT_REPAIREOK_WITH_GOLD_HAVE_BEGIN, this);
		Root.missionSystem.subscribe(MissionMessage.GainCash, this);
		Root.missionSystem.subscribe(MissionMessage.LoseTime, this);
		Root.lotterySystem.subscribe(LotteryMessage.LOTTERY_FREE, this);
		return true;
	}

	public void stop() {
		Root.pvpSystem.unsubscribe(PvpMessage.PVP_EXIT, this);
		Root.rentOrderSystem.unsubscribe(RentOrderMessage.Hire, this);
		Root.robotSystem.unsubscribe(RobotMessage.ROBOT_REPAIRE, this);
		Root.robotSystem.unsubscribe(RobotMessage.ROBOT_REPAIREOK_WITH_GOLD_HAVE_BEGIN, this);
		Root.missionSystem.unsubscribe(MissionMessage.GainCash, this);
		Root.missionSystem.unsubscribe(MissionMessage.LoseTime, this);
		Root.lotterySystem.unsubscribe(LotteryMessage.LOTTERY_FREE, this);
	}

	@Override
	public void onMessage(TopicPublisher publisher, TopicMessage message) {
		//pvp系统发来的消息，
		if (publisher instanceof PvpSystem) {
			if (message instanceof PvpMessage) {
				PvpMessage pvpMessage = (PvpMessage) message;
				//战斗结算,发消息
				if (message.getName() == PvpMessage.PVP_EXIT && pvpMessage.getAttacker() != null && pvpMessage.getDefender() != null) {
					//不管胜利失败
					PushPvpBeated pushPvpBeated = new PushPvpBeated(pvpMessage.getDefender().getId(), Clock.currentTimeSecond(), pvpMessage.getAttacker().getAccount()
							.getNameInPlat());
					Root.pushSystem.addPushPvpBeated(pushPvpBeated);
				}
			}
		}

		//rentorder系统发来的消息，
		if (publisher instanceof RentOrderSystem) {
			if (message instanceof RentOrderMessage) {
				RentOrderMessage rentOrderMessage = (RentOrderMessage) message;
				//租用机器人,发消息
				if (message.getName() == RentOrderMessage.Hire) {
					if (rentOrderMessage.getRenter() != null) {
						PushRentOrderHired pushRentOrderHired = new PushRentOrderHired(rentOrderMessage.getRenter().getId(), Clock.currentTimeSecond(), rentOrderMessage.getHirer()
								.getAccount().getNameInPlat());
						Root.pushSystem.addPushRentOrderHired(pushRentOrderHired);
					}
				}
			}
		}

		//robotsystem系统发来的消息，
		if (publisher instanceof RobotSystem) {
			if (message instanceof RobotMessage) {
				RobotMessage robotMessage = (RobotMessage) message;
				if (message.getName() == RobotMessage.ROBOT_REPAIRE) {
					if (robotMessage.getRepairAllEndTime() - Clock.currentTimeSecond() > 100) {//修理时间挺长的时候才发送
						PushRepaired pushRepaired = new PushRepaired(robotMessage.getPlayer().getId(), robotMessage.getRepairAllEndTime());
						Root.pushSystem.addPushRepaired(pushRepaired);
					}
				}

				//用钻石修理,取消掉修理完成的推送消息
				if (message.getName() == RobotMessage.ROBOT_REPAIREOK_WITH_GOLD_HAVE_BEGIN) {
					Player player = robotMessage.getPlayer();
					Root.pushSystem.cancelPushRepaired(player);
				}
			}
		}

		//关卡收钱
		if (publisher instanceof MissionSystem) {
			if (message instanceof MissionMessage) {
				MissionMessage missionMessage = (MissionMessage) message;
				//收钱
				if (missionMessage.getName() == MissionMessage.GainCash) {
					Mission mission = missionMessage.getMission();
					MissionGainCashMaking missionGainMaking = MissionGainCashLoadData.getInstance().getMissionGainMaking(mission.getMakingId());
					PushCashFull pushCashFull = new PushCashFull(missionMessage.getPlayer().getId(), Clock.currentTimeSecond() + missionGainMaking.getMaxTime() * 60);
					Root.pushSystem.addPushCashFull(pushCashFull);
				}
				//损失收钱时间,满仓后延了.
				if (missionMessage.getName() == MissionMessage.LoseTime) {
					Mission mission = missionMessage.getMission();
					MissionGainCashMaking missionGainMaking = MissionGainCashLoadData.getInstance().getMissionGainMaking(mission.getMakingId());
					PushCashFull pushCashFull = new PushCashFull(missionMessage.getPlayer().getId(), missionGainMaking.getMaxTime() * 60 - mission.getLastGainCashTime());
					Root.pushSystem.addPushCashFull(pushCashFull);
				}
			}
		}

		//免费抽奖//去掉了
//		if (publisher instanceof LotterySystem) {
//			if (message instanceof LotteryMessage) {
//				LotteryMessage lotteryMessage = (LotteryMessage) message;
//				if (lotteryMessage.getName() == LotteryMessage.LOTTERY_FREE) {
//					PushLotteryFree pushLotteryFree = new PushLotteryFree(lotteryMessage.getPlayer().getId(), Clock.currentTimeSecond() + Content.FreeLotteryTime);
//					Root.pushSystem.addPushLotteryFree(pushLotteryFree);
//				}
//			}
//		}

	}
}
