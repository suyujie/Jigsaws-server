package server.node.dao;

import java.sql.SQLException;
import java.util.Map;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;
import gamecore.util.Clock;
import server.node.system.player.Player;
import server.node.system.player.PlayerStatistics;

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

	public void savePlayerStatistics(Long playerId, PlayerStatistics ss) {

		String sql = "INSERT INTO t_statistics (player_id, game_success, game_failure, game_giveup, upload_num, upload_be_good, upload_be_bad, comment_good, comment_bad)VALUES (?,?,?,?,?,?,?,?,?);";
		Object[] args = { playerId, ss.getGameSuccess(), ss.getGameFailure(), ss.getGameGiveup(), ss.getUpLoadNum(),
				ss.getUpLoadBeGood(), ss.getUpLoadBeBad(), ss.getCommentGood(), ss.getCommentBad() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, playerId, sql, args));
	}

	public Map<String, Object> readPlayerStatistics(Long playerId) throws SQLException {
		String sql = "select * from t_statistics where player_id = ? limit 0,1";
		Object[] args = { playerId };
		return SyncDBUtil.readMap(DBOperator.Read, playerId, sql, args, false);
	}

	/**
	 * update PlayerStatistics
	 */
	public void updatePlayerStatistics(Long playerId, PlayerStatistics ss) {
		String sql = "update t_statistics set game_success=?,game_failure=?,game_giveup=?,upload_num=?,upload_be_good=?,upload_be_bad=?,comment_good=?,comment_bad=? where player_id = ? ";
		Object[] args = { ss.getGameSuccess(), ss.getGameFailure(), ss.getGameGiveup(), ss.getUpLoadNum(),
				ss.getUpLoadBeGood(), ss.getUpLoadBeBad(), ss.getCommentGood(), ss.getCommentBad(), playerId };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, playerId, sql, args));
	}

}
