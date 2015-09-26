package server.node.system.robotPart;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.serialize.SerializerJson;
import gamecore.system.AbstractSystem;
import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;
import gamecore.util.Utils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastTable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.dao.PartDao;
import server.node.system.Root;
import server.node.system.egg.EggPart;
import server.node.system.egg.EggPartBag;
import server.node.system.egg.EggPartMessage;
import server.node.system.player.CashType;
import server.node.system.player.GoldType;
import server.node.system.player.Player;

import com.alibaba.fastjson.TypeReference;

/**
 * 部件系统。
 * part的存储id,为一个不重复的long型数字
 * part的缓存id为  storeId
 */
public final class PartSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(PartSystem.class.getName());

	public PartSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("PartSystem start....");
		// 读取制作数据
		boolean b = PartLoadData.getInstance().readData();
		b = b & QualityLoadData.getInstance().readData();
		b = b & RarityUpgradeLoadData.getInstance().readData();

		System.out.println("PartSystem start....OK");
		return b;
	}

	@Override
	public void shutdown() {
	}

	//partbag
	public PartBag getPartBag(Player player) throws SQLException {
		PartBag partBag = RedisHelperJson.getPartBag(player.getId());
		if (partBag == null) {
			partBag = readPartBagFromDB(player);
			partBag.synchronize();
		}
		return partBag;
	}

	//数据库读取partbag
	private PartBag readPartBagFromDB(Player player) throws SQLException {
		PartBag partBag = null;

		PartDao partDao = DaoFactory.getInstance().borrowPartDao();
		Map<String, Object> map = partDao.readPartBag(player);
		DaoFactory.getInstance().returnPartDao(partDao);

		if (map != null) {
			try {
				String partsJson = (String) map.get("parts");
				HashMap<Long, Part> parts = (HashMap<Long, Part>) SerializerJson.deSerializeMap(partsJson, new TypeReference<HashMap<Long, Part>>() {
				});

				//生成partbag
				partBag = new PartBag(player, parts);

				//生成part
				for (Part part : partBag.readAllParts()) {
					partBag.addPart(part, false);//缓存中存入了part,但是没有更新partBag
				}
				partBag.synchronize();//一次性更新partBag

			} catch (Exception e) {
				logger.error(e);
			}
		} else {
			partBag = initPartBag(player);
		}

		return partBag;
	}

	//初始化Partbag
	private PartBag initPartBag(Player player) {

		HashMap<Long, Part> parts = new HashMap<Long, Part>();

		PartBag partBag = new PartBag(player, parts);

		PartDao partDao = DaoFactory.getInstance().borrowPartDao();
		partDao.save(player, partBag);
		DaoFactory.getInstance().returnPartDao(partDao);

		return partBag;
	}

	//生成一个part
	public Part createPart(long storeId, int partType, Integer makingId, int level, int exp, int color) {

		PartMaking partMaking = PartLoadData.getInstance().getMaking(partType, makingId);

		if (partMaking == null) {
			return null;
		}

		List<Integer> bufVals = Root.buffSystem.calculateBuffValue(partMaking);

		Part part = new Part(storeId, partType, makingId, 0, level, exp, color, bufVals);
		return part;
	}

	//生成一个part
	public Part createPart(long storeId, int partType, PartMaking partMaking, int level, int exp, int color) {
		List<Integer> bufVals = Root.buffSystem.calculateBuffValue(partMaking);
		Part part = new Part(storeId, partType, partMaking.getId(), 0, level, exp, color, bufVals);
		return part;
	}

	//得到一个part
	public Part addPart(Player player, long storeId, int partType, Integer id, int level, int exp, int color) throws SQLException {
		PartBag partBag = getPartBag(player);
		Part part = createPart(storeId, partType, id, level, exp, color);
		partBag.addPart(part, true);
		//存入数据
		updatePartBag(player, partBag);

		PartMessage partMessage = new PartMessage(PartMessage.PART_GET, player, part);
		this.publish(partMessage);

		return part;
	}

	//得到一个part
	public Part addPart(Player player, Part part) throws SQLException {
		PartBag partBag = getPartBag(player);
		partBag.addPart(part, true);
		//存入数据
		updatePartBag(player, partBag);

		PartMessage partMessage = new PartMessage(PartMessage.PART_GET, player, part);
		this.publish(partMessage);

		return part;
	}

	//得到一个part
	public Part addPart(Player player, PartSlotType type, Integer makingId) throws SQLException {

		Part part = createPart(Root.idsSystem.takePartId(), type.asCode(), makingId, 1, 0, 0);

		PartBag partBag = getPartBag(player);
		partBag.addPart(part, true);
		//存入数据
		updatePartBag(player, partBag);

		PartMessage partMessage = new PartMessage(PartMessage.PART_GET, player, part);
		this.publish(partMessage);

		return part;
	}

	//得到多个 part 
	public void addParts(Player player, List<Part> parts, boolean asNew) throws SQLException {
		if (parts != null && !parts.isEmpty()) {
			PartBag partBag = getPartBag(player);
			for (Part part : parts) {
				if (part != null) {
					partBag.addPart(part, false);
				}
			}
			partBag.synchronize();

			//存入数据
			updatePartBag(player, partBag);

			if (asNew) {
				PartMessage partMessage = new PartMessage(PartMessage.PART_GET, player, parts);
				this.publish(partMessage);
			}
		}
	}

	/**
	 * 卖出组件
	 */
	public SystemResult sellPart(Player player, Long storeId) throws SQLException {
		SystemResult result = new SystemResult();

		PartBag partBag = Root.partSystem.getPartBag(player);
		Part part = partBag.getPart(storeId);

		if (part != null) {

			int cash = 0;

			PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());

			QualityMaking qualityMaking = QualityLoadData.getInstance().getQualityMaking(partMaking.getPartQualityType().asCode());

			int baseCash = qualityMaking.getPrice();//基础价格

			cash = baseCash + baseCash / 3 * (part.getLevel() - 1);

			//稀有度
			switch (partMaking.getRarity()) {
			case 1:
				cash = cash * 1;
				break;
			case 2:
				cash = cash * 12 / 10;
				break;
			case 3:
				cash = cash * 14 / 10;
				break;
			case 4:
				cash = cash * 16 / 10;
				break;
			case 5:
				cash = cash * 18 / 10;
				break;

			default:
				break;
			}

			if (part.getPartSlotType() == PartSlotType.WEAPON.asCode()) {//武器,额外*1.2
				cash = cash * 12 / 10;
			}

			Root.playerSystem.changeCash(player, cash, CashType.SELL_PART, true);

			partBag.removePart(storeId, true);
			updatePartBag(player, partBag);
		} else {
			logger.error("sell part store id[" + storeId + "] is not exits");
			result.setCode(ErrorCode.PART_SELL_ERROR_NO_PART);
		}

		return result;
	}

	//pvp结果构造egg 里面的part
	public EggPartBag addEggParts(Player player, HashMap<Integer, EggPart> eggParts, FastTable<Integer> eggCostTable) {

		EggPartBag eggPartBag = new EggPartBag(Utils.getOneLongId(), player, eggParts, eggCostTable);

		eggPartBag.synchronize();

		//加入抽蛋日志
		Root.logSystem.addEggLog(player, eggPartBag);

		return eggPartBag;
	}

	//抽取一个eggParat
	public SystemResult getEggPart(Player player, Integer eggIndex) throws SQLException {
		SystemResult result = new SystemResult();
		EggPartBag eggPartBag = RedisHelperJson.getEggPartBag(player.getId());
		if (eggPartBag == null) {
			result.setCode(ErrorCode.NO_EGG_BAG);
			return result;
		} else {
			//扣钱
			int costGold = 0;
			if (eggIndex == 0) {
				costGold = 0;
			} else {
				costGold = eggPartBag.getEggCost(eggIndex);
			}
			if (player.getGold() < costGold) {//gold不够
				result.setCode(ErrorCode.GET_EGG_GOLD_NOT_ENOUGH);
				return result;
			}
			EggPart eggPart = null;
			eggPart = eggPartBag.takeEggPart(eggIndex, true);
			if (eggPart != null) {//可能是能量瓶子,可能是颜色瓶,可能是身体部件
				if (eggPart.getPart() != null) {
					Root.partSystem.addPart(player, eggPart.getPart());
					EggPartMessage eggPartMessage = new EggPartMessage(EggPartMessage.GET_EGG_PART, player, eggPart.getPart());
					this.publish(eggPartMessage);
				}

				if (eggPart.getColorId() != null) {
					Root.colorSystem.addColorNum(player, eggPart.getColorId(), 1);
				}

				if (eggPart.getExpPartId() != null) {
					Root.expPartSystem.addExpPart(player, eggPart.getExpPartId(), 1);
				}
				//扣钱
				Root.playerSystem.changeGold(player, -costGold, GoldType.GET_EGG_PART_COST, true);

			} else {
				result.setCode(ErrorCode.EGG_BAG_NO_THIS_PART);
			}
			//更新抽蛋日志
			Root.logSystem.updateEggLog(player, eggPartBag);
		}

		return result;
	}

	public void updatePartBag(Player player, PartBag partBag) {
		PartDao partDao = DaoFactory.getInstance().borrowPartDao();
		partDao.update(player, partBag);
		DaoFactory.getInstance().returnPartDao(partDao);
	}

}
