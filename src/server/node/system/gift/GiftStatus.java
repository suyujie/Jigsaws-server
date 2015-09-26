package server.node.system.gift;

/**
 * 礼物状态类型
 */
public enum GiftStatus {

	Wait(0), Accept(1)

	;

	private int sc;

	private GiftStatus(int code) {
		this.sc = code;
	}

	public int asCode() {
		return this.sc;
	}

	public static GiftStatus asEnum(int code) {
		for (GiftStatus giftType : GiftStatus.values()) {
			if (giftType.asCode() == code) {
				return giftType;
			}
		}
		return null;
	}

}
