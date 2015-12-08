package gamecore.message;

import com.alibaba.fastjson.JSONObject;

/**
 * 游戏服务器发送给客户端的响应消息。
 */
public class ResponseJson {

	private Integer commandId;
	private int state;
	private JSONObject body;

	public ResponseJson(Integer commandId, int state, JSONObject body) {
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

	public JSONObject getBody() {
		return body;
	}

	public void setBody(JSONObject body) {
		this.body = body;
	}

	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put("state", state);
		json.put("body", getBody());
		return json;
	}

}
