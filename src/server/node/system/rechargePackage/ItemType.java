package server.node.system.rechargePackage;

/** 
 * 商品类型
 */
public enum ItemType {

	// gold 1,cash 2,....

	GOLD(0), CASH(1);

	private int sc;

	private ItemType(int code) {
		this.sc = code;
	}

	public int asCode() {
		return this.sc;
	}

	public static ItemType asEnum(int code) {
		for (ItemType partType : ItemType.values()) {
			if (partType.asCode() == code) {
				return partType;
			}
		}
		return null;
	}

	public static ItemType asEnum(String code) {
		for (ItemType partSlotType : ItemType.values()) {
			if (partSlotType.toString().toUpperCase().equals(code.toUpperCase())) {
				return partSlotType;
			}
		}
		return null;
	}

}
