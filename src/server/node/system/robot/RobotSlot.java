package server.node.system.robot;

import gamecore.io.ByteArrayGameOutput;
import gamecore.util.Clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RobotSlot {

	private static Logger logger = LogManager.getLogger(RobotSlot.class.getName());

	private Integer slot;
	private long repairBeginTime;//修理开始时间
	private long repairEndTime;//维修结束时间戳,0表示没有在维修状态,单位时间为秒
	private int wear;//剩余耐久度

	public RobotSlot() {
	}

	public RobotSlot(Integer slot, int wear, long repairBeginTime, long repairEndTime) {
		this.slot = slot;
		this.wear = wear;
		this.repairBeginTime = repairBeginTime;
		this.repairEndTime = repairEndTime;
	}

	public Integer getSlot() {
		return slot;
	}

	public long getRepairEndTime() {
		return repairEndTime;
	}

	public void setSlot(Integer slot) {
		this.slot = slot;
	}

	public int getWear() {
		return wear;
	}

	public void setWear(int wear) {
		this.wear = wear;
	}

	public void setRepairEndTime(long repairEndTime) {
		this.repairEndTime = repairEndTime;
	}

	public long getRepairBeginTime() {
		return repairBeginTime;
	}

	public void setRepairBeginTime(long repairBeginTime) {
		this.repairBeginTime = repairBeginTime;
	}

	public boolean isRepairing() {
		return repairEndTime != 0;
	}

	public byte[] toByteArray() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			//剩余的耐久
			bago.putInt(wear);
			//维修剩余时间(秒)	
			if (repairEndTime == 0) {
				bago.putInt(0);
			} else {
				int repairNeedTime = (int) (repairEndTime - Clock.currentTimeSecond());
				bago.putInt(repairNeedTime > 0 ? repairNeedTime : 0);
			}

		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}

}
