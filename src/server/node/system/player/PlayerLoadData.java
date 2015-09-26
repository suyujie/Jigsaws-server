package server.node.system.player;

import gamecore.system.AbstractLoadData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javolution.util.FastMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thoughtworks.xstream.XStream;

public final class PlayerLoadData extends AbstractLoadData {

	private final static Logger logger = LogManager.getLogger(PlayerLoadData.class);

	private static final PlayerLoadData instance = new PlayerLoadData();

	private final FastMap<Integer, Integer> nextLevelExp = new FastMap<Integer, Integer>();

	public static PlayerLoadData getInstance() {
		return PlayerLoadData.instance;
	}

	public boolean readMakingData() {

		XStream stream = new XStream();

		File xmlFile = new File(getNewXmlName("expHomeUpDate"));

		if (!xmlFile.exists()) {
			logger.error("read xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("int", Integer.class);
			stream.alias("ArrayOfInt", XmlHomeLevelUpData.class);
			stream.addImplicitCollection(XmlHomeLevelUpData.class, "ArrayOfLevelExp");
			XmlHomeLevelUpData xmlHomeLevelUpData = (XmlHomeLevelUpData) stream.fromXML(new FileReader(xmlFile));

			int i = 0;
			for (Integer exp : xmlHomeLevelUpData.getExps()) {
				this.nextLevelExp.put(++i, exp);
			}

			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		}
		return true;
	}

	public Integer getNeedExp(Integer level) {
		return nextLevelExp.get(level);
	}

}
