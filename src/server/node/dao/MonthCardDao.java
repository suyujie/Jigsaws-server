package server.node.dao;

import java.sql.SQLException;
import java.util.Map;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;
import gamecore.util.Clock;
import server.node.system.monthCard.MonthCard;
import server.node.system.player.Player;

public class MonthCardDao {

	public void save(Player player, MonthCard monthCard) {
		String sql = "insert into t_month_card(player_id,reward_num,buy_t,last_t,t) values (?,?,?,?,?)";
		Object[] args = { player.getId(), monthCard.getRewardNum(), monthCard.getBuyT(), monthCard.getLastT(), Clock.currentTimeSecond() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void update(Player player, MonthCard monthCard) {
		String sql = "update t_month_card set last_t = ? ,reward_num = ? where player_id = ? AND buy_t = ? ";
		Object[] args = { monthCard.getLastT(), monthCard.getRewardNum(), player.getId(), monthCard.getBuyT() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void delete(Player player) {
		String sql = "DELETE FROM t_month_card WHERE player_id = ?";
		Object[] args = { player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public Map<String, Object> readMonthCard(Long playerId) throws SQLException {
		String sql = "select * from t_month_card where player_id = ? order by buy_t desc limit 0,1 ";
		Object[] args = { playerId };
		return SyncDBUtil.readMap(DBOperator.Read, playerId, sql, args, false);
	}

}
