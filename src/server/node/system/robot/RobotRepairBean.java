package server.node.system.robot;

public class RobotRepairBean {

	private Integer slot;
	private int needTime;

	public RobotRepairBean(Integer slot, int needTime) {
		super();
		this.slot = slot;
		this.needTime = needTime;
	}

	public Integer getSlot() {
		return slot;
	}

	public void setSlot(Integer slot) {
		this.slot = slot;
	}

	public int getNeedTime() {
		return needTime;
	}

	public void setNeedTime(int needTime) {
		this.needTime = needTime;
	}

}
