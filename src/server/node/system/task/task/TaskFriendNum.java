package server.node.system.task.task;

import server.node.system.friend.FriendBag;
import server.node.system.player.Player;
import server.node.system.task.TaskLoadData;
import server.node.system.task.TaskMaking;
import server.node.system.task.TaskType;

public class TaskFriendNum extends AbstractTask implements ITask {

	private static final long serialVersionUID = -5717239530756082266L;
	private static final TaskType taskType = TaskType.FriendNum;

	public TaskFriendNum() {
		super(TaskType.FriendNum);
	}

	public void doTask(Player player, FriendBag friendBag) {
		for (int i = 0; i < completed.length; i++) {
			if (completed[i] == 0) {//没完成的
				TaskMaking taskMaking = TaskLoadData.getInstance().getTaskMaking(taskType.asCode());
				if (friendBag.getPlayerIds().size() >= taskMaking.getNeedNumTable()[i]) {//完成了
					completed[i] = 1;
				}
			}
		}
	}

}
