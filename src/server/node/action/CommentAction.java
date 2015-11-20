package server.node.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import gamecore.action.ActionPathSpec;
import gamecore.message.RequestJson;
import gamecore.message.ResponseJson;
import server.node.system.Root;
import server.node.system.player.Player;

@ActionPathSpec("401")
public class CommentAction extends AbstractAction {

	private static final long serialVersionUID = -5591454136604322538L;

	private static final Logger logger = LogManager.getLogger(CommentAction.class);

	@Override
	public ResponseJson execute(RequestJson requestJson) {

		ResponseJson responseJson = new ResponseJson(requestJson.getCommandId(), true, null);

		Player player = getPlayer(requestJson.getSessionId());

		JSONObject jsonObject = requestJson.getBody();

		Long imageId = jsonObject.getLong("imageId");
		Integer comment = jsonObject.getInteger("comment");

		Root.gameImageSystem.commentImage(player, imageId, comment);

		JSONObject resultJson = new JSONObject();
		resultJson.put("success", true);

		responseJson.setBody(resultJson);
		return responseJson;
	}
}
