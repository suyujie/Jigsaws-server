package server.node.system.egg;

import gamecore.entity.AbstractEntity;
import gamecore.io.ByteArrayGameOutput;

import java.util.HashMap;

import javolution.util.FastTable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.player.Player;

/**
 * 在egg里的部件部件包实体。
 */
public class EggPartBag extends AbstractEntity {

	private static final Logger logger = LogManager.getLogger(EggPartBag.class.getName());

	private static final long serialVersionUID = -6956816268709434424L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "egg_partbag_";

	private FastTable<Integer> eggCostTable;
	private HashMap<Integer, EggPart> eggParts;

	private long logId;

	public EggPartBag() {
	}

	public EggPartBag(long logId, Player player, HashMap<Integer, EggPart> eggParts, FastTable<Integer> eggCostTable) {
		super(EggPartBag.generateCacheKey(player.getId()));//玩家playerId当作存储键值
		this.logId = logId;
		this.eggParts = eggParts;
		this.eggCostTable = eggCostTable;
	}

	public HashMap<Integer, EggPart> getEggParts() {
		return eggParts;
	}

	public void setEggParts(HashMap<Integer, EggPart> eggParts) {
		this.eggParts = eggParts;
	}

	public FastTable<Integer> getEggCostTable() {
		return eggCostTable;
	}

	public long getLogId() {
		return logId;
	}

	public void setLogId(long logId) {
		this.logId = logId;
	}

	public Integer getEggCost(int num) {
		if (eggCostTable == null) {
			return 0;
		} else {
			if (eggCostTable.size() < num) {
				return eggCostTable.get(eggCostTable.size() - 1);
			} else {
				return eggCostTable.get(num);
			}
		}
	}

	public void setEggCostTable(FastTable<Integer> eggCostTable) {
		this.eggCostTable = eggCostTable;
	}

	/**
	 * 取出一个part
	 */
	public EggPart takeEggPart(Integer eggIndex, boolean sync) {
		EggPart eggPart = this.eggParts.remove(eggIndex);
		if (sync) {
			this.synchronize();
		}
		return eggPart;
	}

	/**
	 * 当前数量
	 */
	public int getEggPartNum() {
		return this.eggParts.size();
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(EggPartBag.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	public byte[] toByteArray() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			//循环发送组件信息
			if (eggParts != null && !eggParts.isEmpty()) {
				int i = 0;
				for (EggPart eggPart : eggParts.values()) {
					bago.putBytesNoLength(eggPart.toByteArrayAsEgg(getEggCost(i)));
					i++;
				}
			}

		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}

}
