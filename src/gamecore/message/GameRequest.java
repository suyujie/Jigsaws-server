package gamecore.message;

/**
 * 客户端发送给游戏服务器的请求消息。
 */
public class GameRequest {

	private Integer commandId;
	private String sessionId;
	private byte[] body;

	public GameRequest(Integer commandId, String sessionId, byte[] body) {
		this.commandId = commandId;
		this.sessionId = sessionId;
		this.body = body;
	}

	public Integer getCommandId() {
		return commandId;
	}

	public void setCommandId(Integer commandId) {
		this.commandId = commandId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

}
