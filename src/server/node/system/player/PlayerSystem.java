package server.node.system.player;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gamecore.system.AbstractSystem;
import server.node.dao.DaoFactory;
import server.node.dao.PlayerDao;
import server.node.system.RedisHelperJson;
import server.node.system.Root;
import server.node.system.account.Account;

/**
 * 玩家系统。
 */
public final class PlayerSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(PlayerSystem.class.getName());

	public PlayerSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("PlayerSystem start....");
		System.out.println("PlayerSystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {
	}

	/**
	 * 注册玩家
	 */
	public Player register(String deviceId, String sessionId, boolean sync) {

		Long playerId = Root.idsSystem.takeId();

		Account account = new Account(deviceId, playerId, null, null);
		Root.accountSystem.saveAccount(account);

		Player player = new Player(playerId, 0, 0);
		player.setAccount(account);

		PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
		playerDao.savePlayer(player);
		DaoFactory.getInstance().returnPlayerDao(playerDao);

		if (sync) {
			player.synchronize();
		}

		// 发布注册消息
		this.publish(new PlayerMessage(PlayerMessage.Registe, player, sessionId));

		return player;
	}

	/**
	 * 读取player 实体
	 */
	public Player getPlayer(Long playerId) throws SQLException {
		if (playerId == null) {
			return null;
		}
		Player player = RedisHelperJson.getPlayer(playerId);
		if (player == null || player.getAccount() == null) {
			player = readPlayerFromDB(playerId);
			if (player != null) {
				player.synchronize();
			}
		}

		return player;
	}

	/**
	 * 读取player 实体 主动读取需要同步session,被动就不要了
	 */
	public Player getPlayer(Account account) throws SQLException {

		if (account == null || account.getPlayerId() == null) {
			return null;
		}

		Player player = RedisHelperJson.getPlayer(account.getPlayerId());
		if (player == null) {
			player = readPlayerFromDB(account);
			if (player != null) {
				player.synchronize();
			}
		}

		// 发布登陆消息
		this.publish(new PlayerMessage(PlayerMessage.SignIn, player));

		return player;
	}

	public Player getPlayerFromCache(Long playerId) {
		if (playerId == null) {
			return null;
		}
		return RedisHelperJson.getPlayer(playerId);
	}

	private Player encapsulatePlayer(Map<String, Object> map) {
		if (map != null) {
			long id = ((BigInteger) map.get("id")).longValue();
			int level = ((Long) map.get("level")).intValue();
			int exp = ((Long) map.get("exp")).intValue();

			Player player = new Player(id, exp, level);
			return player;
		} else {
			return null;
		}
	}

	/**
	 * 从数据库中读取玩家信息
	 */
	private Player readPlayerFromDB(Account account) throws SQLException {

		Player player = null;
		PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
		Map<String, Object> playerMap = playerDao.readPlayer(account.getPlayerId());
		DaoFactory.getInstance().returnPlayerDao(playerDao);
		if (playerMap != null) {

			player = encapsulatePlayer(playerMap);

			player.setAccount(account);

		}

		return player;
	}

	/**
	 * 从数据库中读取玩家信息
	 */
	private Player readPlayerFromDB(long playerId) throws SQLException {

		Account account = Root.accountSystem.getAccountFromDB(playerId);

		if (account != null) {
			PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
			Map<String, Object> playerMap = playerDao.readPlayer(account.getPlayerId());
			DaoFactory.getInstance().returnPlayerDao(playerDao);
			Player player = encapsulatePlayer(playerMap);
			if (player != null) {
				player.setAccount(account);
			}
			return player;
		} else {
			return null;
		}

	}

}
