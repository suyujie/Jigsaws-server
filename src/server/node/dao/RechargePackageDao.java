package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.sql.SQLException;
import java.util.Map;

import server.node.system.player.Player;
import server.node.system.rechargePackage.RechargePackageBag;

public class RechargePackageDao {

	public Map<String, Object> readRechargeBag(Player player) throws SQLException {
		String sql = "select * from t_recharge where id = ? limit 0,1";
		Object[] args = { player.getId() };
		return SyncDBUtil.readMap(DBOperator.Read, player.getId(), sql, args, false);
	}

	public void save(Player player, RechargePackageBag rechargePackageBag) {

		String sql = "insert into t_recharge(id,buy_ids) values (?,?)";
		Object[] args = { player.getId(), rechargePackageBag.toStorageBuyedIdsJson() };

		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void update(Player player, RechargePackageBag rechargePackageBag) {
		String sql = "update t_recharge set buy_ids = ? where id = ?";
		Object[] args = { rechargePackageBag.toStorageBuyedIdsJson(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

}
