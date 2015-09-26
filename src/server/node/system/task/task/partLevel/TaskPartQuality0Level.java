package server.node.system.task.task.partLevel;

import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;
import server.node.system.task.task.AbstractTask;
import server.node.system.task.task.ITask;

public class TaskPartQuality0Level extends AbstractTask implements ITask {

	private static final long serialVersionUID = -2745467378956978166L;

	public TaskPartQuality0Level() {
		super(TaskType.PartQuality0Level);
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
