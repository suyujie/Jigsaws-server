package server.node.action;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import gamecore.action.ActionPathSpec;
import gamecore.message.RequestJson;
import gamecore.message.ResponseJson;
import server.node.system.Root;
import server.node.system.player.Player;

@ActionPathSpec("201")
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

		JSONObject json = new JSONObject();

		Root.gameImageSystem.commentImage(player, imageId, comment);

		json.put("sessionId", UUID.randomUUID().toString());

		responseJson.setBody(json);

		return responseJson;
	}
}
