package server.node.system.task.task.pvp;

import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;
import server.node.system.task.task.AbstractTask;
import server.node.system.task.task.ITask;

public class TaskPvpBeatWeapon3RobotNum extends AbstractTask implements ITask {

	private static final long serialVersionUID = -835574933923083208L;

	//	PvpBeatRobotWeapon3Num(17), //	17	双持炼狱			多人游戏中，摧毁N个双持机器人
	public TaskPvpBeatWeapon3RobotNum() {
		super(TaskType.PvpBeatRobotWeapon3Num);
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
