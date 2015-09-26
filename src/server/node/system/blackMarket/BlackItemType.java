package server.node.system.blackMarket;

/** 
 * 黑市商品类型
 */
public enum BlackItemType {

	PART(0), CHIP(1), CASH(2), EXP(3),BERG(4),;

	private int sc;

	private BlackItemType(int code) {
		this.sc = code;
	}

	public int asCode() {
		return this.sc;
	}

	public static BlackItemType asEnum(int code) {
		for (BlackItemType type : BlackItemType.values()) {
			if (type.asCode() == code) {
				return type;
			}
		}
		return null;
	}

	public static BlackItemType asEnum(String code) {
		for (BlackItemType type : BlackItemType.values()) {
			if (type.toString().toUpperCase().equals(code.toUpperCase())) {
				return type;
			}
		}
		return null;
	}

}
