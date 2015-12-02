package server.node.system.gameImage;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import common.microsoft.azure.AzureStorage;
import common.microsoft.azure.AzureStorageBean;
import common.qcloud.cosapi.CosCloudUtil;
import gamecore.system.AbstractSystem;
import gamecore.system.SystemResult;
import gamecore.util.Utils;
import server.node.dao.DaoFactory;
import server.node.dao.ImageDao;
import server.node.system.RedisHelperJson;
import server.node.system.Root;
import server.node.system.StorageManager;
import server.node.system.player.Player;

public class GameImageSystem extends AbstractSystem {

	private static final Logger logger = LogManager.getLogger(GameImageSystem.class.getName());

	private static final int imageIdCacheTagMaxNum = 10;

	@Override
	public boolean startup() {
		System.out.println("GameImageSystem start..");

		initImageIds();

		System.out.println("GameImageSystem start..ok");

		return true;
	}

	@Override
	public void shutdown() {
	}

	public GameImage getGameImage(Long id) {

		GameImage gameImage = RedisHelperJson.getGameImage(id);
		if (gameImage == null) {
			gameImage = readFromDB(id);
		}

		return gameImage;

	}

	public GameImage readFromDB(Long id) {
		GameImage gameImage = null;
		ImageDao dao = DaoFactory.getInstance().borrowImageDao();
		Map<String, Object> map = dao.readImage(id);
		DaoFactory.getInstance().returnImageDao(dao);
		if (map != null) {
			gameImage = encapsulateGameImage(map);
		}
		return gameImage;
	}

	public GameImage readGameImage() {

		int index = 0;
		GameImage gameImage = null;

		while (gameImage == null) {
			Long id = RedisHelperJson.getWaitImageIdSet(Utils.randomInt(0, imageIdCacheTagMaxNum));
			if (id != null) {
				gameImage = RedisHelperJson.getGameImage(id);
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
	public void initImageIds() {

		ImageDao dao = DaoFactory.getInstance().borrowImageDao();
		List<Map<String, Object>> list = dao.readImages();
		DaoFactory.getInstance().returnImageDao(dao);

		for (Map<String, Object> map : list) {
			GameImage gameImage = encapsulateGameImage(map);
			gameImage.synchronize();
			addWaitImageIdSet(gameImage.getId());
		}

	}

	private GameImage encapsulateGameImage(Map<String, Object> map) {
		if (map != null) {
			long id = ((BigInteger) map.get("id")).longValue();
			long playerId = ((BigInteger) map.get("player_id")).longValue();
			String url = (String) map.get("url");
			int good = ((Long) map.get("good")).intValue();
			int bad = ((Long) map.get("bad")).intValue();
			GameImage gameImage = new GameImage(id, playerId, url, good, bad);
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
			String access_url = jsonObject.getString("access_url");

			GameImage gi = new GameImage(id, player.getId(), access_url, 0, 0);

			gi.synchronize();

			addWaitImageIdSet(id);

			saveDB(gi);
		}

	}

	public SystemResult commentImage(Player player, Long imageId, Integer comment) {

		SystemResult result = new SystemResult();

		GameImage gi = getGameImage(imageId);

		if (gi == null) {
			result.setCode(gamecore.system.ErrorCode.PARAM_ERROR);
			return result;
		}

		if (comment == 1) {
			gi.setGood(gi.getGood() + 1);
		} else {
			gi.setBad(gi.getBad() + 1);
		}

		gi.synchronize();

		updateDB(gi);

		return result;

	}

	private void updateDB(GameImage gameImage) {
		ImageDao dao = DaoFactory.getInstance().borrowImageDao();
		dao.updateImage(gameImage);
		DaoFactory.getInstance().returnImageDao(dao);
	}

	private void saveDB(GameImage gameImage) {
		ImageDao dao = DaoFactory.getInstance().borrowImageDao();
		dao.saveImage(gameImage);
		DaoFactory.getInstance().returnImageDao(dao);
	}

}
