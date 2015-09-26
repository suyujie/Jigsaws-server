package server.node.system.robot;

public class PartLevelBean {

	private Integer id;
	private Integer level;

	public PartLevelBean(Integer id, Integer level) {
		this.id = id;
		this.level = level;
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

}