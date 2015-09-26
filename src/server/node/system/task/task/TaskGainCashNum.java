package server.node.system.task.task;

import server.node.system.player.Player;
import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;

public class TaskGainCashNum extends AbstractTask implements ITask {

	private static final long serialVersionUID = 6348605175064080455L;
	private static final TaskType taskType = TaskType.GainCashNum;

	public TaskGainCashNum() {
		super(TaskType.GainCashNum);
	}

	public void doTask(Player player, int cashNum) {
		completedNum += cashNum;
		for (int i = 0; i < completed.length; i++) {
			if (completed[i] == 0) {//没完成的
				TaskMaking taskMaking = TaskLoadData.getInstance().getTaskMaking(taskType.asCode());
				if (completedNum >= taskMaking.getNeedNumTable()[i]) {//完成了
					completed[i] = 1;
				}
			}
		}

	}

}
