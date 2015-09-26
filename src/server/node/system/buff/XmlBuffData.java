package server.node.system.buff;

import java.util.ArrayList;

public class XmlBuffData {

	private ArrayList<BuffMaking> ArrayOfBufferData = new ArrayList<BuffMaking>();

	public ArrayList<BuffMaking> getBuffs() {
		return ArrayOfBufferData;
	}

	public void setBuffs(ArrayList<BuffMaking> buffs) {
		this.ArrayOfBufferData = buffs;
	}

}
