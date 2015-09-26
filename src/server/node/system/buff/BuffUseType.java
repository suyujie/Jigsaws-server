package server.node.system.buff;

/** 
 * buff作用类型
 */
public enum BuffUseType {

	NO_FIGHT(0), FIGHT_ONE(1), FIGHT_ALL(2);
	
	private int sc;

	private BuffUseType(int code) {
		this.sc = code;
	}

	public int asCode() {
		return this.sc;
	}

	public static BuffUseType asEnum(int code) {
		for (BuffUseType partType : BuffUseType.values()) {
			if (partType.asCode() == code) {
				return partType;
			}
		}
		return null;
	}

	public static BuffUseType asEnum(String code) {
		for (BuffUseType buffUseType : BuffUseType.values()) {
			if (buffUseType.toString().toUpperCase().equals(code.toUpperCase())) {
				return buffUseType;
			}
		}
		return null;
	}

}
