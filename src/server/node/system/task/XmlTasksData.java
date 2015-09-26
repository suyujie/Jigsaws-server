package server.node.system.task;

import java.util.ArrayList;

public class XmlTasksData {

	private ArrayList<TaskMaking> ArrayOfAchieveData = new ArrayList<TaskMaking>();

	public ArrayList<TaskMaking> getTasks() {
		return ArrayOfAchieveData;
	}

	public void setTasks(ArrayList<TaskMaking> taskMakings) {
		this.ArrayOfAchieveData = taskMakings;
	}

}
