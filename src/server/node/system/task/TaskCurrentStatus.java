package server.node.system.task;

import gamecore.io.ByteArrayGameOutput;

public class TaskCurrentStatus {

	private int id;
	private int level;
	private int canReward;
	private long completedNum;

	public TaskCurrentStatus(int id, int level, boolean completed, int rewarded, long completedNum) {
		super();
		this.id = id;

		this.canReward = (completed && rewarded == 0) ? 1 : 0;
		this.completedNum = completedNum;
		this.level = level;
		//如果3星的时候,完成任务,并且奖励领取,那么这个level发成4...方便前端处理
		if (level == 3 && completed  && rewarded == 1) {
			this.level = 4;
		}

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getCanReward() {
		return canReward;
	}

	public void setCanReward(int canReward) {
		this.canReward = canReward;
	}

	public long getCompletedNum() {
		return completedNum;
	}

	public void setCompletedNum(long completedNum) {
		this.completedNum = completedNum;
	}

	public byte[] toByteArray() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		bago.putInt(id);
		bago.putInt(level);
		bago.put((byte) canReward);
		bago.putLong(completedNum);
		return bago.toByteArray();
	}
}
