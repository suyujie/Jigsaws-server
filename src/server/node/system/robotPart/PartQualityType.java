package server.node.system.robotPart;

import gamecore.util.Utils;

/** 
 * 部件材质
 */
public enum PartQualityType {

	IRON(0), COPPER(1), SILVER(2), GOLD(3), TITANIUM(4);

	private int sc;

	private PartQualityType(int code) {
		this.sc = code;
	}

	public int asCode() {
		return this.sc;
	}

	public static PartQualityType asEnum(int code) {
		for (PartQualityType partType : PartQualityType.values()) {
			if (partType.asCode() == code) {
				return partType;
			}
		}
		return null;
	}

	public static PartQualityType asEnum(String code) {
		for (PartQualityType partQualityType : PartQualityType.values()) {
			if (partQualityType.toString().toUpperCase().equals(code.toUpperCase())) {
				return partQualityType;
			}
		}
		return null;
	}

	public static PartQualityType rand() {
		int sc = Utils.randomInt(0, 4);
		return asEnum(sc);
	}
}
