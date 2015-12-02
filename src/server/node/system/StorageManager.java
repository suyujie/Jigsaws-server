package server.node.system;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import common.qcloud.cosapi.QCloudStorageBean;

/**
 * 存储的配置。
 */
public class StorageManager implements Serializable {

	private static final long serialVersionUID = -3954694024619422798L;

	protected static StorageManager storageManager = null;

	private final static Logger logger = LogManager.getLogger(StorageManager.class.getName());

	public HashMap<Integer, QCloudStorageBean> qCloudStorages = new HashMap<Integer, QCloudStorageBean>();

	public StorageManager() {
	}

	/**
	 * 获取唯一实例.
	 * 
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

			// 存储服务器
			Element storages = root.element("storages");

			if (null != storages) {
				@SuppressWarnings("unchecked")
				List<Element> platForms = storages.elements("platfrom");
				if (null != platForms) {
					for (Element pf : platForms) {// 存储平台

						String platfromTag = pf.attributeValue("tag");

						if (platfromTag.equals("qqCloud")) {
							List<Element> storageArray = pf.elements("storage");
							if (null != storageArray) {
								for (Element st : storageArray) {
									Integer stTag = Integer.parseInt(st.attributeValue("tag"));
									Integer appId = Integer.parseInt(st.element("app_id").getTextTrim());
									String secretId = st.element("secret_id").getTextTrim();
									String secretKey = st.element("secret_key").getTextTrim();
									String bucketNames = st.element("bucketNames").getTextTrim();
									QCloudStorageBean bean = new QCloudStorageBean(appId, secretId, secretKey,
											bucketNames);
									qCloudStorages.put(stTag, bean);
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

}
