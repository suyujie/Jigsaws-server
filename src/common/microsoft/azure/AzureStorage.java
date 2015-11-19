package common.microsoft.azure;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.microsoft.windowsazure.storage.CloudStorageAccount;
import com.microsoft.windowsazure.storage.blob.CloudBlobClient;
import com.microsoft.windowsazure.storage.blob.CloudBlobContainer;
import com.microsoft.windowsazure.storage.blob.CloudBlockBlob;

public class AzureStorage {

	public static CloudBlobContainer createCloudBlobContainer(AzureStorageBean storage, String blobName) {

		String storageConnectionString = "DefaultEndpointsProtocol=http;AccountName=" + storage.storageAccount
				+ ";AccountKey=" + storage.storageAccountKey + ";BlobEndpoint=" + storage.storageConnectionUri;

		CloudStorageAccount storageAccount;
		try {
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient.getContainerReference(blobName);
			container.createIfNotExists();
			return container;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	public static boolean upload2Storage(AzureStorageBean storage, String blobName, String fileName, byte[] fileBytes) {

		CloudBlobContainer container = AzureStorage.createCloudBlobContainer(storage, blobName);
		try {
			CloudBlockBlob blob = container.getBlockBlobReference(fileName);
			InputStream inputStream = new ByteArrayInputStream(fileBytes);
			blob.upload(inputStream, inputStream.available());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
