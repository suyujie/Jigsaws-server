package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.sql.SQLException;
import java.util.Map;

import server.node.system.handbook.HandbookBag;
import server.node.system.player.Player;

public class HandbookDao {

	public Map<String, Object> readHandbookBag(Player player) throws SQLException {
		String sql = "select * from t_handbook_bag where id = ? limit 0,1";
		Object[] args = { player.getId() };
		return SyncDBUtil.readMap(DBOperator.Read, player.getId(), sql, args, false);
	}

	public void save(Player player, HandbookBag handbookBag) {
		String sql = "insert into t_handbook_bag(id,handbooks,rewarded) values (?,?,?)";
		Object[] args = { player.getId(), handbookBag.toStorageHandbooksJson(), handbookBag.toStorageRewardedJson() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void update(Player player, HandbookBag handbookBag) {
		String sql = "update t_handbook_bag set handbooks = ? , rewarded = ? where id = ?";
		Object[] args = { handbookBag.toStorageHandbooksJson(), handbookBag.toStorageRewardedJson(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

}
