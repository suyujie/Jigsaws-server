package server.node.system.ranking;

/** 
 * cash 获取  消耗  
 */
public enum RankingType {

	CUP(0), CUP_AREA(1), SCORE(2), SCORE_AREA(3)

	;

	private int sc;

	private RankingType(int code) {
		this.sc = code;
	}

	public int asCode() {
		return this.sc;
	}

	public static RankingType asEnum(int code) {
		for (RankingType type : RankingType.values()) {
			if (type.asCode() == code) {
				return type;
			}
		}
		return null;
	}

}
