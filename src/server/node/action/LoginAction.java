package server.node.action;

import gamecore.action.ActionPathSpec;
import gamecore.message.RequestJson;
import gamecore.message.ResponseJson;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

@ActionPathSpec("101")
public class LoginAction extends AbstractAction {

	private static final long serialVersionUID = -3465929255066885958L;
	private static final Logger logger = LogManager.getLogger(LoginAction.class);

	@Override
	public ResponseJson execute(RequestJson requestJson) {

		ResponseJson responseJson = new ResponseJson(requestJson.getCommandId(), true, null);

		JSONObject jsonObject = requestJson.getBody();

		String mobileId = jsonObject.getString("mobileId");

		logger.debug("login mobileId:" + mobileId);

		if (mobileId == null || mobileId.equals("") || mobileId.equals("null")) {
			mobileId = UUID.randomUUID().toString();
		}

		if (null != mobileId && mobileId.length() > 0) {

			System.out.println("register");

			JSONObject json = new JSONObject();
			json.put("sessionId", UUID.randomUUID().toString());

			responseJson.setBody(json);

		}

		responseJson.setSuccess(true);

		return responseJson;
	}
}
