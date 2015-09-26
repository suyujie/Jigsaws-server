package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.util.List;
import java.util.Map;

import server.node.system.player.Player;
import server.node.system.robot.RobotSlot;

public class RobotSlotDao {

	/**
	 * 保存 robotSlot
	 */
	public void save(Player player, RobotSlot robotSlot) {
		String sql = "insert into t_robot_slot(player_id,slot,wear,repair_b_t,repair_e_t) values (?,?,?,?,?)";
		Object[] args = { player.getId(), robotSlot.getSlot(), robotSlot.getWear(), robotSlot.getRepairBeginTime(), robotSlot.getRepairEndTime() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * update robotSlot
	 */
	public void updateRobot(Player player, RobotSlot robotSlot) {
		String sql = "update t_robot_slot set wear = ? ,repair_b_t = ? , repair_e_t = ? where player_id = ? and slot = ?";
		Object[] args = { robotSlot.getWear(), robotSlot.getRepairBeginTime(), robotSlot.getRepairEndTime(), player.getId(), robotSlot.getSlot() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * 读取robot slot list
	 */
	public List<Map<String, Object>> readRobotSlots(Player player) {
		String sql = "select * from t_robot_slot where player_id = ?";
		Object[] args = { player.getId() };
		List<Map<String, Object>> list = SyncDBUtil.readList(DBOperator.Read, player.getId(), sql, args, false);
		return list;
	}

}
