package server.node.system.rechargePackage;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;
import gamecore.serialize.SerializerJson;

import java.util.ArrayList;
import java.util.List;

import server.node.system.Content;

public class RechargePackageBag extends AbstractEntity {

	private static final long serialVersionUID = 5655695115089532843L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "recharge_package_bag_";

	private Long id;

	private List<String> buyedIds = new ArrayList<String>();

	public RechargePackageBag() {
	}

	public RechargePackageBag(Long id, List<String> buyedIds) {
		super();
		this.id = id;
		this.buyedIds = buyedIds;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<String> getBuyedIds() {
		return buyedIds;
	}

	public void setBuyedIds(List<String> buyedIds) {
		this.buyedIds = buyedIds;
	}

	public boolean checkIsFirst(String id) {
		if (buyedIds == null || buyedIds.isEmpty()) {
			return true;
		}
		if (buyedIds.contains(id)) {
			return false;
		} else {
			return true;
		}
	}

	public boolean addBuyedId(String id, boolean sync) {
		boolean change = false;
		if (buyedIds == null) {
			buyedIds = new ArrayList<String>();
		}
		if (!buyedIds.contains(id)) {
			change = true;
			buyedIds.add(id);
			if (sync) {
				this.synchronize();
			}
		}
		return change;
	}

	//是否付过费
	public boolean checkPaid() {
		if (buyedIds == null || buyedIds.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public String toStorageBuyedIdsJson() {
		return SerializerJson.serialize(buyedIds);
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(RechargePackageBag.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

}
