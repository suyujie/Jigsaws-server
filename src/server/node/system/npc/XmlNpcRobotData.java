package server.node.system.npc;

import java.util.ArrayList;

public class XmlNpcRobotData {

	private ArrayList<NpcRobotMaking> ArrayOfNpcRobotData = new ArrayList<NpcRobotMaking>();

	public ArrayList<NpcRobotMaking> getNpcRobotMakings() {
		return ArrayOfNpcRobotData;
	}

	public void setNpcRobotMakings(ArrayList<NpcRobotMaking> ArrayOfNpcRobotData) {
		this.ArrayOfNpcRobotData = ArrayOfNpcRobotData;
	}

}
