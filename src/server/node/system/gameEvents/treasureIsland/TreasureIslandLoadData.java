package server.node.system.gameEvents.treasureIsland;

import gamecore.system.AbstractLoadData;

import java.io.File;
import java.io.FileReader;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class TreasureIslandLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(TreasureIslandLoadData.class.getName());

	private static TreasureIslandLoadData instance = null;

	private TreasureIslandBossMaking cashBossMaking = null;
	private TreasureIslandBossMaking expBossMaking = null;

	public static TreasureIslandLoadData getInstance() {
		if (instance == null) {
			instance = new TreasureIslandLoadData();
		}
		return instance;

	}

	public boolean readData() {

		boolean b = readDataTreasureIslandBoss();
		return b;
	}

	private boolean readDataTreasureIslandBoss() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("treasureIsland"));
		if (!xmlFile.exists()) {
			logger.error("read treasureIsland xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("RobotsData", TreasureIslandBossMaking.class);
			stream.alias("ArrayOfRobotsData", XmlTreasureIslandBossData.class);
			stream.addImplicitCollection(XmlTreasureIslandBossData.class, "array");
			XmlTreasureIslandBossData data = (XmlTreasureIslandBossData) stream.fromXML(new FileReader(xmlFile));

			for (TreasureIslandBossMaking making : data.getArray()) {
				making.setTable();
				if (making.getId() == 0) {
					cashBossMaking = making;
				}
				if (making.getId() == 1) {
					expBossMaking = making;
				}
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public TreasureIslandBossMaking getCashBossMaking() {
		return cashBossMaking;
	}

	public void setCashBossMaking(TreasureIslandBossMaking cashBossMaking) {
		this.cashBossMaking = cashBossMaking;
	}

	public TreasureIslandBossMaking getExpBossMaking() {
		return expBossMaking;
	}

	public void setExpBossMaking(TreasureIslandBossMaking expBossMaking) {
		this.expBossMaking = expBossMaking;
	}

}