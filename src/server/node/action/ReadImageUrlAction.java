package server.node.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import gamecore.action.ActionPathSpec;
import gamecore.message.RequestJson;
import gamecore.message.ResponseJson;
import server.node.system.Root;
import server.node.system.gameImage.GameImage;

/**
 * 获取一个图片地址
 * 
 * @author suiyujie
 */
@ActionPathSpec("201")
public class ReadImageUrlAction extends AbstractAction {

	private static final long serialVersionUID = 2561195241515014369L;

	private static final Logger logger = LogManager.getLogger(ReadImageUrlAction.class);

	@Override
	public ResponseJson execute(RequestJson requestJson) {

		ResponseJson responseJson = new ResponseJson(requestJson.getCommandId(), true, null);

		JSONObject json = new JSONObject();

		GameImage gameImage = Root.gameImageSystem.readGameImage();

		json.put("imageId", gameImage.getId());
		json.put("url", gameImage.getImageUrl());

		responseJson.setBody(json);

		return responseJson;
	}
}
