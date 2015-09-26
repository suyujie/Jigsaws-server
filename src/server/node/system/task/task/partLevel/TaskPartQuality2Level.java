package server.node.system.task.task.partLevel;

import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;
import server.node.system.task.task.AbstractTask;
import server.node.system.task.task.ITask;

public class TaskPartQuality2Level extends AbstractTask implements ITask {

	private static final long serialVersionUID = -3592506045183644487L;

	public TaskPartQuality2Level() {
		super(TaskType.PartQuality2Level);
	}

	public void doTask(int newLevel) {
		if (completedNum < newLevel) {
			completedNum = newLevel;
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
