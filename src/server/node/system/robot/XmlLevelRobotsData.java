package server.node.system.robot;

import java.util.ArrayList;

public class XmlLevelRobotsData {

	private ArrayList<LevelRobotMaking> ArrayOfLevelRobotData = new ArrayList<LevelRobotMaking>();

	public ArrayList<LevelRobotMaking> getLevelRobots() {
		return ArrayOfLevelRobotData;
	}

	public void setParts(ArrayList<LevelRobotMaking> LevelRobots) {
		this.ArrayOfLevelRobotData = LevelRobots;
	}

}
