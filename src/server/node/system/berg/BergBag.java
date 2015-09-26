package server.node.system.berg;

import gamecore.entity.AbstractEntity;
import gamecore.io.ByteArrayGameOutput;
import gamecore.serialize.SerializerJson;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BergBag extends AbstractEntity {

	private static final long serialVersionUID = -5516134298560169105L;

	private static final Logger logger = LogManager.getLogger(BergBag.class.getName());

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "berg_bag_";

	private HashMap<Integer, Integer> bergs = new HashMap<Integer, Integer>();

	public BergBag() {
	}

	public BergBag(Long playerId, HashMap<Integer, Integer> bergs) {
		super(BergBag.generateCacheKey(playerId));//玩家playerId当作存储键值
		this.bergs = bergs;
	}

	public HashMap<Integer, Integer> getBergs() {//不要为0的
		Iterator<Map.Entry<Integer, Integer>> it = bergs.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Integer> entry = it.next();
			if (entry.getValue() <= 0) {
				it.remove();
			}
		}
		return bergs;
	}

	public void setBergs(HashMap<Integer, Integer> bergs) {
		this.bergs = bergs;
	}

	public void addBerg(Integer bergId, int num, boolean sync) {
		if (bergs == null) {
			bergs = new HashMap<Integer, Integer>();
		}
		if (bergs.get(bergId) == null) {
			bergs.put(bergId, num);
		} else {
			bergs.put(bergId, bergs.get(bergId) + num);
		}
		if (sync) {
			this.synchronize();
		}
	}

	public boolean removeBerg(Integer bergId, int num, boolean sync) {
		if (bergs == null || bergs.isEmpty()) {
			return false;
		}
		if (bergs.get(bergId) != null && bergs.get(bergId) >= num) {
			bergs.put(bergId, bergs.get(bergId) - num);
			if (bergs.get(bergId) <= 0) {//判断是否<=0
				bergs.remove(bergId);
			}
			if (sync) {
				this.synchronize();
			}
			return true;
		} else {
			return false;
		}
	}

	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(BergBag.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	public String toStorageJson() {
		return SerializerJson.serialize(getBergs());
	}

	public byte[] toByteArray() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			HashMap<Integer, Integer> map = getBergs();
			bago.putInt(map.size());
			for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
				bago.putInt(entry.getKey());
				bago.putInt(entry.getValue());
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}

}
