package server.node.system.trigger;

import java.sql.SQLException;

import gamecore.trigger.TopicMessage;
import gamecore.trigger.TopicPublisher;
import gamecore.trigger.TopicSubscriber;
import gamecore.trigger.Trigger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Root;
import server.node.system.battle.BeatRobotMessage;
import server.node.system.battle.PveMessage;
import server.node.system.battle.PveSystem;
import server.node.system.battle.PvpBattleResult;
import server.node.system.battle.PvpMessage;
import server.node.system.battle.PvpSystem;
import server.node.system.friend.FriendMessage;
import server.node.system.friend.FriendSystem;
import server.node.system.gift.GiftMessage;
import server.node.system.gift.GiftSystem;
import server.node.system.lottery.LotteryMessage;
import server.node.system.lottery.LotterySystem;
import server.node.system.mission.MissionMessage;
import server.node.system.mission.MissionSystem;
import server.node.system.player.Player;
import server.node.system.player.PlayerMessage;
import server.node.system.player.PlayerSystem;
import server.node.system.rent.RentOrderMessage;
import server.node.system.rent.RentOrderSystem;
import server.node.system.robot.RobotMessage;
import server.node.system.robot.RobotSystem;

/**
 * 任务触发器
 */
public final class TaskTrigger implements Trigger, TopicSubscriber {

	@SuppressWarnings("unused")
	private final static Logger logger = LogManager.getLogger(TaskTrigger.class.getName());

	public TaskTrigger() {
	}

	public boolean start() {
		Root.playerSystem.subscribe(PlayerMessage.LEVEL_UP, this);
		Root.pveSystem.subscribe(PveMessage.PVE_EXIT, this);
		Root.robotSystem.subscribe(RobotMessage.ROBOT_PART_LEVELUP, this);
		Root.robotSystem.subscribe(RobotMessage.ROBOT_PAINT, this);
		Root.robotSystem.subscribe(RobotMessage.ROBOT_CHANGE_PART, this);
		Root.pvpSystem.subscribe(PvpMessage.PVP_EXIT, this);
		Root.pvpSystem.subscribe(PvpMessage.PVP_EXIT_NPC, this);
		Root.pvpSystem.subscribe(BeatRobotMessage.PVP_BEAT_ROBOT, this);
		Root.pvpSystem.subscribe(BeatRobotMessage.PVP_BEAT_ROBOT_NPC, this);
		Root.rentOrderSystem.subscribe(RentOrderMessage.Hire, this);
		Root.rentOrderSystem.subscribe(RentOrderMessage.Close, this);
		Root.missionSystem.subscribe(MissionMessage.GainCash, this);
		Root.missionSystem.subscribe(MissionMessage.AddStar, this);
		Root.giftSystem.subscribe(GiftMessage.SEND_GIFT, this);
		Root.lotterySystem.subscribe(LotteryMessage.LOTTERY_PARTS, this);
		return true;
	}

	public void stop() {
		Root.playerSystem.unsubscribe(PlayerMessage.LEVEL_UP, this);
		Root.pveSystem.unsubscribe(PveMessage.PVE_EXIT, this);
		Root.robotSystem.unsubscribe(RobotMessage.ROBOT_PART_LEVELUP, this);
		Root.robotSystem.unsubscribe(RobotMessage.ROBOT_PAINT, this);
		Root.robotSystem.unsubscribe(RobotMessage.ROBOT_CHANGE_PART, this);
		Root.pvpSystem.unsubscribe(PvpMessage.PVP_EXIT, this);
		Root.pvpSystem.unsubscribe(PvpMessage.PVP_EXIT_NPC, this);
		Root.pvpSystem.unsubscribe(BeatRobotMessage.PVP_BEAT_ROBOT, this);
		Root.pvpSystem.unsubscribe(BeatRobotMessage.PVP_BEAT_ROBOT_NPC, this);
		Root.rentOrderSystem.unsubscribe(RentOrderMessage.Hire, this);
		Root.rentOrderSystem.unsubscribe(RentOrderMessage.Close, this);
		Root.missionSystem.unsubscribe(MissionMessage.GainCash, this);
		Root.missionSystem.unsubscribe(MissionMessage.AddStar, this);
		Root.giftSystem.unsubscribe(GiftMessage.SEND_GIFT, this);
		Root.lotterySystem.unsubscribe(LotteryMessage.LOTTERY_PARTS, this);
	}

