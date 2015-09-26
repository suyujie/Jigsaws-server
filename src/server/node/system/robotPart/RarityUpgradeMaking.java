package server.node.system.robotPart;

public class RarityUpgradeMaking {

	private Integer rarity;
	private Integer chip;
	private Integer cash;

	public RarityUpgradeMaking() {
	}

	public RarityUpgradeMaking(Integer rarity, Integer chip, Integer cash) {
		this.rarity = rarity;
		this.chip = chip;
		this.cash = cash;
	}

	public Integer getRarity() {
		return rarity;
	}

	public void setRarity(Integer rarity) {
		this.rarity = rarity;
	}

	public Integer getChip() {
		return chip;
	}

	public void setChip(Integer chip) {
		this.chip = chip;
	}

	public Integer getCash() {
		return cash;
	}

	public void setCash(Integer cash) {
		this.cash = cash;
	}

}