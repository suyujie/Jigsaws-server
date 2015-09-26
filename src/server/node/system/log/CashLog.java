package server.node.system.log;

public class CashLog extends AbstractLog {

	private long beforeCash;
	private long changeCash;
	private long afterCash;
	private int changeTyte;

	public CashLog(Long id, Long playerId, long beforeCash, long changeCash, long afterCash, int changeTyte) {
		super(id, playerId);
		this.beforeCash = beforeCash;
		this.changeCash = changeCash;
		this.afterCash = afterCash;
		this.changeTyte = changeTyte;
	}

	public long getBeforeCash() {
		return beforeCash;
	}

	public long getChangeCash() {
		return changeCash;
	}

	public long getAfterCash() {
		return afterCash;
	}

	public int getChangeTyte() {
		return changeTyte;
	}

}
