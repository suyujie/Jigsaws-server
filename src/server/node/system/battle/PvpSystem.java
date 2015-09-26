package server.node.system.battle;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.system.AbstractSystem;
import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;
import gamecore.util.Clock;
import gamecore.util.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Content;
import server.node.system.Root;
import server.node.system.dailyJob.JobBag;
import server.node.system.npc.NpcLoadData;
import server.node.system.npc.NpcPlayer;
import server.node.system.npc.NpcRobot;
import server.node.system.player.CashType;
import server.node.system.player.Player;
import server.node.system.robot.FightProperty;
import server.node.system.robot.Robot;
import server.node.system.robot.RobotBag;

/**
 * pvp系统。
 */
public final class PvpSystem extends AbstractSystem {

	public static final float[] pvpLootCashRate = { 0.15F, 0.35F, 0.50F };
	public static final float pvpLootMissionCashRate = 1 / 3F;

	private static Logger logger = LogManager.getLogger(PvpSystem.class.getName());

	@Override
	public boolean startup() {
		System.out.println("PvpSystem start....");
		run = true;
		System.out.println("PvpSystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {
		run = false;
	}

	//进入pvp战斗,计算可能结果
	public SystemResult enterPvp(Player attacker, Player defender) {

		SystemResult result = new SystemResult();

		//掠夺cash的比例
		int cashRate = 20 - ((attacker.getLevel() - defender.getLevel()) / 5 * 2);
		if (cashRate < 10) {
			cashRate = 10;
		} else {
			if (cashRate > 30) {
				cashRate = 30;
			}
		}

		//掠夺cash的数量
		int lootCash = (int) (defender.getCash() * cashRate / 100);
		//关卡里面的钱也被抢，已经产钱的三分之一
		int lootGainCash = (int) (Root.missionSystem.previewGainMission(defender, null) * pvpLootMissionCashRate);
		//defender 每个关卡最多减少的时间
		HashMap<Integer, Long> missionLoseTime = Root.missionSystem.missionLoseTime(defender, null, pvpLootMissionCashRate);
		//胜利后可以得到的钱
		int winCash = lootCash + lootGainCash;
		//获得的cash
		int winCash_1 = (int) (winCash * pvpLootCashRate[0]);
		int winCash_2 = (int) (winCash * pvpLootCashRate[1]);
		int[] cashs = { winCash_1, winCash_2, winCash - winCash_1 - winCash_2 };

		//胜利获得的勋章
		int winCup = 15 + (int) (attacker.getPlayerStatistics().getCupNum() / 1000) * 5
				- ((attacker.getPlayerStatistics().getCupNum() - defender.getPlayerStatistics().getCupNum()) / 15);
		if (winCup < 1) {
			winCup = 1;
		} else {
			if (winCup > 35) {
				winCup = 35;
			}
		}

		//失败损失的勋章
		int loseCup = 15 + (int) (defender.getPlayerStatistics().getCupNum() / 1000) * 5
				- ((defender.getPlayerStatistics().getCupNum() - attacker.getPlayerStatistics().getCupNum()) / 15);
		if (loseCup < 1) {
			loseCup = 1;
		} else {
			if (loseCup > 35) {
				loseCup = 35;
			}
		}

		try {
			//被抢的exp
			Integer expId = Root.expPartSystem.beLootExp(defender, null);
			//被抢的chip
			String chipName = Root.chipSystem.beLootChip(defender, null);
			//被抢的bergId
			Integer bergId = Root.bergSystem.beLootBerg(defender, null);

			PvpBattle pvpBattle = new PvpBattle(attacker, defender, null, lootCash, lootGainCash, missionLoseTime, cashs, winCup, loseCup, expId, chipName, bergId,
					Clock.currentTimeSecond());

			//加入本方机器人,在战斗验证的时候加入的
			RobotBag attackerRobotBag = Root.robotSystem.getRobotBag(attacker);
			pvpBattle.setAttackRobots(attackerRobotBag.readFightRobots(true));

			//加入对手机器人
			RobotBag defenderRobotBag = Root.robotSystem.getRobotBag(defender);
			pvpBattle.setDefendRobots(defenderRobotBag.readFightRobots(false));

			//同步到缓存去
			pvpBattle.synchronize();

			result.setBindle(pvpBattle);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;

	}

	//pvp中换位置
	public void changeSlot(Player player, RobotBag robotBag) {
		PvpBattle pvpBattle = RedisHelperJson.getPvpBattle(player.getId());

		if (pvpBattle != null) {
			pvpBattle.setAttackRobots(robotBag.readFightRobots(true));
			pvpBattle.synchronize();
		}

	}

	//进入pvp战斗,计算可能结果
	public SystemResult enterPvpNpc(Player attacker, NpcPlayer npc) {

		SystemResult result = new SystemResult();

		//掠夺cash的比例
		int cashRate = 20 - ((attacker.getLevel() - npc.getLevel()) / 5 * 2);
		if (cashRate < 10) {
			cashRate = 10;
		} else {
			if (cashRate > 30) {
				cashRate = 30;
			}
		}
		//掠夺cash的数量
		int winCash = (int) (npc.getCash() * cashRate / 100);
		if (winCash <= 1) {
			winCash = 1;
		} else {
			if (winCash > 300000) {
				winCash = 300000;
			}
		}

		int winCash_1 = (int) (winCash * pvpLootCashRate[0]);
		int winCash_2 = (int) (winCash * pvpLootCashRate[1]);
		int[] cashs = { winCash_1, winCash_2, winCash - winCash_1 - winCash_2 };

		int winCup = Utils.randomInt(10, 20);
		int loseCup = Utils.randomInt(10, 20);
		if (attacker.getLevel() > 25) {//25级之后，抢的cup减少，丢的增多
			winCup = 1;
			loseCup = 20;
		}

		PvpBattle pvpBattle = new PvpBattle(attacker, null, npc, winCash, 0, null, cashs, winCup, loseCup, null, null, null, Clock.currentTimeSecond());

		//加入本方机器人,在战斗验证的时候加入的
		RobotBag attackerRobotBag = Root.robotSystem.getRobotBag(attacker);
		pvpBattle.setAttackRobots(attackerRobotBag.readFightRobots(true));

		//同步到缓存去
		pvpBattle.synchronize();

		result.setBindle(pvpBattle);

		return result;

	}

	//开打,战场生效
	public SystemResult doPvp(Player player) throws SQLException {
		SystemResult result = new SystemResult();

		PvpBattle pvpBattle = RedisHelperJson.getPvpBattle(player.getId());

		if (pvpBattle != null) {

			//第一次胜利,在这里扣耐久.因为进战斗的时候没扣耐久
			if (!pvpBattle.isConsumeWear()) {//这次战斗还没有扣耐久
				Root.robotSystem.consumeWearByRobots(player, null, pvpBattle.readAttackRobotArray(), Content.pvpUseWear);
				pvpBattle.setConsumeWear(true);
				pvpBattle.synchronize();
			}

			pvpBattle.setBeginTime(Clock.currentTimeSecond());
			pvpBattle.setValid(true);
			pvpBattle.synchronize();

			JobBag jobBag = Root.jobSystem.getJobBag(player);
			//MoreFight(2), 	越战越勇	进行n场比赛。
			Root.jobSystem.doJobMoreFight(player, jobBag, false);
			//JobPvp	12	竞技达人	进行三次pvp对战。
			Root.jobSystem.doJobPVP(player, jobBag, false);

			Root.jobSystem.updateJobBagToCacheAndDB(player, jobBag);
		}

		return result;
	}

	/**
	 * 出pvp战斗,结算
	 * @throws SQLException 
	 */
	public SystemResult exitPvp(Player attacker, PvpBattleResult pvpBattleResult) throws SQLException {

		SystemResult result = new SystemResult();

		PvpBattle pvpBattle = RedisHelperJson.getPvpBattle(attacker.getId());

		RedisHelperJson.removePvpBattle(attacker.getId());//删除缓存里的战场

		if (pvpBattle == null || pvpBattle.isValid() == false) {//没有战场  或者  战场无效  或者  战斗已经结算
			result.setCode(ErrorCode.NO_PVP_BATTLE);
			return result;
		} else {

			pvpBattleResult.setPvpBattle(pvpBattle);

			pvpBattleResult.setWinCup(pvpBattle.getWinCup());
			pvpBattleResult.setLoseCup(pvpBattle.getLoseCup());
			//开始结束时间
			pvpBattleResult.setBeginTime(pvpBattle.getBeginTime());
			pvpBattleResult.setEndTime(Clock.currentTimeSecond());

			if (!pvpBattleResult.isWin()) {//输了,算钱,能量块

				Float lootRate = 0.0F;
				switch (pvpBattleResult.getDefenderWinNum()) {
				case 0:
					lootRate = pvpLootCashRate[0] + pvpLootCashRate[1] + pvpLootCashRate[2];
					break;
				case 1:
					lootRate = pvpLootCashRate[0] + pvpLootCashRate[1];
					break;
				case 2:
					lootRate = pvpLootCashRate[0];
					break;
				default:
					break;
				}

				pvpBattleResult.setPvpLootRate(lootRate);
				pvpBattleResult.setLootCash(pvpBattleResult.getPvpBattle().getLootCash() + pvpBattleResult.getPvpBattle().getLootGainCash());
				pvpBattleResult.setDefenderMissionLoseSeconds(pvpBattleResult.getPvpBattle().getDefenderMissionLoseSeconds());

			} else {
				pvpBattleResult.setPvpLootRate(pvpLootCashRate[0] + pvpLootCashRate[1] + pvpLootCashRate[2]);
				pvpBattleResult.setLootCash(pvpBattleResult.getPvpBattle().getLootCash() + pvpBattleResult.getPvpBattle().getLootGainCash());
				pvpBattleResult.setDefenderMissionLoseSeconds(pvpBattleResult.getPvpBattle().getDefenderMissionLoseSeconds());
			}

			if (pvpBattleResult.getPvpBattle().getDefenderId() != null) {//真人玩家

				Player defender = Root.playerSystem.getPlayer(pvpBattleResult.getPvpBattle().getDefenderId());

				return exitPvpOpponent(attacker, defender, pvpBattleResult);
			} else {
				return exitPvpNpc(attacker, pvpBattleResult);
			}
		}

	}

	/**
	 * 处理非正常退出的战斗
	 * @throws SQLException 
	 */
	public SystemResult autoExitPvp(Player attacker) throws SQLException {

		//以输的惨作为结果
		PvpBattleResult pvpBattleResult = new PvpBattleResult(false, false, 0, 3);

		return exitPvp(attacker, pvpBattleResult);

	}

	/**
	 * 出npc_pvp战斗,结算
	 */
	private SystemResult exitPvpNpc(Player attacker, PvpBattleResult pvpBattleResult) {

		SystemResult result = new SystemResult();

		//结算  钱和cup
		handlePvpCashCup(attacker, null, pvpBattleResult);

		NpcPlayer npcPlayer = pvpBattleResult.getPvpBattle().getPvpNpc();
		npcPlayer.synchronize(5 * 24);

		// 发消息,这个消息作用:1攻击者加入战斗记录,2攻击者如果有护盾,那就去掉
		this.publish(new PvpMessage(PvpMessage.PVP_EXIT_NPC, attacker, null, pvpBattleResult));

		//第一次胜利,在这里扣耐久.因为进战斗的时候没扣耐久
		if (!pvpBattleResult.getPvpBattle().isConsumeWear()) {//这次战斗还没有扣耐久
			Root.robotSystem.consumeWearByRobots(attacker, null, pvpBattleResult.getPvpBattle().readAttackRobotArray(), Content.pvpUseWear);
		}

		result.setBindle(pvpBattleResult);

		return result;
	}

	/**
	 * 出pvp真人战斗,结算
	 * @throws SQLException 
	 */
	private SystemResult exitPvpOpponent(Player attacker, Player defender, PvpBattleResult pvpBattleResult) throws SQLException {

		SystemResult result = new SystemResult();

		//结算  钱和cup
		handlePvpCashCup(attacker, defender, pvpBattleResult);
		//结算经验块
		pvpBattleResult.setLootExpId(handlePvpExpPart(attacker, defender, pvpBattleResult));
		//结算chip
		pvpBattleResult.setLootChipName(handlePvpChip(attacker, defender, pvpBattleResult));
		//结算berg
		pvpBattleResult.setLootBergId(handlePvpBerg(attacker, defender, pvpBattleResult));

		// 发消息,这个消息作用:1被打玩家加护盾,2加入战斗记录,3攻击者如果有护盾,那就去掉
		this.publish(new PvpMessage(PvpMessage.PVP_EXIT, attacker, defender, pvpBattleResult));

		result.setBindle(pvpBattleResult);

		return result;
	}

	/**
	 * 结算 钱 和 cup
	 * @param attacker
	 * @param defender
	 * @param pvpBattle
	 * @param pvpBattleResult
	 */
	private void handlePvpCashCup(Player attacker, Player defender, PvpBattleResult pvpBattleResult) {
		Root.playerSystem.changeCash(attacker, pvpBattleResult.attackerWinLootCash(), CashType.PVP_GET, false);
		if (pvpBattleResult.isWin()) {
			Root.playerSystem.updateStatistics(attacker, true, false, pvpBattleResult.getPvpBattle().getWinCup(), pvpBattleResult.getAttackerWinNum(), false);
		} else {
			Root.playerSystem.updateStatistics(attacker, true, false, -pvpBattleResult.getPvpBattle().getLoseCup(), pvpBattleResult.getAttackerWinNum(), false);
		}

		attacker.synchronize();

		if (defender != null && !defender.checkProtect() && !defender.checkOnLine()) {
			//-cash from player
			Root.playerSystem.changeCash(defender, -pvpBattleResult.defenderLoseCash(), CashType.PVP_LOSE, false);
			//-time from mission
			Root.missionSystem.loseGainTime(defender, null, pvpBattleResult.getDefenderMissionLoseSeconds());
			if (pvpBattleResult.isWin()) {
				Root.playerSystem.updateStatistics(defender, false, true, -pvpBattleResult.getPvpBattle().getWinCup(), pvpBattleResult.getDefenderWinNum(), false);
			} else {
				Root.playerSystem.updateStatistics(defender, false, true, pvpBattleResult.getPvpBattle().getLoseCup(), pvpBattleResult.getDefenderWinNum(), false);
			}
			defender.synchronize();
		}
	}

	/**
	 * 抢对方的能量块,胜利2场以上
	 * @throws SQLException 
	 */
	private Integer handlePvpExpPart(Player attacker, Player defender, PvpBattleResult pvpBattleResult) throws SQLException {

		//防守方机器人阵亡两个,才有这个东西
		if (pvpBattleResult.getAttackerWinNum() >= 2) {
			if (defender != null && pvpBattleResult.getPvpBattle() != null && pvpBattleResult.getPvpBattle().getLootExpPartId() != null) {
				//真实的防守方,损失expPart
				Root.expPartSystem.dropExpPart(defender, null, pvpBattleResult.getPvpBattle().getLootExpPartId());
				//attacker,得到expPart
				Root.expPartSystem.addExpPart(attacker, pvpBattleResult.getPvpBattle().getLootExpPartId(), 1);

				return pvpBattleResult.getPvpBattle().getLootExpPartId();
			}
		}
		return null;
	}

	private String handlePvpChip(Player attacker, Player defender, PvpBattleResult pvpBattleResult) {

		if (pvpBattleResult.getAttackerWinNum() == 3) {
			if (defender != null && pvpBattleResult.getPvpBattle() != null && pvpBattleResult.getPvpBattle().getLootChipName() != null) {
				try {
					//真实的防守方,损失chip
					Root.chipSystem.removeChip(defender, null, pvpBattleResult.getPvpBattle().getLootChipName(), 1);
					//attacker,得到
					Root.chipSystem.addChipNum(attacker, null, pvpBattleResult.getPvpBattle().getLootChipName(), 1);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return pvpBattleResult.getPvpBattle().getLootChipName();
			}
		}
		return null;
	}

	private Integer handlePvpBerg(Player attacker, Player defender, PvpBattleResult pvpBattleResult) {

		if (pvpBattleResult.getAttackerWinNum() == 3) {
			if (defender != null && pvpBattleResult.getPvpBattle() != null && pvpBattleResult.getPvpBattle().getLootBergId() != null) {
				//真实的防守方,损失berg
				try {
					
					Root.bergSystem.removeBerg(defender, null, pvpBattleResult.getPvpBattle().getLootBergId(), 1);
					//attacker,得到berg
					Root.bergSystem.addBergNum(attacker, null, pvpBattleResult.getPvpBattle().getLootBergId(), 1);

					return pvpBattleResult.getPvpBattle().getLootBergId();

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		}
		return null;
	}

	//中途验证属性值
	public SystemResult check(Player player, BattleCheckBean battleCheckBean) {
		SystemResult result = new SystemResult();

		PvpBattle pvpBattle = RedisHelperJson.getPvpBattle(player.getId());

		//验证攻击者的机器人
		ArrayList<CheckRobot> attackerRobots = battleCheckBean.getAttackerRobots();

		for (CheckRobot checkRobot : attackerRobots) {

			if (checkRobot.getPlayerId() == player.getId()) {//自己的

				Robot robot = pvpBattle.getAttackRobots().get(checkRobot.getSlot());

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

		//验证对手的机器人	

		if (pvpBattle.getPvpNpc() != null) {//是个npc
			NpcPlayer npcPlayer = pvpBattle.getPvpNpc();
			ArrayList<CheckRobot> defenderRobots = battleCheckBean.getDefenderRobots();

			for (CheckRobot checkRobot : defenderRobots) {

				NpcRobot npcRobot = npcPlayer.getAttackRobots().get(checkRobot.getSlot());

				FightProperty fightProperty = npcRobot.refreshFightProperty();

				FightProperty addFightProperty = npcRobot.additionFightProperty(player.getLevel(), fightProperty);

				if (fightProperty.getAtk() + addFightProperty.getAtk() != checkRobot.getAtk() || fightProperty.getDef() + addFightProperty.getDef() != checkRobot.getDef()
						|| fightProperty.getHp() + addFightProperty.getHp() != checkRobot.getHp() || fightProperty.getCrit() + addFightProperty.getCrit() != checkRobot.getCrit()) {
					//不一致,可能作弊
					result.setCode(ErrorCode.CHECK_BATTLE_DEFENDER_ERROR);

					logger.error("defender_client     slot:[" + checkRobot.getSlot() + "] atk:[" + checkRobot.getAtk() + "] def:[" + checkRobot.getDef() + "] hp:["
							+ checkRobot.getHp() + "] crit:[" + checkRobot.getCrit() + "]");
					logger.error("defender_server     slot:[" + checkRobot.getSlot() + "] atk:[" + fightProperty.getAtk() + addFightProperty.getAtk() + "] def:["
							+ fightProperty.getDef() + addFightProperty.getDef() + "] hp:[" + fightProperty.getHp() + addFightProperty.getHp() + "] crit:["
							+ fightProperty.getCrit() + addFightProperty.getCrit() + "]");

					break;
				}

			}

		} else {//玩家对手

			ArrayList<CheckRobot> defenderRobots = battleCheckBean.getDefenderRobots();

			for (CheckRobot checkRobot : defenderRobots) {

				Robot robot = pvpBattle.getDefendRobots().get(checkRobot.getSlot());

				if (robot != null) {
					FightProperty fightProperty = robot.refreshFightProperty();
					FightProperty addFightProperty = robot.additionFightProperty(player.getLevel(), fightProperty);

					if (fightProperty.getAtk() + addFightProperty.getAtk() != checkRobot.getAtk() || fightProperty.getDef() + addFightProperty.getDef() != checkRobot.getDef()
							|| fightProperty.getHp() + addFightProperty.getHp() != checkRobot.getHp()
							|| fightProperty.getCrit() + addFightProperty.getCrit() != checkRobot.getCrit()) {
						//不一致,可能作弊
						result.setCode(ErrorCode.CHECK_BATTLE_DEFENDER_ERROR);

						logger.error("defender_client     slot:[" + checkRobot.getSlot() + "] atk:[" + checkRobot.getAtk() + "] def:[" + checkRobot.getDef() + "] hp:["
								+ checkRobot.getHp() + "] crit:[" + checkRobot.getCrit() + "]");
						logger.error("defender_server     slot:[" + checkRobot.getSlot() + "] atk:[" + fightProperty.getAtk() + addFightProperty.getAtk() + "] def:["
								+ fightProperty.getDef() + addFightProperty.getDef() + "] hp:[" + fightProperty.getHp() + addFightProperty.getHp() + "] crit:["
								+ fightProperty.getCrit() + addFightProperty.getCrit() + "]");

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

		PvpBattle pvpBattle = RedisHelperJson.getPvpBattle(player.getId());

		if (pvpBattle != null) {

			if (pvpBattle.getPvpNpc() != null) {//打的是npc

				NpcPlayer pvpNpc = pvpBattle.getPvpNpc();

				if (pvpNpc != null) {
					for (DamageBean bean : damageCheckBean.getDamageBeans()) {
						//伤害值小于(攻击-防御 )*maxDamage即为合法
						Robot attackRobot = pvpBattle.getAttackRobots().get(bean.getAttackRobotId());

						FightProperty fightProperty = attackRobot.refreshFightProperty();

						//验证自己的战斗属性
						if (fightProperty.getAtk() != bean.getAtk() || fightProperty.getDef() != bean.getDef() || fightProperty.getHp() != bean.getHp()
								|| fightProperty.getCrit() != bean.getCrit()) {

							//不匹配
							result.setCode(ErrorCode.CHECK_BATTLE_ATTACK_ERROR);

							logger.error("attack client         :  id:[" + bean.getAttackRobotId() + "] atk:[" + bean.getAtk() + "] def:[" + bean.getDef() + "] hp:["
									+ bean.getHp() + "] crit:[" + bean.getCrit() + "]");
							logger.error("attack server         :  id:[" + attackRobot.getId() + "] atk:[" + fightProperty.getAtk() + "] def:[" + fightProperty.getDef() + "] hp:["
									+ fightProperty.getHp() + "] crit:[" + fightProperty.getCrit() + "]");

							break;
						}

						NpcRobot defendRobot = NpcLoadData.getInstance().getIdNpcRobots().get(bean.getDefendRobotId());

						FightProperty deFightProperty = defendRobot.refreshFightProperty();

						if ((fightProperty.getAtk() - deFightProperty.getDef()) * Content.maxDamage < bean.getDamage()) {
							//非法伤害
							result.setCode(ErrorCode.CHECK_BATTLE_DAMAGE_ERROR);

							logger.error("damage:[" + bean.getDamage() + "] attRobotId:[" + bean.getAttackRobotId() + "] defRobotId:[" + bean.getDefendRobotId()
									+ "] atk def hp crit:[" + bean.getAtk() + " " + bean.getDef() + " " + bean.getHp() + " " + bean.getCrit() + "]");
							logger.error("damage:[" + (fightProperty.getAtk() - deFightProperty.getDef()) + "] damage:[" + bean.getDamage() + "]");
							break;
						}
					}
				}

			} else {//打的是真实玩家

				for (DamageBean bean : damageCheckBean.getDamageBeans()) {

					Robot attackRobot = pvpBattle.getAttackRobots().get(bean.getAttackRobotId());

					FightProperty fightProperty = attackRobot.refreshFightProperty();

					//验证自己的战斗属性
					if (fightProperty.getAtk() != bean.getAtk() || fightProperty.getDef() != bean.getDef() || fightProperty.getHp() != bean.getHp()
							|| fightProperty.getCrit() != bean.getCrit()) {
						//不匹配
						result.setCode(ErrorCode.CHECK_BATTLE_DEFENDER_ERROR);

						if (logger.isDebugEnabled()) {
							logger.debug("attack client         :  id:[" + bean.getAttackRobotId() + "] atk:[" + bean.getAtk() + "] def:[" + bean.getDef() + "] hp:["
									+ bean.getHp() + "] crit:[" + bean.getCrit() + "]");
							logger.debug("attack server         :  id:[" + attackRobot.getId() + "] atk:[" + fightProperty.getAtk() + "] def:[" + fightProperty.getDef() + "] hp:["
									+ fightProperty.getHp() + "] crit:[" + fightProperty.getCrit() + "]");
						}

						break;
					}

					//伤害值小于(攻击-防御 )*maxDamage即为合法
					if (attackRobot != null) {//确定有这个robot.如果是租赁的,那这里为null
						Robot defendRobot = pvpBattle.getDefendRobots().get(bean.getDefendRobotId());

						FightProperty deFightProperty = defendRobot.refreshFightProperty();

						if ((fightProperty.getAtk() - deFightProperty.getDef()) * Content.maxDamage < bean.getDamage()) {
							//非法伤害
							result.setCode(ErrorCode.CHECK_BATTLE_DAMAGE_ERROR);

							if (logger.isDebugEnabled()) {
								logger.info("damage:[" + bean.getDamage() + "] attRobotId:[" + bean.getAttackRobotId() + "] defRobotId:[" + bean.getDefendRobotId()
										+ "] atk def hp crit:[" + bean.getAtk() + " " + bean.getDef() + " " + bean.getHp() + " " + bean.getCrit() + "]");
								logger.info("damage:[" + (fightProperty.getAtk() - deFightProperty.getDef()) + "] damage:[" + bean.getDamage() + "]");
							}
							break;
						}
					}
				}

			}

		}

		return result;
	}

	/**
	 * 复仇
	 */
	public void revengePvp(Player player, Long revengeRecordId) {

		PvpBattle pvpBattle = RedisHelperJson.getPvpBattle(player.getId());
		if (pvpBattle != null) {
			pvpBattle.setType(1);
			pvpBattle.synchronize();
		}

		Root.recordSystem.revengeDefenceRecord(player, revengeRecordId);

	}

	/**
	 * 杀人
	 */
	public SystemResult beatRobotInPvp(Player player, Long defenderId, int deadRobotSlot) {
		SystemResult result = new SystemResult();

		PvpBattle pvpBattle = RedisHelperJson.getPvpBattle(player.getId());

		if (pvpBattle != null) {
			if (pvpBattle.getPvpNpc() != null) {//npc
				NpcPlayer npcPlayer = Root.npcSystem.getNpcPlayer(defenderId);
				if (npcPlayer != null) {
					NpcRobot npcRobot = npcPlayer.getAttackRobots().get(deadRobotSlot);
					if (npcRobot != null) {
						BeatRobotMessage message = new BeatRobotMessage(BeatRobotMessage.PVP_BEAT_ROBOT_NPC, player, npcRobot);
						this.publish(message);
					}
				}
			}
			if (pvpBattle.getDefenderId() != null) {
				//防御方的机器人包
				Robot beatedRobot = pvpBattle.getDefendRobots().get(deadRobotSlot);
				if (beatedRobot != null) {
					beatedRobot.refreshFightProperty();
					BeatRobotMessage message = new BeatRobotMessage(BeatRobotMessage.PVP_BEAT_ROBOT, player, beatedRobot);
					this.publish(message);
				}
			}
		}

		return result;
	}

}
