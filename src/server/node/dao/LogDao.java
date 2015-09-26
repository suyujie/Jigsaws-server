package server.node.dao;

import gamecore.db.AsyncDBBacthTask;
import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.task.TaskCenter;
import gamecore.util.Clock;
import gamecore.util.Utils;

import java.util.ArrayList;
import java.util.List;

import server.node.system.dailyJob.JobCurrentStatus;
import server.node.system.egg.EggPartBag;
import server.node.system.gameEvents.bergWheel.BergWheel;
import server.node.system.gameEvents.chipDeathWheel.DeathWheel;
import server.node.system.gameEvents.chipDeathWheel.DeathWheelBoss;
import server.node.system.log.CashLog;
import server.node.system.log.GoldLog;
import server.node.system.log.LevelTimeLog;
import server.node.system.log.PveLog;
import server.node.system.log.PvpLog;
import server.node.system.log.SignLog;
import server.node.system.log.TreasureIslandLog;
import server.node.system.monthCard.MonthCard;
import server.node.system.player.Player;

public class LogDao {

	public LogDao() {
		super();
	}

	/**
	 * 添加登陆日志
	 */
	public void addSignLog(Player player, SignLog log) {
		String sql = "INSERT INTO t_sign_log(id,player_id,before_level,before_cash,before_gold,sign_in_t) VALUES (?,?,?,?,?,?);";
		Object[] args = { log.getId(), log.getPlayerId(), log.getBeforeLevel(), log.getBeforeCash(), log.getBeforeGold(), log.getCt() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	/**
	 * 更新 登陆日志
	 */
	public void updateSignLog(Player player, SignLog log) {
		String sql = "UPDATE t_sign_log SET after_level = ?, after_cash = ?, after_gold = ?, max_point_id = ?,sign_out_t = ? WHERE id = ?";
		Object[] args = { log.getAfterLevel(), log.getAfterCash(), log.getAfterGold(), log.getMaxPointId(), log.getSt(), log.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	/**
	 * 添加CashLog
	 */
	public void addCashLog(Player player, CashLog cashLog) {
		String sql = "INSERT INTO t_cash_log(id,player_id,before_cash,change_cash, after_cash,change_type,t) VALUES (?,?,?,?,?,?,?);";
		Object[] args = { cashLog.getId(), cashLog.getPlayerId(), cashLog.getBeforeCash(), cashLog.getChangeCash(), cashLog.getAfterCash(), cashLog.getChangeTyte(),
				cashLog.getCt() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	/**
	 * 添加GoldLog
	 */
	public void addGoldLog(Player player, GoldLog goldLog) {
		String sql = "INSERT INTO t_gold_log(id,player_id,before_gold,change_gold, after_gold,change_type,t) VALUES (?,?,?,?,?,?,?)";
		Object[] args = { goldLog.getId(), goldLog.getPlayerId(), goldLog.getBeforeGold(), goldLog.getChangeGold(), goldLog.getAfterGold(), goldLog.getChangeTyte(),
				goldLog.getCt() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	/**
	 * 添加level time
	 */
	public void addLevelTimeLog(Player player, LevelTimeLog log) {
		String sql = "INSERT INTO t_level_time_log(id,player_id,LEVEL,all_time,t) VALUES (?,?,?,?,?);";
		Object[] args = { log.getId(), log.getPlayerId(), log.getLevel(), log.getAllTime(), log.getSt() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	/**
	 * 添加pve log
	 */
	public void addPveLog(Player player, PveLog log) {
		String sql = "INSERT INTO t_pve_log(id, player_id,point_id,point_star, win, get_cash,first_pass,enter_time,exit_time,weapon,t) VALUES (?,?,?,?,?,?,?,?,?,?,?);";
		Object[] args = { log.getId(), log.getPlayerId(), log.getPointId(), log.getPointStar(), log.getWin(), log.getCash(), log.isFirstPass() ? 1 : 0, log.getEnterTime(),
				log.getExitTime(), log.getWeaponStr(), log.getSt() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	/**
	 * 添加pvp log
	 */
	public void addPvpLog(Player player, PvpLog log) {
		String sql = "INSERT INTO t_pvp_log(id, attacker_id,attacker_level,defender_id,defender_level, attacker_win,win_cash,attacker_weapon,defender_weapon,begin_time,end_time,t) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
		Object[] args = { log.getId(), log.getAttackerId(), log.getAttackerLevel(), log.getDefenderId(), log.getDefenderLevel(), log.getAttackerWin(), log.getWinCash(),
				log.getAttackerWeapon(), log.getDefenderWeapon(), log.getBeginTime(), log.getEndTime(), log.getSt() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	/**
	 * dailyJob log
	 */
	public void addDailyJobLog(Player player, List<JobCurrentStatus> statuses, long ct) {

		List<String> sqls = new ArrayList<>();
		List<Object[]> params = new ArrayList<>();

		for (JobCurrentStatus s : statuses) {
			if (s != null) {
				sqls.add("INSERT INTO t_daily_job_log(id, player_id,job_id,completed,get_job_t,t) VALUES (?,?,?,?,?,?);");
				Object[] param = { Utils.getOneLongId(), player.getId(), s.getId(), s.getState() == 0 ? 0 : 1, ct, Clock.currentTimeSecond() };
				params.add(param);
			}
		}

		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBBacthTask(DBOperator.Log, player.getId(), sqls, params));

	}

	/**
	 * 添加egg log
	 */
	public void addEggLog(Player player, EggPartBag eggPartBag) {
		String sql = "INSERT INTO t_egg_log(id, player_id,all_num,open_num,get_egg_t,t) VALUES (?,?,?,?,?,?);";
		Object[] args = { eggPartBag.getLogId(), player.getId(), eggPartBag.getEggPartNum(), 0, Clock.currentTimeSecond(), Clock.currentTimeSecond() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	/**
	 * 更新egg log
	 */
	public void updateEggLog(Player player, EggPartBag eggPartBag) {
		String sql = "UPDATE t_egg_log set open_num = all_num - ?, t = ? WHERE id = ?;";
		Object[] args = { eggPartBag.getEggPartNum(), Clock.currentTimeSecond(), eggPartBag.getLogId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	/**
	 * 月卡日志
	 */
	public void addMonthCardLog(Player player, MonthCard monthCard) {
		String sql = "INSERT INTO t_month_card_log (player_id, buy_t,reward_num, t) VALUES (?,?,?,?)";
		Object[] args = { player.getId(), monthCard.getBuyT(), monthCard.getRewardNum(), Clock.currentTimeSecond() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	/**
	 * 月卡日志
	 */
	public void updateMonthCardLog(Player player, MonthCard monthCard) {
		String sql = "UPDATE t_month_card_log SET reward_num = ?,t = ? WHERE player_id = ? and buy_t = ?";
		Object[] args = { monthCard.getRewardNum(), Clock.currentTimeSecond(), player.getId(), monthCard.getBuyT() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	/**
	 * 金银岛
	 */
	public void addTreasureIslandLog(Player player, TreasureIslandLog log) {
		String sql = "INSERT INTO t_treasure_island_log (id,player_id, cash,exp_id, exp_num, kill_num,reset_index,t) VALUES (?,?,?,?,?,?,?,?)";
		Object[] args = { log.getTreasureIsland().getId(), player.getId(), log.getCash(), log.getExpId(), log.getExpNum(), log.getKillNum(), log.getResetIndex(), log.getSt() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	/**
	 * 车轮战
	 */
	public void addBergWheelLog(Player player, BergWheel bergWheel) {
		String sql = "INSERT INTO t_berg_wheel_log (id,player_id, battle_id,hard_level,reset_index,relive_num,berg_id,berg_num,t) VALUES (?,?,?,?,?,?,?,?,?)";
		Object[] args = { bergWheel.getId(), player.getId(), bergWheel.getBattleId(), bergWheel.getBattle().getBossLevel(), bergWheel.getResetNum(), bergWheel.getReliveNum(),
				bergWheel.getBattle().getBergId(), bergWheel.getBattle().getBergNum(), Clock.currentTimeSecond() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	public void updateBergWheelLog(Player player, BergWheel bergWheel) {
		String sql = "UPDATE t_berg_wheel_log SET  berg_id = ?, berg_num = ? WHERE id=? AND player_id = ? AND reset_index = ? AND battle_id = ? ";
		Object[] args = { bergWheel.getBattle().getBergId(), bergWheel.getBattle().getBergNum(), bergWheel.getId(), player.getId(), bergWheel.getResetNum(),
				bergWheel.getBattleId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	/**
	 * 死亡轮盘
	 */
	public void addDeathWheelLog(Player player, DeathWheel deathWheel, DeathWheelBoss boss) {
		String sql = "INSERT INTO t_death_wheel_log (id,player_id, hard_level,reset_index,do_num,t) VALUES (?,?,?,?,?,?)";
		Object[] args = { deathWheel.getId(), player.getId(), deathWheel.getBattleHardLevel(), deathWheel.getResetNum(), boss.getDoNum(), Clock.currentTimeSecond() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	public void updateDeathWheelLog(Player player, DeathWheel deathWheel, DeathWheelBoss boss, Integer chipNum, Boolean isWin) {
		if (isWin != null && isWin) {
			String sql = "UPDATE t_death_wheel_log SET do_num = ?, chip_name = ?, chip_num = ? WHERE id=? AND  player_id = ? AND reset_index = ? AND hard_level = ? ";
			Object[] args = { boss.getDoNum(), boss.getChipName(), chipNum, deathWheel.getId(), player.getId(), deathWheel.getResetNum(), deathWheel.getBattleHardLevel() };
			TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
		} else {
			String sql = "UPDATE t_death_wheel_log SET do_num = ? WHERE id=? AND  player_id = ? AND reset_index = ? AND hard_level = ? ";
			Object[] args = { boss.getDoNum(), deathWheel.getId(), player.getId(), deathWheel.getResetNum(), deathWheel.getBattleHardLevel() };
			TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
		}
	}
}
