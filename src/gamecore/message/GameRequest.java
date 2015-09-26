package gamecore.message;

/**
 * 客户端发送给游戏服务器的请求消息。
 */
public class GameRequest {

	private short commandId;
	private String mobileId;
	private int checkId;
	private Long playerId;
	private byte[] body;

	public GameRequest(short commandId, String mobileId, int checkId, Long playerId, byte[] body) {
		this.commandId = commandId;
		this.mobileId = mobileId;
		this.checkId = checkId;
		this.playerId = playerId;
		this.body = body;
	}

	public short getCommandId() {
		return commandId;
	}

	public void setCommandId(short commandId) {
		this.commandId = commandId;
	}

	public String getMobileId() {
		return mobileId;
	}

	public void setMobileId(String mobileId) {
		this.mobileId = mobileId;
	}

	public int getCheckId() {
		return checkId;
	}

	public void setCheckId(int checkId) {
		this.checkId = checkId;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

}