	@Override
	public void onMessage(TopicPublisher publisher, TopicMessage message) {
		//玩家系统发布的升级消息,0 taskHomeLevel
		if (publisher instanceof PlayerSystem && message instanceof PlayerMessage) {
			PlayerMessage playerMessage = (PlayerMessage) message;
			if (playerMessage.getName() == PlayerMessage.LEVEL_UP) {
				Player player = playerMessage.getPlayer();
				try {
					Root.taskSystem.doTaskHomeLevel(player);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		//关卡系统发来的消息,加星
		if (publisher instanceof MissionSystem && message instanceof MissionMessage) {
			MissionMessage missionMessage = (MissionMessage) message;
			if (missionMessage.getName() == MissionMessage.AddStar) {
				try {
					Root.taskSystem.doTaskStarNum(missionMessage.getPlayer(), missionMessage.getMissionBag());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		//抽奖系统发来的消息,抽到part
		if (publisher instanceof LotterySystem && message instanceof LotteryMessage) {
			LotteryMessage lotteryMessage = (LotteryMessage) message;
			if (lotteryMessage.getName() == LotteryMessage.LOTTERY_PARTS) {
				try {
					Root.taskSystem.doTaskPartNum(lotteryMessage.getPlayer(), null, lotteryMessage.getPartPOs(), true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		//战斗系统发来的通关消息
		if (publisher instanceof PveSystem && message instanceof PveMessage) {
			PveMessage pveMessage = (PveMessage) message;
			Player player = pveMessage.getPlayer();
			if (pveMessage.getName() == PveMessage.PVE_EXIT) {
				//pve战斗结束相关task
				try {
					Root.taskSystem.doTaskWhenExitPve(player, pveMessage.getPveBattleResult());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		//机器人系统发布来的机器人消息,
		if (publisher instanceof RobotSystem && message instanceof RobotMessage) {
			RobotMessage robotMessage = (RobotMessage) message;
			Player player = robotMessage.getPlayer();
			//robot part 升级
			if (robotMessage.getName() == RobotMessage.ROBOT_PART_LEVELUP) {
				if (robotMessage.isLevelUp()) {//等级上升了
					try {
						Root.taskSystem.doTaskPartLevel(robotMessage.getPlayer(), robotMessage.getPart());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			//robot part 涂装颜色
			if (robotMessage.getName() == RobotMessage.ROBOT_PAINT) {
				try {
					Root.taskSystem.doTaskPaintNum(player, robotMessage.getPaintParts());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			//robot change part  换装
			if (robotMessage.getName() == RobotMessage.ROBOT_CHANGE_PART) {
				try {
					Root.taskSystem.doTaskRobotQualityNum(player, robotMessage.getRobot());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		//pvp系统发来的消息,
		if (publisher instanceof PvpSystem) {

			if (message instanceof PvpMessage) {
				PvpMessage pvpMessage = (PvpMessage) message;

				if (pvpMessage.getName() == PvpMessage.PVP_EXIT) {
					Player defender = pvpMessage.getDefender();
					PvpBattleResult pvpBattleResult = pvpMessage.getPvpBattleResult();
					try {
						Root.taskSystem.doTaskWhenExitPvp(pvpMessage.getAttacker(), defender, pvpBattleResult);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

				if (pvpMessage.getName() == PvpMessage.PVP_EXIT_NPC) {
					PvpBattleResult pvpBattleResult = pvpMessage.getPvpBattleResult();
					try {
						Root.taskSystem.doTaskWhenExitPvpNpc(pvpMessage.getAttacker(), pvpMessage.getPvpBattleResult().getPvpBattle().getPvpNpc(), pvpBattleResult);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}

			if (message instanceof BeatRobotMessage) {
				BeatRobotMessage beatRobotMessage = (BeatRobotMessage) message;

				//击败一个机器人
				if (beatRobotMessage.getName() == BeatRobotMessage.PVP_BEAT_ROBOT) {
					try {
						Root.taskSystem.doTaskPvpBeatRobot(beatRobotMessage.getAttacker(), beatRobotMessage.getBeatedRobot(), null);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				//击败一个机器人  是npc的
				if (beatRobotMessage.getName() == BeatRobotMessage.PVP_BEAT_ROBOT_NPC) {
					try {
						Root.taskSystem.doTaskPvpBeatRobot(beatRobotMessage.getAttacker(), null, beatRobotMessage.getBeatedNpcRobot());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}

		}

		//订单系统发来的消息
		if (publisher instanceof RentOrderSystem && message instanceof RentOrderMessage) {
			RentOrderMessage rentOrderMessage = (RentOrderMessage) message;
			Player hirer = rentOrderMessage.getHirer();
			Player renter = rentOrderMessage.getRenter();
			if (rentOrderMessage.getName() == RentOrderMessage.Close) {
				try {
					Root.taskSystem.doTaskRentRobotNum(renter);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rentOrderMessage.getName() == RentOrderMessage.Hire) {
				try {
					Root.taskSystem.doTaskHireRobotNum(hirer);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		//单人游戏中收钱
		if (publisher instanceof MissionSystem && message instanceof MissionMessage) {
			MissionMessage missionMessage = (MissionMessage) message;
			Player player = missionMessage.getPlayer();
			int cashNum = missionMessage.getCashNum();
			if (missionMessage.getName() == MissionMessage.GainCash) {
				try {
					Root.taskSystem.doTaskGainCashNum(player, cashNum);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		//好友系统发来的消息
		if (publisher instanceof FriendSystem && message instanceof FriendMessage) {
			FriendMessage friendMessage = (FriendMessage) message;
			Player player = friendMessage.getPlayer();
			if (friendMessage.getName() == FriendMessage.SYNC_FRIENDS) {
				//好友数量
				try {
					Root.taskSystem.doTaskFriendNum(player, friendMessage.getFriendBag());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		//gift系统发来的消息
		if (publisher instanceof GiftSystem && message instanceof GiftMessage) {
			GiftMessage giftMessage = (GiftMessage) message;
			Player player = giftMessage.getPlayer();
			if (giftMessage.getName() == GiftMessage.SEND_GIFT) {
				//赠送体力次数
				try {
					Root.taskSystem.doTaskGivePowerNum(player);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
