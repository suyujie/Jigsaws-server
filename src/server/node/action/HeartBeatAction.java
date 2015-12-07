package server.node.action;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import gamecore.action.ActionPathSpec;
import gamecore.message.RequestJson;
import gamecore.message.ResponseJson;
import server.node.system.Root;
import server.node.system.player.Player;
import server.node.system.session.Session;

@ActionPathSpec("1001")
public class HeartBeatAction extends AbstractAction {

	private static final long serialVersionUID = -8713017600045501218L;

	private static final Logger logger = LogManager.getLogger(HeartBeatAction.class);

	@Override
	public ResponseJson execute(RequestJson requestJson) {

		ResponseJson responseJson = new ResponseJson(requestJson.getCommandId(), SC_OK, null);

		Player player = getPlayer(requestJson.getSessionId());

		JSONObject resultJson = getResultJson();

		if (player == null) {
			responseJson.setState(SC_DISCONNECT);
			responseJson.setBody(resultJson);
			return responseJson;
		}

		String sessionId = requestJson.getSessionId();

		if (StringUtils.isNotBlank(sessionId)) {

			Session session = Root.sessionSystem.getSession(sessionId);

			Root.sessionSystem.updateOrSaveSession(session);

			responseJson.setBody(resultJson);
		}

		return responseJson;
	}
}
