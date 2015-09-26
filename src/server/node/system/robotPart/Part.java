package server.node.system.robotPart;

import gamecore.io.ByteArrayGameOutput;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.player.MoneyType;

public class Part implements Serializable {

	private static final Logger logger = LogManager.getLogger(Part.class.getName());

	private static final long serialVersionUID = -1388986563767686229L;

	private Long storeId;
	private int partSlotType;
	private Integer makingId;
	private int addRarity;
	private int level;
	private int exp;
	private int evExp;
	private int color;
	private List<Integer> bufVal;

	public Part() {
	}

	public Part(Long storeId, int partSlotType, Integer makingId, int addRarity, int level, int exp, int color, List<Integer> bufVal) {
		super();
		this.partSlotType = partSlotType;
		this.storeId = storeId;
		this.makingId = makingId;
		this.addRarity = addRarity;
		this.level = level;
		this.exp = exp;
		this.color = color;
		this.bufVal = bufVal;
	}

	public Integer getMakingId() {
		return makingId;
	}

	public void setMakingId(Integer makingId) {
		this.makingId = makingId;
	}

	public int getAddRarity() {
		return addRarity;
	}

	public void setAddRarity(int addRarity) {
		this.addRarity = addRarity;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getPartSlotType() {
		return partSlotType;
	}

	public void setPartSlotType(int partSlotType) {
		this.partSlotType = partSlotType;
	}

	public List<Integer> getBufVal() {
		return bufVal;
	}

	public void setBufVal(List<Integer> bufVal) {
		this.bufVal = bufVal;
	}

	public Long getStoreId() {
		return storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	public int getEvExp() {
		return evExp;
	}

	public void setEvExp(int evExp) {
		this.evExp = evExp;
	}

	public int maxLevel() {
		PartMaking making = PartLoadData.getInstance().getMaking(partSlotType, makingId);
		int r = making.getRarity() + addRarity;
		if (r == 1)
			return 15;
		else
			return r * 10;

	}

	public byte[] toByteArray() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			bago.putLong(storeId);
			bago.putShort(makingId.shortValue());
			bago.putShort((short) partSlotType);
			bago.putShort((short) level);
			bago.putInt(exp);
			bago.putInt(evExp);
			bago.put((byte) addRarity);
			bago.putInt(color);

			if (bufVal != null && !bufVal.isEmpty()) {
				bago.putInt(bufVal.size());
				for (Integer bv : bufVal) {
					bago.putInt(bv);
				}
			} else {
				bago.putInt(0);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}

	public byte[] toByteArrayAsNew() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			bago.putLong(storeId);
			bago.putShort(makingId.shortValue());
			bago.putShort((short) partSlotType);
			bago.putShort((short) level);
			bago.putInt(exp);
			bago.putInt(color);

			if (bufVal != null && !bufVal.isEmpty()) {
				bago.putInt(bufVal.size());
				for (Integer bv : bufVal) {
					bago.putInt(bv);
				}
			} else {
				bago.putInt(0);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}

	public byte[] toByteArrayAsDefender() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			bago.putLong(storeId);
			bago.putShort(makingId.shortValue());
			bago.putShort((short) partSlotType);
			bago.putShort((short) level);
			bago.putInt(exp);
			bago.putInt(color);

			if (bufVal != null && !bufVal.isEmpty()) {
				bago.putInt(bufVal.size());
				for (Integer bv : bufVal) {
					bago.putInt(bv);
				}
			} else {
				bago.putInt(0);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}

	public byte[] toByteArrayAsEgg(int gold) {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {

			bago.putInt(MoneyType.GOLD.asCode());
			bago.putInt(gold);
			bago.putLong(storeId);
			bago.putShort(makingId.shortValue());
			bago.putShort((short) partSlotType);
			bago.putShort((short) level);

			if (bufVal != null && !bufVal.isEmpty()) {
				bago.putInt(bufVal.size());
				for (Integer bv : bufVal) {
					bago.putInt(bv);
				}
			} else {
				bago.putInt(0);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}

}
