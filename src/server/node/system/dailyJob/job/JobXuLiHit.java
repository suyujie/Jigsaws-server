package server.node.system.dailyJob.job;

import server.node.system.dailyJob.JobCurrentStatus;
import server.node.system.dailyJob.JobLoadData;
import server.node.system.dailyJob.JobMaking;
import server.node.system.dailyJob.JobType;

public class JobXuLiHit extends AbstractJob implements IJob {

	private static final long serialVersionUID = -957273786839851366L;

	public JobType jobType = JobType.XuLiHit;

	public JobXuLiHit() {
	}

	public void doJob(int hitNum) {

		if (status == 0) {//没完成的
			completedNum += hitNum;
			JobMaking jobMaking = JobLoadData.getInstance().getJobMaking(jobType.asCode());
			if (completedNum >= jobMaking.getNum()) {//完成了
				status = 1;
			}
		}

	}

	@Override
	public void doJob() {
	}

	public int reward() {
		int gold = 0;
		if (status == 1) {//还没有领取,可以领了
			status = 2;
			JobMaking jobMaking = JobLoadData.getInstance().getJobMaking(jobType.asCode());
			gold = jobMaking.getRewardNum();
		}
		return gold;
	}

	public JobCurrentStatus readCurrent() {
		JobMaking jobMaking = JobLoadData.getInstance().getJobMaking(jobType.asCode());
		JobCurrentStatus currentStatus = new JobCurrentStatus(jobMaking.getId(), status, jobMaking.getRewardNum(), completedNum, jobMaking.getNum(), -1);
		currentStatus.setNeedStr(jobMaking.getNum().toString());
		return currentStatus;
	}
}
