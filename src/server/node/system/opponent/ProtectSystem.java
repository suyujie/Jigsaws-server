package server.node.system.opponent;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.system.AbstractSystem;
import gamecore.system.SystemResult;
import gamecore.task.TaskCenter;
import gamecore.util.Clock;
import gamecore.util.Utils;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.dao.PlayerDao;
import server.node.system.Content;
import server.node.system.Root;
import server.node.system.player.Player;

/**
 * 护盾系统。
 */
public final class ProtectSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(ProtectSystem.class.getName());

	//在本服务器上 被打  加上护盾的玩家id,key:护盾到期时间,value:玩家id list,,,这个只处理到期删除
	private Queue<Long> protectPlayerIds = null;

	//两个触发保护的条件
	private static int partNumProtect = 5;//保护触发的数量
	private static float cashProtect = 0.7F;//保护触发的cash的临界值(关卡日产出的70%)

	@Override
	public boolean startup() {
		System.out.println("ProtectSystem start....");

		protectPlayerIds = new ArrayBlockingQueue<Long>(10000000);
		initProtectedPlayer();
		logger.info("protected player num : " + protectPlayerIds.size());
		TaskCenter.getInstance().scheduleAtFixedRate(new CheckProtectTimeOut(), Utils.randomInt(1, 20), 60, TimeUnit.SECONDS);
		System.out.println("ProtectSystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {
	}

	public void initProtectedPlayer() {
		PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
		List<Map<String, Object>> list = playerDao.readPlayerProtected();
		DaoFactory.getInstance().returnPlayerDao(playerDao);

		if (list != null) {
			for (Map<String, Object> map : list) {
				if (map != null) {
					Long id = ((BigInteger) map.get("id")).longValue();
					addProtectQueue(id);
				}
			}
		}
	}

	/**
	 * 检查是否加入护盾
	 */
	public SystemResult checkProtect(Player player, int losePartNum, int loseCash) {

		SystemResult result = new SystemResult();

		PvpLoseBag pvpLoseBag = RedisHelperJson.getPvpLoseBag(player.getId());

		if (pvpLoseBag == null) {
			pvpLoseBag = new PvpLoseBag(player.getId());
		}

		pvpLoseBag.setCash(pvpLoseBag.getCash() + loseCash);
		pvpLoseBag.setPartNum(pvpLoseBag.getPartNum() + losePartNum);

		boolean protect = false;

		//check部件数量
		if (pvpLoseBag.getPartNum() >= partNumProtect) {
			protect = true;
		} else {//check cash数量
			int gainCashPerHour = Root.missionSystem.gainCashPerHour(player);
			if (pvpLoseBag.getCash() >= gainCashPerHour * 24 * cashProtect) {
				protect = true;
			}
		}

		//满足条件 加入保护
		if (protect) {
			//删除缓存
			RedisHelperJson.removePvpLoseBag(player.getId());
			//加入保护
			addProtect(player);
		} else {//未满足条件,更新缓存
			pvpLoseBag.synchronize();
		}

		return result;
	}

	/**
	 * 加入护盾
	 */
	private SystemResult addProtect(Player player) {

		SystemResult result = new SystemResult();

		player.setProtectEndTime(Clock.currentTimeSecond() + Content.protectTime);

		player.synchronize();

		PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
		playerDao.updateProtectTime(player);
		DaoFactory.getInstance().returnPlayerDao(playerDao);

		addProtectQueue(player.getId());

		return result;
	}

	private void addProtectQueue(Long playerId) {
		synchronized (protectPlayerIds) {
			if (!protectPlayerIds.contains(playerId)) {
				protectPlayerIds.add(playerId);
			}
		}
	}

	/**
	 * 护盾超时
	 */
	public SystemResult protectTimeOut(Player player) {

		if (logger.isDebugEnabled()) {
			logger.debug("protectTimeOut : " + player.getId());
		}

		SystemResult result = new SystemResult();

		player.setProtectEndTime(0);

		PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
		playerDao.updateProtectTime(player);
		DaoFactory.getInstance().returnPlayerDao(playerDao);

		//加入对手列表
		if (!player.checkOnLine()) {//不在线
			Root.opponentSystem.addOpponent(player.getPlayerStatistics().getCupNum(), player);
		}

		return result;
	}

	/**
	 * 攻击了别人
	 * 取消护盾
	 */
	public SystemResult cancelProtect(Player player) {

		if (logger.isDebugEnabled()) {
			logger.debug("cancelProtect : " + player.getId());
		}

		SystemResult result = new SystemResult();

		player.setProtectEndTime(0);

		player.synchronize();

		PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
		playerDao.updateProtectTime(player);
		DaoFactory.getInstance().returnPlayerDao(playerDao);

		// protectPlayerIds 到期会自己从列表中移除的

		//加入对手列表
		Root.opponentSystem.addOpponent(player.getPlayerStatistics().getCupNum(), player);

		return result;
	}

	/**
	 * 守护任务。
	 * 内部线程类,过滤玩家
	 */
	protected class CheckProtectTimeOut implements Runnable {

		private Logger logger2 = LogManager.getLogger(ProtectSystem.class.getName());

		protected CheckProtectTimeOut() {
		}

		@Override
		public void run() {

			int indexNum = 0;

			try {
				while (protectPlayerIds != null && !protectPlayerIds.isEmpty() && indexNum <= 200) {
					indexNum++;
					Long pid = protectPlayerIds.peek();//获取第一个元素,但是不移除
					if (pid != null) {
						Player player = Root.playerSystem.getPlayer(pid);

						synchronized (protectPlayerIds) {
							if (player == null) {
								protectPlayerIds.remove(pid);//移除
							} else {
								if (player.checkProtect()) {//检查是否保护,还有护盾
									break;//终止此次检查,因为后面的肯定还没到时间
								} else {//护盾取消了
									protectPlayerIds.remove(pid);//移除
									protectTimeOut(player);//保护到期,去掉
								}
							}
						}
					}
				}
			} catch (Exception e) {
				logger2.error(e.getMessage());
			}

		}
	}

}
