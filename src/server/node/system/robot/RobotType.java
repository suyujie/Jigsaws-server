package server.node.system.robot;

/** 
 * robot所处的仓库
 */
public enum RobotType {

	BATTLE(0), STORAGE(1);

	private int sc;

	private RobotType(int code) {
		this.sc = code;
	}

	public int asCode() {
		return this.sc;
	}

	public static RobotType asEnum(int code) {
		for (RobotType partType : RobotType.values()) {
			if (partType.asCode() == code) {
				return partType;
			}
		}
		return null;
	}
}
