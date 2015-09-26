package server.node.system.berg;

public class BergMaking {

	private Integer id;
	private Integer level;
	private Integer type;
	private BergType bergType;
	private Integer value;
	private Integer fuseNum;
	private Integer fuseMoney;
	private Integer fraction;
	private String picture;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public BergType getBergType() {
		return bergType;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Integer getFuseNum() {
		return fuseNum;
	}

	public void setFuseNum(Integer fuseNum) {
		this.fuseNum = fuseNum;
	}

	public Integer getFuseMoney() {
		return fuseMoney;
	}

	public void setFuseMoney(Integer fuseMoney) {
		this.fuseMoney = fuseMoney;
	}

	public Integer getFraction() {
		return fraction;
	}

	public void setFraction(Integer fraction) {
		this.fraction = fraction;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public void setBergType(BergType bergType) {
		this.bergType = bergType;
	}

	public void setBergType() {
		this.bergType = BergType.asEnum(type);
	}

}
