package gamecore.message;

import com.alibaba.fastjson.JSONObject;

/**
 * 客户端发送给游戏服务器的请求消息。
 */
public class RequestJson {

	private String commandId;
	private String sessionId;
	private JSONObject body;

	public RequestJson(String commandId, String sessionId, JSONObject body) {
		this.commandId = commandId;
		this.sessionId = sessionId;
		this.body = body;
	}

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public JSONObject getBody() {
		return body;
	}

	public void setBody(JSONObject body) {
		this.body = body;
	}

}
