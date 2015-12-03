package server.node.action;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import gamecore.action.ActionPathSpec;
import gamecore.message.RequestJson;
import gamecore.message.ResponseJson;
import gamecore.util.Utils;
import server.node.system.Root;
import server.node.system.jigsaw.Jigsaw;
import server.node.system.player.Player;
import server.node.system.player.PlayerStatistics;

/**
 * 获取一个拼图游戏
 * 
 * @author suiyujie
 */
@ActionPathSpec("201")
public class ReadJigsawAction extends AbstractAction {

	private static final long serialVersionUID = 2561195241515014369L;

	private static final Logger logger = LogManager.getLogger(ReadJigsawAction.class);

	@Override
	public ResponseJson execute(RequestJson requestJson) {

		ResponseJson responseJson = new ResponseJson(requestJson.getCommandId(), true, null);

		Player player = getPlayer(requestJson.getSessionId());

		JSONObject json = new JSONObject();

		Jigsaw gameImage = null;

		// 判断玩家是否玩过很多，玩的多，随机给官方，玩的少，只给官方
		try {
			PlayerStatistics pss = Root.playerSystem.getPlayerStatistics(player);

			// 玩的挺多的了，90%给玩家上传的图片
			if (pss.getCommentGood() + pss.getCommentBad() > 10) {
				if (Utils.successRate(9, 10)) {
					gameImage = Root.jigsawSystem.readJigsaw(player);
				}
			}

			// 刚开始玩，或者没有匹配到玩家上传的图片，给官方图片
			if (gameImage == null) {
				gameImage = Root.jigsawSystem.readJigsaw_guanfang(player);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		json.put("imageId", gameImage.getId());
		json.put("url", gameImage.getUrl());

		responseJson.setBody(json);

		return responseJson;
	}
}
