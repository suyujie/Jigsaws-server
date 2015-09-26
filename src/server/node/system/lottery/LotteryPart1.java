package server.node.system.lottery;

import server.node.system.robotPart.PartSlotType;

/**
 * 抽奖元素
 */
public final class LotteryPart1 {

	private int partId;
	private PartSlotType partSlotType;

	public LotteryPart1(int partId, PartSlotType partSlotType) {
		super();
		this.partId = partId;
		this.partSlotType = partSlotType;
	}

	public int getPartId() {
		return partId;
	}

	public void setPartId(int partId) {
		this.partId = partId;
	}

	public PartSlotType getPartSlotType() {
		return partSlotType;
	}

	public void setPartSlotType(PartSlotType partSlotType) {
		this.partSlotType = partSlotType;
	}

}
