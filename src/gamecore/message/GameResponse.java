package gamecore.message;

import server.node.action.AbstractAction;

/**
 * 游戏服务器发送给客户端的响应消息。
 */
public class GameResponse {

	private Integer commandId;
	private short success;
	private byte[] body;

	public GameResponse(Integer commandId, byte[] body) {
		this.commandId = commandId;
		this.success = AbstractAction.SC_OK;
		this.body = body;
	}

	public GameResponse(Integer commandId, short success, byte[] body) {
		this.commandId = commandId;
		this.success = success;
		this.body = body;
	}

	public Integer getCommandId() {
		return commandId;
	}

	public void setCommandId(Integer commandId) {
		this.commandId = commandId;
	}

	public short getSuccess() {
		return success;
	}

	public void setSuccess(short success) {
		this.success = success;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

}
