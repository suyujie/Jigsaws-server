package server.node.system.boss;

import gamecore.system.AbstractLoadData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class BossLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(BossLoadData.class.getName());

	private static BossLoadData instance = null;

	FastMap<Integer, BossMaking> bosses = null;

	public static BossLoadData getInstance() {
		if (instance == null) {
			instance = new BossLoadData();
		}
		return instance;

	}

	public boolean readData() {

		if (logger.isDebugEnabled()) {
			logger.info("read boss data");
		}

		this.bosses = new FastMap<Integer, BossMaking>();

		return readData_boss();
	}

	/**
	 * 读取boss
	 */
	private boolean readData_boss() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("boss"));
		if (!xmlFile.exists()) {
			logger.error("read boss xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("BossData", BossMaking.class);
			stream.alias("ArrayOfBossData", XmlBossData.class);
			stream.addImplicitCollection(XmlBossData.class, "ArrayOfBossData");
			XmlBossData xmlBossData = (XmlBossData) stream.fromXML(new FileReader(xmlFile));

			for (BossMaking bossMaking : xmlBossData.getBosses()) {
				bossMaking.setTables();
				this.bosses.put(bossMaking.getId(), bossMaking);
			}

			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
			return false;
		}

	}

	public BossMaking getBossMaking(Integer id) {
		return this.bosses.get(id);
	}

}