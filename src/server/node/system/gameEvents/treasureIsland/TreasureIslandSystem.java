package server.node.system.gameEvents.treasureIsland;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.system.AbstractSystem;
import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;
import gamecore.util.ArithmeticUtils;
import gamecore.util.Clock;
import gamecore.util.DateUtils;
import gamecore.util.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Content;
import server.node.system.Root;
import server.node.system.battle.BattleCheckBean;
import server.node.system.battle.CheckRobot;
import server.node.system.battle.DamageBean;
import server.node.system.battle.DamageCheckBean;
import server.node.system.battle.PveBattle;
import server.node.system.boss.BossLoadData;
import server.node.system.boss.BossMaking;
import server.node.system.player.CashType;
import server.node.system.player.GoldType;
import server.node.system.player.Player;
import server.node.system.robot.FightProperty;
import server.node.system.robot.Robot;

public final class TreasureIslandSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(TreasureIslandSystem.class.getName());

	public static final int openLevel = 9;//开放等级
	public static final int expNum = 3;
	public static final int[] resetGold = { 50, 80, 120, 200 };//重置花钻

	public TreasureIslandSystem() {
	}

	@Override
	public boolean startup() {
		run = true;
		System.out.println("TreasureIslandSystem start....");
		boolean b = TreasureIslandLoadData.getInstance().readData();
		System.out.println("TreasureIslandSystem start....OK");
		return b;
	}

	@Override
	public void shutdown() {
		run = false;
	}

	public TreasureIsland getTreasureIsland(Player player, Boolean checkTimeOut, boolean createIfNull) {

		TreasureIsland island = RedisHelperJson.getTreasureIsland(player.getId());

		if (island != null && checkTimeOut && island.checkTimeOut()) {// 有副本,需要验证超时,并且超时了
			island = null;
		}

		if (island == null && createIfNull) {
			island = createTreasureIsland(player);
		}

		return island;
	}

	private TreasureIsland createTreasureIsland(Player player) {

		int bossLevel = player.getLevel() - 5;
		if (bossLevel < 1) {
			bossLevel = 1;
		}

		TreasureIsland treasureIsland = new TreasureIsland(Utils.getOneLongId(), player, bossLevel, Clock.currentTimeSecond(), 0, 0);

		if (DateUtils.dayOfYear() % 2 == 0) {//金钱
			treasureIsland.setType(TreasureIslandType.CASH);
			treasureIsland.setAllCash(Root.missionSystem.gainCashPerHour(player) * 24 * 4);//4天的产量
		} else {//经验
			treasureIsland.setType(TreasureIslandType.EXP);
			Integer expId = Root.missionSystem.calculateGainMaxExpId(player) - 3;
			if (expId < 0) {
				expId = 0;
			}
			treasureIsland.setExpId(expId);//最大的经验块id
			treasureIsland.setExpNum(expNum);
		}

		treasureIsland.synchronize();

		return treasureIsland;
	}

	public SystemResult resetTreasureIsland(Player player, TreasureIsland treasureIsland) {
		SystemResult result = new SystemResult();
		if (treasureIsland == null) {
			treasureIsland = getTreasureIsland(player, false, false);
		}
		if (treasureIsland != null) {
			Root.playerSystem.changeGold(player, -resetNeedGold(treasureIsland), GoldType.RESET_TREASURE_ISLAND, true);
			treasureIsland.setResetNum(treasureIsland.getResetNum() + 1);
			treasureIsland.setCompletedNum(0);
			treasureIsland.synchronize();
		} else {
			result.setCode(ErrorCode.TREASURE_ISLAND_NULL);
		}

		return result;
	}

	public int resetNeedGold(TreasureIsland treasureIsland) {
		if (treasureIsland.getResetNum() >= resetGold.length) {
			return resetGold[resetGold.length - 1];
		} else {
			return resetGold[treasureIsland.getResetNum()];
		}
	}

	public SystemResult endBattleCash(Player player, int bossHp) {

		SystemResult result = new SystemResult();

		TreasureIsland treasureIsland = getTreasureIsland(player, false, false);

		if (treasureIsland != null && treasureIsland.getType() == TreasureIslandType.CASH) {

			TreasureIslandBossMaking making = TreasureIslandLoadData.getInstance().getCashBossMaking();

			int allHp = making.getHpByLevel(treasureIsland.getBossLevel());

			//(总血量-剩余血量)/总血量*所有cash
			int cash = ((Float) (ArithmeticUtils.div((allHp - bossHp), allHp, 4) * treasureIsland.getAllCash())).intValue();

			Root.playerSystem.changeCash(player, cash, CashType.TREASURE_ISLAND_GET, true);

			treasureIsland.setCompletedNum(treasureIsland.getCompletedNum() + 1);

			treasureIsland.synchronize();

			//日志
			Root.logSystem.addTreasureIslandCashLog(player, treasureIsland, cash);

			result.setBindle(cash);

			//发送消息
			this.publish(new TreasureIslandMessage(TreasureIslandMessage.TreasureIsland_End_Cash, player));
		}

		return result;
	}

	public SystemResult endBattleExp(Player player, int killNum) throws SQLException {

		SystemResult result = new SystemResult();

		TreasureIsland treasureIsland = getTreasureIsland(player, false, false);

		if (treasureIsland != null && treasureIsland.getType() == TreasureIslandType.EXP) {

			Root.expPartSystem.addExpPart(player, treasureIsland.getExpId(), killNum * treasureIsland.getExpNum());

			treasureIsland.setCompletedNum(treasureIsland.getCompletedNum() + 1);

			treasureIsland.synchronize();

			//日志
			Root.logSystem.addTreasureIslandExpLog(player, treasureIsland, treasureIsland.getExpId(), killNum, killNum * treasureIsland.getExpNum());
			result.setBindle(killNum * treasureIsland.getExpNum());

			//发送消息
			this.publish(new TreasureIslandMessage(TreasureIslandMessage.TreasureIsland_End_Exp, player));
		}

		return result;
	}

	//中途验证属性值
	public SystemResult check(Player player, BattleCheckBean battleCheckBean) {
		SystemResult result = new SystemResult();

		HashMap<Integer, Robot> robots = Root.robotSystem.getRobotBag(player).getBattleRobots();

		//验证攻击者的机器人
		ArrayList<CheckRobot> attackerRobots = battleCheckBean.getAttackerRobots();

		for (CheckRobot checkRobot : attackerRobots) {

			if (checkRobot.getPlayerId() == player.getId()) {//自己的

				Robot robot = robots.get(checkRobot.getSlot());

				if (robot != null) {

					FightProperty fightProperty = robot.refreshFightProperty();

					if (fightProperty.getAtk() != checkRobot.getAtk() || fightProperty.getDef() != checkRobot.getDef() || fightProperty.getHp() != checkRobot.getHp()
							|| fightProperty.getCrit() != checkRobot.getCrit()) {
						//不一致,可能作弊
						result.setCode(ErrorCode.CHECK_BATTLE_ATTACK_ERROR);

						logger.error("attack client         :  slot:[" + checkRobot.getSlot() + "] atk:[" + checkRobot.getAtk() + "] def:[" + checkRobot.getDef() + "] hp:["
								+ checkRobot.getHp() + "] crit:[" + checkRobot.getCrit() + "]");
						logger.error("attack server         :  slot:[" + checkRobot.getSlot() + "] atk:[" + fightProperty.getAtk() + "] def:[" + fightProperty.getDef() + "] hp:["
								+ fightProperty.getHp() + "] crit:[" + fightProperty.getCrit() + "]");

						break;
					}
				}

			}

		}

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
