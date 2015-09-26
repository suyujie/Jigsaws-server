package server.node.system.pay;

/** 
 * 支付平台
 */
public enum PayChannel {

	Wp("wp"), PayPal("payPal"), Ali("ali"), Union("union"),
	
	 Test("test")

	;

	private String sc;

	private PayChannel(String sc) {
		this.sc = sc;
	}

	public String asCode() {
		return this.sc;
	}

	public static PayChannel asEnum(String code) {
		for (PayChannel pc : PayChannel.values()) {
			if (pc.asCode().equals(code)) {
				return pc;
			}
		}
		return null;
	}

}
