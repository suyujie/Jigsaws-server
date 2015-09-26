package server.node.system.robotPart;

import java.util.ArrayList;

public class XmlRarityUpgradeData {

	private ArrayList<RarityUpgradeMaking> ArrayOfRarityUpgrade = new ArrayList<RarityUpgradeMaking>();

	public ArrayList<RarityUpgradeMaking> getRarityUpgradeMakings() {
		return ArrayOfRarityUpgrade;
	}

	public void setQualityMakings(ArrayList<RarityUpgradeMaking> makings) {
		this.ArrayOfRarityUpgrade = makings;
	}

}
