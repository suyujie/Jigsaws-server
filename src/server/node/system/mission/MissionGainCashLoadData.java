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
public class MissionGainCashLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(MissionGainCashLoadData.class.getName());

	private static MissionGainCashLoadData instance = null;

	FastMap<Integer, MissionGainCashMaking> missionGains = null;

	public static MissionGainCashLoadData getInstance() {
		if (instance == null) {
			instance = new MissionGainCashLoadData();
		}
		return instance;

	}

	public boolean readData() {

		this.missionGains = new FastMap<Integer, MissionGainCashMaking>();

		boolean b = readDataMissionGain();

		return b;
	}

	/**
	 * 读取missionGain
	 */
	private boolean readDataMissionGain() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("missionGain"));
		if (!xmlFile.exists()) {
			logger.error("read missionGain xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("MissionData", MissionGainCashMaking.class);
			stream.alias("ArrayOfMissionData", XmlMissionGainCashData.class);
			stream.addImplicitCollection(XmlMissionGainCashData.class, "missionGainArray");
			XmlMissionGainCashData xmlMissionGainData = (XmlMissionGainCashData) stream.fromXML(new FileReader(xmlFile));

			for (MissionGainCashMaking missionGainMaking : xmlMissionGainData.getMissionGainArray()) {
				this.missionGains.put(missionGainMaking.getId(), missionGainMaking);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public MissionGainCashMaking getMissionGainMaking(Integer id) {
		return this.missionGains.get(id);
	}

}