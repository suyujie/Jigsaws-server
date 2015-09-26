package server.node.system.berg;

import gamecore.system.AbstractLoadData;

import java.io.File;
import java.io.FileReader;

import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class BergLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(BergLoadData.class.getName());

	private static BergLoadData instance = null;

	private FastMap<Integer, BergMaking> bergMakings = new FastMap<Integer, BergMaking>();
	private FastMap<String, BergMaking> typeLevel_making = new FastMap<String, BergMaking>();

	public static BergLoadData getInstance() {
		if (instance == null) {
			instance = new BergLoadData();
		}
		return instance;

	}

	public boolean readData() {

		if (logger.isDebugEnabled()) {
			logger.info("read BergMaking data");
		}
		boolean b = readData_berg();

		return b;

	}

	private boolean readData_berg() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("berg"));
		if (!xmlFile.exists()) {
			logger.error("read berg xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("BergData", BergMaking.class);
			stream.alias("ArrayOfBergData", XmlBergData.class);
			stream.addImplicitCollection(XmlBergData.class, "array");
			XmlBergData xmlBergData = (XmlBergData) stream.fromXML(new FileReader(xmlFile));

			for (BergMaking making : xmlBergData.getArray()) {
				making.setBergType();
				this.bergMakings.put(making.getId(), making);
				this.typeLevel_making.put(new StringBuffer().append(making.getBergType().asCode()).append("_").append(making.getLevel()).toString(), making);
			}

			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	public FastMap<Integer, BergMaking> getBergMakings() {
		return bergMakings;
	}

	public void setBergMakings(FastMap<Integer, BergMaking> bergMakings) {
		this.bergMakings = bergMakings;
	}

	public BergMaking getBergMaking(Integer id) {
		return bergMakings.get(id);
	}

	public BergMaking getBergMaking(Integer type, int level) {
		return typeLevel_making.get(new StringBuffer().append(type).append("_").append(level).toString());
	}
}