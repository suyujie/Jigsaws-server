package server.node.system.log;

import gamecore.util.Clock;

public abstract class AbstractLog {

	private Long id;
	private Long playerId;
	private Long ct;//创建时间  create time
	private Long st;//存储 时间 store time

	public AbstractLog() {
		this.ct = Clock.currentTimeSecond();
		this.st = Clock.currentTimeSecond();
	}

	public AbstractLog(Long id) {
		this.id = id;
		this.ct = Clock.currentTimeSecond();
		this.st = Clock.currentTimeSecond();
	}

	public AbstractLog(Long id, Long playerId) {
		this.id = id;
		this.playerId = playerId;
		this.ct = Clock.currentTimeSecond();
		this.st = Clock.currentTimeSecond();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public Long getCt() {
		return ct;
	}

	public void setCt(Long ct) {
		this.ct = ct;
	}

	public Long getSt() {
		return st;
	}

	public void setSt(Long st) {
		this.st = st;
	}

}
