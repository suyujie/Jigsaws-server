package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.sql.SQLException;
import java.util.Map;

import server.node.system.player.Player;
import server.node.system.task.TaskBag;

public class TaskDao {

	public Map<String, Object> readTask(Player player) throws SQLException {
		String sql = "select * from t_task where id = ? limit 0,1";
		Object[] args = { player.getId() };
		return SyncDBUtil.readMap(DBOperator.Read, player.getId(), sql, args, false);
	}

	public void saveTaskBag(Player player, TaskBag taskBag) {
		String sql = "INSERT INTO t_task(id,task_title_id, tasks) values (?,?,?)";
		Object[] args = { player.getId(), taskBag.getTitleTaskId(), taskBag.toStorageJson() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void updateTasks(Player player, TaskBag taskBag) {
		String sql = "UPDATE t_task set tasks = ? where id = ?";
		Object[] args = { taskBag.toStorageJson(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void updateTaskTitleId(Player player, TaskBag taskBag) {
		String sql = "UPDATE t_task set task_title_id = ?  where id = ?";
		Object[] args = { taskBag.getTitleTaskId(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

}
