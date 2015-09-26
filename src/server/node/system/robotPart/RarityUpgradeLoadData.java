package server.node.system.robotPart;

import gamecore.system.AbstractLoadData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class RarityUpgradeLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(RarityUpgradeLoadData.class.getName());

	private static RarityUpgradeLoadData instance = null;

	FastMap<Integer, RarityUpgradeMaking> makings = new FastMap<Integer, RarityUpgradeMaking>();

	public static RarityUpgradeLoadData getInstance() {
		if (instance == null) {
			instance = new RarityUpgradeLoadData();
		}
		return instance;

	}

	public boolean readData() {

		boolean b = readData_rarityUpgrade();

		return b;
	}

	private boolean readData_rarityUpgrade() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("rarityUpgradeData"));
		if (!xmlFile.exists()) {
			logger.error("read xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("RarityUpgrade", RarityUpgradeMaking.class);
			stream.alias("ArrayOfRarityUpgrade", XmlRarityUpgradeData.class);
			stream.addImplicitCollection(XmlRarityUpgradeData.class, "ArrayOfRarityUpgrade");
			XmlRarityUpgradeData xmlRarityUpgradeData = (XmlRarityUpgradeData) stream.fromXML(new FileReader(xmlFile));

			for (RarityUpgradeMaking making : xmlRarityUpgradeData.getRarityUpgradeMakings()) {
				this.makings.put(making.getRarity(), making);
			}
			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		}
		return false;
	}

	public RarityUpgradeMaking getRarityUpgradeMaking(Integer rarity) {
		return this.makings.get(rarity);
	}

	public List<RarityUpgradeMaking> needCashChip() {
		List<RarityUpgradeMaking> r = new ArrayList<RarityUpgradeMaking>();
		for (int i = 1; i <= makings.size(); i++) {
			RarityUpgradeMaking m = makings.get(i);
			if (m != null) {
				r.add(m);
			}
		}
		return r;
	}

}