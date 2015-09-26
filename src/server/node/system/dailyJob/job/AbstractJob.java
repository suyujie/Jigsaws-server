package server.node.system.dailyJob.job;

import java.io.Serializable;

public class AbstractJob implements Serializable {

	private static final long serialVersionUID = 4283877653754556148L;

	public int completedNum = 0;
	public int status = 0;//0 未完成,1完成未领奖,2领奖完

	public AbstractJob() {
	}

	public int getCompletedNum() {
		return completedNum;
	}

	public void setCompletedNum(int completedNum) {
		this.completedNum = completedNum;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
