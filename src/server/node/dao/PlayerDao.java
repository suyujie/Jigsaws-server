package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.AsyncDBTransactionTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;
import gamecore.util.Clock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import server.node.system.ConfigManager;
import server.node.system.Content;
import server.node.system.account.Account;
import server.node.system.player.Player;

public class PlayerDao {

	/**
	 * 保存account player
	 */
	public void saveAccountPlayer(Account account, Player player) {

		List<String> sqls = new ArrayList<>();
		List<Object[]> args = new ArrayList<>();

		sqls.add("insert into t_account(mobile_id,plat,id_in_plat,name_in_plat,player_id,t) values (?,?,?,?,?,?)");

		sqls.add("insert into t_player(id,level,exp,gold,cash,cup_num,protect_end_time, pvp_attack_win_count, pvp_defence_win_count, pvp_beat_robot_count,let_count,online,online_time,t) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

		Object[] args_account = { account.getMobileId(), account.getPlat(), account.getIdInPlat(), account.getNameInPlat(), account.getPlayerId(), Clock.currentTimeSecond() };
		Object[] args_player = { player.getId(), player.getLevel(), player.getExp(), player.getGold(), player.getCash(), player.getPlayerStatistics().getCupNum(),
				player.getProtectEndTime(), player.getPlayerStatistics().getPvpAttackWinCount(), player.getPlayerStatistics().getPvpDefenceWinCount(),
				player.getPlayerStatistics().getPvpBeatRobotCount(), player.getPlayerStatistics().getLetCount(), player.getOnLine(), player.getOnLineTime(),
				Clock.currentTimeSecond() };

		args.add(args_account);
		args.add(args_player);

		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTransactionTask(DBOperator.Write, player.getId(), sqls, args));
	}

	//单独保存player
	public void savePlayer(Player player) {

		String sql = "insert into t_player(id,level,exp,gold,cash,cup_num,protect_end_time, pvp_attack_win_count, pvp_defence_win_count, pvp_beat_robot_count,let_count,online,online_time,t) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] args = { player.getId(), player.getLevel(), player.getExp(), player.getGold(), player.getCash(), player.getPlayerStatistics().getCupNum(),
				player.getProtectEndTime(), player.getPlayerStatistics().getPvpAttackWinCount(), player.getPlayerStatistics().getPvpDefenceWinCount(),
				player.getPlayerStatistics().getPvpBeatRobotCount(), player.getPlayerStatistics().getLetCount(), player.getOnLine(), player.getOnLineTime(),
				Clock.currentTimeSecond() };
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
		Object[] args = { player.getPlayerStatistics().getCupNum(), player.getPlayerStatistics().getPvpAttackWinCount(), player.getPlayerStatistics().getPvpDefenceWinCount(),
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
	 * 更新玩家的在线状态
	 */
	public void updateOnline(Player player) {
		String sql = "update t_player set online = ? ,last_signin_time = ? , online_time = ? where id = ? ";
		Object[] args = { player.getOnLine(), player.getLastSignT(), player.getOnLineTime(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * 读取不在线用户的id,可以被打的那种
	 */
	public List<Map<String, Object>> readPlayerAsOpponent() {
		String sql = "select id,cup_num from t_player where mod(id,?) = ? and online = ? and level >=? and protect_end_time = ? order by rand()  limit 0,10000";
		Object[] args = { ConfigManager.getInstance().nodeNum, ConfigManager.getInstance().tag - 1, 0, Content.PvpMinLevel, 0 };
		return SyncDBUtil.readList(DBOperator.Read, sql, args);
	}

	/**
	 * 读取在线用户
	 */
	public List<Map<String, Object>> readPlayerOnline() {
		String sql = "select id,cup_num from t_player where mod(id,?) = ? and online = ? order by level desc limit 0,10000";
		Object[] args = { ConfigManager.getInstance().nodeNum, ConfigManager.getInstance().tag - 1, 1 };
		return SyncDBUtil.readList(DBOperator.Read, sql, args);
	}

	/**
	 * 读取被保护用户
	 */
	public List<Map<String, Object>> readPlayerProtected() {
		String sql = "select id,cup_num from t_player where mod(id,?) = ? and protect_end_time > ? order by protect_end_time desc limit 0,10000";
		Object[] args = { ConfigManager.getInstance().nodeNum, ConfigManager.getInstance().tag - 1, 0 };
		return SyncDBUtil.readList(DBOperator.Read, sql, args);
	}

	/**
	 * 部分排行
	 */
	public List<Map<String, Object>> readPlayerRanking() {
		String sql = "select id,cup_num from t_player where mod(id,?) = ? order by cup_num desc limit 0,100";
		Object[] args = { ConfigManager.getInstance().nodeNum, ConfigManager.getInstance().tag - 1 };
		return SyncDBUtil.readList(DBOperator.Read, sql, args);
	}

}
