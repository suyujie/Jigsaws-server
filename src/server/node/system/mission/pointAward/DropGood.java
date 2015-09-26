package server.node.system.mission.pointAward;

public class DropGood {

	Integer type;
	Integer id;
	Integer level;
	Integer rate;

	public DropGood(Integer type, Integer id, Integer level, Integer rate) {
		super();
		this.type = type;
		this.id = id;
		this.level = level;
		this.rate = rate;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getRate() {
		return rate;
	}

	public void setRate(Integer rate) {
		this.rate = rate;
	}

}
