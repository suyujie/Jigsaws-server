package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.sql.SQLException;
import java.util.Map;

import server.node.system.color.ColorBag;
import server.node.system.player.Player;

public class ColorDao {

	/**
	 * 读取colorbag
	 */
	public Map<String, Object> readColorBag(Player player) throws SQLException {
		String sql = "select * from t_color where id = ? limit 0,1";
		Object[] args = { player.getId() };
		return SyncDBUtil.readMap(DBOperator.Read, player.getId(), sql, args, false);
	}

	/**
	 * 保存 colorbag
	 */
	public void save(Player player, ColorBag colorBag) {

		String sql = "insert into t_color(id,color_num) values (?,?)";
		Object[] args = { player.getId(), colorBag.toStrArray() };

		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * 更新colorbag
	 */
	public void update(Player player, ColorBag colorBag) {
		String sql = "update t_color set color_num = ? where id = ?";
		Object[] args = { colorBag.toStrArray(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

}
