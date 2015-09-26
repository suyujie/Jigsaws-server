package server.node.system.gameEvents.bergWheel;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Content;
import server.node.system.Root;
import server.node.system.battle.BattleCheckBean;
import server.node.system.battle.CheckRobot;
import server.node.system.battle.DamageBean;
import server.node.system.battle.DamageCheckBean;
import server.node.system.battle.PveBattle;
import server.node.system.berg.BergLoadData;
import server.node.system.berg.BergMaking;
import server.node.system.boss.BossLoadData;
import server.node.system.boss.BossMaking;
import server.node.system.player.GoldType;
import server.node.system.player.Player;
import server.node.system.robot.FightProperty;
import server.node.system.robot.Robot;
import server.node.system.robotPart.PartLoadData;
import server.node.system.robotPart.PartQualityType;
import server.node.system.robotPart.WeaponType;

public final class BergWheelSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(BergWheelSystem.class.getName());

	public static final int openLevel = 13;//开放等级
	public static final int[] reliveGold = { 10, 20, 40, 80 };
	public static final int[] resetGold = { 50, 80, 120, 200 };

	public BergWheelSystem() {
	}

	@Override
	public boolean startup() {
		run = true;
		System.out.println("BergWheelSystem start....");
		boolean b = BergWheelLoadData.getInstance().readData();
		System.out.println("BergWheelSystem start....OK");
		return b;
	}

	@Override
	public void shutdown() {
		run = false;
	}

	public BergWheel getBergWheel(Player player, boolean checkTimeOut, boolean createIfNull) {

		BergWheel bergWheel = RedisHelperJson.getBergWheel(player.getId());

		if (bergWheel != null && checkTimeOut && bergWheel.checkTimeOut()) {// 有副本,需要验证超时,并且超时了
			bergWheel = null;
		}

		if (bergWheel == null && createIfNull) {
			bergWheel = createBergWheel(player);
		}

		return bergWheel;
	}

	private BergWheel createBergWheel(Player player) {

		BergWheel bergWheel = new BergWheel(Utils.getOneLongId(), player, Clock.currentTimeSecond(), 0);

		BergWheelMaking bergWheelMaking = BergWheelLoadData.getInstance().bergWheelMaking;

		List<HashMap<String, Integer>> bergAwardFirst6 = new ArrayList<HashMap<String, Integer>>();
		for (int i = 0; i < 6; i++) {
			HashMap<String, Integer> bergIdAndNum = new HashMap<String, Integer>();
			int bossLevel = this.createBossLevel(player, i);
			bergIdAndNum.put("level", bergWheelMaking.getBergLevel(bossLevel));
			bergIdAndNum.put("num", bergWheelMaking.getBergNum(bossLevel));
			bergAwardFirst6.add(bergIdAndNum);
		}

		bergWheel.setBergAwardFirst6(bergAwardFirst6);

		bergWheel.synchronize();

		return bergWheel;
	}

	public int resetNeedGold(BergWheel bergWheel) {
		if (bergWheel.getResetNum() >= resetGold.length) {
			return resetGold[resetGold.length - 1];
		} else {
			return resetGold[bergWheel.getResetNum()];
		}
	}

	public int reliveNeedGold(BergWheel bergWheel) {
		if (bergWheel.getReliveNum() >= reliveGold.length) {
			return reliveGold[reliveGold.length - 1];
		} else {
			return reliveGold[bergWheel.getReliveNum()];
		}
	}

	public SystemResult resetBergWheel(Player player, BergWheel bergWheel) {
		SystemResult result = new SystemResult();
		if (bergWheel == null) {
			bergWheel = getBergWheel(player, false, false);
		}
		if (bergWheel != null) {

			Root.playerSystem.changeGold(player, -resetNeedGold(bergWheel), GoldType.RESET_BERG_WHEEL, true);

			bergWheel.setResetNum(bergWheel.getResetNum() + 1);
			bergWheel.setCompletedNum(0);
			bergWheel.setReliveNum(0);
			bergWheel.synchronize();
		} else {
			result.setCode(ErrorCode.BERG_WHEEL_NULL);
		}
		return result;
	}

	public SystemResult relive(Player player, BergWheel bergWheel) {
		SystemResult result = new SystemResult();
		if (bergWheel == null) {
			bergWheel = getBergWheel(player, false, false);
		}
		Root.playerSystem.changeGold(player, -reliveNeedGold(bergWheel), GoldType.RELIVE_BERG_WHEEL, true);
		bergWheel.setReliveNum(bergWheel.getReliveNum() + 1);
		bergWheel.synchronize();

		result.setBindle(reliveNeedGold(bergWheel));//下次复活需要的gold

		return result;
	}

	private Integer createBossLevel(Player player, Integer battleId) {
		Integer level = (player.getLevel() + battleId * 2) - 9;
		if (level <= 0) {
			return 1;
		} else {
			return level;
		}
	}

	private PartQualityType createPartQualityType(Integer battleId) {

		PartQualityType qualityType;
		if (battleId > 4) {
			qualityType = PartQualityType.TITANIUM;
		} else {
			qualityType = PartQualityType.asEnum(battleId);
		}

		return qualityType;
	}

	public SystemResult enterBattle(Player player, Integer battleId) throws SQLException {

		SystemResult result = new SystemResult();

		BergWheel bergWheel = getBergWheel(player, false, false);

		if (bergWheel == null) {
			result.setCode(ErrorCode.BERG_WHEEL_NULL);
			return result;
		} else {
			//先结算之前的战斗
			if (bergWheel.getBattle() != null) {
				exitBattle(player, bergWheel);
			}

			createBattle(player, bergWheel, battleId);

		}

		result.setBindle(bergWheel);

		return result;
	}

	private void createBattle(Player player, BergWheel bergWheel, Integer battleId) {

		//生成下一关战斗
		BergWheelBattle battle = new BergWheelBattle();

		BergWheelMaking making = BergWheelLoadData.getInstance().bergWheelMaking;

		String bossSuit = Utils.randomSelectOne(PartLoadData.getInstance().suitNames);
		WeaponType weaponType = PartLoadData.getInstance().getWeaponTypeBySuitName(bossSuit);

		battle.setBossSuit(bossSuit);
		battle.setWeaponType(weaponType);
		battle.setBossLevel(createBossLevel(player, battleId));
		battle.setQualityType(createPartQualityType(battleId));
		battle.setBergId(making.getBergId(making.getBergLevel(battle.getBossLevel())));
		battle.setBergNum(making.getBergNum(battle.getBossLevel()));
		bergWheel.setBattleId(battleId);

		bergWheel.setBattle(battle);

		//车轮战正式开始了,如果不是0,表示同一个车轮张的其他战斗
		if (battleId == 0) {
			bergWheel.setCompletedNum(bergWheel.getCompletedNum() + 1);
		}

		bergWheel.synchronize();

		Root.logSystem.addBergWheelLog(player, bergWheel);
	}

	private SystemResult exitBattle(Player player, BergWheel bergWheel) throws SQLException {

		SystemResult result = new SystemResult();

		BergWheelBattle battle = bergWheel.getBattle();

		Root.bergSystem.addBergNum(player, null, battle.getBergId(), battle.getBergNum());

		Root.logSystem.updateBergWheelLog(player, bergWheel);

		bergWheel.setBattle(null);

		bergWheel.synchronize();

		return result;
	}

	//中途验证属性值
	public SystemResult check(Player player, BattleCheckBean battleCheckBean) {
		SystemResult result = new SystemResult();

		//验证攻击者的机器人
		ArrayList<CheckRobot> attackerRobots = battleCheckBean.getAttackerRobots();

		HashMap<Integer, Robot> robots = Root.robotSystem.getRobotBag(player).getBattleRobots();

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
