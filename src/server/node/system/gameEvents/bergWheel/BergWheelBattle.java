package server.node.system.gameEvents.bergWheel;

import server.node.system.robotPart.PartQualityType;
import server.node.system.robotPart.WeaponType;

public final class BergWheelBattle {

	private String bossSuit;
	private Integer bossLevel;
	private WeaponType weaponType;
	private PartQualityType qualityType;

	//如果胜利了,得到的水晶id和数量
	private int bergId;
	private int bergNum;

	public BergWheelBattle() {
	}

	public BergWheelBattle(Integer bossLevel) {
		this.bossLevel = bossLevel;
	}

	public Integer getBossLevel() {
		return bossLevel;
	}

	public void setBossLevel(Integer bossLevel) {
		this.bossLevel = bossLevel;
	}

	public WeaponType getWeaponType() {
		return weaponType;
	}

	public void setWeaponType(WeaponType weaponType) {
		this.weaponType = weaponType;
	}

	public int getBergId() {
		return bergId;
	}

	public void setBergId(int bergId) {
		this.bergId = bergId;
	}

	public int getBergNum() {
		return bergNum;
	}

	public void setBergNum(int bergNum) {
		this.bergNum = bergNum;
	}

	public String getBossSuit() {
		return bossSuit;
	}

	public void setBossSuit(String bossSuit) {
		this.bossSuit = bossSuit;
	}

	public PartQualityType getQualityType() {
		return qualityType;
	}

	public void setQualityType(PartQualityType qualityType) {
		this.qualityType = qualityType;
	}

}
