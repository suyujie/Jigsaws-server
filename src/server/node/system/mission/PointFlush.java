package server.node.system.mission;

/**
 * 大关卡
 */
public final class PointFlush {

	private int makingId;//关卡id
	private long time;//上次刷的时间,秒
	private int flushNum;//已刷次数

	public PointFlush() {
	}

	public PointFlush(int makingId, long time, int flushNum) {
		super();
		this.makingId = makingId;
		this.time = time;
		this.flushNum = flushNum;
	}

	public int getMakingId() {
		return makingId;
	}

	public void setMakingId(int makingId) {
		this.makingId = makingId;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getFlushNum() {
		return flushNum;
	}

	public void setFlushNum(int flushNum) {
		this.flushNum = flushNum;
	}

}
