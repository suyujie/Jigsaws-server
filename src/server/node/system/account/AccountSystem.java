package server.node.system.account;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.system.AbstractSystem;
import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Map;

import server.node.dao.AccountDao;
import server.node.system.Root;
import server.node.system.player.Player;
import server.node.system.player.PlayerChangeBean;
import server.node.system.session.Session;

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
			String plat = (String) map.get("plat");
			int enable = ((Long) map.get("enable")).intValue();
			String idInPlat = (String) map.get("id_in_plat");
			String nameInPlat = (String) map.get("name_in_plat");
			Long playerId = ((BigInteger) map.get("player_id")).longValue();
			String channel = (String) map.get("channel");
			String device = (String) map.get("device");
			Account account = new Account(mobileId, enable, plat, idInPlat, nameInPlat, playerId, channel, device);
			return account;
		} else {
			return null;
		}
	}

	//插入新account
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
	public Account getAccount(String mobileId) throws SQLException {
		AccountDao accountDao = DaoFactory.getInstance().borrowAccountDao();
		Map<String, Object> map = accountDao.readAccount(mobileId);
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
		if (account.getChannel() == null || account.getDevice() == null || !account.getChannel().equals(channel) || account.getDevice().equals(device)) {
			account.setChannel(channel);
			account.setDevice(device);
		}
		updateDB(account);
	}

	/**
	 * 更新玩家的姓名,plat信息
	 */
	public SystemResult updateAccountInfo(Player player, String plat, String idInPlat, String name) {
		SystemResult result = new SystemResult();

		player.getAccount().setPlatAdnIdInPlat(plat, idInPlat, name);

		updateDB(player.getAccount());

		player.synchronize();

		return result;
	}

	/**
	 * 改名字
	 */
	public SystemResult updateName(Player player, String name) {
		SystemResult result = new SystemResult();

		player.getAccount().setNameInPlat(name);

		updateDB(player.getAccount());

		player.synchronize();

		return result;
	}

	/**
	 * 更改玩家档案
	 * @param account,当前的账户
	 * playerId,老的档案的playerid
	 */
	public SystemResult changePlayer(Player player_new) {
		SystemResult result = new SystemResult();

		PlayerChangeBean playerChangeBean = RedisHelperJson.getPlayerChangeBean(player_new.getId());
		RedisHelperJson.removePlayerChangeBean(player_new.getId());

		if (playerChangeBean != null) {

			//老档案
			Player player_old = playerChangeBean.getPlayer_old();
			//老帐户
			Account account_old = playerChangeBean.getAccount_old();
			//新账户
			Account account_new = playerChangeBean.getAccount_new();

			//新账户 的平台信息加入,playerid指向 老档案
			account_new.setPlat(account_old.getPlat());
			account_new.setIdInPlat(account_old.getIdInPlat());
			account_new.setPlayerId(player_old.getId());
			account_new.setNameInPlat(account_old.getNameInPlat());
			account_new.setEnable(1);

			//老档案player换成新的account
			player_old.setAccount(account_new);
			player_old.synchronize();

			//老帐户 account_old 的平台信息清空,但是不更新playerid,这样 老帐户也可以玩老档案
			account_old.setIdInPlat(null);
			account_old.setPlat(null);
			account_old.setNameInPlat(null);
			account_old.setEnable(0);

			AccountDao accountDao = DaoFactory.getInstance().borrowAccountDao();
			accountDao.updateAccount(account_old);
			accountDao.updateAccount(account_new);
			DaoFactory.getInstance().returnAccountDao(accountDao);

			//session 更新,新account的session 里面的player变成老的
			Session session = Root.sessionSystem.getSession(account_new.getMobileId());
			session.setPlayerId(player_old.getId());
			session.synchronize();

			//移除老的session
			Root.sessionSystem.removeSession(account_old.getMobileId());
			//移除新的player(之后再也不会有人用了,但是db不动)
			RedisHelperJson.removePlayer(player_new.getId());

		} else {
			result.setCode(ErrorCode.PARAM_ERROR);
		}

		return result;
	}

	private void updateDB(Account account) {
		AccountDao dao = DaoFactory.getInstance().borrowAccountDao();
		dao.updateAccount(account);
		DaoFactory.getInstance().returnAccountDao(dao);
	}

}
