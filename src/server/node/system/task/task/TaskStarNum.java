package server.node.system.task.task;

import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;

public class TaskStarNum extends AbstractTask implements ITask {

	private static final long serialVersionUID = -4764526037874482226L;

	public TaskStarNum() {
		super(TaskType.StarNum);
	}

	public void doTask(int star) {
		completedNum = star;
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
