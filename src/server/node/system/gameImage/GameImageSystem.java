package server.node.system.gameImage;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.microsoft.azure.AzureStorage;
import common.microsoft.azure.AzureStorageBean;
import gamecore.system.AbstractSystem;
import gamecore.util.Utils;
import server.node.dao.DaoFactory;
import server.node.dao.ImageDao;
import server.node.system.RedisHelperJson;
import server.node.system.Root;
import server.node.system.StorageManager;
import server.node.system.player.Player;

public class GameImageSystem extends AbstractSystem {

	private static final Logger logger = LogManager.getLogger(GameImageSystem.class.getName());

	private List<Long> gameImages = new ArrayList<Long>();

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

	public GameImage readGameImage() {

		GameImage gameImage = null;

		while (gameImage == null) {
			Long id = Utils.randomSelectOne(gameImages);
			gameImage = RedisHelperJson.getGameImage(id);
		}

		return gameImage;

	}

	public void initImageIds() {

		ImageDao dao = DaoFactory.getInstance().borrowImageDao();
		List<Map<String, Object>> list = dao.readImages();
		DaoFactory.getInstance().returnImageDao(dao);

		for (Map<String, Object> map : list) {
			GameImage gameImage = encapsulateGameImage(map);
			gameImage.synchronize();
			gameImages.add(gameImage.getId());
		}

	}

	private GameImage encapsulateGameImage(Map<String, Object> map) {
		if (map != null) {
			long id = ((BigInteger) map.get("id")).longValue();
			long playerId = ((BigInteger) map.get("player_id")).longValue();
			GameImage gameImage = new GameImage(id, playerId);
			return gameImage;
		} else {
			return null;
		}
	}

	public void uploadImage(Player player, byte[] bytes) {

		Long id = Root.idsSystem.takeId();
		GameImage gi = new GameImage(id, player.getId());

		gi.synchronize();

		logger.info("--id " + id + "    " + gi.getPlayerId());
		logger.info("uploading.... -->  azure storage");

		// Azure上传
		uploadAzure2Storage(id.toString(), bytes);
		logger.info("uploaded      -->  azure storage");
	}

	/**
	 * 
	 */
	private boolean uploadAzure2Storage(String filename, byte[] bytes) {
		String blobName = "jigsawstest";
		StringBuffer sb = new StringBuffer();

		AzureStorageBean storage = StorageManager.getInstance().azureStorages.get(1);
		try {

			// Azure上传
			boolean uploadSuccess = AzureStorage.upload2Storage(storage, blobName, filename, bytes);
			if (uploadSuccess) {
				logger.info(sb.append(storage.storageAccount).append(" [").append(blobName).append("]").append(" [")
						.append(filename).append("] ok").append("\n").toString());
				return true;
			} else {
				logger.info(sb.append(storage.storageAccount).append(" [").append(blobName).append("]").append(" [")
						.append(filename).append("] error").append("\n"));
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			sb.append(storage.storageAccount).append(" error").append("\n");
		}

		return false;
	}

	public void commentImage(Player player, Long imageId, Integer comment) {

		logger.debug("--" + player.getId() + "  " + imageId + "  " + comment);

	}

}
