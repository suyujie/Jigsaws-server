package server.node.system.account;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Map;

import gamecore.system.AbstractSystem;
import gamecore.system.SystemResult;
import server.node.dao.AccountDao;
import server.node.dao.DaoFactory;
import server.node.system.player.Player;

/**
 * 人物系统。
 */
public final class AccountSystem extends AbstractSystem {

	public AccountSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("AccountSystem start....");
		System.out.println("AccountSystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {
	}

	private Account encapsulateAccount(Map<String, Object> map) {
		if (map != null) {
			String mobileId = (String) map.get("mobile_id");
			String name = (String) map.get("name");
			Long playerId = ((BigInteger) map.get("player_id")).longValue();
			String channel = (String) map.get("channel");
			String device = (String) map.get("device");
			Account account = new Account(mobileId, name, playerId, channel, device);
			return account;
		} else {
			return null;
		}
	}

	// 插入新account
	public void saveAccount(Account account) {
		AccountDao accountDao = DaoFactory.getInstance().borrowAccountDao();
		accountDao.saveAccount(account);
		DaoFactory.getInstance().returnAccountDao(accountDao);
	}

	/**
	 * 读取玩家的account
	 */
	public Account getAccountFromDB(Long playerId) {
		AccountDao accountDao = DaoFactory.getInstance().borrowAccountDao();
		Map<String, Object> map = accountDao.readAccount(playerId);
		DaoFactory.getInstance().returnAccountDao(accountDao);
		return encapsulateAccount(map);
	}

	/**
	 * 读取玩家的account
	 */
	public Account getAccount(String deviceId) throws SQLException {
		AccountDao accountDao = DaoFactory.getInstance().borrowAccountDao();
		Map<String, Object> map = accountDao.readAccount(deviceId);
		DaoFactory.getInstance().returnAccountDao(accountDao);
		return encapsulateAccount(map);
	}

	/**
	 * 读取玩家的account
	 */
	public Account getAccountFromDB(String plat, String idInPlat) {
		AccountDao accountDao = DaoFactory.getInstance().borrowAccountDao();
		Map<String, Object> map = accountDao.readAccount(plat, idInPlat);
		DaoFactory.getInstance().returnAccountDao(accountDao);
		return encapsulateAccount(map);
	}

	/**
	 * 更新玩家的设备信息
	 */
	public void updateChannelDevice(Account account, String channel, String device) {
		if (account.getChannel() == null || account.getDevice() == null || !account.getChannel().equals(channel)
				|| account.getDevice().equals(device)) {
			account.setChannel(channel);
			account.setDevice(device);
		}
		updateDB(account);
	}

	/**
	 * 改名字
	 */
	public SystemResult updateName(Player player, String name) {
		SystemResult result = new SystemResult();

		player.getAccount().setName(name);

		updateDB(player.getAccount());

		player.synchronize();

		return result;
	}

	private void updateDB(Account account) {
		AccountDao dao = DaoFactory.getInstance().borrowAccountDao();
		dao.updateAccount(account);
		DaoFactory.getInstance().returnAccountDao(dao);
	}

}
