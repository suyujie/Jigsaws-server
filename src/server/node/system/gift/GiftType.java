package server.node.system.gift;

/**
 * task类型
 */
public enum GiftType {

	Wear(0)

	;

	private int sc;

	private GiftType(int code) {
		this.sc = code;
	}

	public int asCode() {
		return this.sc;
	}

	public static GiftType asEnum(int code) {
		for (GiftType giftType : GiftType.values()) {
			if (giftType.asCode() == code) {
				return giftType;
			}
		}
		return null;
	}

	public static GiftType asEnum(String code) {
		for (GiftType giftType : GiftType.values()) {
			if (giftType.toString().toUpperCase().equals(code.toUpperCase())) {
				return giftType;
			}
		}
		return null;
	}

}
