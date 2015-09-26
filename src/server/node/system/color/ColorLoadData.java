package server.node.system.color;

import gamecore.system.AbstractLoadData;
import gamecore.util.Utils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class ColorLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(ColorLoadData.class.getName());

	private static ColorLoadData instance = null;

	FastMap<Integer, ColorMaking> colors = null;

	List<Integer> colorIds = null;
	List<Integer> colorIdWithoutDefault = null;

	public static ColorLoadData getInstance() {
		if (instance == null) {
			instance = new ColorLoadData();
		}
		return instance;

	}

	public boolean readData() {

		this.colors = new FastMap<Integer, ColorMaking>();
		this.colorIds = new ArrayList<Integer>();
		this.colorIdWithoutDefault = new ArrayList<Integer>();

		return readData_color();
	}

	/**
	 * 读取point
	 */
	private boolean readData_color() {

		if (logger.isDebugEnabled()) {
			logger.info("read color data");
		}

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("color"));
		if (!xmlFile.exists()) {
			logger.error("read color xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("ColorData", ColorMaking.class);
			stream.alias("ArrayOfColorData", XmlColorsData.class);
			stream.addImplicitCollection(XmlColorsData.class, "ArrayOfColorData");
			XmlColorsData xmlColorsData = (XmlColorsData) stream.fromXML(new FileReader(xmlFile));

			for (ColorMaking colorMaking : xmlColorsData.getColors()) {
				this.colors.put(colorMaking.getId(), colorMaking);
				this.colorIds.add(colorMaking.getId());
				if (colorMaking.getId() != 0) {
					this.colorIdWithoutDefault.add(colorMaking.getId());
				}
			}

			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getRandomColorId(int num) {
		return (List<Integer>) Utils.randomSelectWithRepeat(colorIds, num);
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getRandomColorIdWithoutDefaultColor(int num) {
		return (List<Integer>) Utils.randomSelectWithRepeat(colorIdWithoutDefault, num);
	}

}