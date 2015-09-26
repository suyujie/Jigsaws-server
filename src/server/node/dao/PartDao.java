package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.sql.SQLException;
import java.util.Map;

import server.node.system.player.Player;
import server.node.system.robotPart.PartBag;

public class PartDao {

	/**
	 * 读取partbag
	 */
	public Map<String, Object> readPartBag(Player player) throws SQLException {
		String sql = "select * from t_part_bag where id = ? limit 0,1";
		Object[] args = { player.getId() };
		return SyncDBUtil.readMap(DBOperator.Read, player.getId(), sql, args, false);
	}

	/**
	 * 保存 partbag
	 */
	public void save(Player player, PartBag partBag) {

		String sql = "insert into t_part_bag(id,parts) values (?,?)";
		Object[] args = { player.getId(), partBag.toStorgeJson() };

		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * 更新partbag
	 */
	public void update(Player player, PartBag partBag) {
		String sql = "update t_part_bag set parts = ? where id = ?";
		Object[] args = { partBag.toStorgeJson(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

}
