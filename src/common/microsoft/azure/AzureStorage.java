package common.microsoft.azure;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.windowsazure.storage.CloudStorageAccount;
import com.microsoft.windowsazure.storage.blob.CloudBlobClient;
import com.microsoft.windowsazure.storage.blob.CloudBlobContainer;

public class AzureStorage {
	
	private static Logger logger = LogManager.getLogger(AzureStorage.class.getName());

	public static CloudBlobContainer createCloudBlobContainer(AzureStorageBean storage, String blobName) {

		String storageConnectionString = "DefaultEndpointsProtocol=http;AccountName=" + storage.storageAccount + ";AccountKey=" + storage.storageAccountKey + ";BlobEndpoint="
				+ storage.storageConnectionUri;

		CloudStorageAccount storageAccount;
		try {
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient.getContainerReference(blobName);
			container.createIfNotExists();
			return container;
		} catch (Exception e) {
			logger.error(e);
		}

		return null;

	}

}
