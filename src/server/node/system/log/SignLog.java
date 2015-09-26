package server.node.system.log;


public class SignLog extends AbstractLog {

	private Integer beforeGold;
	private Integer afterGold;
	private Long beforeCash;
	private Long afterCash;
	private Integer beforeLevel;
	private Integer afterLevel;
	private Integer maxPointId;

	public SignLog(Long id, Long playerId, Integer beforeLevel, Integer beforeGold, Long beforeCash) {
		super(id, playerId);
		this.beforeLevel = beforeLevel;
		this.beforeGold = beforeGold;
		this.beforeCash = beforeCash;
	}

	public SignLog(Long id, Integer afterLevel, Integer afterGold, Long afterCash, Integer maxPointId) {
		super(id);
		this.afterLevel = afterLevel;
		this.afterGold = afterGold;
		this.afterCash = afterCash;
		this.maxPointId = maxPointId;
	}

	public Integer getBeforeGold() {
		return beforeGold;
	}

	public void setBeforeGold(Integer beforeGold) {
		this.beforeGold = beforeGold;
	}

	public Integer getAfterGold() {
		return afterGold;
	}

	public void setAfterGold(Integer afterGold) {
		this.afterGold = afterGold;
	}

	public Long getBeforeCash() {
		return beforeCash;
	}

	public void setBeforeCash(Long beforeCash) {
		this.beforeCash = beforeCash;
	}

	public Long getAfterCash() {
		return afterCash;
	}

	public void setAfterCash(Long afterCash) {
		this.afterCash = afterCash;
	}

	public Integer getBeforeLevel() {
		return beforeLevel;
	}

	public void setBeforeLevel(Integer beforeLevel) {
		this.beforeLevel = beforeLevel;
	}

	public Integer getAfterLevel() {
		return afterLevel;
	}

	public void setAfterLevel(Integer afterLevel) {
		this.afterLevel = afterLevel;
	}

	public Integer getMaxPointId() {
		return maxPointId;
	}

	public void setMaxPointId(Integer maxPointId) {
		this.maxPointId = maxPointId;
	}

}
