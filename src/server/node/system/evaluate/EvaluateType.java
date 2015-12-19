package server.node.system.evaluate;

/**
 * 评价
 */
public enum EvaluateType {

	GOOD(1, "good"), BAD(2, "bad"), DROP(3, "drop"), REPORT(4, "举报");

	private int sc;
	private String desc;

	private EvaluateType(int code, String desc) {
		this.sc = code;
		this.desc = desc;
	}

	public int asCode() {
		return this.sc;
	}

	public String asDesc() {
		return this.desc;
	}

	public static EvaluateType asEnum(int code) {
		for (EvaluateType type : EvaluateType.values()) {
			if (type.asCode() == code) {
				return type;
			}
		}
		return null;
	}

	public static EvaluateType asEnum(String code) {
		for (EvaluateType type : EvaluateType.values()) {
			if (type.toString().toUpperCase().equals(code.toUpperCase())) {
				return type;
			}
		}
		return null;
	}

	public static EvaluateType asEnumByDesc(String desc) {
		for (EvaluateType type : EvaluateType.values()) {
			if (type.asDesc().toUpperCase().equals(desc.toUpperCase())) {
				return type;
			}
		}
		return null;
	}

}
