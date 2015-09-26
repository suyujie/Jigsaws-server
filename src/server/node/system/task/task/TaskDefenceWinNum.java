package server.node.system.task.task;

import server.node.system.player.Player;
import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;

public class TaskDefenceWinNum extends AbstractTask implements ITask {

	private static final long serialVersionUID = 8627440989732062227L;
	
	public TaskDefenceWinNum() {
		super(TaskType.DefenceWinNum);
	}

	public void doTask(Player player) {

		for (int i = 0; i < completed.length; i++) {
			if (completed[i] == 0) {//没完成的
				TaskMaking taskMaking = TaskLoadData.getInstance().getTaskMaking(taskType.asCode());
				if (player.getPlayerStatistics().getPvpDefenceWinCount() >= taskMaking.getNeedNumTable()[i]) {//完成了
					completed[i] = 1;
				}
			}
		}

	}

}
