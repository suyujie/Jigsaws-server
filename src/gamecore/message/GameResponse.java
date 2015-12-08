package gamecore.message;

import server.node.action.AbstractAction;

/**
 * 游戏服务器发送给客户端的响应消息。
 */
public class GameResponse {

	private Integer commandId;
	private int state;
	private byte[] body;

	public GameResponse(Integer commandId, int state, byte[] body) {
		this.commandId = commandId;
		this.state = state;
		this.body = body;
	}

	public Integer getCommandId() {
		return commandId;
	}

	public void setCommandId(Integer commandId) {
		this.commandId = commandId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

}
