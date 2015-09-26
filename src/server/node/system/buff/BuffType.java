package server.node.system.buff;

/** 
 * 部件类型
 */
public enum BuffType {

	//				1	攻击力增加。
	//				2	防御力增加。
	//				3	血量增加。
	//				4	会心增加。
	//				16	cash增加。是buff类型,
	//				28	exp增加。是buff类型,

	ATK_UP(1), DEF_UP(2), HP_UP(3), CRIT_UP(4), CASH_UP(16), EXP_UP(28);

	private int sc;

	private BuffType(int code) {
		this.sc = code;
	}

	public int asCode() {
		return this.sc;
	}

	public static BuffType asEnum(int code) {
		for (BuffType partType : BuffType.values()) {
			if (partType.asCode() == code) {
				return partType;
			}
		}
		return null;
	}

}
