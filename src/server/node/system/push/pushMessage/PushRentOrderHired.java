package server.node.system.push.pushMessage;

//订单租出 推送消息
public class PushRentOrderHired extends AbstractPushMessage {

	private static final long serialVersionUID = -6580447460510906522L;
	public String hirerName;

	public PushRentOrderHired() {
	}

	public PushRentOrderHired(long playerId, long sendTime, String hirerName) {
		super(playerId, sendTime);
		this.hirerName = hirerName;
	}

	public String getHirerName() {
		return hirerName;
	}

	public void setHirerName(String hirerName) {
		this.hirerName = hirerName;
	}

}
