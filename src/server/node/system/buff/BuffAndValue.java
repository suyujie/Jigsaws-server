package server.node.system.buff;

import java.io.Serializable;

public class BuffAndValue implements Serializable {

	private static final long serialVersionUID = 5927046012910018442L;

	private Integer buffId;
	private Integer value;

	public BuffAndValue(Integer buffId, Integer value) {
		super();
		this.buffId = buffId;
		this.value = value;
	}

	public Integer getBuffId() {
		return buffId;
	}

	public void setBuffId(Integer buffId) {
		this.buffId = buffId;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

}