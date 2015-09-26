package server.node.system.push.pushMessage;

public class PushLotteryFree extends AbstractPushMessage {

	private static final long serialVersionUID = 8094053340613574899L;

	public PushLotteryFree() {
	}

	public PushLotteryFree(long playerId, long sendTime) {
		super(playerId, sendTime);
	}

}
