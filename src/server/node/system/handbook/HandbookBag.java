package server.node.system.handbook;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;
import gamecore.serialize.SerializerJson;

import java.util.HashMap;

import server.node.system.Content;
import server.node.system.robotPart.PartMaking;
import server.node.system.robotPart.PartQualityType;
import server.node.system.robotPart.PartSlotType;

/**
 * 图鉴
 */
public class HandbookBag extends AbstractEntity {

	private static final long serialVersionUID = 6996586475946806158L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "handbook_bag_";

	private Long id;

	/**
	 * 结构
	 * 机器人套装  --  部件  --  钛16 金8 银4 铜2 铁1
	 */
	private HashMap<String, HashMap<PartSlotType, Integer>> handbooks = new HashMap<String, HashMap<PartSlotType, Integer>>();

	private HashMap<String, Boolean> rewarded = new HashMap<String, Boolean>();

	public HandbookBag() {
	}

	public HandbookBag(Long id) {
		super(HandbookBag.generateCacheKey(id));
		this.id = id;
	}

	public HandbookBag(Long id, HashMap<String, HashMap<PartSlotType, Integer>> handbooks, HashMap<String, Boolean> rewarded) {
		super(HandbookBag.generateCacheKey(id));
		this.id = id;
		this.handbooks = handbooks;
		this.rewarded = rewarded;
	}

	private int quality2Int(PartQualityType type) {
		switch (type) {
		case IRON:
			return 1;
		case COPPER:
			return 2;
		case SILVER:
			return 4;
		case GOLD:
			return 8;
		case TITANIUM:
			return 16;
		default:
			return 0;
		}
	}

	public HashMap<String, HashMap<PartSlotType, Integer>> getHandbooks() {
		return handbooks;
	}

	public void setHandbooks(HashMap<String, HashMap<PartSlotType, Integer>> handbooks) {
		this.handbooks = handbooks;
	}

	public HashMap<String, Boolean> getRewarded() {
		return rewarded;
	}

	public void setRewarded(HashMap<String, Boolean> rewarded) {
		this.rewarded = rewarded;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 获取新部件,只算头身子胳膊腿武器
	 */
	public boolean addNewHandbook(PartMaking partMaking, boolean sync) {

		if (partMaking.getPartSlotType() != null && partMaking.getPartSlotType().asCode() <= PartSlotType.WEAPON.asCode()) {

			boolean update = false;
			HashMap<PartSlotType, Integer> partSlots = handbooks.get(partMaking.getSuitName());
			if (partSlots == null) {
				partSlots = new HashMap<PartSlotType, Integer>();
				update = true;
			}

			Integer qualityHave = partSlots.get(partMaking.getPartSlotType());

			if (qualityHave == null) {
				qualityHave = 0;
				update = true;
			}

			int q = quality2Int(partMaking.getPartQualityType());

			if (qualityHave == 0 || qualityHave / q == 0) {//以前没有过,得加上
				qualityHave += q;
				update = true;
				if (qualityHave > 31) {
					qualityHave = 31;
				}
			}

			if (update) {
				partSlots.put(partMaking.getPartSlotType(), qualityHave);
				handbooks.put(partMaking.getSuitName(), partSlots);
			}

			if (sync) {
				this.synchronize();
			}
			return update;
		} else {
			return false;
		}

	}

	/**
	 * 可否领取奖励
	 */
	public boolean checkRewardHandbook(String suitName) {

		Boolean reward = rewarded.get(suitName);
		if (reward) {//已经领取了,不能重复领取
			return false;
		}

		HashMap<PartSlotType, Integer> partSlots = handbooks.get(suitName);
		if (partSlots == null) {
			return false;
		}

		for (Integer i : partSlots.values()) {
			if (i != 31) {
				return false;
			}
		}

		return true;

	}

	/**
	 * 领取钻石奖励
	 */
	public void rewardHandbook(String suitName, boolean sync) {

		Boolean reward = rewarded.get(suitName);
		if (reward == null || !reward) {//没领取或者为null
			rewarded.put(suitName, true);//标记为领取
		}

		if (sync) {
			this.synchronize();
		}

	}

	public byte[] readHandbook(String suitName) {

		byte[] r = { (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0 };

		HashMap<PartSlotType, Integer> handbook = handbooks.get(suitName);
		if (handbook == null) {
			return r;
		} else {
			//HEAD(0), BODY(1), ARM(2), LEG(3), WEAPON(4)
			r[0] = handbook.get(PartSlotType.HEAD) == null ? (byte) 0 : handbook.get(PartSlotType.HEAD).byteValue();
			r[1] = handbook.get(PartSlotType.BODY) == null ? (byte) 0 : handbook.get(PartSlotType.BODY).byteValue();
			r[2] = handbook.get(PartSlotType.ARM) == null ? (byte) 0 : handbook.get(PartSlotType.ARM).byteValue();
			r[3] = handbook.get(PartSlotType.LEG) == null ? (byte) 0 : handbook.get(PartSlotType.LEG).byteValue();
			r[4] = handbook.get(PartSlotType.WEAPON) == null ? (byte) 0 : handbook.get(PartSlotType.WEAPON).byteValue();
		}
		return r;
	}

	//领取过钻石吗?
	public boolean haveRewardedGold(String suitName) {
		Boolean reward = rewarded.get(suitName);
		if (reward == null || !reward) {
			return false;
		} else {
			return true;
		}
	}

	//	public byte[] toStorageHandbooksBytes() {
	//		return SerializerKryo.serialize(handbooks);
	//	}
	//
	//	public byte[] toStorageRewardedBytes() {
	//		return SerializerKryo.serialize(rewarded);
	//	}

	public String toStorageHandbooksJson() {
		return SerializerJson.serialize(handbooks);
	}

	public String toStorageRewardedJson() {
		return SerializerJson.serialize(rewarded);
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(HandbookBag.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

}
