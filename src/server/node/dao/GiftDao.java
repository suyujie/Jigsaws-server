package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.sql.SQLException;
import java.util.Map;

import server.node.system.gift.GiftBag;
import server.node.system.player.Player;

public class GiftDao {

	/**
	 * 读取gift
	 */
	public Map<String, Object> readGiftBagPO(Player player) throws SQLException {
		String sql = "select * from t_gift where id = ? limit 0,1";
		Object[] args = { player.getId() };
		return SyncDBUtil.readMap(DBOperator.Read, player.getId(), sql, args, false);
	}

	/**
	 * 保存 gift
	 */
	public void saveGiftBag(Player player, GiftBag giftBag) {
		String sql = "INSERT INTO t_gift(id, gifts) values (?,?)";
		Object[] args = { player.getId(), giftBag.toJson() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void updateGiftBag(Player player, GiftBag giftBag) {
		String sql = "UPDATE t_gift set gifts = ? where id = ?";
		Object[] args = { giftBag.toJson(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

}
