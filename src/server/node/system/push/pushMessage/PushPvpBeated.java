package server.node.system.push.pushMessage;

//被打消息
public class PushPvpBeated extends AbstractPushMessage {

	private static final long serialVersionUID = -4514216918170698879L;

	public String attackerName;

	public PushPvpBeated() {
	}

	public PushPvpBeated(long playerId, long sendTime, String attackerName) {
		super(playerId, sendTime);
		this.attackerName = attackerName;
	}

	public String getAttackerName() {
		return attackerName;
	}

	public void setAttackerName(String attackerName) {
		this.attackerName = attackerName;
	}

}
