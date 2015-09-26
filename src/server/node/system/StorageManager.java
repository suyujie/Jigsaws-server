package server.node.system;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import common.aws.AwsS3StorageBean;
import common.microsoft.azure.AzureStorageBean;

/**
 * 存储的配置。
 */
public class StorageManager implements Serializable {

	private static final long serialVersionUID = -3954694024619422798L;

	protected static StorageManager storageManager = null;

	private final static Logger logger = LogManager.getLogger(StorageManager.class.getName());

	// 数据文件路径
	public String dataPath;
	//aws s3 存储
	public HashMap<Integer, AwsS3StorageBean> awsS3StorageBeans = new HashMap<Integer, AwsS3StorageBean>();
	//微软的存储,根据语言来分
	public HashMap<Integer, AzureStorageBean> azureStorages = new HashMap<Integer, AzureStorageBean>();

	public StorageManager() {
	}

	/**
	 * 获取唯一实例.
	 * @return
	 */
	public static StorageManager getInstance() {
		if (storageManager != null) {
			return storageManager;
		} else {
			storageManager = new StorageManager();
			return storageManager;
		}
	}

	// 从配置文件加载配置
	public boolean load() {

		File file = new File(this.getClass().getResource("/").getPath() + "properties/storage.xml");

		if (!file.exists()) {
			logger.error("NOT FOUND node config file!");
			return false;
		}

		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(file);
			Element root = doc.getRootElement();

			Element storage = root.element("storage");

			//数据文件路径
			this.dataPath = root.element("dataPath").getTextTrim();

			//存储服务器
			Element storages = root.element("storages");

			if (null != storages) {
				@SuppressWarnings("unchecked")
				List<Element> platForms = storages.elements("platfrom");
				if (null != platForms) {
					for (Element pf : platForms) {//存储平台

						String platfromTag = pf.attributeValue("tag");

						if (platfromTag.equals("aws")) {
							List<Element> storageArray = pf.elements("storage");
							if (null != storageArray) {
								for (Element st : storageArray) {
									Integer stTag = Integer.parseInt(st.attributeValue("tag"));
									String cdnUrl = st.element("cdnUrl").getTextTrim();
									String bucketName = st.element("bucketName").getTextTrim();
									String accessUrl = st.element("accessUrl").getTextTrim();
									String accessId = st.element("accessId").getTextTrim();
									String accessKey = st.element("accessKey").getTextTrim();
									AwsS3StorageBean awsS3StorageBean = new AwsS3StorageBean(cdnUrl, bucketName, accessUrl, accessId, accessKey);
									awsS3StorageBeans.put(stTag, awsS3StorageBean);
								}
							}
						}

						if (platfromTag.equals("azure")) {
							List<Element> storageArray = pf.elements("storage");
							if (null != storageArray) {
								for (Element st : storageArray) {
									Integer stTag = Integer.parseInt(st.attributeValue("tag"));
									String storageAccount = st.element("storageAccount").getTextTrim();
									String storageAccountKey = st.element("storageAccountKey").getTextTrim();
									String storageConnectionUri = st.element("storageConnectionUri").getTextTrim();
									AzureStorageBean storageBean = new AzureStorageBean(storageAccount, storageAccountKey, storageConnectionUri);
									azureStorages.put(stTag, storageBean);
								}
							}
						}

					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		toString();

		return true;
	}

	public List<AzureStorageBean> getAzureStorageBeans() {
		List<AzureStorageBean> result = new ArrayList<>();
		result.addAll(this.azureStorages.values());
		return result;
	}

	public List<AwsS3StorageBean> getAwsS3StorageBeans() {
		List<AwsS3StorageBean> result = new ArrayList<>();
		result.addAll(this.awsS3StorageBeans.values());
		return result;
	}
}
