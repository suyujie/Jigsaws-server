package server.node.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gamecore.action.ActionPathSpec;
import gamecore.io.ByteArrayGameInput;
import gamecore.io.GameInput;
import gamecore.message.GameRequest;
import gamecore.message.GameResponse;
import server.node.system.Root;
import server.node.system.player.Player;

@ActionPathSpec("301")
public class UploadAction extends AbstractAction {

	private static final long serialVersionUID = 6500127464526687123L;

	private static final Logger logger = LogManager.getLogger(UploadAction.class);

	@Override
	public GameResponse execute(GameRequest gameRequest) {

		GameResponse gameResponse = new GameResponse(gameRequest.getCommandId(), SC_OK, null);

		logger.debug("   player.sessionId:" + gameRequest.getSessionId());

		Player player = getPlayer(gameRequest.getSessionId());
		if (player == null) {
			gameResponse.setState(SC_DISCONNECT);
			return gameResponse;
		}

		GameInput input = new ByteArrayGameInput(gameRequest.getBody());

		byte[] img = input.getBytes();

		logger.debug("bmpBytesStr:" + img.length);

		Root.jigsawSystem.uploadJigsaw(player, img);

		return gameResponse;
	}
}
