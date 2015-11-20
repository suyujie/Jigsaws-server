package gamecore.message;

import server.node.action.AbstractAction;

/**
 * 游戏服务器发送给客户端的响应消息。
 */
public class GameResponse {

	private Integer commandId;
	private byte status;
	private byte[] body;

	public GameResponse(Integer commandId, byte[] body) {
		this.commandId = commandId;
		this.status = AbstractAction.SC_OK;
		this.body = body;
	}

	public GameResponse(Integer commandId, byte status, byte[] body) {
		this.commandId = commandId;
		this.status = status;
		this.body = body;
	}

	public Integer getCommandId() {
		return commandId;
	}

	public void setCommandId(Integer commandId) {
		this.commandId = commandId;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

}
