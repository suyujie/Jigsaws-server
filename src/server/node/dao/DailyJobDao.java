package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.sql.SQLException;
import java.util.Map;

import server.node.system.dailyJob.JobBag;
import server.node.system.player.Player;

public class DailyJobDao {

	public Map<String, Object> readDailyJob(Player player) throws SQLException {
		String sql = "select * from t_daily_job where id = ? limit 0,1";
		Object[] args = { player.getId() };
		return SyncDBUtil.readMap(DBOperator.Read, player.getId(), sql, args, false);
	}

	public void saveDailyJob(Player player, JobBag jobBag) {
		String sql = "INSERT INTO t_daily_job(id, jobs) values (?,?)";
		Object[] args = { player.getId(), jobBag.toJson() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void updateDailyJob(Player player, JobBag jobBag) {
		String sql = "UPDATE t_daily_job set jobs = ? where id = ?";
		Object[] args = { jobBag.toJson(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

}
