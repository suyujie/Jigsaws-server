package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.util.Map;

import server.node.system.friend.FriendBag;
import server.node.system.player.Player;

public class FriendDao {

	/**
	 * 读取friend
	 */
	public Map<String, Object> readFriendBagPO(Player player) {
		String sql = "select * from t_friend where id = ? limit 0,1";
		Object[] args = { player.getId() };
		return SyncDBUtil.readMap(DBOperator.Read, sql, args);
	}

	/**
	 * 保存 friendBagPO
	 */
	public void saveFriendBag(Player player, FriendBag friendBag) {
		String sql = "INSERT INTO t_friend(id, friends) values (?,?)";
		Object[] args = { player.getId(), friendBag.toJson() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void updateFriendBag(Player player, FriendBag friendBag) {
		String sql = "UPDATE t_friend set friends = ? where id = ?";
		Object[] args = { friendBag.toJson(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

}
