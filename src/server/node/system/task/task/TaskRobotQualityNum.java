package server.node.system.task.task;

import java.util.HashSet;
import java.util.Set;

import server.node.system.robotPart.PartQualityType;
import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;

public class TaskRobotQualityNum extends AbstractTask implements ITask {

	private static final long serialVersionUID = -2148689512097966401L;

	private Set<PartQualityType> qualitySet = new HashSet<PartQualityType>();

	public TaskRobotQualityNum() {
		super(TaskType.RobotQualityNum);
	}

	public void doTask(PartQualityType type) {
		if (!qualitySet.contains(type)) {
			qualitySet.add(type);
			completedNum = qualitySet.size();
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

	public byte[] getStatus() {
		//	IRON(0), COPPER(1), SILVER(2), GOLD(3), TITANIUM(4);
		byte[] bs = { 0, 0, 0, 0, 0 };
		for (PartQualityType q : qualitySet) {
			if (q != null) {
				bs[q.asCode()] = 1;
			}
		}
		return bs;
	}

}
