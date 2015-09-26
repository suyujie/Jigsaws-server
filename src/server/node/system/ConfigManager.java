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

/**
 * 服务器配置。
 */
public class ConfigManager implements Serializable {

	private static final long serialVersionUID = -1878191790142520632L;

	protected static ConfigManager configManager = null;

	private final static Logger logger = LogManager.getLogger(ConfigManager.class.getName());
	//节点数量
	public Integer nodeNum;
	//节点标签。
	public Integer tag;
	//域名
	public String nodeDomain;
	//工作端口。
	public int workPort;
	//支付端口。
	public int payPort;
	//管理端口。
	public int mgmtPort;
	//工作连接器队列大小。
	public int workQueueSize;
	//支付连接器队列大小。
	public int payQueueSize;
	//管理连接器队列大小。
	public int mgmtQueueSize;
	//集群 Peer 列表。
	public HashMap<Integer, String> nodes = new HashMap<Integer, String>();
	//管理服务器的ip列表
	public List<String> managers = new ArrayList<String>();
	public String managerAccessUrl = null;
	//任务中心最大任务数量。默认：200
	public int maxTasks;
	// 任务窗每秒任务数量。默认：10
	public int tasksPerSecond;

	public ConfigManager() {
	}

	/**
	 * 获取唯一实例.
	 * @return
	 */
	public static ConfigManager getInstance() {
		if (configManager != null) {
			return configManager;
		} else {
			configManager = new ConfigManager();
			return configManager;
		}
	}

	// 从配置文件加载配置
	public boolean load() {

		File file = new File(this.getClass().getResource("/").getPath() + "properties/node.xml");

		if (!file.exists()) {
			logger.error("NOT FOUND node config file!");
			return false;
		}

		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(file);
			Element root = doc.getRootElement();

			this.tag = Integer.parseInt(root.attribute("tag").getValue());

			// server
			Element server = root.element("server");

			this.nodeDomain = root.element("nodeDomain").getTextTrim();

			this.workPort = Integer.parseInt(server.element("work-port").getTextTrim());
			this.payPort = Integer.parseInt(server.element("pay-port").getTextTrim());
			this.mgmtPort = Integer.parseInt(server.element("mgmt-port").getTextTrim());

			this.workQueueSize = Integer.parseInt(server.element("work-queue-size").getTextTrim());
			this.payQueueSize = Integer.parseInt(server.element("pay-queue-size").getTextTrim());
			this.mgmtQueueSize = Integer.parseInt(server.element("mgmt-queue-size").getTextTrim());

			//集群逻辑服务器列表
			Element nodeElements = root.element("nodes");
			if (null != nodeElements) {
				@SuppressWarnings("unchecked")
				List<Element> nodeElementArray = nodeElements.elements("node");
				if (null != nodeElements) {
					for (Element el : nodeElementArray) {
						String strTag = el.attributeValue("tag");
						String address = el.getTextTrim();
						int nTag = Integer.parseInt(strTag);
						this.nodes.put(nTag, address);
					}
				}
			}

			this.nodeNum = this.nodes.size();

			//管理服务器列表
			Element managerServerElement = root.element("managerServer");
			if (null != managerServerElement) {
				@SuppressWarnings("unchecked")
				List<Element> manaElementArray = managerServerElement.elements("manager");
				if (null != nodeElements) {
					for (Element el : manaElementArray) {
						String strTag = el.attributeValue("tag");
						String address = el.getTextTrim();
						if (Integer.parseInt(strTag) == 1) {
							managerAccessUrl = address;//主服务地址
						}
						this.managers.add(address.split(":")[0]);
					}
				}
			}

			// performances
			Element performances = root.element("performances");
			this.maxTasks = Integer.parseInt(performances.element("max-tasks").getTextTrim());
			this.tasksPerSecond = Integer.parseInt(performances.element("window-tps").getTextTrim());

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		toString();

		return true;
	}

}
