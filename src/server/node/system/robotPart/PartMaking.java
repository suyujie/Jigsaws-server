package server.node.system.robotPart;

import gamecore.util.DataUtils;
import javolution.util.FastTable;

public class PartMaking {

	private Integer id;
	private String name;
	private String bufferId;

	private PartSlotType partSlotType;
	private PartQualityType partQualityType;
	private WeaponType weaponType;
	private String suitName;
	private Integer version;

	private String scoreStr;
	private String atkStr;
	private String defStr;
	private String hpStr;
	private String crlStr;
	private String bufferStr;
	private String wqAddStr;
	private String expStr;
	private String skillStr;
	private String useExpStr;
	private Integer rarity;
	private Integer evolveId;

	private FastTable<Integer> bufferIdTable;
	private FastTable<Integer> scoreTable;
	private FastTable<Integer> atkTable;
	private FastTable<Integer> defTable;
	private FastTable<Integer> hpTable;
	private FastTable<Integer> crlTable;
	private FastTable<Integer> expTable;
	private FastTable<Integer> useExpTable;
	private FastTable<Integer> bufferValueTable;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBufferId() {
		return bufferId;
	}

	public void setBufferId(String bufferId) {
		this.bufferId = bufferId;
	}

	public String getScoreStr() {
		return scoreStr;
	}

	public void setScoreStr(String scoreStr) {
		this.scoreStr = scoreStr;
	}

	public String getAtkStr() {
		return atkStr;
	}

	public void setAtkStr(String atkStr) {
		this.atkStr = atkStr;
	}

	public String getDefStr() {
		return defStr;
	}

	public void setDefStr(String defStr) {
		this.defStr = defStr;
	}

	public String getHpStr() {
		return hpStr;
	}

	public void setHpStr(String hpStr) {
		this.hpStr = hpStr;
	}

	public String getCrlStr() {
		return crlStr;
	}

	public void setCrlStr(String crlStr) {
		this.crlStr = crlStr;
	}

	public String getBufferStr() {
		return bufferStr;
	}

	public void setBufferStr(String bufferStr) {
		this.bufferStr = bufferStr;
	}

	public String getWqAddStr() {
		return wqAddStr;
	}

	public void setWqAddStr(String wqAddStr) {
		this.wqAddStr = wqAddStr;
	}

	public String getExpStr() {
		return expStr;
	}

	public void setExpStr(String expStr) {
		this.expStr = expStr;
	}

	public String getSkillStr() {
		return skillStr;
	}

	public void setSkillStr(String skillStr) {
		this.skillStr = skillStr;
	}

	public String getUseExpStr() {
		return useExpStr;
	}

