package server.node.system.buff;

import java.util.ArrayList;

public class XmlSuitBuffData {

	private ArrayList<SuitBuffMaking> ArrayOfSameNameBuffer = new ArrayList<SuitBuffMaking>();

	public ArrayList<SuitBuffMaking> getBuffs() {
		return ArrayOfSameNameBuffer;
	}

	public void setBuffs(ArrayList<SuitBuffMaking> buffs) {
		this.ArrayOfSameNameBuffer = buffs;
	}

}
