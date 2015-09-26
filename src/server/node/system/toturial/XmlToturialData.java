package server.node.system.toturial;

import java.util.ArrayList;

public class XmlToturialData {

	private ArrayList<ToturialMaking> ArrayOfTeachEvent = new ArrayList<ToturialMaking>();

	public ArrayList<ToturialMaking> getToturialMakings() {
		return ArrayOfTeachEvent;
	}

	public void setTasks(ArrayList<ToturialMaking> toturialMakings) {
		this.ArrayOfTeachEvent = toturialMakings;
	}

}
