package server.node.system.robotPart;

import gamecore.util.Utils;

/** 
 * 武器类型
 */
public enum WeaponType {

	//			weapon_1 = 长柄
	//			weapon_2 = 重击
	//			weapon_3 = 双持
	//			weapon_4 = 火枪
	//			weapon_5 = 盾牌

	NONE(0), SPEAR(1), HEAVY(2), DOUBLE(3), GUN(4), SHIELD(5);

	private int sc;

	private WeaponType(int code) {
		this.sc = code;
	}

	public int asCode() {
		return this.sc;
	}

	public static WeaponType asEnum(int code) {
		for (WeaponType partType : WeaponType.values()) {
			if (partType.asCode() == code) {
				return partType;
			}
		}
		return null;
	}

	public static WeaponType asEnum(String code) {
		for (WeaponType type : WeaponType.values()) {
			if (type.toString().toUpperCase().equals(code.toUpperCase())) {
				return type;
			}
		}
		return null;
	}

	public static WeaponType randomOneWithOutNone() {
		return asEnum(Utils.randomInt(1, 5));
	}

}
