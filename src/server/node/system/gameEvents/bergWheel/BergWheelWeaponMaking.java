package server.node.system.gameEvents.bergWheel;

import server.node.system.robotPart.WeaponType;

public class BergWheelWeaponMaking {

	private int weaponType;
	private WeaponType wType;
	private int ai;
	private int crt;

	public int getWeaponType() {
		return weaponType;
	}

	public void setWeaponType(int weaponType) {
		this.weaponType = weaponType;
	}

	public int getAi() {
		return ai;
	}

	public void setAi(int ai) {
		this.ai = ai;
	}

	public int getCrt() {
		return crt;
	}

	public void setCrt(int crt) {
		this.crt = crt;
	}

	public WeaponType getwType() {
		return wType;
	}

	public void setwType(WeaponType wType) {
		this.wType = wType;
	}

}
