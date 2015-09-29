package server.node.action;

import gamecore.action.ActionPathSpec;
import gamecore.message.RequestJson;
import gamecore.message.ResponseJson;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

@ActionPathSpec("101")
public class LoginAction extends AbstractAction {

	private static final long serialVersionUID = -8285918742136898103L;
	private static final Logger logger = LogManager.getLogger(LoginAction.class);

	@Override
	public ResponseJson execute(RequestJson requestJson) {

		ResponseJson responseJson = new ResponseJson(requestJson.getCommandId(), true, null);

		JSONObject jsonObject = requestJson.getBody();

		String deviceId = jsonObject.getString("deviceId");

		logger.debug("login deviceId:" + deviceId);

		if (StringUtils.isNotBlank(deviceId)) {

			JSONObject json = new JSONObject();
			json.put("sessionId", UUID.randomUUID().toString());

			responseJson.setBody(json);
		}

		return responseJson;
	}
}
