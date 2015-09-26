package server.node.system.expPart;

import java.util.ArrayList;

public class XmlExpPartData {

	private ArrayList<ExpPartMaking> ArrayOfPartsData = new ArrayList<ExpPartMaking>();

	public ArrayList<ExpPartMaking> getExpParts() {
		return ArrayOfPartsData;
	}

	public void setParts(ArrayList<ExpPartMaking> expParts) {
		this.ArrayOfPartsData = expParts;
	}

}
