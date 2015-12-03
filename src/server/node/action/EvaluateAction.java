package server.node.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import gamecore.action.ActionPathSpec;
import gamecore.message.RequestJson;
import gamecore.message.ResponseJson;
import server.node.system.Root;
import server.node.system.evaluate.EvaluateType;
import server.node.system.player.Player;

@ActionPathSpec("401")
public class EvaluateAction extends AbstractAction {

	private static final long serialVersionUID = -5591454136604322538L;

	private static final Logger logger = LogManager.getLogger(EvaluateAction.class);

	@Override
	public ResponseJson execute(RequestJson requestJson) {

		ResponseJson responseJson = new ResponseJson(requestJson.getCommandId(), true, null);

		Player player = getPlayer(requestJson.getSessionId());

		JSONObject resultJson = new JSONObject();

		if (player == null) {
			resultJson.put(STATUS, SC_DISCONNECT);

			responseJson.setBody(resultJson);
			return responseJson;
		}

		JSONObject jsonObject = requestJson.getBody();

		Long imageId = jsonObject.getLong("imageId");
		Integer comment = jsonObject.getInteger("comment");

		EvaluateType type = EvaluateType.asEnum(comment);

		Root.evaluateSystem.EvaluateJigsaw(player, imageId, type);

		resultJson.put(STATUS, SC_DISCONNECT);

		responseJson.setBody(resultJson);
		return responseJson;
	}
}
