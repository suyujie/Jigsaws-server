package gamecore.message;

import server.node.action.AbstractAction;

/**
 * 游戏服务器发送给客户端的响应消息。
 */
public class GameResponse {

	private short commandId;
	private short success;
	private byte[] body;
	private byte[] security;

	public GameResponse(short commandId, byte[] body, byte[] security) {
		this.commandId = commandId;
		this.success = AbstractAction.SC_OK;
		this.body = body;
		this.security = security;
	}

	public GameResponse(short commandId, short success, byte[] body, byte[] security) {
		this.commandId = commandId;
		this.success = success;
		this.body = body;
		this.security = security;
	}

	public short getCommandId() {
		return commandId;
	}

	public void setCommandId(short commandId) {
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

	public byte[] getSecurity() {
		return security;
	}

	public void setSecurity(byte[] security) {
		this.security = security;
	}

}
