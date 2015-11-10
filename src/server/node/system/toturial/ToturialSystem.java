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
		// boolean b = ToturialLoadData.getInstance().readData();
		System.out.println("ToturialSystem start....OK");
		return true;
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
			toturial = new Toturial(player, currentId, buttonId, rewardedIds, isPvpWinOne, btn_str, gradeStatus,
					gradeTime, addOneHead);

			toturial.synchronize();

		} else {
			toturial = initToturial(player);
		}

		return toturial;
	}

	// 初始化Toturial
	private Toturial initToturial(Player player) {

		// currentPointId从0开始
		Toturial toturial = new Toturial(player, -1, -1, new ArrayList<Integer>(), 0, "", 0, 0, 0);

		ToturialDao toturialDao = DaoFactory.getInstance().borrowToturialDao();
		toturialDao.saveToturial(player, toturial);
		DaoFactory.getInstance().returnToturialDao(toturialDao);

		return toturial;
	}

	// update,以客户端为准的更新
	public Toturial updateToturialByClient(Player player, Integer currentId, Integer buttonId) throws SQLException {

		Toturial toturial = getToturial(player);

		boolean change = false;

		if (currentId != null && currentId != toturial.getCurrentId()) {
			toturial.setCurrentId(currentId);
			change = true;
		}

		if (buttonId != null && buttonId != toturial.getButtonId()) {
			toturial.setButtonId(buttonId);
			change = true;
		}

		if (change) {
			updateCacheAndDb(player, toturial);
		}

		return toturial;

	}

	// 点击过的按钮
	public void updateClickBtnStr(Player player, String btnStr) throws SQLException {

		Toturial toturial = getToturial(player);
		toturial.setBtnStr(btnStr);

		updateCacheAndDb(player, toturial);

	}

	public void updateCacheAndDb(Player player, Toturial toturial) {

		toturial.synchronize();

		ToturialDao toturialDao = DaoFactory.getInstance().borrowToturialDao();
		toturialDao.updateToturial(player, toturial);
		DaoFactory.getInstance().returnToturialDao(toturialDao);
	}

}
