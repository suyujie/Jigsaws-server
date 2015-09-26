package server.node.system.gameEvents.treasureIsland;

import gamecore.util.Utils;

/** 
 * 金银岛  钱  exp
 */
public enum TreasureIslandType {

	CASH(0, "cash"), EXP(1, "exp");

	private int sc;
	private String desc;

	private TreasureIslandType(int code, String desc) {
		this.sc = code;
		this.desc = desc;
	}

	public int asCode() {
		return this.sc;
	}

	public String asDesc() {
		return this.desc;
	}

	public static TreasureIslandType asEnum(int code) {
		for (TreasureIslandType partType : TreasureIslandType.values()) {
			if (partType.asCode() == code) {
				return partType;
			}
		}
		return null;
	}

	public static TreasureIslandType asEnum(String code) {
		for (TreasureIslandType partSlotType : TreasureIslandType.values()) {
			if (partSlotType.toString().toUpperCase().equals(code.toUpperCase())) {
				return partSlotType;
			}
		}
		return null;
	}

	public static TreasureIslandType asEnumByDesc(String desc) {
		for (TreasureIslandType partSlotType : TreasureIslandType.values()) {
			if (partSlotType.asDesc().toUpperCase().equals(desc.toUpperCase())) {
				return partSlotType;
			}
		}
		return null;
	}

	public static TreasureIslandType randAsRobotPart() {
		int sc = Utils.randomInt(0, 4);
		return asEnum(sc);
	}

}
