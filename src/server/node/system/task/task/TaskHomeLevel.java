package server.node.system.task.task;

import server.node.system.player.Player;
import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;

public class TaskHomeLevel extends AbstractTask implements ITask {

	private static final long serialVersionUID = 7812499066000242218L;

	public TaskHomeLevel() {
		super(TaskType.HomeLevel);
	}

	public void doTask(Player player) {
		completedNum = player.getLevel();
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
