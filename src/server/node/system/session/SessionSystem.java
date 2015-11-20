package server.node.system.session;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gamecore.system.AbstractSystem;
import gamecore.util.Clock;
import server.node.system.ConfigManager;
import server.node.system.RedisHelperJson;
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

		System.out.println("SessionSystem start....OK");

		return true;
	}

	@Override
	public void shutdown() {
	}

	public Session getSession(String sessionId) {
		return RedisHelperJson.getSession(sessionId);
	}

	// 更新保存session
	public void updateOrSaveSession(Player player, String sessionId) {
		if (player != null) {
			Session session = getSession(sessionId);
			if (session == null) {
				session = new Session(ConfigManager.getInstance().tag, sessionId, player.getId(),
						Clock.currentTimeSecond());
			} else {
				session.setActiveT(Clock.currentTimeSecond());
			}
			session.synchronize();
		}
	}

	// 更新保存session
	public void updateOrSaveSession(Session session) {
		if (session != null) {
			session.setActiveT(Clock.currentTimeSecond());
			session.synchronize();
		}
	}

	// 删除session
	public void removeSession(String mobileId) {
		RedisHelperJson.removeSession(mobileId);
	}

	// 有玩家登录了
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

	// 玩家掉线
	public void signOut(Player player, Long playerId) {
		if (player == null) {
			player = Root.playerSystem.getPlayerFromCache(playerId);
		}
		if (player != null) {

			player.synchronize();

			SessionMessage sessionMessage = new SessionMessage(SessionMessage.SignOut, player);
			this.publish(sessionMessage);

		}
	}

	/**
	 * 守护任务。 内部线程类,过滤玩家
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
						if (player == null || !player.checkOnLine()) {// 已经不在缓存里了,或者的确掉线,说明已经掉线
							signOut(player, pid);// 掉线处理
						} else {
							// 没掉线,放入临时set
							tempOnlineId.add(pid);
						}
					}
				}
				// 没掉线,放入临时set,放入队尾
				for (Long id : tempOnlineId) {
					if (!onLinePlayer.contains(id)) {
						onLinePlayer.addLast(id);
					}
				}

			}

		}
	}

}
