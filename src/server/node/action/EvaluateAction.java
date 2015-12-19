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

		ResponseJson responseJson = new ResponseJson(requestJson.getCommandId(), SC_OK, null);

		Player player = getPlayer(requestJson.getSessionId());

		JSONObject resultJson = getResultJson();
		if (player == null) {
			responseJson.setState(SC_DISCONNECT);
			return responseJson;
		}

		JSONObject reqBody = requestJson.getBody();

		Long jigsawId = reqBody.getLong("imageId");
		Integer comment = reqBody.getInteger("comment");

		EvaluateType type = EvaluateType.asEnum(comment);

		// 举报
		if (type == EvaluateType.REPORT) {
			Root.jigsawSystem.reportJigsaw(player, jigsawId);
		} else {// 普通评价
			Root.evaluateSystem.evaluateJigsaw(player, jigsawId, type);
		}

		responseJson.setBody(resultJson);
		return responseJson;
	}
}
