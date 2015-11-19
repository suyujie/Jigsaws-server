package gamecore.message;

import com.alibaba.fastjson.JSONObject;

/**
 * 游戏服务器发送给客户端的响应消息。
 */
public class ResponseJson {

	private Integer commandId;
	private boolean success;
	private JSONObject body;

	public ResponseJson(Integer commandId, boolean success, JSONObject body) {
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
