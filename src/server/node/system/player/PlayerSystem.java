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
import server.node.system.evaluate.EvaluateType;
import server.node.system.jigsaw.Jigsaw;

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

	public PlayerStatistics getPlayerStatistics(Player player) throws SQLException {

		if (player.getStatistics() != null) {
			return player.getStatistics();
		} else {
			PlayerStatistics pss = readPlayerStatisticsFromDB(player);
			player.setStatistics(pss);
			player.synchronize();

			return pss;
		}

	}

	public Player getPlayer(Account account, String sessionId) throws SQLException {

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
		this.publish(new PlayerMessage(PlayerMessage.SignIn, player, sessionId));

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

	private PlayerStatistics encapsulatePlayerStatistics(Map<String, Object> map) {
		if (map != null) {
			int gameSuccess = ((Long) map.get("game_success")).intValue();
			int gameFailure = ((Long) map.get("game_failure")).intValue();
			int gameGiveup = ((Long) map.get("game_giveup")).intValue();
			int upLoadNum = ((Long) map.get("upload_num")).intValue();
			int upLoadBeGood = ((Long) map.get("upload_be_good")).intValue();
			int upLoadBeBad = ((Long) map.get("upload_be_bad")).intValue();
			int commentGood = ((Long) map.get("comment_good")).intValue();
			int commentBad = ((Long) map.get("comment_bad")).intValue();

			PlayerStatistics ps = new PlayerStatistics(gameSuccess, gameFailure, gameGiveup, upLoadNum, upLoadBeGood,
					upLoadBeBad, commentGood, commentBad);
			return ps;
		} else {
			return new PlayerStatistics();
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

	private PlayerStatistics readPlayerStatisticsFromDB(Player player) throws SQLException {

		PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
		Map<String, Object> statisticsMap = playerDao.readPlayerStatistics(player.getId());
		DaoFactory.getInstance().returnPlayerDao(playerDao);

		if (statisticsMap != null) {
			return encapsulatePlayerStatistics(statisticsMap);
		} else {
			return initPlayerStatistics(player);
		}

	}

	private PlayerStatistics initPlayerStatistics(Player player) throws SQLException {

		PlayerStatistics pss = new PlayerStatistics(0, 0, 0, 0, 0, 0, 0, 0);

		return pss;
	}

	// 玩游戏的人的统计信息更新
	public void updatePlayerStatisticsAsPlayer(Player player, EvaluateType evaluateType) throws SQLException {

		// 玩拼图的人的统计信息
		PlayerStatistics pss = getPlayerStatistics(player);
		if (evaluateType == EvaluateType.BAD) {
			pss.setCommentBad(pss.getCommentBad() + 1);
		}
		if (evaluateType == EvaluateType.GOOD) {
			pss.setCommentGood(pss.getCommentGood() + 1);
		}
		pss.setGameSuccess(pss.getGameSuccess() + 1);
		player.setStatistics(pss);
		player.synchronize();
		updateStatistics(player);

	}

	// 提供游戏的人的统计信息
	public void updatePlayerStatisticsAsOwner(Long playerId, EvaluateType evaluateType) throws SQLException {

		// 拼图的提供者,官方的话，不统计

		Player ownPlayer = getPlayer(playerId);

		PlayerStatistics opss = getPlayerStatistics(ownPlayer);
		if (evaluateType == EvaluateType.BAD) {
			opss.setUpLoadBeBad(opss.getUpLoadBeBad() + 1);
		}
		if (evaluateType == EvaluateType.GOOD) {
			opss.setUpLoadBeGood(opss.getUpLoadBeGood() + 1);
		}

		ownPlayer.setStatistics(opss);
		ownPlayer.synchronize();
		updateStatistics(ownPlayer);

	}

	public void saveStatistics(Player player) {
		PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
		playerDao.savePlayerStatistics(player.getId(), player.getStatistics());
		DaoFactory.getInstance().returnPlayerDao(playerDao);
	}

	public void updateStatistics(Player player) {
		PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
		playerDao.updatePlayerStatistics(player.getId(), player.getStatistics());
		DaoFactory.getInstance().returnPlayerDao(playerDao);
	}

}
