package server.node.system.berg;

import gamecore.util.Utils;

public enum BergType {

	ATK(0), DEF(1), CRT(2), HP(3);

	private int sc;

	private BergType(int code) {
		this.sc = code;
	}

	public int asCode() {
		return this.sc;
	}

	public static BergType asEnum(int code) {
		for (BergType type : BergType.values()) {
			if (type.asCode() == code) {
				return type;
			}
		}
		return null;
	}

	public static BergType rand() {
		int sc = Utils.randomInt(0, 3);
		return asEnum(sc);
	}

	public static Integer randCode() {
		return Utils.randomInt(0, 3);
	}
}
