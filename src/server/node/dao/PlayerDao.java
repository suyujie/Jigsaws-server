package server.node.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gamecore.db.AsyncDBTask;
import gamecore.db.AsyncDBTransactionTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;
import gamecore.util.Clock;
import server.node.system.ConfigManager;
import server.node.system.account.Account;
import server.node.system.player.Player;

public class PlayerDao {

	// 单独保存player
	public void savePlayer(Player player) {

		String sql = "insert into t_player(id,level,exp,gold,cash,cup_num,protect_end_time, pvp_attack_win_count, pvp_defence_win_count, pvp_beat_robot_count,let_count,online,online_time,t) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] args = { player.getId(), player.getLevel(), player.getExp(), player.getGold(), player.getCash(),
				player.getPlayerStatistics().getCupNum(), player.getProtectEndTime(),
				player.getPlayerStatistics().getPvpAttackWinCount(),
				player.getPlayerStatistics().getPvpDefenceWinCount(),
				player.getPlayerStatistics().getPvpBeatRobotCount(), player.getPlayerStatistics().getLetCount(),
				player.getOnLine(), player.getOnLineTime(), Clock.currentTimeSecond() };
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

	/**
	 * 更新玩家的gold
	 */
	public void updatePlayerGold(Player player) {
		String sql = "update t_player set gold = ? where id = ? ";
		Object[] args = { player.getGold(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * 更新玩家的cash
	 */
	public void updatePlayerCash(Player player) {
		String sql = "update t_player set cash= ? where id = ? ";
		Object[] args = { player.getCash(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * 更新玩家的level exp
	 */
	public void updatePlayerLevelExp(Player player) {
		String sql = "update t_player set level= ? , exp = ? where id = ? ";
		Object[] args = { player.getLevel(), player.getExp(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * 更新玩家的战斗属性,和统计
	 */
	public void updatePlayerStatistics(Player player) {
		String sql = "update t_player set cup_num = ?,pvp_attack_win_count = ?,pvp_defence_win_count=?,pvp_beat_robot_count = ? where id = ? ";
		Object[] args = { player.getPlayerStatistics().getCupNum(), player.getPlayerStatistics().getPvpAttackWinCount(),
				player.getPlayerStatistics().getPvpDefenceWinCount(),
				player.getPlayerStatistics().getPvpBeatRobotCount(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * 更新完家的保护时间
	 */
	public void updateProtectTime(Player player) {
		String sql = "update t_player set protect_end_time = ? where id = ? ";
		Object[] args = { player.getProtectEndTime(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * 更新完家的推送地址
	 */
	public void updatePushUri(Player player) {
		String sql = "update t_player set push_uri = ? where id = ? ";
		Object[] args = { player.getPushUri(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * 更新完家语言
	 */
	public void updateLang(Player player) {
		String sql = "update t_player set lang = ? where id = ? ";
		Object[] args = { player.getLang().asCode(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * 更新头像状态
	 */
	public void updateImg(Player player) {
		String sql = "update t_player set have_img = ? where id = ? ";
		Object[] args = { player.getHaveImg(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * 读取在线用户
	 */
	public List<Map<String, Object>> readPlayerOnline() {
		String sql = "select id,cup_num from t_player where mod(id,?) = ? and online = ? order by level desc limit 0,10000";
		Object[] args = { ConfigManager.getInstance().nodeNum, ConfigManager.getInstance().tag - 1, 1 };
		return SyncDBUtil.readList(DBOperator.Read, sql, args);
	}

}
