package common.coin;

/** 
 * 货币类型
 */
public enum CoinType {

	//	人民币(CNY)  美元  USD  英镑 GBP ¥
	CNY("CNY", "¥"),
	USD("USD", "$"), 
	//GBP("GBP", "￡"), CAD("CAD", "C$")

	;

	private String code;
	private String type;

	private CoinType(String code, String type) {
		this.code = code;
		this.type = type;
	}

	public String asCode() {
		return this.code;
	}

	public String asType() {
		return this.type;
	}

	public static CoinType asEnum(String code) {
		for (CoinType type : CoinType.values()) {
			if (type.asCode().equals(code)) {
				return type;
			}
		}
		return null;
	}

}
