package server.node.system.mission;

import java.io.Serializable;

public class MissionGainCashMaking implements Serializable {

	private static final long serialVersionUID = 3861887833664707821L;

	private Integer id;//mission id
	private Integer correction;//修正参数
	private int maxTime;//最大时间

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCorrection() {
		return correction;
	}

	public void setCorrection(Integer correction) {
		this.correction = correction;
	}

	public int getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

}
