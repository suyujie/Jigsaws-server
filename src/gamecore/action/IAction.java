package gamecore.action;

import gamecore.message.GameRequest;
import gamecore.message.GameResponse;
import gamecore.message.RequestJson;
import gamecore.message.ResponseJson;

public interface IAction {

	/**
	 * 处理游戏请求。
	 */
	public GameResponse execute(GameRequest msg);

	/**
	 * 处理manager请求
	 */
	public ResponseJson execute(RequestJson json);

}
