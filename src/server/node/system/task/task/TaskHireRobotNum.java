package server.node.system.task.task;

import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;

public class TaskHireRobotNum extends AbstractTask implements ITask {

	private static final long serialVersionUID = -6131440326053106773L;

	public TaskHireRobotNum() {
		super(TaskType.HireRobotNum);
	}

	public void doTask() {
		completedNum++;
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
