package server.node.system.color;

import java.util.ArrayList;

public class XmlColorsData {

	private ArrayList<ColorMaking> ArrayOfColorData = new ArrayList<ColorMaking>();

	public ArrayList<ColorMaking> getColors() {
		return ArrayOfColorData;
	}

	public void setColors(ArrayList<ColorMaking> colorMakings) {
		this.ArrayOfColorData = colorMakings;
	}

}
