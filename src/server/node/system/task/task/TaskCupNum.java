package server.node.system.task.task;

import server.node.system.player.Player;
import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;

public class TaskCupNum extends AbstractTask implements ITask {

	private static final long serialVersionUID = 3700832263289643209L;
	private static final TaskType taskType = TaskType.CupNum;

	public TaskCupNum() {
		super(TaskType.CupNum);
	}

	public void doTask(Player player) {

		if (completedNum < player.getPlayerStatistics().getCupNum()) {
			completedNum = player.getPlayerStatistics().getCupNum();
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

}
