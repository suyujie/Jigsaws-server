package server.node.system.rechargePackage;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.serialize.SerializerJson;
import gamecore.system.AbstractSystem;
import gamecore.util.NetAccessTool;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.http.HttpMethod;

import server.node.dao.RechargePackageDao;
import server.node.system.ConfigManager;
import server.node.system.monthCard.MonthCardSystem;
import server.node.system.player.Player;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.coin.CoinType;
import common.language.LangType;

public class RechargePackageSystem extends AbstractSystem {

	private HashMap<String, RechargePackage> rechargePackages = new HashMap<String, RechargePackage>();
	private List<String> rechargePackageIds = new ArrayList<String>();

	@Override
	public boolean startup() {

		System.out.println("RechargePackageSystem start....");
		// 从web要数据
		flushRechargePackage();

		System.out.println("RechargePackageSystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {

	}

	public RechargePackageBag getRechargePackageBag(Player player) throws SQLException {
		RechargePackageBag rechargePackageBag = RedisHelperJson.getRechargePackageBag(player.getId());
		if (rechargePackageBag == null) {
			rechargePackageBag = readRechargePackageBagFromDB(player);
		}
		return rechargePackageBag;
	}

	private RechargePackageBag readRechargePackageBagFromDB(Player player) throws SQLException {
		RechargePackageBag rechargePackageBag = null;
		RechargePackageDao rechargePackageDao = DaoFactory.getInstance().borrowRechargePackageDao();
		Map<String, Object> map = rechargePackageDao.readRechargeBag(player);
		DaoFactory.getInstance().returnRechargePackageDao(rechargePackageDao);

		if (map != null) {
			try {

				String buyed_Ids = (String) map.get("buy_ids");
				List<String> buyedIds = (List<String>) SerializerJson.deSerializeList(buyed_Ids, String.class);

				rechargePackageBag = new RechargePackageBag(player.getId(), buyedIds);
				rechargePackageBag.synchronize();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			rechargePackageBag = initRechargePackageBag(player);
		}

		return rechargePackageBag;
	}

	private RechargePackageBag initRechargePackageBag(Player player) {

		RechargePackageBag rechargePackageBag = new RechargePackageBag(player.getId(), null);
		rechargePackageBag.synchronize();

		RechargePackageDao rechargePackageDao = DaoFactory.getInstance().borrowRechargePackageDao();
		rechargePackageDao.save(player, rechargePackageBag);
		DaoFactory.getInstance().returnRechargePackageDao(rechargePackageDao);

		return rechargePackageBag;
	}

	public void buyedRechargePackage(Player player, RechargePackageBag rechargePackageBag, RechargePackage rechargePackage) throws SQLException {
		if (rechargePackageBag == null) {
			rechargePackageBag = getRechargePackageBag(player);
		}

		boolean change = rechargePackageBag.addBuyedId(rechargePackage.getId(), true);
		if (change) {
			RechargePackageDao rechargePackageDao = DaoFactory.getInstance().borrowRechargePackageDao();
			rechargePackageDao.update(player, rechargePackageBag);
			DaoFactory.getInstance().returnRechargePackageDao(rechargePackageDao);
		}

	}

	public RechargePackage getRechargePackage(String id) {
		return rechargePackages.get(id);
	}

	public List<RechargePackage> getRechargePackagesWithOutMonthCard() {
		List<RechargePackage> list = new ArrayList<RechargePackage>();
		for (String id : rechargePackageIds) {
			if (id != null && !id.equals(MonthCardSystem.MonthCardId) && rechargePackages.get(id) != null) {
				list.add(rechargePackages.get(id));
			}
		}
		return list;
	}

	public RechargePackage getMonthCardRechargePackage() {
		return rechargePackages.get(MonthCardSystem.MonthCardId);
	}

	//读取兑换包
	public boolean flushRechargePackage() {

		rechargePackages.clear();
		rechargePackageIds.clear();

		String url = new StringBuffer("http://").append(ConfigManager.getInstance().managerAccessUrl).append("/robot_web/forNode/enabled_recharge_package").toString();

		String result = NetAccessTool.accessServer(url, null, HttpMethod.GET);

		JSONObject resultJson = JSONObject.parseObject(result);

		JSONArray array = resultJson.getJSONArray("enabledRechargePackages");

		System.out.println();
		System.out.println(array.toJSONString());
		System.out.println();

		for (int i = 0; i < array.size(); i++) {
			RechargePackage rechargePackage = new RechargePackage();

			JSONObject rp = array.getJSONObject(i);

			//id
			rechargePackage.setId(rp.getString("id"));
			//picUrl
			rechargePackage.setPic(rp.getString("picUrl"));

			//name
			JSONObject nameJson = rp.getJSONObject("names");
			HashMap<LangType, String> names = new HashMap<LangType, String>();
			names.put(LangType.en_US, nameJson.getString("en_US"));
			names.put(LangType.zh_CN, nameJson.getString("zh_CN"));
			names.put(LangType.es_ES, nameJson.getString("es_ES"));
			rechargePackage.setName(names);

			//item
			JSONObject itemJson = rp.getJSONObject("items");
			HashMap<ItemType, Integer> items = new HashMap<ItemType, Integer>();
			items.put(ItemType.CASH, itemJson.getInteger("CASH"));
			items.put(ItemType.GOLD, itemJson.getInteger("GOLD"));
			rechargePackage.setItem(items);

			//coin prices
			JSONObject coinJson = rp.getJSONObject("prices");
			HashMap<CoinType, Integer> coins = new HashMap<CoinType, Integer>();
			coins.put(CoinType.USD, coinJson.getInteger(CoinType.USD.asCode()));
			coins.put(CoinType.CNY, coinJson.getInteger(CoinType.CNY.asCode()));
			rechargePackage.setCoin(coins);

			//desc
			JSONObject descJson = rp.getJSONObject("descriptions");
			HashMap<LangType, String> descs = new HashMap<LangType, String>();
			descs.put(LangType.en_US, descJson.getString("en_US"));
			descs.put(LangType.zh_CN, descJson.getString("es_ES"));
			descs.put(LangType.es_ES, descJson.getString("zh_CN"));

			rechargePackage.setDesc(descs);

			rechargePackages.put(rechargePackage.getId(), rechargePackage);
			rechargePackageIds.add(rechargePackage.getId());

		}

		return true;
	}

}
