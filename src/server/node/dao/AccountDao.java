package server.node.dao;

import java.sql.SQLException;
import java.util.Map;

import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.util.Clock;
import gamecore.util.Utils;
import server.node.system.account.Account;

public class AccountDao {

	public Map<String, Object> readAccount(Long playerId) {
		String sql = "select * from t_account where player_id = ? limit 0,1";
		Object[] args = { playerId, 1 };
		return SyncDBUtil.readMap(DBOperator.Read, sql, args);
	}

	public Map<String, Object> readAccount(String deviceId) throws SQLException {
		String sql = "select * from t_account where device_id = ? limit 0,1";
		Object[] args = { deviceId };
		return SyncDBUtil.readMap(DBOperator.Read, new Long(Utils.hashCode(deviceId)), sql, args, false);
	}

	/**
	 * 保存account
	 */
	public void saveAccount(Account account) {
		String sql = "insert into t_account(device_id,player_id,channel,device,ct) values (?,?,?,?,?)";
		Object[] args = { account.getDeviceId(), account.getPlayerId(), account.getChannel(), account.getDevice(),
				Clock.currentTimeSecond() };

		SyncDBUtil.execute(DBOperator.Write, new Long(Utils.hashCode(account.getDeviceId())), sql, args);

	}

}
