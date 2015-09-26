package server.node.system.task.task.pvp;

import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;
import server.node.system.task.task.AbstractTask;
import server.node.system.task.task.ITask;

public class TaskPvpBeatQuality3RobotNum extends AbstractTask implements ITask {

	private static final long serialVersionUID = -4180546731098532092L;

	public TaskPvpBeatQuality3RobotNum() {
		super(TaskType.PvpBeatRobotQuality3Num);
	}

	@Override
	public void doTask() {
		completedNum += 1;
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
