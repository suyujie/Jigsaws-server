package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.task.TaskCenter;
import server.node.system.log.CashLog;
import server.node.system.log.GoldLog;
import server.node.system.log.SignLog;
import server.node.system.player.Player;

public class LogDao {

	public LogDao() {
		super();
	}

	public void addSignLog(Player player, SignLog log) {
		String sql = "INSERT INTO t_sign_log(id,player_id,before_level,before_cash,before_gold,sign_in_t) VALUES (?,?,?,?,?,?);";
		Object[] args = { log.getId(), log.getPlayerId(), log.getBeforeLevel(), log.getBeforeCash(),
				log.getBeforeGold(), log.getCt() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	public void updateSignLog(Player player, SignLog log) {
		String sql = "UPDATE t_sign_log SET after_level = ?, after_cash = ?, after_gold = ?, max_point_id = ?,sign_out_t = ? WHERE id = ?";
		Object[] args = { log.getAfterLevel(), log.getAfterCash(), log.getAfterGold(), log.getMaxPointId(), log.getSt(),
				log.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	public void addCashLog(Player player, CashLog cashLog) {
		String sql = "INSERT INTO t_cash_log(id,player_id,before_cash,change_cash, after_cash,change_type,t) VALUES (?,?,?,?,?,?,?);";
		Object[] args = { cashLog.getId(), cashLog.getPlayerId(), cashLog.getBeforeCash(), cashLog.getChangeCash(),
				cashLog.getAfterCash(), cashLog.getChangeTyte(), cashLog.getCt() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

	public void addGoldLog(Player player, GoldLog goldLog) {
		String sql = "INSERT INTO t_gold_log(id,player_id,before_gold,change_gold, after_gold,change_type,t) VALUES (?,?,?,?,?,?,?)";
		Object[] args = { goldLog.getId(), goldLog.getPlayerId(), goldLog.getBeforeGold(), goldLog.getChangeGold(),
				goldLog.getAfterGold(), goldLog.getChangeTyte(), goldLog.getCt() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

}
