package server.node.system.expPart;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.serialize.SerializerJson;
import gamecore.system.AbstractSystem;
import gamecore.system.SystemResult;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.dao.ExpPartDao;
import server.node.system.player.Player;

import com.alibaba.fastjson.TypeReference;

/**
 * 部件系统。
 * part的存储id,为一个不重复的long型数字
 * part的缓存id为  storeId
 */
public final class ExpPartSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(ExpPartSystem.class.getName());

	public ExpPartSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("ExpPartSystem start....");
		// 读取制作数据
		boolean b = ExpPartLoadData.getInstance().readData();

		System.out.println("ExpPartSystem start....OK");
		return b;
	}

	@Override
	public void shutdown() {
	}

	public ExpPartBag getExpPartBag(Player player) throws SQLException {
		ExpPartBag expPartBag = RedisHelperJson.getExpPartBag(player.getId());
		if (expPartBag == null) {
			expPartBag = readExpPartBagFromDB(player);
			expPartBag.synchronize();
		}
		return expPartBag;
	}

	private ExpPartBag readExpPartBagFromDB(Player player) throws SQLException {
		ExpPartBag expPartBag = null;

		ExpPartDao expPartDao = DaoFactory.getInstance().borrowExpPartDao();
		Map<String, Object> map = expPartDao.readExpPartBag(player);
		DaoFactory.getInstance().returnExpPartDao(expPartDao);

		if (map != null) {
			try {
				String partsJson = (String) map.get("exp_parts");
				HashMap<Integer, Integer> expParts = (HashMap<Integer, Integer>) SerializerJson.deSerializeMap(partsJson, new TypeReference<HashMap<Integer, Integer>>() {
				});

				expPartBag = new ExpPartBag(player, expParts);

				expPartBag.synchronize();

			} catch (Exception e) {
				logger.error(e);
			}
		} else {
			expPartBag = initExpPartBag(player);
		}

		return expPartBag;
	}

	private ExpPartBag initExpPartBag(Player player) {

		HashMap<Integer, Integer> expParts = new HashMap<Integer, Integer>();

		ExpPartBag expPartBag = new ExpPartBag(player, expParts);

		ExpPartDao expPartDao = DaoFactory.getInstance().borrowExpPartDao();
		expPartDao.save(player, expPartBag);
		DaoFactory.getInstance().returnExpPartDao(expPartDao);

		return expPartBag;
	}

	public void addExpPart(Player player, Integer id, int num) throws SQLException {
		ExpPartBag expPartBag = getExpPartBag(player);
		expPartBag.addExpPart(id, num, true);
		updateDB(player, expPartBag);
	}

	//得到多个 part 
	public void addExpParts(Player player, List<Integer> parts) throws SQLException {
		ExpPartBag expPartBag = getExpPartBag(player);
		for (Integer id : parts) {
			expPartBag.addExpPart(id, 1, false);
		}
		expPartBag.synchronize();
		updateDB(player, expPartBag);
	}

	public SystemResult dropExpParts(Player player, ExpPartBag expPartBag, List<Integer> expParts) throws SQLException {
		SystemResult result = new SystemResult();

		if (expPartBag == null) {
			expPartBag = getExpPartBag(player);
		}
		for (Integer expPartId : expParts) {
			if (expPartId != null) {
				expPartBag.removeExpPart(expPartId, 1, false);
			}
		}
		expPartBag.synchronize();
		updateDB(player, expPartBag);
		return result;
	}

	public SystemResult dropExpPart(Player player, ExpPartBag expPartBag, Integer expPartId) throws SQLException {
		SystemResult result = new SystemResult();

		if (expPartBag == null) {
			expPartBag = getExpPartBag(player);
		}
		if (expPartId != null) {
			expPartBag.removeExpPart(expPartId, 1, false);
		}
		expPartBag.synchronize();
		updateDB(player, expPartBag);
		return result;
	}

	public Integer beLootExp(Player player, ExpPartBag expPartBag) throws SQLException {
		if (expPartBag == null) {
			expPartBag = getExpPartBag(player);
		}

		return expPartBag.randomOne();
	}

	public void updateDB(Player player, ExpPartBag expPartBag) {
		ExpPartDao expPartDao = DaoFactory.getInstance().borrowExpPartDao();
		expPartDao.update(player, expPartBag);
		DaoFactory.getInstance().returnExpPartDao(expPartDao);
	}

}
