package server.node.dao;

import java.sql.SQLException;
import java.util.Map;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;
import gamecore.util.Clock;
import server.node.system.player.Player;

public class PlayerDao {

	// 单独保存player
	public void savePlayer(Player player) {

		String sql = "insert into t_player(id,level,exp,last_signin_time) values (?,?,?,?)";
		Object[] args = { player.getId(), player.getLevel(), player.getExp(), Clock.currentTimeSecond() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * 读取player
	 */
	public Map<String, Object> readPlayer(Long playerId) throws SQLException {
		String sql = "select * from t_player where id = ? limit 0,1";
		Object[] args = { playerId };
		return SyncDBUtil.readMap(DBOperator.Read, playerId, sql, args, false);
	}

}
