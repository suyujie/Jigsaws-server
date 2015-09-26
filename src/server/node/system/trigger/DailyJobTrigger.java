package server.node.system.trigger;

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
import server.node.system.dailyJob.JobBag;
import server.node.system.gameEvents.treasureIsland.TreasureIslandMessage;
import server.node.system.gameEvents.treasureIsland.TreasureIslandSystem;
import server.node.system.player.Player;
import server.node.system.rent.RentOrderMessage;
import server.node.system.rent.RentOrderSystem;
import server.node.system.robot.RobotMessage;
import server.node.system.robot.RobotSystem;
import server.node.system.robotPart.PartMessage;
import server.node.system.robotPart.PartSystem;

/**
 * 日常任务触发器
 */
public final class DailyJobTrigger implements Trigger, TopicSubscriber {

	private final static Logger logger = LogManager.getLogger(DailyJobTrigger.class.getName());

	public DailyJobTrigger() {
	}

	public boolean start() {
		Root.pveSystem.subscribe(PveMessage.PVE_EXIT, this);
		Root.pvpSystem.subscribe(PvpMessage.PVP_EXIT, this);
		Root.pvpSystem.subscribe(PvpMessage.PVP_EXIT_NPC, this);
		Root.pvpSystem.subscribe(BeatRobotMessage.PVP_BEAT_ROBOT, this);
		Root.pvpSystem.subscribe(BeatRobotMessage.PVP_BEAT_ROBOT_NPC, this);
		Root.robotSystem.subscribe(RobotMessage.ROBOT_PART_LEVELUP, this);
		Root.robotSystem.subscribe(RobotMessage.ROBOT_REPAIRE, this);
		Root.robotSystem.subscribe(RobotMessage.ROBOT_REPAIREOK_WITH_GOLD_NO_BEGIN, this);
		Root.rentOrderSystem.subscribe(RentOrderMessage.Hire, this);
		Root.rentOrderSystem.subscribe(RentOrderMessage.Close, this);
		Root.partSystem.subscribe(PartMessage.PART_GET, this);
		Root.robotSystem.subscribe(RobotMessage.ROBOT_PAINT, this);
		Root.treasureIslandSystem.subscribe(TreasureIslandMessage.TreasureIsland_End_Cash, this);
		Root.treasureIslandSystem.subscribe(TreasureIslandMessage.TreasureIsland_End_Exp, this);
		return true;
	}

	public void stop() {
		Root.pveSystem.unsubscribe(PveMessage.PVE_EXIT, this);
		Root.pvpSystem.unsubscribe(PvpMessage.PVP_EXIT, this);
		Root.pvpSystem.unsubscribe(PvpMessage.PVP_EXIT_NPC, this);
		Root.pvpSystem.unsubscribe(BeatRobotMessage.PVP_BEAT_ROBOT, this);
		Root.pvpSystem.unsubscribe(BeatRobotMessage.PVP_BEAT_ROBOT_NPC, this);
		Root.robotSystem.unsubscribe(RobotMessage.ROBOT_PART_LEVELUP, this);
		Root.robotSystem.unsubscribe(RobotMessage.ROBOT_REPAIRE, this);
		Root.robotSystem.unsubscribe(RobotMessage.ROBOT_REPAIREOK_WITH_GOLD_NO_BEGIN, this);
		Root.rentOrderSystem.unsubscribe(RentOrderMessage.Hire, this);
		Root.rentOrderSystem.unsubscribe(RentOrderMessage.Close, this);
		Root.partSystem.unsubscribe(PartMessage.PART_GET, this);
		Root.robotSystem.unsubscribe(RobotMessage.ROBOT_PAINT, this);
		Root.treasureIslandSystem.unsubscribe(TreasureIslandMessage.TreasureIsland_End_Cash, this);
		Root.treasureIslandSystem.unsubscribe(TreasureIslandMessage.TreasureIsland_End_Exp, this);
	}

