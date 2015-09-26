package server.node.system.color;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.system.AbstractSystem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.dao.ColorDao;
import server.node.system.player.Player;

/**
 * 颜色瓶系统。
 */
public final class ColorSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(ColorSystem.class.getName());

	public ColorSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("ColorSystem start....");
		run = true;
		boolean b = ColorLoadData.getInstance().readData();
		System.out.println("ColorSystem start....OK");

		return b;
	}

	@Override
	public void shutdown() {
	}

	//获取ColorBag
	public ColorBag getColorBag(Player player) throws SQLException {
		ColorBag colorBag = RedisHelperJson.getColorBag(player.getId());
		if (colorBag == null) {
			colorBag = readColorBagFromDB(player);
			colorBag.synchronize();
		}
		return colorBag;
	}

	//数据库读取colorBag
	private ColorBag readColorBagFromDB(Player player) throws SQLException {
		ColorBag colorBag = null;
		ColorDao colorDao = DaoFactory.getInstance().borrowColorDao();
		Map<String, Object> map = colorDao.readColorBag(player);
		DaoFactory.getInstance().returnColorDao(colorDao);

		if (map != null) {
			try {

				String colorNums = (String) map.get("color_num");
				List<Integer> nums = new ArrayList<Integer>();

				for (String s : colorNums.split("-")) {
					if (s != null && s.length() > 0) {
						nums.add(Integer.parseInt(s));
					}
				}

				//涂装瓶的颜色数量
				List<Integer> colorIds = ColorLoadData.getInstance().colorIds;

				HashMap<Integer, Integer> colors = new HashMap<Integer, Integer>();

				for (int i = 0; i < colorIds.size(); i++) {
					colors.put(i, nums.get(i));
				}

				//生成colorBag
				colorBag = new ColorBag(player.getId(), colors);

				colorBag.synchronize();

			} catch (Exception e) {
				logger.error(e);
			}
		} else {
			colorBag = initColorBag(player);
		}

		return colorBag;
	}

	//初始化ColorBag
	private ColorBag initColorBag(Player player) {

		HashMap<Integer, Integer> colors = new HashMap<Integer, Integer>();
		for (Integer colorId : ColorLoadData.getInstance().colorIds) {
			colors.put(colorId, 0);
		}

		ColorBag colorBag = new ColorBag(player.getId(), colors);

		ColorDao colorDao = DaoFactory.getInstance().borrowColorDao();
		colorDao.save(player, colorBag);
		DaoFactory.getInstance().returnColorDao(colorDao);

		return colorBag;
	}

	//减少颜色瓶
	public boolean removeColor(Player player, ColorBag colorBag, Integer colorCode, int num) throws SQLException {
		if (colorBag == null) {
			colorBag = getColorBag(player);
		}
		boolean result = colorBag.removeColor(colorCode, num, true);

		if (result) {
			ColorDao colorDao = DaoFactory.getInstance().borrowColorDao();
			colorDao.update(player, colorBag);
			DaoFactory.getInstance().returnColorDao(colorDao);
		}

		return result;
	}

	//减少颜色瓶,全部取出来
	public boolean removeColor(Player player, ColorBag colorBag, Integer colorCode) throws SQLException {
		if (colorBag == null) {
			colorBag = getColorBag(player);
		}
		boolean result = colorBag.removeColor(colorCode, true);

		if (result) {
			ColorDao colorDao = DaoFactory.getInstance().borrowColorDao();
			colorDao.update(player, colorBag);
			DaoFactory.getInstance().returnColorDao(colorDao);
		}

		return result;
	}

	//得到多个颜色瓶
	public List<Integer> addColorNum(Player player, int colorNum) throws SQLException {

		List<Integer> newColors = null;

		ColorBag colorBag = getColorBag(player);

		boolean haveColor = false;

		for (Integer cn : colorBag.getColors().values()) {
			if (cn > 0) {
				haveColor = true;
			}
		}

		if (haveColor) {//有颜色瓶子
			newColors = ColorLoadData.getInstance().getRandomColorId(colorNum);
		} else {//还没有颜色瓶,这时候不给默认颜色瓶
			newColors = ColorLoadData.getInstance().getRandomColorIdWithoutDefaultColor(colorNum);
		}

		for (Integer c : newColors) {
			colorBag.addColor(c, 1, false);
		}
		colorBag.synchronize();

		ColorDao colorDao = DaoFactory.getInstance().borrowColorDao();
		colorDao.update(player, colorBag);
		DaoFactory.getInstance().returnColorDao(colorDao);

		return newColors;
	}

	public void addColorNum(Player player, int colorId, int num) throws SQLException {

		ColorBag colorBag = getColorBag(player);

		colorBag.addColor(colorId, num, true);

		ColorDao colorDao = DaoFactory.getInstance().borrowColorDao();
		colorDao.update(player, colorBag);
		DaoFactory.getInstance().returnColorDao(colorDao);

	}

}
