package server.node.system.mission;

import java.io.Serializable;

public class MissionMaking implements Serializable {

	private static final long serialVersionUID = 5478121124318275817L;

	private Integer id;

	public MissionMaking(Integer id) {
		super();
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}