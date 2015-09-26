package server.node.system.log;


public class GoldLog extends AbstractLog {

	private long beforeGold;
	private long changeGold;
	private long afterGold;
	private int changeTyte;

	public GoldLog(Long id, Long playerId, long beforeGold, long changeGold, long afterGold, int changeTyte) {
		super(id, playerId);
		this.beforeGold = beforeGold;
		this.changeGold = changeGold;
		this.afterGold = afterGold;
		this.changeTyte = changeTyte;
	}

	public long getBeforeGold() {
		return beforeGold;
	}

	public long getChangeGold() {
		return changeGold;
	}

	public long getAfterGold() {
		return afterGold;
	}

	public int getChangeTyte() {
		return changeTyte;
	}

}
