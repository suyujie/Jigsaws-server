package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.sql.SQLException;
import java.util.Map;

import server.node.system.chip.ChipBag;
import server.node.system.player.Player;

public class ChipDao {

	public Map<String, Object> readChipBag(Player player) throws SQLException {
		String sql = "select * from t_chip where id = ? limit 0,1";
		Object[] args = { player.getId() };
		return SyncDBUtil.readMap(DBOperator.Read, player.getId(), sql, args, false);
	}

	public void save(Player player, ChipBag chipBag) {

		String sql = "insert into t_chip(id,chips) values (?,?)";
		Object[] args = { player.getId(), chipBag.toStorageJson() };

		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void update(Player player, ChipBag chipBag) {
		String sql = "update t_chip set chips = ? where id = ?";
		Object[] args = { chipBag.toStorageJson(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

}
