package server.node.system.mission;

import gamecore.system.AbstractLoadData;

import java.io.File;
import java.io.FileReader;

import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class MissionGainExpLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(MissionGainExpLoadData.class.getName());

	private static MissionGainExpLoadData instance = null;

	FastMap<Integer, MissionGainExpMaking> missionGainExps = null;

	public static MissionGainExpLoadData getInstance() {
		if (instance == null) {
			instance = new MissionGainExpLoadData();
		}
		return instance;

	}

	public boolean readData() {

		this.missionGainExps = new FastMap<Integer, MissionGainExpMaking>();

		boolean b = readDataMissionGainExp();

		return b;
	}

	private boolean readDataMissionGainExp() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("missionExp"));
		if (!xmlFile.exists()) {
			logger.error("read missionExp xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("MissionExp", MissionGainExpMaking.class);
			stream.alias("ArrayOfMissionExp", XmlMissionGainExpData.class);
			stream.addImplicitCollection(XmlMissionGainExpData.class, "array");
			XmlMissionGainExpData data = (XmlMissionGainExpData) stream.fromXML(new FileReader(xmlFile));

			for (MissionGainExpMaking making : data.getArray()) {
				making.setLevelExpIds();
				this.missionGainExps.put(making.getMissionId(), making);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public MissionGainExpMaking getMissionGainExpMaking(Integer id) {
		return this.missionGainExps.get(id);
	}

}