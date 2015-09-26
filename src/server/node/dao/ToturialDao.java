package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.sql.SQLException;
import java.util.Map;

import server.node.system.player.Player;
import server.node.system.toturial.Toturial;

public class ToturialDao {

	public Map<String, Object> readToturial(Player player) throws SQLException {
		String sql = "select * from t_toturial where id = ? limit 0,1";
		Object[] args = { player.getId() };
		return SyncDBUtil.readMap(DBOperator.Read, player.getId(), sql, args, false);
	}

	public void saveToturial(Player player, Toturial toturial) {
		String sql = "INSERT INTO t_toturial(id, current_id,button_id,rewarded_ids,is_pvp_win_one,btn_str,grade_status,grade_time,add_one_head) values (?,?,?,?,?,?,?,?,?)";
		Object[] args = { player.getId(), toturial.getCurrentId(), toturial.getButtonId(), toturial.rewardedIdsToString(), toturial.getIsPvpWinOne(), toturial.getBtnStr(),
				toturial.getGradeStatus(), toturial.getGradeTime(), toturial.getAddOneHead() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void updateToturial(Player player, Toturial toturial) {
		String sql = "UPDATE t_toturial set current_id = ?,button_id = ?,rewarded_ids = ?,is_pvp_win_one = ?,btn_str = ?,grade_status = ?,grade_time = ?,add_one_head = ? where id = ?";
		Object[] args = { toturial.getCurrentId(), toturial.getButtonId(), toturial.rewardedIdsToString(), toturial.getIsPvpWinOne(), toturial.getBtnStr(),
				toturial.getGradeStatus(), toturial.getGradeTime(), toturial.getAddOneHead(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

}
