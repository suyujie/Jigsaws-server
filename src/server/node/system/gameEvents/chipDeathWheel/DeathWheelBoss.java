package server.node.system.gameEvents.chipDeathWheel;

import gamecore.io.ByteArrayGameOutput;
import server.node.system.gameEvents.chipDeathWheel.makingData.DeathWheelBossMaking;
import server.node.system.gameEvents.chipDeathWheel.makingData.DeathWheelExtraBossMaking;
import server.node.system.player.Player;
import server.node.system.robotPart.WeaponType;

public final class DeathWheelBoss {

	private String chipName;
	private Integer bossMakingId;//对应xml里面的id,实际上是0---4 难度
	private WeaponType weaponType;

	private String fieldName;
	private String field;
	private String other;
	private int bossLevel;

	private int doNum = 0;
	private int passNum = 0;

	public DeathWheelBoss() {
	}

	public DeathWheelBoss(Integer bossMakingId, int bossLevel, int doNum, int passNum) {
		this.bossMakingId = bossMakingId;
		this.bossLevel = bossLevel;
		this.doNum = doNum;
		this.passNum = passNum;
	}

	public String getChipName() {
		return chipName;
	}

	public void setChipName(String chipName) {
		this.chipName = chipName;
	}

	public int getBossLevel() {
		return bossLevel;
	}

	public void setBossLevel(int bossLevel) {
		this.bossLevel = bossLevel;
	}

	public Integer getBossMakingId() {
		return bossMakingId;
	}

	public void setBossMakingId(Integer bossMakingId) {
		this.bossMakingId = bossMakingId;
	}

	public WeaponType getWeaponType() {
		return weaponType;
	}

	public void setWeaponType(WeaponType weaponType) {
		this.weaponType = weaponType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public int getDoNum() {
		return doNum;
	}

	public void setDoNum(int doNum) {
		this.doNum = doNum;
	}

	public int getPassNum() {
		return passNum;
	}

	public void setPassNum(int passNum) {
		this.passNum = passNum;
	}

	public byte[] toByteArray(Player player) {
		ByteArrayGameOutput go = new ByteArrayGameOutput();
		try {
			go.putString(chipName);
			go.putString(fieldName);

			DeathWheelBossMaking deathWheelBossMaking = DeathWheelLoadData.getInstance().getDeathWheelBossMaking(bossMakingId);
			DeathWheelExtraBossMaking extraBossMaking = DeathWheelLoadData.getInstance().getDeathWheelExtraBossMaking(weaponType);

			go.putInt(deathWheelBossMaking.getScoreTable().get(bossLevel - 1));
			go.putInt(deathWheelBossMaking.getAtkTable().get(bossLevel - 1));
			go.putInt(deathWheelBossMaking.getDefTable().get(bossLevel - 1));
			Float hpScale = extraBossMaking.getHpScale() * deathWheelBossMaking.getHpTable().get(bossLevel - 1);
			go.putInt(hpScale.intValue());
			go.putInt(deathWheelBossMaking.getCrtTable().get(bossLevel - 1));
			go.putInt(deathWheelBossMaking.getScale());
			go.putInt(weaponType.asCode());

			go.putString(field);
			go.putString(other);

			go.putBytesNoLength(extraBossMaking.toByteArray());

			go.putInt(1);
			go.putInt(passNum);
			go.putInt(deathWheelBossMaking.getChipnum());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return go.toByteArray();
	}

}
