package server.node.system.task.task.paint;

import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;
import server.node.system.task.task.AbstractTask;
import server.node.system.task.task.ITask;

public class TaskPaintPartQuality4Num extends AbstractTask implements ITask {

	private static final long serialVersionUID = -3360928932492856616L;

	public TaskPaintPartQuality4Num() {
		super(TaskType.PaintPartQuality4Num);
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
