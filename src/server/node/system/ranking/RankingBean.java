package server.node.system.ranking;

public class RankingBean {

	private String name;
	private long playerId;
	private int record;
	private byte weaponType;
	private int homelevel;

	public RankingBean(String name, long playerId, int record, byte weaponType, int homelevel) {
		super();
		this.name = name;
		this.playerId = playerId;
		this.record = record;
		this.weaponType = weaponType;
		this.homelevel = homelevel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public int getRecord() {
		return record;
	}

	public void setRecord(int record) {
		this.record = record;
	}

	public byte getWeaponType() {
		return weaponType;
	}

	public void setWeaponType(byte weaponType) {
		this.weaponType = weaponType;
	}

	public int getHomelevel() {
		return homelevel;
	}

	public void setHomelevel(int homelevel) {
		this.homelevel = homelevel;
	}

}
