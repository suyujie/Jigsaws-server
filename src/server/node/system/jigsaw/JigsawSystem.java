package server.node.system.jigsaw;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import common.qcloud.cosapi.CosCloudUtil;
import gamecore.system.AbstractSystem;
import gamecore.system.SystemResult;
import gamecore.util.Utils;
import server.node.dao.DaoFactory;
import server.node.dao.ImageDao;
import server.node.system.RedisHelperJson;
import server.node.system.Root;
import server.node.system.player.Player;

public class JigsawSystem extends AbstractSystem {

	private static final Logger logger = LogManager.getLogger(JigsawSystem.class.getName());

	// 信息放入是个缓存key中
	private static final int imageIdCacheTagMaxNum = 10;

	@Override
	public boolean startup() {
		System.out.println("GameImageSystem start..");

		// 加载官方图片信息
		JigsawLoadData.getInstance().readData();

		// 初始化玩家图片
		initImages();

		System.out.println("GameImageSystem start..ok");

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
		ImageDao dao = DaoFactory.getInstance().borrowImageDao();
		Map<String, Object> map = dao.readImage(id);
		DaoFactory.getInstance().returnImageDao(dao);
		if (map != null) {
			gameImage = encapsulateJigsaw(map);
		}
		return gameImage;
	}

	public Jigsaw readJigsaw(Player player) {

		int index = 0;
		Jigsaw gameImage = null;

		// 玩家玩过的最近的几个
		PlayedJigsawBag playedJigsawBag = RedisHelperJson.getPlayedJigsawBag(player.getId());

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

	public Jigsaw readJigsaw_guanfang(Player player) {

		int index = 0;
		Jigsaw gameImage = null;

		// 玩家玩过的最近的几个
		PlayedJigsawBag playedJigsawBag = RedisHelperJson.getPlayedJigsawBag(player.getId());

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

	private void addWaitImageIdSet(Long id) {
		RedisHelperJson.addWaitImageIdSet(Utils.randomInt(0, imageIdCacheTagMaxNum), id);
	}

	/**
	 * 初始化图片数据，放入缓存
	 */
	public void initImages() {

		ImageDao dao = DaoFactory.getInstance().borrowImageDao();
		List<Map<String, Object>> list = dao.readImages();
		DaoFactory.getInstance().returnImageDao(dao);

		if (list != null) {
			for (Map<String, Object> map : list) {
				Jigsaw gameImage = encapsulateJigsaw(map);
				gameImage.synchronize();
				addWaitImageIdSet(gameImage.getId());
			}
		}

	}

	private Jigsaw encapsulateJigsaw(Map<String, Object> map) {
		if (map != null) {
			long id = ((BigInteger) map.get("id")).longValue();
			long playerId = ((BigInteger) map.get("player_id")).longValue();
			String url = (String) map.get("url");
			int good = ((Long) map.get("good")).intValue();
			int bad = ((Long) map.get("bad")).intValue();
			Jigsaw gameImage = new Jigsaw(id, playerId, url, good, bad);
			return gameImage;
		} else {
			return null;
		}
	}

	public void uploadImage(Player player, byte[] bytes) {

		Long id = Root.idsSystem.takeId();

		// 腾讯云 上传
		String result = CosCloudUtil.updateFile(id.toString(), bytes);

		if (result != null) {
			JSONObject jsonObject = JSONObject.parseObject(result);

			boolean success = jsonObject.getInteger("code") == 0;
			if (success) {
				String access_url = jsonObject.getJSONObject("data").getString("access_url");

				Jigsaw gi = new Jigsaw(id, player.getId(), access_url, 0, 0);

				gi.synchronize();

				addWaitImageIdSet(id);

				saveDB(gi);
			}
		}

	}

	public void updateDB(Jigsaw jigsaw) {
		ImageDao dao = DaoFactory.getInstance().borrowImageDao();
		dao.updateImage(jigsaw);
		DaoFactory.getInstance().returnImageDao(dao);
	}

	public void saveDB(Jigsaw jigsaw) {
		ImageDao dao = DaoFactory.getInstance().borrowImageDao();
		dao.saveImage(jigsaw);
		DaoFactory.getInstance().returnImageDao(dao);
	}

}
