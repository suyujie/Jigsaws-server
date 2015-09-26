package server.node.system.npc;

import java.io.Serializable;

public class NpcMaking implements Serializable {

	private static final long serialVersionUID = -3596919677624469201L;

	private Integer id;

	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}