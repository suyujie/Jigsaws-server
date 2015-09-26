package server.node.system.task.task;

import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;

public class TaskHitNum extends AbstractTask implements ITask {

	private static final long serialVersionUID = -2369172734499753419L;

	public TaskHitNum() {
		super(TaskType.HitNum);
	}

	public void doTask(int hitNum) {
		completedNum += hitNum;
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
