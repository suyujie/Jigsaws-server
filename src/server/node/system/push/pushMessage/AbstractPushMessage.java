package server.node.system.push.pushMessage;

import java.io.Serializable;

public class AbstractPushMessage implements Serializable {

	private static final long serialVersionUID = -8216405855026235881L;

	public long playerId;

	public long sendTime;

	public AbstractPushMessage() {
	}

	public AbstractPushMessage(long playerId, long sendTime) {
		super();
		this.playerId = playerId;
		this.sendTime = sendTime;
	}

	public void sendPush() {

	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

}
