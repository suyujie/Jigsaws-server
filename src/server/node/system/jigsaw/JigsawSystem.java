package server.node.system.jigsaw;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import common.qcloud.cosapi.CosCloudUtil;
import gamecore.system.AbstractSystem;
import gamecore.task.TaskCenter;
import gamecore.util.Utils;
import server.node.dao.DaoFactory;
import server.node.dao.JigsawDao;
import server.node.system.RedisHelperJson;
import server.node.system.Root;
import server.node.system.player.Player;

public class JigsawSystem extends AbstractSystem {

	private static final Logger logger = LogManager.getLogger(JigsawSystem.class.getName());

	// 信息放入是个缓存key中
	private static final int imageIdCacheTagMaxNum = 10;

	private static Queue<Long> deleteingJigsaws = new ArrayBlockingQueue<Long>(10000000);

	@Override
	public boolean startup() {
		System.out.println("JigsawSystem start..");

		// 加载官方Jigsaw信息
		JigsawLoadData.getInstance().readData();

		// 初始化玩家的Jigsaw
		TaskCenter.getInstance().schedule(new LoadJigsaw2WaitingList(), 0, TimeUnit.SECONDS);

		// 处理要删除的Jigsaw
		TaskCenter.getInstance().scheduleWithFixedDelay(new LoadJigsaw2WaitingList(), 0, 10, TimeUnit.SECONDS);

		System.out.println("JigsawSystem start..ok");

		return true;
	}

	@Override
	public void shutdown() {
	}

	public Jigsaw getJigsaw(Long id) {

		Jigsaw jigsaw = RedisHelperJson.getJigsaw(id);
		if (jigsaw == null) {
			jigsaw = readFromDB(id);
			jigsaw.synchronize();
		}

		return jigsaw;

	}

	private Jigsaw readFromDB(Long id) {
		Jigsaw gameImage = null;
		JigsawDao dao = DaoFactory.getInstance().borrowJigsawDao();
		Map<String, Object> map = dao.read(id);
		DaoFactory.getInstance().returnJigsawDao(dao);
		if (map != null) {
			gameImage = encapsulateJigsaw(map);
		}
		return gameImage;
	}

	public Jigsaw readJigsaw(Player player) {

		int index = 0;
		Jigsaw gameImage = null;

		// 玩家玩过的最近的几个
		PlayedJigsawBag playedJigsawBag = getPlayedJigsawBag(player);

		while (gameImage == null) {

			// 从缓存中一次读取20个
			List<Long> ids = RedisHelperJson.getWaitJigsawIdSet(Utils.randomInt(0, imageIdCacheTagMaxNum), 20);
			for (Long id : ids) {
				if (id != null) {
					// 检查是否最近玩过这个
					if (!playedJigsawBag.contains(id)) {// 不包含，最近玩过了
						gameImage = RedisHelperJson.getJigsaw(id);
						if (gameImage != null) {
							return gameImage;
						}
					}
				}
			}

			if (index++ > 10) {
				break;
			}
		}

		return gameImage;

	}

	private PlayedJigsawBag getPlayedJigsawBag(Player player) {
		// 玩家玩过的最近的几个
		PlayedJigsawBag playedJigsawBag = RedisHelperJson.getPlayedJigsawBag(player.getId());
		if (playedJigsawBag == null) {
			playedJigsawBag = new PlayedJigsawBag(player.getId());
			playedJigsawBag.synchronize();
		}
		return playedJigsawBag;
	}

	public void playedJigsaw(Player player, Jigsaw jigsaw) {

		PlayedJigsawBag playedJigsawBag = RedisHelperJson.getPlayedJigsawBag(player.getId());
		playedJigsawBag.addPlayedId(jigsaw.getId());
		playedJigsawBag.synchronize();

	}

	public void playedJigsaw(Player player, Long jigsawId) {

		PlayedJigsawBag playedJigsawBag = RedisHelperJson.getPlayedJigsawBag(player.getId());
		playedJigsawBag.addPlayedId(jigsawId);
		playedJigsawBag.synchronize();

	}

