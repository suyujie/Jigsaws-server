package server.node.action;

import java.sql.SQLException;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import gamecore.action.ActionPathSpec;
import gamecore.message.RequestJson;
import gamecore.message.ResponseJson;
import server.node.system.Root;
import server.node.system.account.Account;
import server.node.system.player.Player;

@ActionPathSpec("101")
public class LoginAction extends AbstractAction {

	private static final long serialVersionUID = -8285918742136898103L;
	private static final Logger logger = LogManager.getLogger(LoginAction.class);

	@Override
	public ResponseJson execute(RequestJson requestJson) {

		ResponseJson responseJson = new ResponseJson(requestJson.getCommandId(), SC_OK, null);

		JSONObject reqBody = requestJson.getBody();
		String deviceId = reqBody.getString("deviceId");

		String sessionId = UUID.randomUUID().toString();

		JSONObject resultJson = getResultJson();

		if (StringUtils.isNotBlank(deviceId)) {

			Player player = null;

			try {
				// 登陆
				Account account = Root.accountSystem.getAccount(deviceId);
				if (account == null) {
					player = Root.playerSystem.register(deviceId, sessionId, true);
					resultJson.put("reg", true);
				} else {
					player = Root.playerSystem.getPlayer(account, sessionId);
					resultJson.put("reg", false);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

			resultJson.put("sessionId", sessionId);

			responseJson.setBody(resultJson);
		}

		return responseJson;
	}
}
