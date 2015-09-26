package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.sql.SQLException;
import java.util.Map;

import server.node.system.expPart.ExpPartBag;
import server.node.system.player.Player;

public class ExpPartDao {

	public Map<String, Object> readExpPartBag(Player player) throws SQLException {
		String sql = "select * from t_exp_part_bag where id = ? limit 0,1";
		Object[] args = { player.getId() };
		return SyncDBUtil.readMap(DBOperator.Read, player.getId(), sql, args, false);
	}

	public void save(Player player, ExpPartBag expPartBag) {
		String sql = "insert into t_exp_part_bag(id,exp_parts) values (?,?)";
		Object[] args = { player.getId(), expPartBag.toStorgeJson() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void update(Player player, ExpPartBag expPartBag) {
		String sql = "update t_exp_part_bag set exp_parts = ? where id = ?";
		Object[] args = { expPartBag.toStorgeJson(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

}
