package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.util.List;
import java.util.Map;

import server.node.system.ConfigManager;
import server.node.system.player.Player;
import server.node.system.robot.Robot;
import server.node.system.robot.RobotType;

public class RobotDao {

	/**
	 * 保存 robot
	 */
	public void save(Player player, Robot robot, int score) {
		String sql = "insert into t_robot(id,player_id,bag,slot,score,parts,bergs) values (?,?,?,?,?,?,?)";
		Object[] args = { robot.getId(), player.getId(), robot.getRobotType().asCode(), robot.getSlot(), score, robot.partsJson(), robot.bergsJson() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * update robot
	 */
	public void updateRobot(Player player, Robot robot, int score) {
		String sql = "update t_robot set bag = ? ,slot=?, score = ?, parts = ?, bergs = ? where id = ?";
		Object[] args = { robot.getRobotType().asCode(), robot.getSlot(), score, robot.partsJson(), robot.bergsJson(), robot.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * delete robot
	 */
	public void deleteRobot(Player player, Robot robot) {
		String sql = "DELETE FROM t_robot WHERE id = ?";
		Object[] args = { robot.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * 读取robot list
	 */
	public List<Map<String, Object>> readRobots(Player player) {
		String sql = "select * from t_robot where player_id = ?";
		Object[] args = { player.getId() };
		List<Map<String, Object>> list = SyncDBUtil.readList(DBOperator.Read, player.getId(), sql, args, false);
		return list;
	}

	/**
	 * 读取robot list
	 */
	public List<Map<String, Object>> readRobotsRanking() {
		String sql = "select * from t_robot  where mod(id,?) = ? and bag = ? order by score desc limit 0,100";
		Object[] args = { ConfigManager.getInstance().nodeNum, ConfigManager.getInstance().tag - 1, RobotType.BATTLE.asCode() };
		List<Map<String, Object>> list = SyncDBUtil.readList(DBOperator.Read, sql, args);
		return list;
	}

}
