package server.node.system.player;

import java.io.Serializable;

/**
 * 玩家的部分统计和状态
 */
public class PlayerStatistics implements Serializable {

	private static final long serialVersionUID = -1647241546730237530L;

	private int gameSuccess;// 游戏胜利
	private int gameFailure;// 游戏失败
	private int gameGiveup;// 游戏放弃

	private int upLoadNum;// 上传数量

	private int upLoadGood;// 被评为好图
	private int upLoadBad;// 被评为不好图

	public PlayerStatistics() {

	}

	public PlayerStatistics(int gameSuccess, int gameFailure, int gameGiveup, int upLoadNum, int upLoadGood,
			int upLoadBad) {
		this.gameSuccess = gameSuccess;
		this.gameFailure = gameFailure;
		this.gameGiveup = gameGiveup;
		this.upLoadNum = upLoadNum;
		this.upLoadGood = upLoadGood;
		this.upLoadBad = upLoadBad;
	}

	public int getGameSuccess() {
		return gameSuccess;
	}

	public void setGameSuccess(int gameSuccess) {
		this.gameSuccess = gameSuccess;
	}

	public int getGameFailure() {
		return gameFailure;
	}

	public void setGameFailure(int gameFailure) {
		this.gameFailure = gameFailure;
	}

	public int getGameGiveup() {
		return gameGiveup;
	}

	public void setGameGiveup(int gameGiveup) {
		this.gameGiveup = gameGiveup;
	}

	public int getUpLoadNum() {
		return upLoadNum;
	}

	public void setUpLoadNum(int upLoadNum) {
		this.upLoadNum = upLoadNum;
	}

	public int getUpLoadGood() {
		return upLoadGood;
	}

	public void setUpLoadGood(int upLoadGood) {
		this.upLoadGood = upLoadGood;
	}

	public int getUpLoadBad() {
		return upLoadBad;
	}

	public void setUpLoadBad(int upLoadBad) {
		this.upLoadBad = upLoadBad;
	}

}
