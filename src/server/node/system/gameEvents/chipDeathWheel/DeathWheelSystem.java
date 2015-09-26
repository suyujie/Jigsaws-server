package server.node.system.gameEvents.chipDeathWheel;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.system.AbstractSystem;
import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;
import gamecore.util.Clock;
import gamecore.util.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Root;
import server.node.system.battle.BattleCheckBean;
import server.node.system.battle.CheckRobot;
import server.node.system.battle.DamageCheckBean;
import server.node.system.gameEvents.chipDeathWheel.makingData.DeathWheelBattleMapMaking;
import server.node.system.gameEvents.chipDeathWheel.makingData.DeathWheelBossMaking;
import server.node.system.player.GoldType;
import server.node.system.player.Player;
import server.node.system.robot.Robot;
import server.node.system.robot.RobotBag;
import server.node.system.robotPart.PartLoadData;
import server.node.system.robotPart.WeaponType;

public final class DeathWheelSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(DeathWheelSystem.class.getName());

	public static final int openLevel = 15;//开放等级
	public static final int[] resetGold = { 50, 80, 120, 200 };

	public DeathWheelSystem() {
	}

	@Override
	public boolean startup() {
		run = true;
		System.out.println("DeathWheelSystem start....");
		boolean b = DeathWheelLoadData.getInstance().readData();
		System.out.println("DeathWheelSystem start....OK");
		return b;
	}

	@Override
	public void shutdown() {
		run = false;
	}

	public DeathWheel getDeathWheel(Player player, boolean checkTimeOut, boolean createIfNull) {

		DeathWheel deathWheel = RedisHelperJson.getDeathWheel(player.getId());

		if (deathWheel != null && checkTimeOut && deathWheel.checkTimeOut()) {// 有副本,需要验证超时,并且超时了
			deathWheel = null;
		}

		if (deathWheel == null && createIfNull) {
			deathWheel = createDeathWheel(player);
		}

		return deathWheel;
	}

	public DeathWheel createDeathWheel(Player player) {

		DeathWheel deathWheel = new DeathWheel(Utils.getOneLongId(), player, Clock.currentTimeSecond(), 0, null);

		deathWheel.synchronize();

		return deathWheel;
	}

	public DeathWheel createBosses(Player player, List<String> chipNames) {

		Map<Integer, DeathWheelBoss> bosses = new HashMap<Integer, DeathWheelBoss>();

		//加入选择的芯片
		SelectChipsInDeathWheel chipsInDeathWheel = RedisHelperJson.getSelectChipsInDeathWheel(player.getId());
		if (chipsInDeathWheel == null) {
			chipsInDeathWheel = new SelectChipsInDeathWheel(player);
		}
		chipsInDeathWheel.addRandChips(chipNames, true);
		

		//chipnames 只有三个,需要额外的随机两个
		chipNames.addAll(Utils.randomSelect(PartLoadData.getInstance().suitNames, 5 - chipNames.size(), chipNames));

		java.util.Collections.shuffle(chipNames);//乱序

		List<String> others = new ArrayList<String>();

		int hardLevel = 0;
		for (String chipName : chipNames) {
			DeathWheelBossMaking bossMaking = DeathWheelLoadData.getInstance().getDeathWheelBossMaking(hardLevel);//根据难度,获得making
			WeaponType weaponType = PartLoadData.getInstance().getWeaponTypeBySuitName(chipName);

			int bossLevel = player.getLevel() - 5;
			if (bossLevel < 1) {
				bossLevel = 1;
			}

			DeathWheelBoss boss = new DeathWheelBoss(bossMaking.getId(), bossLevel, 0, 0);

			boss.setChipName(chipName);
			boss.setWeaponType(weaponType);

			//battle field
			DeathWheelBattleMapMaking deathWheelBattleMapMaking = DeathWheelLoadData.getInstance().getDeathWheelBattleMapMaking();
			//battle other,不重复的
			String battleOther = DeathWheelLoadData.getInstance().getOther(weaponType, others);
			others.add(battleOther);

			boss.setFieldName(deathWheelBattleMapMaking.getName());
			boss.setField(deathWheelBattleMapMaking.getOneField());
			boss.setOther(battleOther);
			bosses.put(hardLevel, boss);
			hardLevel++;
		}

		DeathWheel deathWheel = getDeathWheel(player, false, false);

		deathWheel.setBosses(bosses);
		deathWheel.setBattleHardLevel(null);

		RobotBag robotBag = Root.robotSystem.getRobotBag(player);
		HashMap<Integer, Robot> robots = robotBag.getBattleRobots();
		deathWheel.setAttackRobots(robots);

		deathWheel.synchronize();

		return deathWheel;
	}

	public int resetNeedGold(DeathWheel deathWheel) {
		if (deathWheel.getResetNum() >= resetGold.length) {
			return resetGold[resetGold.length - 1];
		} else {
			return resetGold[deathWheel.getResetNum()];
		}
	}

	public SystemResult resetDeathWheel(Player player, DeathWheel deathWheel) {
		SystemResult result = new SystemResult();
		if (deathWheel == null) {
			deathWheel = getDeathWheel(player, false, false);
		}

		if (deathWheel != null) {
			Root.playerSystem.changeGold(player, -resetNeedGold(deathWheel), GoldType.RESET_DEATHWHEEL, true);
			deathWheel.setResetNum(deathWheel.getResetNum() + 1);
			deathWheel.setBosses(null);
			deathWheel.synchronize();
		} else {
			result.setCode(ErrorCode.DEATH_WHEEL_NULL);
		}

		return result;
	}

	public SystemResult enterBattle(Player player, Integer hardLevel) {

		SystemResult result = new SystemResult();

		DeathWheel deathWheel = getDeathWheel(player, false, false);
		if (deathWheel != null) {
			DeathWheelBoss boss = deathWheel.getBosses().get(hardLevel);
			boss.setDoNum(boss.getDoNum() + 1);
			deathWheel.putBoss(boss, true);
			deathWheel.setBattleHardLevel(hardLevel);

			deathWheel.synchronize();

			if (boss.getDoNum() == 1) {//今天第一次打的时候,插入日志库
				Root.logSystem.addDeathWheelLog(player, deathWheel);
			} else {//不是第一次,更新数据库
				Root.logSystem.updateDeathWheelLog(player, deathWheel, null, null);
			}

		} else {
			result.setCode(ErrorCode.PARAM_ERROR);
		}

		return result;
	}

	public SystemResult exitBattle(Player player, Integer hardLevel, boolean win) throws SQLException {

		SystemResult result = new SystemResult();

		DeathWheel deathWheel = getDeathWheel(player, false, false);
		if (deathWheel != null && deathWheel.getBattleHardLevel() != null) {
			if (win) {//胜利
				DeathWheelBoss boss = deathWheel.getBosses().get(hardLevel);
				//加入芯片
				DeathWheelBossMaking deathWheelBossMaking = DeathWheelLoadData.getInstance().getDeathWheelBossMaking(boss.getBossMakingId());
				Root.chipSystem.addChipNum(player, null, boss.getChipName(), deathWheelBossMaking.getChipnum());
				result.setMap("chipName", boss.getChipName());
				result.setMap("chipNum", deathWheelBossMaking.getChipnum());

				boss.setPassNum(boss.getPassNum() + 1);

				Root.logSystem.updateDeathWheelLog(player, deathWheel, deathWheelBossMaking.getChipnum(), true);

			} else {
				Root.logSystem.updateDeathWheelLog(player, deathWheel, null, false);
			}
			deathWheel.setBattleHardLevel(null);
		} else {
			result.setCode(ErrorCode.DEATH_WHEEL_NULL);
		}

		deathWheel.synchronize();

		return result;
	}

	//中途验证属性值
	public SystemResult check(Player player, BattleCheckBean battleCheckBean) {
		SystemResult result = new SystemResult();

		//验证攻击者的机器人
		ArrayList<CheckRobot> attackerRobots = battleCheckBean.getAttackerRobots();

		for (CheckRobot checkRobot : attackerRobots) {

			if (checkRobot.getPlayerId() == player.getId()) {//自己的

				//				Robot robot = battle.getAttackRobots().get(checkRobot.getSlot());
				//
				//				if (robot != null) {
				//
				//					FightProperty fightProperty = robot.refreshFightProperty();
				//
				//					if (fightProperty.getAtk() != checkRobot.getAtk() || fightProperty.getDef() != checkRobot.getDef() || fightProperty.getHp() != checkRobot.getHp()
				//							|| fightProperty.getCrit() != checkRobot.getCrit()) {
				//						//不一致,可能作弊
				//						result.setCode(ErrorCode.CHECK_BATTLE_ATTACK_ERROR);
				//
				//						logger.error("attack client         :  slot:[" + checkRobot.getSlot() + "] atk:[" + checkRobot.getAtk() + "] def:[" + checkRobot.getDef() + "] hp:["
				//								+ checkRobot.getHp() + "] crit:[" + checkRobot.getCrit() + "]");
				//						logger.error("attack server         :  slot:[" + checkRobot.getSlot() + "] atk:[" + fightProperty.getAtk() + "] def:[" + fightProperty.getDef() + "] hp:["
				//								+ fightProperty.getHp() + "] crit:[" + fightProperty.getCrit() + "]");
				//
				//						break;
				//					}
				//				}

			}

		}

		return result;
	}

	//验证伤害范围
	public SystemResult checkDamage(Player player, DamageCheckBean damageCheckBean) {
		SystemResult result = new SystemResult();

		//		Robot attackRobot;
		//		BossMaking bossMaking;
		//
		//		for (DamageBean bean : damageCheckBean.getDamageBeans()) {
		//
		//			attackRobot = pveBattle.getAttackRobots().get(bean.getAttackRobotId());
		//			if (attackRobot == null) {
		//				if (bean.getAttackRobotId() == pveBattle.getAttackHireRobot().getId()) {
		//					attackRobot = pveBattle.getAttackHireRobot();
		//				}
		//			}
		//
		//			if (attackRobot != null) {
		//
		//				FightProperty fightProperty = attackRobot.refreshFightProperty();
		//
		//				//验证自己的战斗属性
		//				if (fightProperty.getAtk() != bean.getAtk() || fightProperty.getDef() != bean.getDef() || fightProperty.getHp() != bean.getHp()
		//						|| fightProperty.getCrit() != bean.getCrit()) {
		//					//不匹配
		//					result.setCode(ErrorCode.CHECK_BATTLE_ATTACK_ERROR);
		//
		//					logger.error("attack client		:	atk:[" + bean.getAtk() + "] def:[" + bean.getDef() + "] hp:[" + bean.getHp() + "] crit:[" + bean.getCrit() + "]");
		//					logger.error("attack server		:   atk:[" + fightProperty.getAtk() + "] def:[" + fightProperty.getDef() + "] hp:[" + fightProperty.getHp() + "] crit:["
		//							+ fightProperty.getCrit() + "] --id[" + attackRobot.getId() + "]");
		//					break;
		//				}
		//
		//				//伤害值小于(攻击-防御 )*Content.maxDamage即为合法
		//				bossMaking = BossLoadData.getInstance().getBossMaking(bean.getDefendRobotId());
		//				if (attackRobot != null) {//确定有这个robot.如果是租赁的,那这里为null
		//					if ((fightProperty.getAtk() - bossMaking.getAtkTable().get(pveBattle.getPointPO().getStar() - 1)) * Content.maxDamage < bean.getDamage()) {
		//						//非法伤害
		//						result.setCode(ErrorCode.CHECK_BATTLE_DAMAGE_ERROR);
		//
		//						logger.error("damage:[" + bean.getDamage() + "] attRobotId:[" + bean.getAttackRobotId() + "] defRobotId:[" + bean.getDefendRobotId()
		//								+ "] atk def hp crit:[" + bean.getAtk() + " " + bean.getDef() + " " + bean.getHp() + " " + bean.getCrit() + "]");
		//
		//						break;
		//					}
		//				}
		//			}
		//		}

		return result;
	}
}
