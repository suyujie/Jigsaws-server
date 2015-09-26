package server.node.system.task.task;

import server.node.system.task.TaskCurrentStatus;
import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;

public class AbstractTask implements ITask {

	private static final long serialVersionUID = 8455740152125747283L;
	public TaskType taskType;
	public long completedNum;
	public int[] completed;
	public int[] awarded;

	public AbstractTask(TaskType taskType) {
		this.taskType = taskType;
		this.completedNum = 0L;
		this.completed = new int[] { 0, 0, 0 };
		this.awarded = new int[] { 0, 0, 0 };
	}

	public long getCompletedNum() {
		return completedNum;
	}

	public void setCompletedNum(long completedNum) {
		this.completedNum = completedNum;
	}

	public int[] getCompleted() {
		return completed;
	}

	public void setCompleted(int[] completed) {
		this.completed = completed;
	}

	public int[] getAwarded() {
		return awarded;
	}

	public void setAwarded(int[] awarded) {
		this.awarded = awarded;
	}

	public boolean checkCompleted(int star, TaskMaking taskMaking) {
		if (taskMaking == null) {
			taskMaking = TaskLoadData.getInstance().getTaskMaking(taskType.asCode());
		}
		return completedNum >= taskMaking.getNeedNumTable()[star - 1];
	}

	@Override
	public TaskCurrentStatus readCurrent() {
		TaskCurrentStatus currentStatus = null;
		TaskMaking taskMaking = TaskLoadData.getInstance().getTaskMaking(taskType.asCode());
		for (int i = 0; i < awarded.length; i++) {
			currentStatus = new TaskCurrentStatus(taskType.asCode(), i + 1, checkCompleted(i + 1, taskMaking), awarded[i], completedNum);
			if (awarded[i] == 0) {//这个星级还没有领取
				break;
			}
		}
		return currentStatus;
	}

	@Override
	public void doTask() {
	}

	@Override
	public int reward(int star) {

		TaskMaking taskMaking = TaskLoadData.getInstance().getTaskMaking(taskType.asCode());

		if (awarded[star - 1] == 0 && (completed[star - 1] == 1 && checkCompleted(star, taskMaking))) {//这个星级还没有领取,可以领了,可能因为达成条件的降低,客户端认为已经完成了,那就验证一下新的数据是否完成了
			awarded[star - 1] = 1;
			return taskMaking.getRewardGoldTable()[star - 1];
		}
		return 0;
	}

}
