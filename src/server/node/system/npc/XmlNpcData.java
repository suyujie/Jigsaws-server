package server.node.system.npc;

import java.util.ArrayList;

public class XmlNpcData {

	private ArrayList<NpcMaking> ArrayOfNpcData = new ArrayList<NpcMaking>();

	public ArrayList<NpcMaking> getNpcMakings() {
		return ArrayOfNpcData;
	}

	public void setNpcMakings(ArrayList<NpcMaking> npcMakings) {
		this.ArrayOfNpcData = npcMakings;
	}

}
