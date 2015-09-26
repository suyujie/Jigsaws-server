package server.node.system.task;

import java.io.Serializable;

public class TaskMaking implements Serializable {

	private static final long serialVersionUID = 1560599314281359482L;

	private Integer id;
	private Integer type;
	private String ifNumStrList;
	private String rewardNumStrList;

	private int[] needNumTable;
	private int[] rewardGoldTable;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getIfNumStrList() {
		return ifNumStrList;
	}

	public void setIfNumStrList(String ifNumStrList) {
		this.ifNumStrList = ifNumStrList;
	}

	public String getRewardNumStrList() {
		return rewardNumStrList;
	}

	public void setRewardNumStrList(String rewardNumStrList) {
		this.rewardNumStrList = rewardNumStrList;
	}

	public int[] getNeedNumTable() {
		return needNumTable;
	}

	public void setNeedNumTable(int[] needNumTable) {
		this.needNumTable = needNumTable;
	}

	public int[] getRewardGoldTable() {
		return rewardGoldTable;
	}

	public void setRewardGoldTable(int[] rewardGoldTable) {
		this.rewardGoldTable = rewardGoldTable;
	}

}
