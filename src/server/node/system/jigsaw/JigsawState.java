package server.node.system.jigsaw;

/**
 * 游戏的状态
 */
public enum JigsawState {

	ENABLE(0, "enable"), DELETE(2, "delete"),

	REPORT(3, "被举报"), REPORT_OK(4, "举报反正"),

	;

	private int sc;
	private String desc;

	private JigsawState(int code, String desc) {
		this.sc = code;
		this.desc = desc;
	}

	public int asCode() {
		return this.sc;
	}

	public String asDesc() {
		return this.desc;
	}

	public static JigsawState asEnum(int code) {
		for (JigsawState type : JigsawState.values()) {
			if (type.asCode() == code) {
				return type;
			}
		}
		return null;
	}

	public static JigsawState asEnum(String code) {
		for (JigsawState type : JigsawState.values()) {
			if (type.toString().toUpperCase().equals(code.toUpperCase())) {
				return type;
			}
		}
		return null;
	}

	public static JigsawState asEnumByDesc(String desc) {
		for (JigsawState type : JigsawState.values()) {
			if (type.asDesc().toUpperCase().equals(desc.toUpperCase())) {
				return type;
			}
		}
		return null;
	}

}
