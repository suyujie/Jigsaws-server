package server.node.system.toturial;

import gamecore.entity.AbstractEntity;
import gamecore.util.Clock;

import java.util.ArrayList;
import java.util.List;

import server.node.system.player.Player;

/**
 * 新手引导  实体
 */
public class Toturial extends AbstractEntity {

	private static final long serialVersionUID = 2457872603254451862L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "toturial_";

	private Integer currentId;//当前刚通过的id
	private Integer buttonId;
	private List<Integer> rewardedIds;//拿过奖励的id
	private int isPvpWinOne;//pvp 是否赢过一次
	private String btnStr;//点击过的按钮String
	private int gradeStatus;//评价,-1永不评价,0未评价(default),2已经评价, 时间(精确到小时)稍后评价(2天)
	private int gradeTime;//评论时间,精确到小时
	private int addOneHead = 0;//0 没给过,1给过了

	public Toturial() {
	}

	public Toturial(Player player, Integer currentId, Integer buttonId, List<Integer> rewardedIds, int isPvpWinOne, String btnStr, int gradeStatus, int gradeTime, int addOneHead) {
		super(Toturial.generateCacheKey(player.getId()));//玩家playerId当作存储键值
		this.currentId = currentId;
		this.buttonId = buttonId;
		this.rewardedIds = rewardedIds;
		this.isPvpWinOne = isPvpWinOne;
		this.btnStr = btnStr;
		this.gradeStatus = gradeStatus;
		this.gradeTime = gradeTime;
		this.addOneHead = addOneHead;
	}

	public Integer getCurrentId() {
		return currentId;
	}

	public void setCurrentId(Integer currentId) {
		this.currentId = currentId;
	}

	public Integer getButtonId() {
		return buttonId;
	}

	public void setButtonId(Integer buttonId) {
		this.buttonId = buttonId;
	}

	public List<Integer> getRewardedIds() {
		return rewardedIds;
	}

	public void setRewardedIds(List<Integer> rewardedIds) {
		this.rewardedIds = rewardedIds;
	}

	public void addRewardedId(Integer rewardId) {
		if (rewardedIds == null) {
			rewardedIds = new ArrayList<Integer>();
		}
		rewardedIds.add(rewardId);
	}

	public int getIsPvpWinOne() {
		return isPvpWinOne;
	}

	public void setIsPvpWinOne(int isPvpWinOne) {
		this.isPvpWinOne = isPvpWinOne;
	}

	public String getBtnStr() {
		return btnStr;
	}

	public void setBtnStr(String btnStr) {
		this.btnStr = btnStr;
	}

	public int getGradeStatus() {
		return gradeStatus;
	}

	public void setGradeStatus(int gradeStatus) {
		this.gradeStatus = gradeStatus;
	}

	public int getGradeTime() {
		return gradeTime;
	}

	public void setGradeTime(int gradeTime) {
		this.gradeTime = gradeTime;
	}

	public int getAddOneHead() {
		return addOneHead;
	}

	public void setAddOneHead(int addOneHead) {
		this.addOneHead = addOneHead;
	}

	public int readGradeForClient() {

		//-1:永不评价,2已经评价,0:未评价(default),1 稍后评价,  5代表首次稍后评价,6代表二次稍后评价

		if (gradeStatus == -1 || gradeStatus == 2 || gradeStatus == 0) { //-1:永不评价,2已经评价,0:未评价(default)
			return gradeStatus;
		}

		if (gradeStatus == 1) {
			if (Clock.currentTimeSecond() / 3600 - 48 > gradeTime) {//距离首次 稍后评价已经过了2天,弹出
				return 0;
			} else {
				return 1;
			}
		}

		if (gradeStatus == 5) {
			if (Clock.currentTimeSecond() / 3600 - 72 > gradeTime) {//距离2次 稍后评价已经过了3天,弹出
				return 0;
			} else {
				return 1;
			}
		}

		//以后就永不弹出
		return -1;

	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(Toturial.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	public String rewardedIdsToString() {
		StringBuffer sb = new StringBuffer();

		if (rewardedIds == null || rewardedIds.isEmpty()) {
			return "";
		} else {
			for (Integer id : rewardedIds) {
				sb.append(id).append("-");
			}
			sb.deleteCharAt(sb.lastIndexOf("-"));
		}

		return sb.toString();

	}

}
