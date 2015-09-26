package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.sql.SQLException;
import java.util.Map;

import server.node.system.berg.BergBag;
import server.node.system.player.Player;

public class BergDao {

	public Map<String, Object> readBergBag(Player player) throws SQLException {
		String sql = "select * from t_berg where id = ? limit 0,1";
		Object[] args = { player.getId() };
		return SyncDBUtil.readMap(DBOperator.Read, player.getId(), sql, args, false);
	}

	public void save(Player player, BergBag bergBag) {

		String sql = "insert into t_berg(id,bergs) values (?,?)";
		Object[] args = { player.getId(), bergBag.toStorageJson() };

		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void update(Player player, BergBag bergBag) {
		String sql = "update t_berg set bergs = ? where id = ?";
		Object[] args = { bergBag.toStorageJson(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

}
