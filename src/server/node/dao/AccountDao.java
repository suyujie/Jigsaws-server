package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;
import gamecore.util.Clock;
import gamecore.util.Utils;

import java.sql.SQLException;
import java.util.Map;

import server.node.system.account.Account;

public class AccountDao {

	public Map<String, Object> readAccount(Long playerId) {
		String sql = "select * from t_account where player_id = ? and enable = ? limit 0,1";
		Object[] args = { playerId, 1 };
		return SyncDBUtil.readMap(DBOperator.Read, sql, args);
	}

	public Map<String, Object> readAccount(String mobileId) throws SQLException {
		String sql = "select * from t_account where mobile_id = ? limit 0,1";
		Object[] args = { mobileId };
		return SyncDBUtil.readMap(DBOperator.Read, new Long(Utils.hashCode(mobileId)), sql, args, false);
	}

	public Map<String, Object> readAccount(String plat, String idInPlat) {
		String sql = "select * from t_account where plat = ? and id_in_plat = ? limit 0,1";
		Object[] args = { plat, idInPlat };
		return SyncDBUtil.readMap(DBOperator.Read, sql, args);
	}

	/**
	 * 保存account
	 */
	public void saveAccount(Account account) {
		String sql = "insert into t_account(mobile_id,name,player_id,t,channel,device) values (?,?,?,?,?,?)";
		Object[] args = { account.getDeviceId(), account.getName(), account.getPlayerId(), Clock.currentTimeSecond(),
				account.getChannel(), account.getDevice() };
		TaskCenter.getInstance()
				.execute(new AsyncDBTask(DBOperator.Write, new Long(Utils.hashCode(account.getDeviceId())), sql, args));
	}

	/**
	 * 更新 account
	 */
	public void updateAccount(Account account) {
		String sql = "update t_account set  name = ? , player_id = ?,channel=?,device=? where mobile_id = ?";
		Object[] args = { account.getName(), account.getPlayerId(), account.getChannel(), account.getDevice(),
				account.getDeviceId() };
		TaskCenter.getInstance().executeWithSlidingWindow(
				new AsyncDBTask(DBOperator.Write, new Long(Utils.hashCode(account.getDeviceId())), sql, args));
	}

}
