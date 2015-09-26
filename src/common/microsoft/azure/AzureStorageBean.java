package common.microsoft.azure;

public class AzureStorageBean {

	// 存储账户
	public String storageAccount;
	// 存储密钥
	public String storageAccountKey;
	//uri
	public String storageConnectionUri;

	public AzureStorageBean(String storageAccount, String storageAccountKey, String storageConnectionUri) {
		this.storageAccount = storageAccount;
		this.storageAccountKey = storageAccountKey;
		this.storageConnectionUri = storageConnectionUri;
	}

	public String getStorageAccount() {
		return storageAccount;
	}

	public void setStorageAccount(String storageAccount) {
		this.storageAccount = storageAccount;
	}

	public String getStorageAccountKey() {
		return storageAccountKey;
	}

	public void setStorageAccountKey(String storageAccountKey) {
		this.storageAccountKey = storageAccountKey;
	}

	public String getStorageConnectionUri() {
		return storageConnectionUri;
	}

	public void setStorageConnectionUri(String storageConnectionUri) {
		this.storageConnectionUri = storageConnectionUri;
	}

}
