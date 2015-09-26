package gamecore.message;

import com.alibaba.fastjson.JSONObject;

/**
 * 游戏服务器发送给客户端的响应消息。
 */
public class ResponseJson {

	private String commandId;
	private boolean success;
	private JSONObject body;

	public ResponseJson(String commandId, boolean success, JSONObject body) {
		this.commandId = commandId;
		this.success = success;
		this.body = body;
	}

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public JSONObject getBody() {
		return body;
	}

	public void setBody(JSONObject body) {
		this.body = body;
	}

}
