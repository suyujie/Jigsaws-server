package server.node.system.pay;

/** 
 * 支付状态
 */
public enum PayStatus {

	OK(0, "成功"),

	NO_THIS_ORDER(1, "没有这个订单"), REPEAT_ORDER(2, "重复订单"), VALID_ERROR(3, "平台验证失败"), EXCEPTION(4, "异常报错"),

	WAITING_PAID(5, "待支付"), PAID_COIN_NO_ADD_GOLD(6, "已付费，未加GOLD"), COMPENSATE_DONE(7, "已补偿"),

	TEST(10, "测试"), ;

	private int sc;
	private String desc;

	private PayStatus(int code, String desc) {
		this.sc = code;
		this.desc = desc;
	}

	public int asCode() {
		return this.sc;
	}

	public String asDesc() {
		return this.desc;
	}

	public static PayStatus asEnum(int code) {
		for (PayStatus ps : PayStatus.values()) {
			if (ps.asCode() == code) {
				return ps;
			}
		}
		return null;
	}

}
