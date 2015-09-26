package server.node.system.session;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.system.AbstractSystem;
import gamecore.task.TaskCenter;
import gamecore.util.Clock;
import gamecore.util.Utils;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.dao.PlayerDao;
import server.node.system.ConfigManager;
import server.node.system.Root;
import server.node.system.player.Player;

/**
 * session会话系统。
 */
public final class SessionSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(SessionSystem.class.getName());

	// 登录过的玩家id列表
	private LinkedList<Long> onLinePlayer;

	public SessionSystem() {
	}

	@Override
	public boolean startup() {

		System.out.println("SessionSystem start....");

		onLinePlayer = new LinkedList<Long>();
		initOnlinePlayer();
		logger.info("      online player num : " + onLinePlayer.size());
		TaskCenter.getInstance().scheduleAtFixedRate(new CheckSignOut(), Utils.randomInt(1, 60), 10, TimeUnit.SECONDS);

		System.out.println("SessionSystem start....OK");

		return true;
	}

	@Override
	public void shutdown() {
	}

	public Session getSession(String mobileId) {
		return RedisHelperJson.getSession(mobileId);
	}

	public void initOnlinePlayer() {
		PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
		List<Map<String, Object>> list = playerDao.readPlayerOnline();
		DaoFactory.getInstance().returnPlayerDao(playerDao);

		if (list != null) {
			for (Map<String, Object> map : list) {
				if (map != null) {
					Long id = ((BigInteger) map.get("id")).longValue();
					saveOnlinePlayerList(id);
				}
			}
		}
	}

	//更新保存session
	public void updateOrSaveSession(Player player) {
		if (player != null) {
			Session session = getSession(player.getAccount().getMobileId());
			if (session == null) {
				session = new Session(ConfigManager.getInstance().tag, player.getAccount().getMobileId(), player.getId(), Clock.currentTimeSecond());
			} else {
				session.setActiveT(Clock.currentTimeSecond());
			}
			session.synchronize();
		}
	}

	//更新保存session
	public void updateOrSaveSession(Session session) {
		if (session != null) {
			session.setActiveT(Clock.currentTimeSecond());
			session.synchronize();
		}
	}

	//删除session
	public void removeSession(String mobileId) {
		RedisHelperJson.removeSession(mobileId);
	}

	//有玩家登录了
	public void saveOnlinePlayerList(Player player) {
		if (player != null) {
			saveOnlinePlayerList(player.getId());
		}
	}

	private void saveOnlinePlayerList(Long playerId) {
		if (playerId != null) {
			synchronized (onLinePlayer) {
				if (!onLinePlayer.contains(playerId)) {
					onLinePlayer.addLast(playerId);
				}
			}
		}
	}

	/**
	 * 登录
	 */
	public void signIn(Player player) {

		player.setOnLine(1);
		//	player.setLastSignT(Clock.currentTimeSecond());
		player.synchronize();

		PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
		playerDao.updateOnline(player);
		DaoFactory.getInstance().returnPlayerDao(playerDao);
	}

	//玩家掉线
	public void signOut(Player player, Long playerId) {
		if (player == null) {
			player = Root.playerSystem.getPlayerFromCache(playerId);
		}
		if (player != null) {

			player.setOnLine(0);
			if (player.getLastSignT() != null) {
				player.setOnLineTime(player.getOnLineTime() + Clock.currentTimeSecond() - player.getLastSignT());
				player.setLastSignT(null);
			}

			PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
			playerDao.updateOnline(player);
			DaoFactory.getInstance().returnPlayerDao(playerDao);

			player.synchronize();

			SessionMessage sessionMessage = new SessionMessage(SessionMessage.SignOut, player);
			this.publish(sessionMessage);

		}
	}

	/**
	 * 守护任务。
	 * 内部线程类,过滤玩家
	 */
	protected class CheckSignOut implements Runnable {

		@Override
		public void run() {

			synchronized (onLinePlayer) {
				int indexNum = 0;

				Set<Long> tempOnlineId = new HashSet<Long>();

				while (!onLinePlayer.isEmpty() && indexNum < 200) {
					indexNum++;
					Long pid = onLinePlayer.removeFirst();
					if (pid != null) {
						Player player = Root.playerSystem.getPlayerFromCache(pid);
						if (player == null || !player.checkOnLine()) {//已经不在缓存里了,或者的确掉线,说明已经掉线
							signOut(player, pid);//掉线处理
						} else {
							//没掉线,放入临时set
							tempOnlineId.add(pid);
						}
					}
				}
				//没掉线,放入临时set,放入队尾
				for (Long id : tempOnlineId) {
					if (!onLinePlayer.contains(id)) {
						onLinePlayer.addLast(id);
					}
				}

			}

		}
	}

}
