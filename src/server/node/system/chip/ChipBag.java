package server.node.system.chip;

import gamecore.entity.AbstractEntity;
import gamecore.io.ByteArrayGameOutput;
import gamecore.serialize.SerializerJson;
import gamecore.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 芯片  魂
 */
public class ChipBag extends AbstractEntity {

	private static final long serialVersionUID = 3831845578030122134L;

	private static final Logger logger = LogManager.getLogger(ChipBag.class.getName());

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "chip_bag_";

	private HashMap<String, Integer> chips = new HashMap<String, Integer>();

	public ChipBag() {
	}

	public ChipBag(Long playerId, HashMap<String, Integer> chips) {
		super(ChipBag.generateCacheKey(playerId));//玩家playerId当作存储键值
		this.chips = chips;
	}

	public HashMap<String, Integer> getChips() {
		return chips;
	}

	public void setChips(HashMap<String, Integer> chips) {
		this.chips = chips;
	}

	public void addChip(String chipkey, int num, boolean sync) {
		if (chipkey != null && !chipkey.equals("null")) {
			if (chips.containsKey(chipkey)) {
				chips.put(chipkey, chips.get(chipkey) + num);
			} else {
				chips.put(chipkey, num);
			}
			if (sync) {
				this.synchronize();
			}
		}
	}

	public boolean removeChip(String chipkey, int num, boolean sync) {
		if (chipkey == null || chipkey.equals("null")) {
			return false;
		}
		if (chips.get(chipkey) >= num) {
			chips.put(chipkey, chips.get(chipkey) - num);
			if (sync) {
				this.synchronize();
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean removeChip(String chipkey, boolean sync) {
		if (chipkey == null || chipkey.equals("null")) {
			return false;
		}
		chips.put(chipkey, 0);
		if (sync) {
			this.synchronize();
		}
		return true;
	}

	public String randomOneChip() {
		if (chips != null && !chips.isEmpty()) {
			return Utils.randomSelectMapKey(chips);
		}
		return null;
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(ChipBag.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	public String toStorageJson() {
		return SerializerJson.serialize(chips);
	}

	public byte[] toByteArray() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			bago.putInt(chips.size());
			for (Map.Entry<String, Integer> entry : chips.entrySet()) {
				bago.putString(entry.getKey());
				bago.putInt(entry.getValue());
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}

}