	@Override
	public void onMessage(TopicPublisher publisher, TopicMessage message) {
		//战斗系统发来的通关消息
		if (publisher instanceof PveSystem && message instanceof PveMessage) {
			PveMessage pveMessage = (PveMessage) message;
			Player player = pveMessage.getPlayer();

			if (pveMessage.getName() == PveMessage.PVE_EXIT) {

				try {
					JobBag jobBag = Root.jobSystem.getJobBag(player);

					if (pveMessage.getPveBattleResult().isWin()) {
						//WinOne(1), 		旗开得胜	取得一场战斗胜利。
						Root.jobSystem.doJobWinOne(player, jobBag, false);
						//无伤胜利
						if (pveMessage.getPveBattleResult().isNoHurt()) {
							Root.jobSystem.doJobWinNoHit(player, jobBag, true);
						}
					}

					Root.jobSystem.updateJobBagToCacheAndDB(player, jobBag);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}

		//pvp系统发来的消息,
		if (publisher instanceof PvpSystem) {

			try {

				if (message instanceof PvpMessage) {
					PvpMessage pvpMessage = (PvpMessage) message;
					if (pvpMessage.getName() == PvpMessage.PVP_EXIT) {

						PvpBattleResult pvpBattleResult = pvpMessage.getPvpBattleResult();
						if (pvpBattleResult.isWin()) {

							JobBag jobBag = Root.jobSystem.getJobBag(pvpMessage.getAttacker());

							//WinOne(1), 		旗开得胜	取得一场战斗胜利。
							Root.jobSystem.doJobWinOne(pvpMessage.getAttacker(), jobBag, false);
							//获得X场竞技对战胜利。
							Root.jobSystem.doJobPvpWin(pvpMessage.getAttacker(), jobBag, false);
							//在一场竞技对战中一挑三胜利。
							if (pvpBattleResult.getAttackerWinNum() == 3) {
								Root.jobSystem.doJobPvp1vs3(pvpMessage.getAttacker(), jobBag, false);
							}
							//无伤胜利
							if (pvpBattleResult.isNoHurt()) {
								Root.jobSystem.doJobWinNoHit(pvpMessage.getAttacker(), jobBag, false);
							}
							Root.jobSystem.updateJobBagToCacheAndDB(pvpMessage.getAttacker(), jobBag);
						}
					}

					if (pvpMessage.getName() == PvpMessage.PVP_EXIT_NPC) {
						PvpBattleResult pvpBattleResult = pvpMessage.getPvpBattleResult();
						if (pvpBattleResult.isWin()) {
							JobBag jobBag = Root.jobSystem.getJobBag(pvpMessage.getAttacker());
							//WinOne(1), 		旗开得胜	取得一场战斗胜利。
							Root.jobSystem.doJobWinOne(pvpMessage.getAttacker(), jobBag, false);
							//获得X场竞技对战胜利。
							Root.jobSystem.doJobPvpWin(pvpMessage.getAttacker(), jobBag, false);
							//在一场竞技对战中一挑三胜利。
							if (pvpBattleResult.getAttackerWinNum() == 3) {
								Root.jobSystem.doJobPvp1vs3(pvpMessage.getAttacker(), jobBag, false);
							}
							//无伤胜利
							if (pvpBattleResult.isNoHurt()) {
								Root.jobSystem.doJobWinNoHit(pvpMessage.getAttacker(), jobBag, false);
							}
							Root.jobSystem.updateJobBagToCacheAndDB(pvpMessage.getAttacker(), jobBag);
						}
					}
				}

				if (message instanceof BeatRobotMessage) {
					BeatRobotMessage beatRobotMessage = (BeatRobotMessage) message;
					JobBag jobBag = Root.jobSystem.getJobBag(beatRobotMessage.getAttacker());
					//击败一个机器人
					if (beatRobotMessage.getName() == BeatRobotMessage.PVP_BEAT_ROBOT) {
						Root.jobSystem.doJobPvpBeatWeapon(beatRobotMessage.getAttacker(), jobBag, beatRobotMessage.getBeatedRobot(), null, true);
					}
					//击败一个机器人  是npc的
					if (beatRobotMessage.getName() == BeatRobotMessage.PVP_BEAT_ROBOT_NPC) {
						Root.jobSystem.doJobPvpBeatWeapon(beatRobotMessage.getAttacker(), jobBag, null, beatRobotMessage.getBeatedNpcRobot(), true);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		//机器人系统发布来的机器人消息,
		if (publisher instanceof RobotSystem && message instanceof RobotMessage) {

			try {
				RobotMessage robotMessage = (RobotMessage) message;
				Player player = robotMessage.getPlayer();
				//JobPartLevelUp	10	节节高升	升级一个零件。
				if (robotMessage.getName() == RobotMessage.ROBOT_PART_LEVELUP) {
					Root.jobSystem.doJobPartLevelUp(player, null, true);
				}
				//进行修理。
				if (robotMessage.getName() == RobotMessage.ROBOT_REPAIRE) {
					Root.jobSystem.doJobRepaireRobot(player, null, true);
				}
				//进行修理。
				if (robotMessage.getName() == RobotMessage.ROBOT_REPAIREOK_WITH_GOLD_NO_BEGIN) {
					Root.jobSystem.doJobRepaireRobot(player, null, true);
				}

				//使用一次喷灌进行涂装改色。
				if (robotMessage.getName() == RobotMessage.ROBOT_PAINT) {
					Root.jobSystem.doJobPaint(player, null, true);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		//订单系统发来的消息
		if (publisher instanceof RentOrderSystem && message instanceof RentOrderMessage) {
			try {
				RentOrderMessage rentOrderMessage = (RentOrderMessage) message;
				Player hirer = rentOrderMessage.getHirer();
				Player renter = rentOrderMessage.getRenter();
				if (rentOrderMessage.getName() == RentOrderMessage.Close) {
					//JobRentRobot	14	机甲商人	出租的机器人被别人使用一次。
					Root.jobSystem.doJobRentRobot(renter, null, true);
				}
				if (rentOrderMessage.getName() == RentOrderMessage.Hire) {
					//JobHireRobot	13	租赁机甲	租赁机器人参加一次战斗
					Root.jobSystem.doJobHireRobot(hirer, null, true);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		//零件系统发来的消息
		if (publisher instanceof PartSystem) {
			try {
				if (message instanceof PartMessage) {//零件消息
					PartMessage partMessage = (PartMessage) message;
					if (partMessage.getName() == PartMessage.PART_GET) {
						if (partMessage.getPart() != null) {//获得一个部件
							Root.jobSystem.doJobPartGet(partMessage.getPlayer(), null, 1, true);
						}
						if (partMessage.getParts() != null) {//获得多个部件
							Root.jobSystem.doJobPartGet(partMessage.getPlayer(), null, partMessage.getParts().size(), true);
						}
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		//金银岛
		if (publisher instanceof TreasureIslandSystem) {
			try {
				if (message instanceof TreasureIslandMessage) {
					TreasureIslandMessage treasureIslandMessage = (TreasureIslandMessage) message;
					if (treasureIslandMessage.getName() == TreasureIslandMessage.TreasureIsland_End_Cash) {
						Root.jobSystem.doJobMoreFight(treasureIslandMessage.getPlayer(), null, true);
					}
					if (treasureIslandMessage.getName() == TreasureIslandMessage.TreasureIsland_End_Exp) {
						Root.jobSystem.doJobMoreFight(treasureIslandMessage.getPlayer(), null, true);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

	}
}