	public Jigsaw readJigsaw_guanfang(Player player) {

		int index = 0;
		Jigsaw gameImage = null;

		PlayedJigsawBag playedJigsawBag = getPlayedJigsawBag(player);

		while (gameImage == null) {

			// 从缓存中一次读取20个
			List<Long> ids = JigsawLoadData.getInstance().readRandomList(20);

			Collections.shuffle(ids);

			for (Long id : ids) {

				if (id != null) {

					// 检查是否最近玩过这个
					if (!playedJigsawBag.contains(id)) {// 不包含，最近玩过了
						gameImage = JigsawLoadData.getInstance().readGameImage(id);
						if (gameImage != null) {
							return gameImage;
						}
					}

				}

			}

			if (index++ > 10) {
				break;
			}
		}

		return gameImage;

	}

	private void addWaitJigsawIdSet(Long id) {
		RedisHelperJson.addWaitImageIdSet(Utils.randomInt(0, imageIdCacheTagMaxNum), id);
	}

	private Jigsaw encapsulateJigsaw(Map<String, Object> map) {
		if (map != null) {
			long id = ((BigInteger) map.get("id")).longValue();
			long playerId = ((BigInteger) map.get("player_id")).longValue();
			String url = (String) map.get("url");
			String bucketName = (String) map.get("bucket_name");
			int good = ((Long) map.get("good")).intValue();
			int bad = ((Long) map.get("bad")).intValue();
			JigsawState state = JigsawState.asEnum(((Long) map.get("state")).intValue());

			Jigsaw gameImage = new Jigsaw(id, playerId, url, bucketName, good, bad, state);
			return gameImage;
		} else {
			return null;
		}
	}

	public void uploadJigsaw(Player player, byte[] bytes) {

		Long id = Root.idsSystem.takeId();

		String bucketName = CosCloudUtil.readBucketName();

		Jigsaw gi = new Jigsaw(id, player.getId(), null, bucketName, 0, 0, JigsawState.ENABLE);

		// 腾讯云 上传
		String result = CosCloudUtil.updateFile(bucketName, id.toString(), bytes);

		System.out.println("===================");
		System.out.println(result);
		System.out.println("===================");

		if (result != null) {
			JSONObject jsonObject = JSONObject.parseObject(result);

			boolean success = jsonObject.getInteger("code") == 0;
			if (success) {
				String access_url = jsonObject.getJSONObject("data").getString("access_url");

				gi.synchronize();

				addWaitJigsawIdSet(id);

				saveDB(gi);
			}
		}

	}

	private void deleteJigsaw(Long jigsawId) {
		synchronized (deleteingJigsaws) {
			if (!deleteingJigsaws.contains(jigsawId)) {
				deleteingJigsaws.add(jigsawId);
			}
		}
	}

	public void updateDB(Jigsaw jigsaw) {
		JigsawDao dao = DaoFactory.getInstance().borrowJigsawDao();
		dao.update(jigsaw);
		DaoFactory.getInstance().returnJigsawDao(dao);
	}

	public void saveDB(Jigsaw jigsaw) {
		JigsawDao dao = DaoFactory.getInstance().borrowJigsawDao();
		dao.save(jigsaw);
		DaoFactory.getInstance().returnJigsawDao(dao);
	}

	/**
	 * 守护任务。 内部线程类,从db中载入jigsaw进入可玩列表
	 */
	protected class LoadJigsaw2WaitingList implements Runnable {

		protected LoadJigsaw2WaitingList() {
		}

		int loadNumEveryTime = 100;

		@Override
		public void run() {

			int index = 0;

			while (true) {
				JigsawDao dao = DaoFactory.getInstance().borrowJigsawDao();
				List<Map<String, Object>> list = dao.read(index * loadNumEveryTime, loadNumEveryTime);
				DaoFactory.getInstance().returnJigsawDao(dao);

				if (list != null) {
					for (Map<String, Object> map : list) {
						Jigsaw gameImage = encapsulateJigsaw(map);
						gameImage.synchronize();
						addWaitJigsawIdSet(gameImage.getId());
					}
				}

				if (list == null || list.size() < loadNumEveryTime) {
					break;
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				index++;

			}

		}
	}

}
