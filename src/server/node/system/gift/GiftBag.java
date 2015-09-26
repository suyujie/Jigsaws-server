package server.node.system.gift;

import gamecore.entity.AbstractEntity;
import gamecore.serialize.SerializerJson;

import java.util.Map;

import server.node.system.gift.gift.GiftWear;

/**
 * 大关卡包实体。
 */
public class GiftBag extends AbstractEntity {

	private static final long serialVersionUID = 3561068634267201050L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "gift_bag_";

	private Map<Long, GiftWear> giftWears;

	public GiftBag() {
	}

	public GiftBag(Long playerId, Map<Long, GiftWear> giftWears) {
		super(generateCacheKey(playerId));
		this.giftWears = giftWears;
	}

	public Map<Long, GiftWear> getGiftWears() {
		return giftWears;
	}

	public void setGiftWears(Map<Long, GiftWear> giftWears) {
		this.giftWears = giftWears;
	}

	//增加一份耐久礼物
	public void addGiftWear(GiftWear giftWear, boolean sync) {
		giftWears.put(giftWear.getId(), giftWear);
		if (sync) {
			this.synchronize();
		}
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long playerId) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(GiftBag.CKPrefix).append(playerId).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	public String toJson() {
		return SerializerJson.serialize(giftWears);
	}

}
