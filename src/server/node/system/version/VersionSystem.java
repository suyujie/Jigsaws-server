package server.node.system.version;

import gamecore.system.AbstractSystem;
import gamecore.util.FileUtil;
import gamecore.util.NetAccessTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpMethod;

import server.node.system.ConfigManager;
import server.node.system.StorageManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 版本系统。
 */
public final class VersionSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(VersionSystem.class.getName());

	//仅服务器端用的xml文件
	private static List<String> serverOnlyXmls = new ArrayList<String>(Arrays.asList("NpcData.xml", "NpcRobot.xml", "RechargePackage.xml", "dailyJob.xml", "levelRobot.xml",
			"lotteryPartSlot.xml", "lotteryRobot.xml", "lotteryQuality.xml", "missionGain.xml", "handbook.xml", "languageServerChinese.xml", "languageServerEnglish.xml",
			"languageServerFanChinese.xml", "languageServerSpanish.xml", "rarityUpgradeData.xml", "DeathWheelRobotBossData.xml", "DeathWheelExtrabossData.xml",
			"DeathWheelBattleField.xml", "DeathWheelBattleOther.xml", "DeathWheelBattleOtherWeapon.xml", "BergWheelRobotBossData.xml", "BergWheelWeaponAI.xml"));
	//客户端版本
	private static Integer gameVersion = null;
	//客户端最新下载地址
	private static String newVersionUrl = null;
	//xml版本
	private HashMap<String, Integer> xmlVersion = null;
	//res版本
	private HashMap<String, Integer> resVersion = null;

	@Override
	public boolean startup() {

		System.out.println("VersionSystem start..");

		xmlVersion = new HashMap<String, Integer>();
		resVersion = new HashMap<String, Integer>();

		FileUtil.createDir(StorageManager.getInstance().dataPath);

		updateVersion();
		updateResVersion();
		updateXmlVersion();

		System.out.println("VersionSystem start..ok");

		return true;
	}

	@Override
	public void shutdown() {
	}

	//获取gameVersion
	public int getGameVersion() {
		return gameVersion;
	}

	//获取
	public String getNewVersionUrl() {
		return newVersionUrl;
	}

	//客户端获取最新的xml版本
	public String getXmlVersion() {

		StringBuffer sb = new StringBuffer();

		Iterator<Entry<String, Integer>> iter = xmlVersion.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Integer> entry = iter.next();
			String key = entry.getKey();
			if (!serverOnlyXmls.contains(key)) {//仅仅服务器用的xml不给客户端发送
				Integer val = entry.getValue();
				sb.append(key).append(":").append(val).append(",");
			}
		}

		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();

	}

	//客户端获取最新的res版本
	public String getResVersion() {

		StringBuffer sb = new StringBuffer();

		Iterator<Entry<String, Integer>> iter = resVersion.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Integer> entry = iter.next();
			String key = entry.getKey();
			if (!serverOnlyXmls.contains(key)) {//仅仅服务器用的xml不给客户端发送
				Integer val = entry.getValue();
				sb.append(key).append(":").append(val).append(",");
			}
		}

		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();

	}

	//服务器端去web端获取最新的 游戏 版本
	public boolean updateVersion() {

		String url = new StringBuffer("http://").append(ConfigManager.getInstance().managerAccessUrl).append("/robot_web/forNode/gameversion").toString();

		String result = NetAccessTool.accessServer(url, null, HttpMethod.GET);

		try {
			JSONObject resultJson = JSONObject.parseObject(result);
			gameVersion = resultJson.getIntValue("gameVerison");
			newVersionUrl = resultJson.getString("gameUrl");
		} catch (Exception e) {
			logger.error("access manager_server for updateGameVersion error");
			logger.error(result);
			return false;
		}

		return true;
	}

	//服务器端去web端获取最新的 xml 版本
	public List<String> updateXmlVersion() {

		List<String> xmlNameList = new ArrayList<String>();

		String url = new StringBuffer("http://").append(ConfigManager.getInstance().managerAccessUrl).append("/robot_web/forNode/xmlversion").toString();

		String result = NetAccessTool.accessServer(url, null, HttpMethod.GET);

		if (result == null || result.length() == 0) {
			logger.error("access manager_server for updateXmlVersion error");
			logger.error(result);
		} else {

			try {
				JSONObject resultJson = JSONObject.parseObject(result);

				String xml = resultJson.getString("xmlVersion");

				if (xml != null && !xml.equals("")) {
					String[] xmlVersionStrs = xml.split(",");
					for (String xmlVersionStr : xmlVersionStrs) {
						String[] xmlVs = xmlVersionStr.split(":");
						String name = xmlVs[0];
						int version = Integer.parseInt(xmlVs[1]);
						if (xmlVersion.get(name) == null || version > xmlVersion.get(name)) {
							xmlVersion.put(name, version);
							updateXmlFile(name);
							xmlNameList.add(name);
						}
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}

		return xmlNameList;
	}

	//服务器端去web端获取最新的 res 版本
	public boolean updateResVersion() {

		String url = new StringBuffer("http://").append(ConfigManager.getInstance().managerAccessUrl).append("/robot_web/forNode/resversion").toString();

		String result = NetAccessTool.accessServer(url, null, HttpMethod.GET);

		try {

			JSONObject resultJson = JSONObject.parseObject(result);

			String res = resultJson.getString("resVersion");

			if (res != null && !res.equals("")) {
				String[] resVersionStrs = res.split(",");
				for (String resVersionStr : resVersionStrs) {
					String[] resVs = resVersionStr.split(":");
					resVersion.put(resVs[0], Integer.parseInt(resVs[1]));
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}

		return true;
	}

	private void updateXmlFile(String xmlName) {

		String url = new StringBuffer("http://").append(ConfigManager.getInstance().managerAccessUrl).append("/robot_web/forNode/xmlFile").toString();

		JSONObject args = new JSONObject();
		args.put("xmlName", xmlName);

		String result = NetAccessTool.accessServer(url, args, HttpMethod.GET);

		JSONObject resultJson = JSONObject.parseObject(result);

		int length = resultJson.getIntValue("length");
		JSONArray fileArray = resultJson.getJSONArray("file");
		if (length == fileArray.size()) {
			byte[] fileByteArray = new byte[fileArray.size()];
			for (int i = 0; i < fileArray.size(); i++) {
				fileByteArray[i] = ((Integer) fileArray.get(i)).byteValue();
			}
			//写入文件
			FileUtil.writeFile(StorageManager.getInstance().dataPath + xmlName, fileByteArray);
		}

	}

}
