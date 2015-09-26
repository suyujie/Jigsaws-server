package server.node.system.dailyJob.job;

import server.node.system.dailyJob.JobCurrentStatus;
import server.node.system.dailyJob.JobLoadData;
import server.node.system.dailyJob.JobMaking;
import server.node.system.dailyJob.JobType;

public class JobMoreFight extends AbstractJob implements IJob {

	private static final long serialVersionUID = -6702696846284662391L;

	public JobType jobType = JobType.MoreFight;

	public JobMoreFight() {
	}

	@Override
	public void doJob() {
		if (status == 0) {//没完成的
			completedNum++;
			JobMaking jobMaking = JobLoadData.getInstance().getJobMaking(jobType.asCode());
			if (completedNum >= jobMaking.getNum()) {//完成了
				status = 1;
			}
		}
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
