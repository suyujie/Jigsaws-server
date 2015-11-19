package server.node.system.gameImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.microsoft.azure.AzureStorage;
import common.microsoft.azure.AzureStorageBean;
import gamecore.system.AbstractSystem;
import server.node.system.Root;
import server.node.system.StorageManager;
import server.node.system.player.Player;

public class GameImageSystem extends AbstractSystem {

	private static final Logger logger = LogManager.getLogger(GameImageSystem.class.getName());

	@Override
	public boolean startup() {
		System.out.println("GameImageSystem start..");

		System.out.println("GameImageSystem start..ok");

		return true;
	}

	@Override
	public void shutdown() {
	}

	public void uploadImage(Player player, byte[] bytes) {

		Long id = Root.idsSystem.takeId();
		GameImage gi = new GameImage(id, player.getId());

		gi.synchronize();

		// Azure上传
		uploadAzure2Storage(id.toString(), bytes);
	}

	/**
	 * 
	 */
	private boolean uploadAzure2Storage(String filename, byte[] bytes) {
		String blobName = "jigsaws_test";
		StringBuffer sb = new StringBuffer();

		AzureStorageBean storage = StorageManager.getInstance().azureStorages.get(1);
		try {

			// Azure上传
			boolean uploadSuccess = AzureStorage.upload2Storage(storage, blobName, filename, bytes);
			if (uploadSuccess) {
				sb.append(storage.storageAccount).append(" [").append(blobName).append("]").append(" [")
						.append(filename).append("] ok").append("\n");
				return true;
			} else {
				sb.append(storage.storageAccount).append(" [").append(blobName).append("]").append(" [")
						.append(filename).append("] error").append("\n");
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
