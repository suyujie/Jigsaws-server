package server.node.system.log;

public class PvpLog extends AbstractLog {

	private Long attackerId;
	private Long defenderId;

	private Integer attackerLevel;
	private Integer defenderLevel;

	private Integer attackerWin;
	private Integer winCash;

	private String attackerWeapon;
	private String defenderWeapon;

	private long beginTime;
	private long endTime;

	public PvpLog(Long id, Long attackerId, Long defenderId, Integer attackerLevel, Integer defenderLevel, Integer attackerWin, Integer winCash, String attackerWeapon,
			String defenderWeapon, long beginTime, long endTime) {
		super(id);
		this.attackerId = attackerId;
		this.defenderId = defenderId;
		this.attackerLevel = attackerLevel;
		this.defenderLevel = defenderLevel;
		this.attackerWin = attackerWin;
		this.winCash = winCash;
		this.attackerWeapon = attackerWeapon;
		this.defenderWeapon = defenderWeapon;
		this.beginTime = beginTime;
		this.endTime = endTime;
	}

	public String getAttackerWeapon() {
		return attackerWeapon;
	}

	public void setAttackerWeapon(String attackerWeapon) {
		this.attackerWeapon = attackerWeapon;
	}

	public String getDefenderWeapon() {
		return defenderWeapon;
	}

	public void setDefenderWeapon(String defenderWeapon) {
		this.defenderWeapon = defenderWeapon;
	}

	public long getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public Long getAttackerId() {
		return attackerId;
	}

	public void setAttackerId(Long attackerId) {
		this.attackerId = attackerId;
	}

	public Long getDefenderId() {
		return defenderId;
	}

	public void setDefenderId(Long defenderId) {
		this.defenderId = defenderId;
	}

	public Integer getAttackerLevel() {
		return attackerLevel;
	}

	public void setAttackerLevel(Integer attackerLevel) {
		this.attackerLevel = attackerLevel;
	}

	public Integer getDefenderLevel() {
		return defenderLevel;
	}

	public void setDefenderLevel(Integer defenderLevel) {
		this.defenderLevel = defenderLevel;
	}

	public Integer getAttackerWin() {
		return attackerWin;
	}

	public void setAttackerWin(Integer attackerWin) {
		this.attackerWin = attackerWin;
	}

	public Integer getWinCash() {
		return winCash;
	}

	public void setWinCash(Integer winCash) {
		this.winCash = winCash;
	}

}
