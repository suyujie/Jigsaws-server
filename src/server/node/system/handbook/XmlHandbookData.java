package server.node.system.handbook;

import java.util.ArrayList;

public class XmlHandbookData {

	private ArrayList<HandbookMaking> ArrayOfHandbookData = new ArrayList<HandbookMaking>();

	public ArrayList<HandbookMaking> getHandbooks() {
		return ArrayOfHandbookData;
	}

	public void setHandbooks(ArrayList<HandbookMaking> handbooks) {
		this.ArrayOfHandbookData = handbooks;
	}

}
