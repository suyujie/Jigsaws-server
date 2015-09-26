package server.node.system.berg;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.serialize.SerializerJson;
import gamecore.system.AbstractSystem;
import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;
import gamecore.util.BaseWeightPool;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.dao.BergDao;
import server.node.system.Root;
import server.node.system.player.CashType;
import server.node.system.player.GoldType;
import server.node.system.player.Player;

import com.alibaba.fastjson.TypeReference;

public final class BergSystem extends AbstractSystem {

	private static int openLevel = 13;

	private static int[] beLootRateByLevel = { 60, 50, 40, 30, 20, 10, 8, 4, 2, 1 };

	private static Logger logger = LogManager.getLogger(BergSystem.class.getName());

	public BergSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("BergSystem start....");
		boolean b = BergLoadData.getInstance().readData();
		System.out.println("BergSystem start....OK");
		return b;
	}

	@Override
	public void shutdown() {
	}

	public BergBag getBergBag(Player player) throws SQLException {
		BergBag bergBag = RedisHelperJson.getBergBag(player.getId());
		if (bergBag == null) {
			bergBag = readBergBagFromDB(player);
			bergBag.synchronize();
		}
		return bergBag;
	}

	private BergBag readBergBagFromDB(Player player) throws SQLException {
		BergBag bergBag = null;
		BergDao bergDao = DaoFactory.getInstance().borrowBergDao();
		Map<String, Object> map = bergDao.readBergBag(player);
		DaoFactory.getInstance().returnBergDao(bergDao);

		if (map != null) {
			try {

				String bergsJson = (String) map.get("bergs");
				HashMap<Integer, Integer> bergs = (HashMap<Integer, Integer>) SerializerJson.deSerializeMap(bergsJson, new TypeReference<HashMap<Integer, Integer>>() {
				});

				bergBag = new BergBag(player.getId(), bergs);
				bergBag.synchronize();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			bergBag = initBergBag(player);
		}

		return bergBag;
	}

	private BergBag initBergBag(Player player) {

		HashMap<Integer, Integer> bergs = new HashMap<Integer, Integer>();

		//默认给  : 5个0号水晶, 1个1级防御水晶，1个1级暴击水晶，1个1级生命水晶
		bergs.put(0, 5);
		bergs.put(100, 1);
		bergs.put(200, 1);
		bergs.put(300, 1);

		BergBag bergBag = new BergBag(player.getId(), bergs);

		BergDao bergDao = DaoFactory.getInstance().borrowBergDao();
		bergDao.save(player, bergBag);
		DaoFactory.getInstance().returnBergDao(bergDao);

		return bergBag;
	}

	public SystemResult upgrade(Player player, BergBag bergBag, Integer bergId) throws SQLException {
		SystemResult result = new SystemResult();
		if (bergBag == null) {
			bergBag = getBergBag(player);
		}

		BergMaking bergMaking = BergLoadData.getInstance().getBergMaking(bergId);

		int needNum = bergMaking.getFuseNum();//多少个合成一个,如果是0,表示不可合成...
		if (needNum == 0) {//已经顶级,不能合成了
			result.setCode(ErrorCode.PARAM_ERROR);
			return result;
		} else {
			//需要的钱
			int needCash = bergMaking.getFuseMoney();
			if (bergBag.removeBerg(bergId, needNum, false)) {//成功的取出needNum个来,没有同步缓存

				//花钱
				if (player.getCash() >= needCash) {
					Root.playerSystem.changeCash(player, -needCash, CashType.BERG_UPGRADE, true);

					bergBag.addBerg(bergId + 1, 1, false);
					bergBag.synchronize();
					//更新数据库
					updateBergBag(player, bergBag);

				} else {

					int needGold = Root.missionSystem.cash2Gold(player, needCash - player.getCash());
					if (player.getGold() > needGold) {//gold足够
						Root.playerSystem.changeCash(player, -(int) player.getCash(), CashType.BERG_UPGRADE, false);
						Root.playerSystem.changeGold(player, -needGold, GoldType.BERG_UPGRADE, false);

						player.synchronize();

						bergBag.addBerg(bergId + 1, 1, false);
						bergBag.synchronize();
						//更新数据库
						updateBergBag(player, bergBag);

					} else {
						result.setCode(ErrorCode.GOLD_NOT_ENOUGH);
						logger.error("upgrade error ,gold is not enough");
					}
				}
			} else {
				result.setCode(ErrorCode.BERG_UPGRADE_NOT_ENOUGH);
				logger.error("upgrade error ,gold is not enough");
			}
		}

		return result;
	}

	//计算被抢的
	public Integer beLootBerg(Player player, BergBag bergBag) throws SQLException {

		//水晶模块开始前的下一级，不能被抢
		if (player.getLevel() >= openLevel + 1) {
			if (bergBag == null) {
				bergBag = getBergBag(player);
			}

			if (bergBag != null && bergBag.getBergs() != null && !bergBag.getBergs().isEmpty()) {
				BaseWeightPool baseWeightPool = new BaseWeightPool();

				for (Map.Entry<Integer, Integer> entry : bergBag.getBergs().entrySet()) {
					Integer bergId = entry.getKey();
					Integer bergNum = entry.getValue();
					BergMaking bergMaking = BergLoadData.getInstance().getBergMaking(bergId);
					baseWeightPool.addWeight(beLootRateByLevel[bergMaking.getLevel() - 1] * bergNum, bergId);
				}

				return (Integer) baseWeightPool.getValue();
			}
		}
		return null;
	}

	public void addBergNum(Player player, BergBag bergBag, Integer bergId, int num) throws SQLException {
		if (bergBag == null) {
			bergBag = getBergBag(player);
		}
		if (bergId != null && !bergId.equals("null")) {
			bergBag.addBerg(bergId, num, true);
			updateBergBag(player, bergBag);
		}
	}

	public void removeBerg(Player player, BergBag bergBag, Integer bergId, int num) throws SQLException {
		if (bergBag == null) {
			bergBag = getBergBag(player);
		}
		bergBag.removeBerg(bergId, num, true);

		updateBergBag(player, bergBag);
	}

	public void updateBergBag(Player player, BergBag bergBag) {
		BergDao bergDao = DaoFactory.getInstance().borrowBergDao();
		bergDao.update(player, bergBag);
		DaoFactory.getInstance().returnBergDao(bergDao);
	}

}
