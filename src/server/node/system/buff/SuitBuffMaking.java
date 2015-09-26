package server.node.system.buff;

import java.io.Serializable;

public class SuitBuffMaking implements Serializable {

	private static final long serialVersionUID = -4318524136278334080L;

	private String name;
	private Integer bufferId;
	private Integer pointID;
	private Integer partsId = 0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getBufferId() {
		return bufferId;
	}

	public void setBufferId(Integer bufferId) {
		this.bufferId = bufferId;
	}

	public Integer getPointID() {
		return pointID;
	}

	public void setPointID(Integer pointID) {
		this.pointID = pointID;
	}

	public Integer getPartsId() {
		return partsId;
	}

	public void setPartsId(Integer partsId) {
		this.partsId = partsId;
	}

}