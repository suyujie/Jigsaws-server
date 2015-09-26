package server.node.system.expPart;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;
import gamecore.io.ByteArrayGameOutput;
import gamecore.serialize.SerializerJson;
import gamecore.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Content;
import server.node.system.player.Player;

public class ExpPartBag extends AbstractEntity {

	private static final long serialVersionUID = -2268784886243625326L;

	private static Logger logger = LogManager.getLogger(ExpPartBag.class.getName());

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "exp_part_bag_";

	private HashMap<Integer, Integer> expParts = new HashMap<Integer, Integer>();

	public ExpPartBag() {
	}

	public ExpPartBag(Player player, HashMap<Integer, Integer> expParts) {
		super(ExpPartBag.generateCacheKey(player.getId()));
		this.expParts = expParts;
	}

	public HashMap<Integer, Integer> getExpParts() {
		return expParts;
	}

	public void setExpParts(HashMap<Integer, Integer> expParts) {
		this.expParts = expParts;
	}

	public void addExpPart(Integer expPartId, int num, boolean sync) {
		if (expPartId != null) {
			if (expParts.get(expPartId) == null) {
				expParts.put(expPartId, num);
			} else {
				expParts.put(expPartId, expParts.get(expPartId) + num);
			}
			if (sync) {
				this.synchronize();
			}
		}
	}

	public List<Integer> readExpPartList() {
		List<Integer> list = new ArrayList<Integer>();

		Iterator<Entry<Integer, Integer>> it = expParts.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Integer> entry = (Entry<Integer, Integer>) it.next();
			for (int i = 0; i < entry.getValue(); i++) {
				list.add(entry.getKey());
			}
		}

		return list;
	}

	public boolean removeExpPart(Integer expPartId, int num, boolean sync) {
		if (expPartId != null) {
			if (expParts.get(expPartId) != null && expParts.get(expPartId) >= num) {
				int newNum = expParts.get(expPartId) - num;
				if (newNum == 0) {
					expParts.remove(expPartId);
				} else {
					expParts.put(expPartId, newNum);
				}
				if (sync) {
					this.synchronize();
				}
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public Integer randomOne() {
		if (expParts != null && !expParts.isEmpty()) {
			return Utils.randomSelectMapKey(expParts);
		}
		return null;
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(ExpPartBag.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	public byte[] toByteArray() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			//循环发送组件信息
			if (expParts != null) {
				Iterator<Entry<Integer, Integer>> it = expParts.entrySet().iterator();
				while (it.hasNext()) {
					Entry<Integer, Integer> entry = (Entry<Integer, Integer>) it.next();
					bago.putShort(entry.getKey().shortValue());
					bago.putShort((short) 1);
					bago.putInt(entry.getValue());
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}

	public String toStorgeJson() {
		return SerializerJson.serialize(expParts);
	}

}
