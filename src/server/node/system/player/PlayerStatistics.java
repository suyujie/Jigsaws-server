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

	private int upLoadBeGood;// 被评为好图
	private int upLoadBeBad;// 被评为不好图

	private int commentGood;// 做出的好图评论
	private int commentBad;// 做出的不好评论

	public PlayerStatistics() {

	}

	public PlayerStatistics(int gameSuccess, int gameFailure, int gameGiveup, int upLoadNum, int upLoadBeGood,
			int upLoadBeBad, int commentGood, int commentBad) {
		super();
		this.gameSuccess = gameSuccess;
		this.gameFailure = gameFailure;
		this.gameGiveup = gameGiveup;
		this.upLoadNum = upLoadNum;
		this.upLoadBeGood = upLoadBeGood;
		this.upLoadBeBad = upLoadBeBad;
		this.commentGood = commentGood;
		this.commentBad = commentBad;
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

	public int getUpLoadBeGood() {
		return upLoadBeGood;
	}

	public void setUpLoadBeGood(int upLoadBeGood) {
		this.upLoadBeGood = upLoadBeGood;
	}

	public int getUpLoadBeBad() {
		return upLoadBeBad;
	}

	public void setUpLoadBeBad(int upLoadBeBad) {
		this.upLoadBeBad = upLoadBeBad;
	}

	public int getCommentGood() {
		return commentGood;
	}

	public void setCommentGood(int commentGood) {
		this.commentGood = commentGood;
	}

	public int getCommentBad() {
		return commentBad;
	}

	public void setCommentBad(int commentBad) {
		this.commentBad = commentBad;
	}

}
