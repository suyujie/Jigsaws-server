package server.node.system.opponent;

//防守者
public class Defender {

	private long playerId;
	private long endT;

	public Defender(long playerId, long endT) {
		this.playerId = playerId;
		this.endT = endT;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public long getEndT() {
		return endT;
	}

	public void setEndT(long endT) {
		this.endT = endT;
	}

}
