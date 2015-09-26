package server.node.system.task.task;

import java.io.Serializable;

import server.node.system.task.TaskCurrentStatus;

public interface ITask extends Serializable {

	//取出当前一个
	public TaskCurrentStatus readCurrent();

	//do
	public void doTask();

	//reward
	public int reward(int star);

}
