package server.node.system.robotPart;

import java.util.ArrayList;

public class XmlPartsData {

	private ArrayList<PartMaking> ArrayOfPartsData = new ArrayList<PartMaking>();

	public ArrayList<PartMaking> getParts() {
		return ArrayOfPartsData;
	}

	public void setParts(ArrayList<PartMaking> parts) {
		this.ArrayOfPartsData = parts;
	}

}
