package server.node.system.log;

public class PveLog extends AbstractLog {

	private Integer pointId;
	private Integer pointStar;
	private Integer win;
	private Integer cash;
	private boolean firstPass;
	private long enterTime;
	private long exitTime;
	private String weaponStr;

	public PveLog(Long id, Long playerId, Integer pointId, Integer pointStar, Integer win, Integer cash, boolean firstPass, long enterTime, long exitTime, String weaponStr) {
		super(id, playerId);
		this.pointId = pointId;
		this.pointStar = pointStar;
		this.win = win;
		this.cash = cash;
		this.firstPass = firstPass;
		this.enterTime = enterTime;
		this.exitTime = exitTime;
		this.weaponStr = weaponStr;
	}

	public Integer getPointId() {
		return pointId;
	}

	public void setPointId(Integer pointId) {
		this.pointId = pointId;
	}

	public Integer getPointStar() {
		return pointStar;
	}

	public void setPointStar(Integer pointStar) {
		this.pointStar = pointStar;
	}

	public Integer getWin() {
		return win;
	}

	public void setWin(Integer win) {
		this.win = win;
	}

	public Integer getCash() {
		return cash;
	}

	public void setCash(Integer cash) {
		this.cash = cash;
	}

	public boolean isFirstPass() {
		return firstPass;
	}

	public void setFirstPass(boolean firstPass) {
		this.firstPass = firstPass;
	}

	public long getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(long enterTime) {
		this.enterTime = enterTime;
	}

	public long getExitTime() {
		return exitTime;
	}

	public void setExitTime(long exitTime) {
		this.exitTime = exitTime;
	}

	public String getWeaponStr() {
		return weaponStr;
	}

	public void setWeaponStr(String weaponStr) {
		this.weaponStr = weaponStr;
	}

}
