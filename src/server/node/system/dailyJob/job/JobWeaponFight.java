package server.node.system.dailyJob.job;

import common.language.LangType;
import server.node.system.Root;
import server.node.system.dailyJob.JobCurrentStatus;
import server.node.system.dailyJob.JobLoadData;
import server.node.system.dailyJob.JobMaking;
import server.node.system.dailyJob.JobType;
import server.node.system.robotPart.WeaponType;

public class JobWeaponFight extends AbstractJob implements IJob {

	private static final long serialVersionUID = 8829882246697556305L;

	private WeaponType weaponType;

	public JobType jobType = JobType.WeaponFight;

	public JobWeaponFight() {
	}

	public JobWeaponFight(WeaponType weaponType) {
		this.weaponType = weaponType;
	}

	public WeaponType getWeaponType() {
		return weaponType;
	}

	public void setWeaponType(WeaponType weaponType) {
		this.weaponType = weaponType;
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

	public JobCurrentStatus readCurrent(LangType lang) {
		JobMaking jobMaking = JobLoadData.getInstance().getJobMaking(jobType.asCode());
		JobCurrentStatus currentStatus = new JobCurrentStatus(jobMaking.getId(), status, jobMaking.getRewardNum(), completedNum, jobMaking.getNum(), weaponType.asCode());
		if (lang != null) {
			currentStatus.setNeedStr(Root.langSystem.getMessage(lang, "weapon_" + weaponType.asCode()), jobMaking.getNum().toString());
		}
		return currentStatus;
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

	@Override
	public JobCurrentStatus readCurrent() {
		return null;
	}

}
