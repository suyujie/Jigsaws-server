package server.node.system.mission;

import gamecore.util.DataUtils;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;

public class MissionGainExpMaking implements Serializable {

	private static final long serialVersionUID = -1971251805062586805L;

	private Integer missionId;//mission id
	private String levelStr;//修正参数
	private String expIdStr;
	private int timeMinute;//生产间隔时间,单位 分钟

	private TreeMap<Integer, Integer> levelExpIds = new TreeMap<Integer, Integer>();

	public Integer getMissionId() {
		return missionId;
	}

	public void setMissionId(Integer missionId) {
		this.missionId = missionId;
	}

	public String getLevelStr() {
		return levelStr;
	}

	public void setLevelStr(String levelStr) {
		this.levelStr = levelStr;
	}

	public String getExpIdStr() {
		return expIdStr;
	}

	public void setExpIdStr(String expIdStr) {
		this.expIdStr = expIdStr;
	}

	public int getTimeMinute() {
		return timeMinute;
	}

	public void setTimeMinute(int timeMinute) {
		this.timeMinute = timeMinute;
	}

	public TreeMap<Integer, Integer> getLevelExpIds() {
		return levelExpIds;
	}

	public void setLevelExpIds(TreeMap<Integer, Integer> levelExpIds) {
		this.levelExpIds = levelExpIds;
	}

	public void setLevelExpIds() {
		int[] levels = DataUtils.string2Array(levelStr);
		int[] expIds = DataUtils.string2Array(expIdStr);
		for (int i = 0; i < levels.length; i++) {
			levelExpIds.put(levels[i], expIds[i]);
		}
	}

	public Integer getExpIdByStarNum(Integer starNum) {
		//大于等于当前星数量的
		SortedMap<Integer, Integer> map = levelExpIds.tailMap(starNum, true);
		if (map == null || map.isEmpty()) {
			return null;
		} else {
			return map.get(map.firstKey());
		}
	}
}
