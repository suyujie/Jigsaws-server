package server.node.system.task.task.partLevel;

import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;
import server.node.system.task.task.AbstractTask;
import server.node.system.task.task.ITask;

public class TaskPartQuality4Level extends AbstractTask implements ITask {

	private static final long serialVersionUID = 4016638407235676618L;

	public TaskPartQuality4Level() {
		super(TaskType.PartQuality4Level);
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
