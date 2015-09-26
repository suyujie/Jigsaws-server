package server.node.system.chip;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.serialize.SerializerJson;
import gamecore.system.AbstractSystem;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.dao.ChipDao;
import server.node.system.player.Player;

import com.alibaba.fastjson.TypeReference;

/**
 * 颜色瓶系统。
 */
public final class ChipSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(ChipSystem.class.getName());

	public ChipSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("ChipSystem start....");
		boolean b = true;
		System.out.println("ChipSystem start....OK");
		return b;
	}

	@Override
	public void shutdown() {
	}

	public ChipBag getChipBag(Player player) throws SQLException {
		ChipBag chipBag = RedisHelperJson.getChipBag(player.getId());
		if (chipBag == null) {
			chipBag = readChipBagFromDB(player);
			chipBag.synchronize();
		}
		return chipBag;
	}

	private ChipBag readChipBagFromDB(Player player) throws SQLException {
		ChipBag chipBag = null;
		ChipDao chipDao = DaoFactory.getInstance().borrowChipDao();
		Map<String, Object> map = chipDao.readChipBag(player);
		DaoFactory.getInstance().returnChipDao(chipDao);

		if (map != null) {
			try {

				String chipsJson = (String) map.get("chips");
				HashMap<String, Integer> chips = (HashMap<String, Integer>) SerializerJson.deSerializeMap(chipsJson, new TypeReference<HashMap<String, Integer>>() {
				});

				chipBag = new ChipBag(player.getId(), chips);
				chipBag.synchronize();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			chipBag = initChipBag(player);
		}

		return chipBag;
	}

	private ChipBag initChipBag(Player player) {

		HashMap<String, Integer> chips = new HashMap<String, Integer>();

		ChipBag chipBag = new ChipBag(player.getId(), chips);

		ChipDao chipDao = DaoFactory.getInstance().borrowChipDao();
		chipDao.save(player, chipBag);
		DaoFactory.getInstance().returnChipDao(chipDao);

		return chipBag;
	}

	public boolean removeChip(Player player, ChipBag chipBag, String chipkey, int num) throws SQLException {
		if (chipBag == null) {
			chipBag = getChipBag(player);
		}
		boolean result = chipBag.removeChip(chipkey, num, true);

		if (result) {
			ChipDao chipDao = DaoFactory.getInstance().borrowChipDao();
			chipDao.update(player, chipBag);
			DaoFactory.getInstance().returnChipDao(chipDao);
		}

		return result;
	}

	public boolean removeChip(Player player, ChipBag chipBag, String chipkey) throws SQLException {
		if (chipBag == null) {
			chipBag = getChipBag(player);
		}
		boolean result = chipBag.removeChip(chipkey, true);

		if (result) {
			ChipDao chipDao = DaoFactory.getInstance().borrowChipDao();
			chipDao.update(player, chipBag);
			DaoFactory.getInstance().returnChipDao(chipDao);
		}

		return result;
	}

	public String beLootChip(Player player, ChipBag chipBag) throws SQLException {
		if (chipBag == null) {
			chipBag = getChipBag(player);
		}

		return chipBag.randomOneChip();
	}

	public void addChipNum(Player player, ChipBag chipBag, String chipkey, int num) throws SQLException {

		if (chipBag == null) {
			chipBag = getChipBag(player);
		}

		if (chipkey != null && !chipkey.equals("null")) {
			chipBag.addChip(chipkey, num, true);

			ChipDao chipDao = DaoFactory.getInstance().borrowChipDao();
			chipDao.update(player, chipBag);
			DaoFactory.getInstance().returnChipDao(chipDao);
		}

	}

	public void update(Player player, ChipBag chipBag) {
		chipBag.synchronize();
		ChipDao chipDao = DaoFactory.getInstance().borrowChipDao();
		chipDao.update(player, chipBag);
		DaoFactory.getInstance().returnChipDao(chipDao);
	}

}
