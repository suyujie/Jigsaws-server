package server.node.system.log;

public class LevelTimeLog extends AbstractLog {

	private int level;
	private long allTime;

	public LevelTimeLog(long id, long playerId, int level, long allTime) {
		super(id, playerId);
		this.level = level;
		this.allTime = allTime;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getAllTime() {
		return allTime;
	}

	public void setAllTime(long allTime) {
		this.allTime = allTime;
	}

}
