package server.node.system.gameEvents.bergWheel;

import gamecore.system.AbstractLoadData;

import java.io.File;
import java.io.FileReader;

import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.robotPart.WeaponType;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class BergWheelLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(BergWheelLoadData.class.getName());

	private static BergWheelLoadData instance = null;

	public BergWheelMaking bergWheelMaking;
	FastMap<WeaponType, BergWheelWeaponMaking> bergWheelWeaponMakings = new FastMap<WeaponType, BergWheelWeaponMaking>();

	public static BergWheelLoadData getInstance() {
		if (instance == null) {
			instance = new BergWheelLoadData();
		}
		return instance;

	}

	public boolean readData() {

		boolean b = readBergWheelMaking();
		b = b & readDataBergWheelWeaponAI();
		return b;
	}

	private boolean readBergWheelMaking() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("BergWheelRobotBossData"));
		if (!xmlFile.exists()) {
			logger.error("read BergWheelRobotBossData xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("RobotsData", BergWheelMaking.class);
			bergWheelMaking = (BergWheelMaking) stream.fromXML(new FileReader(xmlFile));
			bergWheelMaking.setTable();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean readDataBergWheelWeaponAI() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("BergWheelWeaponAI"));
		if (!xmlFile.exists()) {
			logger.error("read BergWheelWeaponAI xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("RobotsData", BergWheelWeaponMaking.class);
			stream.alias("ArrayOfRobotsData", XmlBergWheelWeaponBossData.class);
			stream.addImplicitCollection(XmlBergWheelWeaponBossData.class, "array");
			XmlBergWheelWeaponBossData data = (XmlBergWheelWeaponBossData) stream.fromXML(new FileReader(xmlFile));

			for (BergWheelWeaponMaking making : data.getArray()) {
				this.bergWheelWeaponMakings.put(WeaponType.asEnum(making.getWeaponType()), making);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public BergWheelWeaponMaking getBergWheelWeaponMaking(WeaponType type) {
		return bergWheelWeaponMakings.get(type);
	}

}