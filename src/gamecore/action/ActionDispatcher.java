package gamecore.action;

import org.apache.log4j.Logger;

import gamecore.message.GameRequest;
import gamecore.message.GameResponse;
import gamecore.message.RequestJson;
import gamecore.message.ResponseJson;

public class ActionDispatcher {

	private static final Logger logger = Logger.getLogger(ActionDispatcher.class);

	public static ResponseJson dispatchAction(RequestJson msg) {
		Integer commandId = msg.getCommandId();
		IAction action = ActionFactory.getInstance().getAction(commandId);
		if (action == null) {
			throw new RuntimeException("Invalid command ID: " + commandId);
		}

		logger.debug("execute action:" + action);
		
		return action.execute(msg);
	}

	public static ResponseJson dispatchManagerAction(RequestJson msg) {
		Integer commandId = msg.getCommandId();
		IAction action = ActionFactory.getInstance().getManagerAction(commandId);
		if (action == null) {
			throw new RuntimeException("Invalid command ID: " + commandId);
		}

		logger.debug("execute action:" + action);
		
		return action.execute(msg);
	}

	public static GameResponse dispatchAction(GameRequest msg) {
		Integer commandId = msg.getCommandId();
		IAction action = ActionFactory.getInstance().getAction(commandId);
		if (action == null) {
			throw new RuntimeException("Invalid command ID: " + commandId);
		}
		return action.execute(msg);
	}

}
