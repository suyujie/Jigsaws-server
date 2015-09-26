package server.node.system.task.task.paint;

import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;
import server.node.system.task.task.AbstractTask;
import server.node.system.task.task.ITask;

public class TaskPaintPartQuality1Num extends AbstractTask implements ITask {

	private static final long serialVersionUID = -7400192892968671287L;

	public TaskPaintPartQuality1Num() {
		super(TaskType.PaintPartQuality1Num);
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
