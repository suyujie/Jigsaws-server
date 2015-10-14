package server.node.system.blackList;

import gamecore.system.AbstractSystem;
import gamecore.util.NetAccessTool;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpMethod;

import server.node.system.ConfigManager;
import server.node.system.player.Player;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 黑名单系统
 */
public final class BlackListSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(BlackListSystem.class.getName());
	// 黑名单 玩家,时间
	private List<Long> blackList = new ArrayList<Long>();

	@Override
	public boolean startup() {

		System.out.println("BlackListSystem start..");

		//flushBlackList();

		System.out.println("BlackListSystem start..ok");

		return true;
	}

	@Override
	public void shutdown() {
	}

	// 验证是否是黑名单玩家
	public boolean checkBlackList(Player player) {
		return blackList.contains(player.getId());
	}

	// 有新的黑名单
	public boolean addBlackList(List<Long> addIds) {

		for (Long id : addIds) {
			if (!blackList.contains(id)) {
				blackList.add(id);
			}
		}

		return true;
	}

	// 删除黑名单
	public boolean removeBlackList(List<Long> delIds) {
		for (Long id : delIds) {
			if (blackList.contains(id)) {
				blackList.remove(id);
			}
		}
		return true;
	}

	// 读取黑名单
	public boolean flushBlackList() {

		String url = new StringBuffer("http://").append(ConfigManager.getInstance().managerAccessUrl)
				.append("/robot_web/forNode/blacklist").toString();

		String result = NetAccessTool.accessServer(url, null, HttpMethod.GET);

		if (result == null || result.length() == 0) {
			logger.error("access manager_server error");
			return false;
		}

		JSONObject resultJson = JSONObject.parseObject(result);

		logger.info(resultJson.toString());

		JSONArray array = resultJson.getJSONArray("blacklist");

		for (int i = 0; i < array.size(); i++) {
			Long id = array.getLong(i);
			if (!blackList.contains(id)) {
				blackList.add(id);
			}
		}

		return true;
	}

}