	public void setUseExpStr(String useExpStr) {
		this.useExpStr = useExpStr;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getEvolveId() {
		return evolveId;
	}

	public void setEvolveId(Integer evolveId) {
		this.evolveId = evolveId;
	}

	public FastTable<Integer> getScoreTable() {
		return scoreTable;
	}

	public void setScoreTable(FastTable<Integer> scoreTable) {
		this.scoreTable = scoreTable;
	}

	public FastTable<Integer> getAtkTable() {
		return atkTable;
	}

	public void setAtkTable(FastTable<Integer> atkTable) {
		this.atkTable = atkTable;
	}

	public FastTable<Integer> getDefTable() {
		return defTable;
	}

	public void setDefTable(FastTable<Integer> defTable) {
		this.defTable = defTable;
	}

	public FastTable<Integer> getHpTable() {
		return hpTable;
	}

	public void setHpTable(FastTable<Integer> hpTable) {
		this.hpTable = hpTable;
	}

	public FastTable<Integer> getCrlTable() {
		return crlTable;
	}

	public void setCrlTable(FastTable<Integer> crlTable) {
		this.crlTable = crlTable;
	}

	public FastTable<Integer> getExpTable() {
		return expTable;
	}

	public void setExpTable(FastTable<Integer> expTable) {
		this.expTable = expTable;
	}

	public FastTable<Integer> getUseExpTable() {
		return useExpTable;
	}

	public void setUseExpTable(FastTable<Integer> useExpTable) {
		this.useExpTable = useExpTable;
	}

	public FastTable<Integer> getBufferValueTable() {
		return bufferValueTable;
	}

	public void setBufferValueTable(FastTable<Integer> bufferValueTable) {
		this.bufferValueTable = bufferValueTable;
	}

	public Integer getRarity() {
		return rarity;
	}

	public void setRarity(Integer rarity) {
		this.rarity = rarity;
	}

	public PartSlotType getPartSlotType() {
		return partSlotType;
	}

	public void setPartSlotType(PartSlotType partSlotType) {
		this.partSlotType = partSlotType;
	}

	public PartQualityType getPartQualityType() {
		return partQualityType;
	}

	public void setPartQualityType(PartQualityType partQualityType) {
		this.partQualityType = partQualityType;
	}

	public WeaponType getWeaponType() {
		return weaponType;
	}

	public void setWeaponType(WeaponType weaponType) {
		this.weaponType = weaponType;
	}

	public FastTable<Integer> getBufferIdTable() {
		return bufferIdTable;
	}

	public void setBufferIdTable(FastTable<Integer> bufferIdTable) {
		this.bufferIdTable = bufferIdTable;
	}

	public String getSuitName() {
		return suitName;
	}

	public void setSuitName(String suitName) {
		this.suitName = suitName;
	}

	public int getScore(int level) {
		if (scoreTable == null) {
			return 0;
		} else {
			return scoreTable.get(level - 1);
		}
	}

	public int getAtk(int level) {
		if (atkTable == null) {
			return 0;
		} else {
			return atkTable.get(level - 1);
		}
	}

	public int getDef(int level) {
		if (defTable == null) {
			return 0;
		} else {
			return defTable.get(level - 1);
		}
	}

	public int getHp(int level) {
		if (hpTable == null) {
			return 0;
		} else {
			return hpTable.get(level - 1);
		}
	}

	public int getCrl(int level) {
		if (crlTable == null) {
			return 0;
		} else {
			return crlTable.get(level - 1);
		}
	}

	public int getExp(int level) {
		if (expTable == null) {
			return 0;
		} else {
			return expTable.get(level - 1);
		}
	}

	public int getUseExp(int level) {
		if (useExpTable == null) {
			return 0;
		} else {
			return useExpTable.get(level - 1);
		}
	}

	public int getBufferValue(int level) {
		if (bufferValueTable == null) {
			return 0;
		} else {
			return bufferValueTable.get(level - 1);
		}
	}

	public void setSlotQualitySuitType() {
		if (name.contains("@")) {
			//gun_weapon@cowboy_iron
			String[] names = name.replace("@", "_").split("_");
			weaponType = WeaponType.asEnum(names[0]);
			partSlotType = PartSlotType.asEnum(names[1]);
			partQualityType = PartQualityType.asEnum(names[3]);
			suitName = names[2];
		} else {
			//cowboy_arm_iron
			String[] names = name.split("_");
			suitName = names[0];
			partSlotType = PartSlotType.asEnum(names[1]);
			partQualityType = PartQualityType.asEnum(names[2]);
		}
	}

	public void setTables() {

		setBufferIdTable(DataUtils.string2FastTable(getBufferId()));
		setScoreTable(DataUtils.string2FastTable(getScoreStr()));
		setAtkTable(DataUtils.string2FastTable(getAtkStr()));
		setDefTable(DataUtils.string2FastTable(getDefStr()));
		setCrlTable(DataUtils.string2FastTable(getCrlStr()));
		setExpTable(DataUtils.string2FastTable(getExpStr()));
		setUseExpTable(DataUtils.string2FastTable(getUseExpStr()));
		setHpTable(DataUtils.string2FastTable(getHpStr()));
		setBufferValueTable(DataUtils.string2FastTable(getBufferStr()));

	}

	public void checkPartMaking() {
		int level = getScoreTable().size();

		int expLevel = getExpTable().size();
		int useExpLevel = getUseExpTable().size();
		int atkLevel = getAtkTable() == null ? level : getAtkTable().size();
		int defLevel = getDefTable() == null ? level : getDefTable().size();
		int hpLevel = getHpTable() == null ? level : getHpTable().size();
		int crlLevel = getCrlTable() == null ? level : getCrlTable().size();

		if (level != expLevel || level != useExpLevel || level != atkLevel || level != defLevel || level != hpLevel || level != crlLevel) {
			System.out.println("error: " + partSlotType + "[" + getId() + "] level:[" + level + "] expLevel:[" + expLevel + "] useExpLevel:[" + useExpLevel + "] atk:[" + atkLevel
					+ "] def:[" + defLevel + "] hp:[" + hpLevel + "] ctl:[" + crlLevel);
		}
	}
}