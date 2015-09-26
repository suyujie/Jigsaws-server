package server.node.system.task.task.partNum;

import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;
import server.node.system.task.task.AbstractTask;
import server.node.system.task.task.ITask;

public class TaskPartNum extends AbstractTask implements ITask {

	private static final long serialVersionUID = -7478467452409581038L;

	public TaskPartNum() {
		super(TaskType.PartNum);
	}

	public void doTask(int newPartNum) {
		completedNum += newPartNum;
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
