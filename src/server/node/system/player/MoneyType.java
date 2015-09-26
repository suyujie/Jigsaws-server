package server.node.system.player;

/** 
 * cash 获取  消耗  
 */
public enum MoneyType {

	GOLD(1), CASH(0)

	;

	private int sc;

	private MoneyType(int code) {
		this.sc = code;
	}

	public int asCode() {
		return this.sc;
	}

}
