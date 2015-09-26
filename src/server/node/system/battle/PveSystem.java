package server.node.system.battle;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.system.AbstractSystem;
import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;
import gamecore.util.Clock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Content;
import server.node.system.Root;
import server.node.system.boss.BossLoadData;
import server.node.system.boss.BossMaking;
import server.node.system.dailyJob.JobBag;
import server.node.system.egg.EggPart;
import server.node.system.egg.EggPartBag;
import server.node.system.mission.Point;
import server.node.system.mission.PointFlushBag;
import server.node.system.mission.PointLoadData;
import server.node.system.mission.PointMaking;
import server.node.system.mission.pointAward.DropGood;
import server.node.system.player.CashType;
import server.node.system.player.GoldType;
import server.node.system.player.Player;
import server.node.system.robot.FightProperty;
import server.node.system.robot.Robot;
import server.node.system.robot.RobotBag;
import server.node.system.robot.RobotType;
import server.node.system.robotPart.Part;

/**
 * 战斗系统。
 */
public final class PveSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(PveSystem.class.getName());

	public PveSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("PveSystem start....");
		run = true;
		System.out.println("PveSystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {
		run = false;
	}

	//进入一个关卡,进入战斗
	public SystemResult enterBattle(Player player, int pointMakingId) {
		SystemResult result = new SystemResult();

		//小关卡存储
		Point pointPO = Root.missionSystem.getPointPO(player, pointMakingId);

		//小关卡原型
		PointMaking pointMaking = PointLoadData.getInstance().getPointMaking(pointMakingId);

		if (pointPO == null) {
			result.setCode(ErrorCode.POINT_NO_OPEN);
			return result;
		}

		result.setBindle(pointPO);

		PveBattle pveBattle = new PveBattle(player, pointMaking.getMission(), pointPO);
		pveBattle.synchronize();

		return result;

	}

	//开打
	public SystemResult doPve(Player player) throws SQLException {
		SystemResult result = new SystemResult();

		PveBattle pveBattle = RedisHelperJson.getPveBattle(player.getId());

		if (pveBattle != null) {

			JobBag jobBag = Root.jobSystem.getJobBag(player);
			//MoreFight(2), 	越战越勇	进行n场比赛。
			Root.jobSystem.doJobMoreFight(player, jobBag, false);

			Root.jobSystem.updateJobBagToCacheAndDB(player, jobBag);
		}

		return result;
	}

	//结束一个关卡的战斗,战斗信息在缓存里
	public SystemResult exitPve(Player player, PveBattleResult pveBattleResult) throws SQLException {

		SystemResult result = new SystemResult();

		PveBattle pveBattle = RedisHelperJson.getPveBattle(player.getId());

		pveBattleResult.setPveBattle(pveBattle);

		result.setBindle(pveBattle);

		if (pveBattle == null) {//没有战斗
			result.setCode(ErrorCode.NO_BATTLE_NO_PASS);
		} else {

			//参加战斗的机器人.为之后的buff cash_up  exp_up ,,和 使用武器,做准备
			List<Robot> fightRobots = pveBattle.readAttackRobotArray();

			//小关卡存储
			Point pointPO = pveBattle.getPointPO();

			//进出战斗的时间
			pveBattleResult.setEnterTime(pveBattle.getEnterTime());
			pveBattleResult.setExitTime(Clock.currentTimeSecond());

			ArrayList<Part> parts = new ArrayList<Part>();
			ArrayList<Integer> expParts = new ArrayList<Integer>();
			int colorNum = 0;

			//判断是刷新关卡还是首次通关,win才算首通
			boolean firstPass = pointPO.getStar() > pointPO.getPassStar() && pveBattleResult.isWin();

			pveBattleResult.setFirstPass(firstPass);

			if (pveBattleResult.isWin()) {

				//小关卡原型
				PointMaking pointMaking = PointLoadData.getInstance().getPointMaking(pointPO.getMakingId());

				//记录刷了这个关卡
				flushPoint(player, pveBattle.getPointPO().getMakingId());

				//掉落物品
				List<DropGood> dropGoods = null;

				int cash = pointMaking.getCash(pointPO.getStar());
				int exp = pointMaking.getExp(pointPO.getStar());
				int gold = pointMaking.getGold(pointPO.getStar());

				//cashup   expup  的buff加成
				cash += Root.buffSystem.cashUpBuff(cash, fightRobots, pveBattle.getAttackHireRobot(), pveBattle.getAttackNpcRobot());
				exp += Root.buffSystem.expUpBuff(exp, fightRobots, pveBattle.getAttackHireRobot(), pveBattle.getAttackNpcRobot());

				//boss关,走刷蛋
				if (pointMaking.getIsBoss() != null && pointMaking.getIsBoss() == 1) {
					if (firstPass) {//首次通关此星级
						pveBattleResult.setCash(cash);
						pveBattleResult.setExp(exp);
						pveBattleResult.setGold(gold);
					} else {//刷关卡
						pveBattleResult.setCash(cash / 20);
						pveBattleResult.setExp(exp / 20);
					}
					//这里有掉蛋
					awardEggParts(player, pointMaking, pveBattleResult);
				} else {//普通关卡,正常
					if (firstPass) {//首次通关此星级

						pveBattleResult.setCash(cash);
						pveBattleResult.setExp(exp);
						pveBattleResult.setGold(gold);

						if (pointMaking.getId() <= PointLoadData.getInstance().LastToturialPoint) {//教学关 走刷新奖励
							dropGoods = pointMaking.getDropGoodFlushs(null);
						} else {
							dropGoods = pointMaking.getDropGoodStars(pointPO.getStar());
						}

					} else {//刷关卡
						dropGoods = pointMaking.getDropGoodFlushs(null);
						pveBattleResult.setCash(cash / 20);
						pveBattleResult.setExp(exp / 20);
					}
					if (dropGoods != null && dropGoods.size() > 0) {

						for (DropGood dropGood : dropGoods) {

							if (dropGood.getType() == 6) {//6:颜料瓶
								colorNum++;
							} else if (dropGood.getType() == 5) {//5:能量块 expPart
								expParts.add(dropGood.getId());
							} else {//其他组件   0:head,1:body,2:arm,3:leg,4:weapon
								Part part = Root.partSystem.createPart(Root.idsSystem.takePartId(), dropGood.getType(), dropGood.getId(), dropGood.getLevel(), 0, 0);
								if (part != null) {
									parts.add(part);
								}
							}
						}

						//战利品
						pveBattleResult.setParts(parts);
						pveBattleResult.setExpParts(expParts);
					}
				}

				//真正的加给用户
				awardBattle(player, pveBattleResult, colorNum);

			}

			//发送通关信息
			PveMessage pveMessage = new PveMessage(PveMessage.PVE_EXIT, player, pveBattle, pveBattleResult);
			this.publish(pveMessage);

		}

		return result;
	}

	private void flushPoint(Player player, Integer pointMakingId) {
		//小关卡原型
		PointMaking pointMaking = PointLoadData.getInstance().getPointMaking(pointMakingId);
		if (pointMaking.getIsBoss() != null && pointMaking.getIsBoss() == 1) {
			PointFlushBag pointFlushBag = Root.missionSystem.getPointFlushBag(player);
			pointFlushBag.flushPoint(pointMakingId, true);
		}
	}

	//奖励egg
	private void awardEggParts(Player attacker, PointMaking pointMaking, PveBattleResult pveBattleResult) {

		//掉落物品,颜色瓶  expPart  part
		List<DropGood> dropGoods = pointMaking.getDropGoodFlushs(pveBattleResult.getEggNum());

		List<EggPart> eggParts = new ArrayList<EggPart>();

		Integer eggIndex = 0;

		if (dropGoods != null && dropGoods.size() > 0) {

			for (DropGood dropGood : dropGoods) {

				EggPart eggPart = null;

				if (dropGood.getType() == 6) {//6:颜料瓶
					eggPart = new EggPart(eggIndex, dropGood.getId(), null, null);
				} else if (dropGood.getType() == 5) {//5:exp 宝宝
					eggPart = new EggPart(eggIndex, null, dropGood.getId(), null);
				} else {//其他组件   0:head,1:body,2:arm,3:leg,4:weapon
					Part part = Root.partSystem.createPart(Root.idsSystem.takePartId(), dropGood.getType(), dropGood.getId(), dropGood.getLevel(), 0, 0);
					eggPart = new EggPart(eggIndex, null, null, part);
				}
				eggParts.add(eggPart);

				eggIndex++;
			}

		}

		Collections.shuffle(eggParts);

		HashMap<Integer, EggPart> eggMap = new HashMap<Integer, EggPart>();
		for (EggPart eggPart : eggParts) {
			eggMap.put(eggPart.getId(), eggPart);
		}

		EggPartBag eggPartBag = Root.partSystem.addEggParts(attacker, eggMap, pointMaking.getEggCostTable());

		pveBattleResult.setEggPartBag(eggPartBag);
	}

	//得到奖励
	private void awardBattle(Player player, PveBattleResult result, int colorNum) throws SQLException {

		//加入颜料瓶
		if (colorNum > 0) {
			List<Integer> newColors = Root.colorSystem.addColorNum(player, colorNum);
			result.setColors(newColors);
		}

		if (result.getParts() != null && !result.getParts().isEmpty()) {
			Root.partSystem.addParts(player, result.getParts(), true);
		}

		if (result.getExpParts() != null && !result.getExpParts().isEmpty()) {
			Root.expPartSystem.addExpParts(player, result.getExpParts());
		}

		//加gold
		Root.playerSystem.changeGold(player, result.getGold(), GoldType.POINT_GIVE, false);
		//加入cash
		Root.playerSystem.changeCash(player, result.getCash(), CashType.POINT_GET, false);
		//长经验
		Root.playerSystem.addExp(player, result.getExp(), false);

		player.synchronize();

	}

	//中途验证属性值
	public SystemResult check(Player player, BattleCheckBean battleCheckBean) throws SQLException {
		SystemResult result = new SystemResult();

		//验证攻击者的机器人
		RobotBag a_robotBag = Root.robotSystem.getRobotBag(player);
		ArrayList<CheckRobot> attackerRobots = battleCheckBean.getAttackerRobots();

		PveBattle pveBattle = RedisHelperJson.getPveBattle(player.getId());

		for (CheckRobot checkRobot : attackerRobots) {

			if (checkRobot.getPlayerId() == player.getId().longValue()) {//自己的

				Robot robot = a_robotBag.readRobot(RobotType.BATTLE, checkRobot.getSlot());

				FightProperty fightProperty = robot.refreshFightProperty();

				if (fightProperty.getAtk() != checkRobot.getAtk() || fightProperty.getDef() != checkRobot.getDef() || fightProperty.getHp() != checkRobot.getHp()
						|| fightProperty.getCrit() != checkRobot.getCrit()) {

					//不一致,可能作弊
					result.setCode(ErrorCode.CHECK_BATTLE_ATTACK_ERROR);

					logger.error("my   attack client		:	slot:[" + checkRobot.getSlot() + "] atk:[" + checkRobot.getAtk() + "] def:[" + checkRobot.getDef() + "] hp:["
							+ checkRobot.getHp() + "] crit:[" + checkRobot.getCrit() + "]");
					logger.error("my   attack server		:     	slot:[" + robot.getSlot() + "] atk:[" + fightProperty.getAtk() + "] def:[" + fightProperty.getDef() + "] hp:["
							+ fightProperty.getHp() + "] crit:[" + fightProperty.getCrit() + "] --id[" + robot.getId() + "]");

					break;
				}

				pveBattle.addAttackRobot(robot);

			} else {//租借的,有可能是玩家,有可能是npc
				Player rentPlayer = Root.playerSystem.getPlayer(checkRobot.getPlayerId());
				//租赁的玩家的
				if (rentPlayer != null) {

					FightProperty fightProperty = pveBattle.getAttackHireRobot().refreshFightProperty();

					if (fightProperty.getAtk() != checkRobot.getAtk() || fightProperty.getDef() != checkRobot.getDef() || fightProperty.getHp() != checkRobot.getHp()
							|| fightProperty.getCrit() != checkRobot.getCrit()) {

						//不一致,可能作弊
						result.setCode(ErrorCode.CHECK_BATTLE_RENT_ERROR);

						logger.error("rent   attack client	:	slot:[" + checkRobot.getSlot() + "] atk:[" + checkRobot.getAtk() + "] def:[" + checkRobot.getDef() + "] hp:["
								+ checkRobot.getHp() + "] crit:[" + checkRobot.getCrit() + "]");
						logger.error("rent   attack server  :	slot:[" + checkRobot.getSlot() + "] atk:[" + fightProperty.getAtk() + "] def:[" + fightProperty.getDef() + "] hp:["
								+ fightProperty.getHp() + "] crit:[" + fightProperty.getCrit() + "]");
					}

				} else {//租赁的npc的,

				}
			}

		}

		if (!pveBattle.isConsumeWear()) {//没有扣过耐久
			//扣耐久
			Point pointPO = pveBattle.getPointPO();
			PointMaking pointMaking = PointLoadData.getInstance().getPointMaking(pointPO.getMakingId());
			Root.robotSystem.consumeWearByRobots(player, null, pveBattle.readAttackRobotArray(), pointMaking.getWear(pointPO.getStar()));

			pveBattle.setConsumeWear(true);
		}

		pveBattle.synchronize();

		return result;
	}

	//验证伤害范围
	public SystemResult checkDamage(Player player, DamageCheckBean damageCheckBean) {
		SystemResult result = new SystemResult();

		PveBattle pveBattle = RedisHelperJson.getPveBattle(player.getId());

		if (pveBattle != null) {

			Robot attackRobot;
			BossMaking bossMaking;

			for (DamageBean bean : damageCheckBean.getDamageBeans()) {

				attackRobot = pveBattle.getAttackRobots().get(bean.getAttackRobotId());
				if (attackRobot == null) {
					if (bean.getAttackRobotId() == pveBattle.getAttackHireRobot().getId()) {
						attackRobot = pveBattle.getAttackHireRobot();
					}
				}

				if (attackRobot != null) {

					FightProperty fightProperty = attackRobot.refreshFightProperty();

					//验证自己的战斗属性
					if (fightProperty.getAtk() != bean.getAtk() || fightProperty.getDef() != bean.getDef() || fightProperty.getHp() != bean.getHp()
							|| fightProperty.getCrit() != bean.getCrit()) {
						//不匹配
						result.setCode(ErrorCode.CHECK_BATTLE_ATTACK_ERROR);

						logger.error("attack client		:	atk:[" + bean.getAtk() + "] def:[" + bean.getDef() + "] hp:[" + bean.getHp() + "] crit:[" + bean.getCrit() + "]");
						logger.error("attack server		:   atk:[" + fightProperty.getAtk() + "] def:[" + fightProperty.getDef() + "] hp:[" + fightProperty.getHp() + "] crit:["
								+ fightProperty.getCrit() + "] --id[" + attackRobot.getId() + "]");
						break;
					}

					//伤害值小于(攻击-防御 )*Content.maxDamage即为合法
					bossMaking = BossLoadData.getInstance().getBossMaking(bean.getDefendRobotId());
					if (attackRobot != null) {//确定有这个robot.如果是租赁的,那这里为null
						if ((fightProperty.getAtk() - bossMaking.getAtkTable().get(pveBattle.getPointPO().getStar() - 1)) * Content.maxDamage < bean.getDamage()) {
							//非法伤害
							result.setCode(ErrorCode.CHECK_BATTLE_DAMAGE_ERROR);

							logger.error("damage:[" + bean.getDamage() + "] attRobotId:[" + bean.getAttackRobotId() + "] defRobotId:[" + bean.getDefendRobotId()
									+ "] atk def hp crit:[" + bean.getAtk() + " " + bean.getDef() + " " + bean.getHp() + " " + bean.getCrit() + "]");

							break;
						}
					}
				}
			}

		}

		return result;
	}

}
