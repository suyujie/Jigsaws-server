package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.util.List;
import java.util.Map;

import server.node.system.mission.Mission;
import server.node.system.player.Player;

public class MissionDao {

	/**
	 * 保存 mission
	 */
	public void save(Player player, Mission mission) {
		String sql = "insert into t_mission(player_id,making_id,gain_cash_t,gain_exp_t,client_p,points) values (?,?,?,?,?,?)";
		Object[] args = { player.getId(), mission.getMakingId(), mission.getLastGainCashTime(), mission.getLastGainExpTime(), mission.getClientP(), mission.pointsToJson() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * update mission
	 */
	public void updateGain(Player player, Mission mission) {
		String sql = "update t_mission set gain_cash_t = ?,gain_exp_t = ? where player_id = ? and making_id = ?";
		Object[] args = { mission.getLastGainCashTime(), mission.getLastGainExpTime(), player.getId(), mission.getMakingId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * update clientP
	 */
	public void updateClientP(Player player, Mission mission) {
		String sql = "update t_mission set client_p = ? where player_id = ? and making_id = ?";
		Object[] args = { mission.getClientP(), player.getId(), mission.getMakingId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * update pointbag
	 */
	public void updatePoint(Player player, Mission mission) {
		String sql = "update t_mission set points = ? where player_id = ? and making_id = ?";
		Object[] args = { mission.pointsToJson(), player.getId(), mission.getMakingId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * 读取mission list
	 */
	public List<Map<String, Object>> readMissions(Player player) {
		String sql = "select * from t_mission where player_id = ?";
		Object[] args = { player.getId() };
		List<Map<String, Object>> list = SyncDBUtil.readList(DBOperator.Read, player.getId(), sql, args, false);
		return list;
	}

}
