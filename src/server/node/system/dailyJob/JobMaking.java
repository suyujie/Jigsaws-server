package server.node.system.dailyJob;

import java.io.Serializable;

public class JobMaking implements Serializable {

	private static final long serialVersionUID = 4226355210339586873L;

	private Integer id;
	private Integer num;
	private Integer rewardNum;
	private Integer group;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGroup() {
		return group;
	}

	public void setGroup(Integer group) {
		this.group = group;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Integer getRewardNum() {
		return rewardNum;
	}

	public void setRewardNum(Integer rewardNum) {
		this.rewardNum = rewardNum;
	}

}
