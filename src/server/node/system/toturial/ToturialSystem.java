package server.node.system.toturial;

import gamecore.system.AbstractSystem;
import gamecore.util.Clock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import server.node.dao.DaoFactory;
import server.node.dao.ToturialDao;
import server.node.system.RedisHelperJson;
import server.node.system.Root;
import server.node.system.player.CashType;
import server.node.system.player.GoldType;
import server.node.system.player.MoneyType;
import server.node.system.player.Player;

/**
 * 教学系统
 */
public final class ToturialSystem extends AbstractSystem {

	public ToturialSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("ToturialSystem start....");
		boolean b = ToturialLoadData.getInstance().readData();
		System.out.println("ToturialSystem start....OK");
		return b;
	}

	@Override
	public void shutdown() {
	}

	public Toturial getToturial(Player player) throws SQLException {
		Toturial toturial = RedisHelperJson.getToturial(player.getId());
		if (toturial == null) {
			toturial = readTaskFromDB(player);
			toturial.synchronize();
		}
		return toturial;
	}

	private Toturial readTaskFromDB(Player player) throws SQLException {
		Toturial toturial = null;

		ToturialDao toturialDao = DaoFactory.getInstance().borrowToturialDao();
		Map<String, Object> map = toturialDao.readToturial(player);
		DaoFactory.getInstance().returnToturialDao(toturialDao);

		if (map != null) {

			Integer currentId = (Integer) map.get("current_id");
			Integer buttonId = (Integer) map.get("button_id");
			String rewarded_ids = (String) map.get("rewarded_ids");
			Integer isPvpWinOne = (Integer) map.get("is_pvp_win_one");
			String btn_str = (String) map.get("btn_str");
			Integer gradeStatus = (Integer) map.get("grade_status");
			Integer gradeTime = (Integer) map.get("grade_time");
			Integer addOneHead = (Integer) map.get("add_one_head");

			List<Integer> rewardedIds = new ArrayList<Integer>();

			if (rewarded_ids != null && rewarded_ids.length() > 0) {
				for (String s : rewarded_ids.split("-")) {
					if (s != null && s.length() > 0) {
						rewardedIds.add(Integer.parseInt(s));
					}
				}
			}
			toturial = new Toturial(player, currentId, buttonId, rewardedIds, isPvpWinOne, btn_str, gradeStatus, gradeTime, addOneHead);

			toturial.synchronize();

		} else {
			toturial = initToturial(player);
		}

		return toturial;
	}

	//初始化Toturial
	private Toturial initToturial(Player player) {

		// currentPointId从0开始
		Toturial toturial = new Toturial(player, -1, -1, new ArrayList<Integer>(), 0, "", 0, 0, 0);

		ToturialDao toturialDao = DaoFactory.getInstance().borrowToturialDao();
		toturialDao.saveToturial(player, toturial);
		DaoFactory.getInstance().returnToturialDao(toturialDao);

		return toturial;
	}

	//update,以客户端为准的更新
	public Toturial updateToturialByClient(Player player, Integer currentId, Integer buttonId) throws SQLException {

		Toturial toturial = getToturial(player);

		boolean change = false;

		if (currentId != null && currentId != toturial.getCurrentId()) {
			toturial.setCurrentId(currentId);
			change = true;
		}

		//奖励
		change = change | checkAndReward(player, toturial);

		if (buttonId != null && buttonId != toturial.getButtonId()) {
			toturial.setButtonId(buttonId);
			change = true;
		}

		if (change) {
			updateCacheAndDb(player, toturial);
		}

		return toturial;

	}

	//请求进度的时候，如果 currentId不一致,以大的为准
	public Toturial updateToturialWhenRequest(Player player, Integer currentId) throws SQLException {

		Toturial toturial = getToturial(player);

		boolean change = false;

		if (currentId > toturial.getCurrentId()) {
			toturial.setCurrentId(currentId);
			change = true;
		}

		//奖励
		change = change | checkAndReward(player, toturial);

		if (change) {
			updateCacheAndDb(player, toturial);
		}

		return toturial;

	}

	//赢过一场pvp
	public void updatePvpWinOne(Player player) throws SQLException {

		Toturial toturial = getToturial(player);
		if (toturial.getIsPvpWinOne() == 0) {
			toturial.setIsPvpWinOne(1);

			updateCacheAndDb(player, toturial);

		}

	}

	//点击过的按钮
	public void updateClickBtnStr(Player player, String btnStr) throws SQLException {

		Toturial toturial = getToturial(player);
		toturial.setBtnStr(btnStr);

		updateCacheAndDb(player, toturial);

	}

	private boolean checkAndReward(Player player, Toturial toturial) {

		boolean updateToturial = false;

		/**从xml中查看有没有奖励   ,并且查看之前的所有带奖励的id,是否给了奖励 **/

		for (Integer id : ToturialLoadData.getInstance().getToturialMakingIdsWithReward()) {
			if (toturial.getCurrentId() >= id && ToturialLoadData.getInstance().checkHaveReward(id) && !toturial.getRewardedIds().contains(id)) {//有奖励
				updateToturial = reward(player, toturial, id) | updateToturial;
			}
		}

		return updateToturial;
	}

	private boolean reward(Player player, Toturial toturial, Integer id) {

		boolean needUpdate = false;

		ToturialMaking toturialMaking = ToturialLoadData.getInstance().getToturialMaking(id);

		if (toturialMaking != null && toturialMaking.getMoneyType() != null) {
			if (toturialMaking.getMoneyType() == MoneyType.GOLD) {
				Root.playerSystem.changeGold(player, toturialMaking.getRewardNum(), GoldType.TOTURIAL_GET, true);
			} else {
				Root.playerSystem.changeCash(player, toturialMaking.getRewardNum(), CashType.TOTURIAL_GET, true);
			}

			toturial.addRewardedId(id);

			needUpdate = true;
		}

		return needUpdate;
	}

	//评价状态更新
	public void updateGrade(Player player, int gradeStatus) throws SQLException {
		Toturial toturial = getToturial(player);

		//-1:永不评价,2已经评价
		if (gradeStatus == -1 || gradeStatus == 2) {
			toturial.setGradeStatus(gradeStatus);
			toturial.setGradeTime((int) Clock.currentTimeSecond() / 3600);
		}
		//0:未评价(default),1 稍后评价,
		if (gradeStatus == 1) {//稍后评价
			if (toturial.getGradeStatus() == 0) {//第1次稍后评价,5代表首次稍后评价,6代表二次稍后评价
				toturial.setGradeStatus(5);
				toturial.setGradeTime((int) Clock.currentTimeSecond() / 3600);
			}
			if (toturial.getGradeStatus() == 5) {//第2次稍后评价,6代表二次稍后评价
				toturial.setGradeStatus(6);
				toturial.setGradeTime((int) Clock.currentTimeSecond() / 3600);
			}
		}

		updateCacheAndDb(player, toturial);
	}

	public void updateCacheAndDb(Player player, Toturial toturial) {

		toturial.synchronize();

		ToturialDao toturialDao = DaoFactory.getInstance().borrowToturialDao();
		toturialDao.updateToturial(player, toturial);
		DaoFactory.getInstance().returnToturialDao(toturialDao);
	}

}
