package server.node.system.player;

import gamecore.system.AbstractSystem;
import gamecore.system.SystemResult;
import gamecore.util.Utils;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.dao.DaoFactory;
import server.node.dao.PlayerDao;
import server.node.system.Content;
import server.node.system.RedisHelperJson;
import server.node.system.Root;
import server.node.system.account.Account;
import common.language.LangType;

/**
 * 人物系统。
 */
public final class PlayerSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(PlayerSystem.class.getName());

	public PlayerSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("PlayerSystem start....");
		// 读取制作数据
		boolean b = PlayerLoadData.getInstance().readMakingData();

		System.out.println("PlayerSystem start....OK");

		return b;
	}

	@Override
	public void shutdown() {
	}

	/**
	 * 读取玩家信息
	 * 
	 * @param idInPlat
	 * @param plat
	 * @return
	 */
	public void resetSecurity(Player player, boolean sync) {
		// 重置密码 5位数
		byte[] security = new byte[5];
		for (int i = 0; i < security.length; i++) {
			security[i] = (byte) Utils.randomInt(0, 9);
		}
		player.setSecurity(security);

		if (sync) {
			player.synchronize();
		}

	}

	/**
	 * 注册玩家
	 */
	public Player register(String mobileId, String channel, String device, boolean syncPlayer) {

		Long id = Root.idsSystem.takePlayerId();

		// id 截取后8位 暂时做name
		Account account = new Account(mobileId, 1, "local", null, (id % 100000000) + "", id, channel, device);
		Root.accountSystem.saveAccount(account);

		Player player = new Player(id, 0, 0, 0, 1, 0, null, 1, 0, 0, LangType.en_US,
				new PlayerStatistics(Content.DefaultCup, 0, 0, 0, 0));
		player.setAccount(account);

		// 初始化给cash gold
		Root.playerSystem.changeCash(player, Content.DefaultCash, CashType.INIT_GIVE, false);
		Root.playerSystem.changeGold(player, Content.DefaultGold, GoldType.INIT_GIVE, false);

		if (syncPlayer) {
			player.synchronize();
		}

		PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
		playerDao.savePlayer(player);
		DaoFactory.getInstance().returnPlayerDao(playerDao);

		// 发布注册消息
		PlayerMessage playerMessage = new PlayerMessage(PlayerMessage.NewPlayer, player);
		this.publish(playerMessage);

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
		} else {
			// 从缓存中得到player,查看account是否一致,不一致的话,说明两部手机同事登陆了,踢掉之前的.
			if (player.getAccount() != null && !player.getAccount().getMobileId().equals(account.getMobileId())) {
				// 踢掉以前的
				Root.sessionSystem.removeSession(player.getAccount().getMobileId());
			}
			player.setAccount(account);
		}
		if (player != null && player.getOnLine() == 0) {
			player.setOnLine(1);

			player.synchronize();

			logger.debug("login  " + player.getAccount().getMobileId());

			// 发布登陆消息
			this.publish(new PlayerMessage(PlayerMessage.SignIn, player));
		}

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
			int gold = ((Long) map.get("gold")).intValue();
			long cash = ((BigInteger) map.get("cash")).longValue();
			int cupNum = ((Long) map.get("cup_num")).intValue();
			int pvpAttackWinCount = ((Long) map.get("pvp_attack_win_count")).intValue();// 进攻胜利
			int pvpDefenceWinCount = ((Long) map.get("pvp_defence_win_count")).intValue();// 防守胜利
			int pvpBeatRobotCount = ((Long) map.get("pvp_beat_robot_count")).intValue();// 击败机器人数量
			int letCount = ((Long) map.get("let_count")).intValue();// 出租成功次数
			String pushUri = (String) map.get("push_uri");
			Long protectEndTime = ((BigInteger) map.get("protect_end_time")).longValue();
			int onLine = ((Long) map.get("online")).intValue();
			int haveImg = ((Integer) map.get("have_img")).intValue();
			LangType lang = LangType.asEnum((String) map.get("lang"));
			Long onLineTime = ((BigInteger) map.get("online_time")).longValue();

			PlayerStatistics playerStatistics = new PlayerStatistics(cupNum, pvpAttackWinCount, pvpDefenceWinCount,
					pvpBeatRobotCount, letCount);

			Player player = new Player(id, gold, cash, exp, level, protectEndTime, pushUri, onLine, onLineTime, haveImg,
					lang, playerStatistics);
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

	/**
	 * 语言
	 */
	public void updateLang(Player player, LangType lang) {
		if (player.getLang() == null || !player.getLang().equals(lang)) {
			player.setLang(lang);

			player.synchronize();

			PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
			playerDao.updateLang(player);
			DaoFactory.getInstance().returnPlayerDao(playerDao);
		}
	}

	/**
	 * cash 增加
	 */
	public SystemResult changeCash(Player player, int cash, CashType cashType, boolean sync) {

		SystemResult result = new SystemResult();

		if (cash != 0) {

			long oldCash = player.getCash();

			player.setCash(oldCash + cash);

			if (player.getCash() < 0) {
				player.setCash(0);
			}

			if (sync) {
				player.synchronize();
			}

			PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
			playerDao.updatePlayerCash(player);
			DaoFactory.getInstance().returnPlayerDao(playerDao);

			// cash日志
			Root.logSystem.addCashLog(player, oldCash, cash, player.getCash(), cashType.asCode());

			if (cash > 0) {
				CashMessage cashMessage = new CashMessage(CashMessage.CashAdd, player, cash);
				this.publish(cashMessage);
			}

		}

		return result;
	}

	/**
	 * gold 改变
	 */
	public SystemResult changeGold(Player player, int gold, GoldType goldType, boolean sync) {

		SystemResult result = new SystemResult();

		if (gold != 0) {
			int oldGold = player.getGold();
			player.setGold(oldGold + gold);

			if (sync) {
				player.synchronize();
			}

			PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
			playerDao.updatePlayerGold(player);
			DaoFactory.getInstance().returnPlayerDao(playerDao);

			// 钻石 日志
			Root.logSystem.addGoldLog(player, oldGold, gold, player.getGold(), goldType.asCode());
		}

		return result;

	}

	/**
	 * 玩家增加经验,可能导致升级
	 */
	public SystemResult addExp(Player player, int exp, boolean sync) {

		SystemResult result = new SystemResult();

		// 可以用来升级的经验,经验是累加的,而不是下一级需要
		player.setExp(player.getExp() + exp);

		// 升级需要的经验,考虑一次升多级的情况
		while (true) {
			Integer nextLevelNeedExp = PlayerLoadData.getInstance().getNeedExp(player.getLevel());
			if (nextLevelNeedExp != null) {
				if (player.getExp() >= nextLevelNeedExp) {// 可以升级
					player.setLevel(player.getLevel() + 1);// 升级

					// 发送升级消息
					PlayerMessage playerMessage = new PlayerMessage(PlayerMessage.LEVEL_UP, player,
							player.getLevel() - 1, player.getLevel());
					this.publish(playerMessage);

				} else {// 不够升级的
					break;
				}
			} else {// 满级了,级别不再上升
				break;
			}

		}

		if (sync) {
			player.synchronize();
		}

		// 同步入数据库
		PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
		playerDao.updatePlayerLevelExp(player);
		DaoFactory.getInstance().returnPlayerDao(playerDao);

		return result;
	}

	/**
	 * 战斗统计的增加
	 */
	public SystemResult updateStatistics(Player player, boolean winAsAttacker, boolean winAsDefender, int cup,
			int pvpBeatRobotCount, boolean sync) {

		SystemResult result = new SystemResult();

		if (cup != 0) {
			int oldCupNum = player.getPlayerStatistics().getCupNum();
			int newCupNum = oldCupNum + cup > 0 ? oldCupNum + cup : 1;// cup
																		// 最小是1
			player.getPlayerStatistics().setCupNum(newCupNum);
		}

		if (winAsAttacker) {
			player.getPlayerStatistics().setPvpAttackWinCount(player.getPlayerStatistics().getPvpAttackWinCount() + 1);
		}
		if (winAsDefender) {
			player.getPlayerStatistics()
					.setPvpDefenceWinCount(player.getPlayerStatistics().getPvpDefenceWinCount() + 1);
		}
		if (pvpBeatRobotCount > 0) {
			player.getPlayerStatistics()
					.setPvpBeatRobotCount(player.getPlayerStatistics().getPvpBeatRobotCount() + pvpBeatRobotCount);
		}

		if (sync) {
			player.synchronize();
		}

		PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
		playerDao.updatePlayerStatistics(player);
		DaoFactory.getInstance().returnPlayerDao(playerDao);

		return result;
	}

	/**
	 * 改变推送
	 */
	public SystemResult changePushUri(Player player, int isNotify, String pushUri) {

		SystemResult result = new SystemResult();

		if (isNotify == 0) {
			player.setPushUri(null);
		}
		if (isNotify == 1) {
			player.setPushUri(pushUri);
		}

		player.synchronize();

		PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
		playerDao.updatePushUri(player);
		DaoFactory.getInstance().returnPlayerDao(playerDao);

		return result;
	}

	/**
	 * 添加头像
	 */
	public void updateImg(Player player) {
		if (player.getHaveImg() == 0) {// 更新头像有无

			player.setHaveImg(1);
			player.synchronize();

			PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
			playerDao.updateImg(player);
			DaoFactory.getInstance().returnPlayerDao(playerDao);
		}
	}

}
