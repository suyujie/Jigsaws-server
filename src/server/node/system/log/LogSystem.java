package server.node.system.log;

import gamecore.system.AbstractSystem;
import gamecore.util.Clock;
import gamecore.util.Utils;

import java.util.List;

import server.node.dao.DaoFactory;
import server.node.dao.LogDao;
import server.node.system.Root;
import server.node.system.battle.PveBattle;
import server.node.system.battle.PveBattleResult;
import server.node.system.battle.PvpBattleResult;
import server.node.system.dailyJob.JobBag;
import server.node.system.dailyJob.JobCurrentStatus;
import server.node.system.egg.EggPartBag;
import server.node.system.gameEvents.bergWheel.BergWheel;
import server.node.system.gameEvents.chipDeathWheel.DeathWheel;
import server.node.system.gameEvents.treasureIsland.TreasureIsland;
import server.node.system.monthCard.MonthCard;
import server.node.system.player.Player;

import common.coin.CoinType;

public class LogSystem extends AbstractSystem {

	public LogSystem() {
		super();
	}

	@Override
	public boolean startup() {
		System.out.println("LogSystem start....");

		System.out.println("LogSystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {
	}

	private long getLogId() {
		return Utils.getOneLongId();
	}

	public void addSignLog(Player player, boolean sync) {

		Long id = getLogId();
		SignLog signLog = new SignLog(id, player.getId(), player.getLevel(), player.getGold(), player.getCash());

		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.addSignLog(player, signLog);
		DaoFactory.getInstance().returnLogDao(logDao);

		player.setSignLogId(id);
		player.setLastSignT(Clock.currentTimeSecond());

		if (sync) {
			player.synchronize();
		}

	}

	public void updateSignLog(Player player, boolean sync) {

		int maxPointId = Root.missionSystem.getMissionBag(player).readMaxPoint();

		SignLog signLog = new SignLog(player.getSignLogId(), player.getLevel(), player.getGold(), player.getCash(), maxPointId);

		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.updateSignLog(player, signLog);
		DaoFactory.getInstance().returnLogDao(logDao);

		player.setSignLogId(null);
		player.setLastSignT(null);

		if (sync) {
			player.synchronize();
		}
	}

	public void addCashLog(Player player, long beforeCash, long changeCash, long afterCash, int changeTyte) {

		CashLog cashLog = new CashLog(getLogId(), player.getId(), beforeCash, changeCash, afterCash, changeTyte);

		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.addCashLog(player, cashLog);
		DaoFactory.getInstance().returnLogDao(logDao);
	}

	public void addGoldLog(Player player, long beforeGold, long changeGold, long afterGold, int changeTyte) {

		GoldLog goldLog = new GoldLog(getLogId(), player.getId(), beforeGold, changeGold, afterGold, changeTyte);

		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.addGoldLog(player, goldLog);
		DaoFactory.getInstance().returnLogDao(logDao);
	}

	public void addLevelTime(Player player) {

		LevelTimeLog levelTimeLog = new LevelTimeLog(getLogId(), player.getId(), player.getLevel(), player.getOnLineTime() + Clock.currentTimeSecond() - player.getLastSignT());

		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.addLevelTimeLog(player, levelTimeLog);
		DaoFactory.getInstance().returnLogDao(logDao);
	}

	public void addPveLog(Player player, PveBattle pveBattle, PveBattleResult pveBattleResult) {

		String weaponStr = Root.robotSystem.getWeaponIds(pveBattle.readAttackRobotArrayAll());

		PveLog pveLog = new PveLog(getLogId(), player.getId(), pveBattle.getPointPO().getMakingId(), pveBattle.getPointPO().getStar(), pveBattleResult.isWin() ? 1 : 0,
				pveBattleResult.getCash(), pveBattleResult.isFirstPass(), pveBattleResult.getEnterTime(), pveBattleResult.getExitTime(), weaponStr);

		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.addPveLog(player, pveLog);
		DaoFactory.getInstance().returnLogDao(logDao);
	}

	public void addPvpLog(Player attacker, Player defender, PvpBattleResult pvpBattleResult) {

		String attackerWeaponStr = Root.robotSystem.getWeaponIds(pvpBattleResult.getPvpBattle().getAttackRobots().values());
		String defenderWeaponStr = Root.robotSystem.getWeaponIds(defender);

		PvpLog pvpLog = new PvpLog(getLogId(), attacker.getId(), defender.getId(), attacker.getLevel(), defender.getLevel(), pvpBattleResult.isWin() ? 1 : 0,
				pvpBattleResult.attackerWinLootCash(), attackerWeaponStr, defenderWeaponStr, pvpBattleResult.getBeginTime(), pvpBattleResult.getEndTime());

		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.addPvpLog(attacker, pvpLog);
		DaoFactory.getInstance().returnLogDao(logDao);
	}

	public void addPvpNpcLog(Player attacker, PvpBattleResult pvpBattleResult) {

		String attackerWeaponStr = Root.robotSystem.getWeaponIds(pvpBattleResult.getPvpBattle().getAttackRobots().values());
		String defenderWeaponStr = Root.npcSystem.getWeaponIds(pvpBattleResult.getPvpBattle().getPvpNpc().getId());

		PvpLog pvpLog = new PvpLog(getLogId(), attacker.getId(), pvpBattleResult.getPvpBattle().getPvpNpc().getId(), attacker.getLevel(), pvpBattleResult.getPvpBattle()
				.getPvpNpc().getLevel(), pvpBattleResult.isWin() ? 1 : 0, pvpBattleResult.attackerWinLootCash(), attackerWeaponStr, defenderWeaponStr,
				pvpBattleResult.getBeginTime(), pvpBattleResult.getEndTime());

		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.addPvpLog(attacker, pvpLog);
		DaoFactory.getInstance().returnLogDao(logDao);
	}

	public void addDailyJobLog(Player player, JobBag jobBag) {

		List<JobCurrentStatus> currentStatuses = Root.jobSystem.getJobBagStatus(jobBag, null);

		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.addDailyJobLog(player, currentStatuses, jobBag.getJobBagPO().getCreateTime());
		DaoFactory.getInstance().returnLogDao(logDao);

	}

	public void addEggLog(Player player, EggPartBag eggPartBag) {
		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.addEggLog(player, eggPartBag);
		DaoFactory.getInstance().returnLogDao(logDao);
	}

	public void updateEggLog(Player player, EggPartBag eggPartBag) {
		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.updateEggLog(player, eggPartBag);
		DaoFactory.getInstance().returnLogDao(logDao);
	}

	public void addMonthCardLog(Player player, MonthCard monthCard) {
		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.addMonthCardLog(player, monthCard);
		DaoFactory.getInstance().returnLogDao(logDao);
	}

	public void updateMonthCardLog(Player player, MonthCard monthCard) {
		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.updateMonthCardLog(player, monthCard);
		DaoFactory.getInstance().returnLogDao(logDao);
	}

	public void addTreasureIslandCashLog(Player player, TreasureIsland treasureIsland, int cash) {

		TreasureIslandLog log = new TreasureIslandLog(treasureIsland, treasureIsland.getType(), cash, null, 0, 0, treasureIsland.getResetNum());

		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.addTreasureIslandLog(player, log);
		DaoFactory.getInstance().returnLogDao(logDao);
	}

	public void addTreasureIslandExpLog(Player player, TreasureIsland treasureIsland, Integer expId, Integer expNum, Integer killNum) {

		TreasureIslandLog log = new TreasureIslandLog(treasureIsland, treasureIsland.getType(), 0, expId, expNum, killNum, treasureIsland.getResetNum());

		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.addTreasureIslandLog(player, log);
		DaoFactory.getInstance().returnLogDao(logDao);
	}

	public void addBergWheelLog(Player player, BergWheel bergWheel) {
		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.addBergWheelLog(player, bergWheel);
		DaoFactory.getInstance().returnLogDao(logDao);
	}

	public void updateBergWheelLog(Player player, BergWheel bergWheel) {
		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.updateBergWheelLog(player, bergWheel);
		DaoFactory.getInstance().returnLogDao(logDao);
	}

	public void addDeathWheelLog(Player player, DeathWheel deathWheel) {
		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.addDeathWheelLog(player, deathWheel, deathWheel.readCurrentBoss());
		DaoFactory.getInstance().returnLogDao(logDao);
	}

	public void updateDeathWheelLog(Player player, DeathWheel deathWheel, Integer chipNum, Boolean isWin) {
		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.updateDeathWheelLog(player, deathWheel, deathWheel.readCurrentBoss(), chipNum, isWin);
		DaoFactory.getInstance().returnLogDao(logDao);
	}
}
