package server.node.system.robotPart;

import gamecore.util.Utils;

/** 
 * 部件类型
 */
public enum PartSlotType {

	HEAD(0, "head"), BODY(1, "body"), ARM(2, "arm"), LEG(3, "leg"), WEAPON(4, "weapon")

	;

	private int sc;
	private String desc;

	private PartSlotType(int code, String desc) {
		this.sc = code;
		this.desc = desc;
	}

	public int asCode() {
		return this.sc;
	}

	public String asDesc() {
		return this.desc;
	}

	public static PartSlotType asEnum(int code) {
		for (PartSlotType partType : PartSlotType.values()) {
			if (partType.asCode() == code) {
				return partType;
			}
		}
		return null;
	}

	public static PartSlotType asEnum(String code) {
		for (PartSlotType partSlotType : PartSlotType.values()) {
			if (partSlotType.toString().toUpperCase().equals(code.toUpperCase())) {
				return partSlotType;
			}
		}
		return null;
	}

	public static PartSlotType asEnumByDesc(String desc) {
		for (PartSlotType partSlotType : PartSlotType.values()) {
			if (partSlotType.asDesc().toUpperCase().equals(desc.toUpperCase())) {
				return partSlotType;
			}
		}
		return null;
	}

	public static PartSlotType randAsRobotPart() {
		int sc = Utils.randomInt(0, 4);
		return asEnum(sc);
	}

}
