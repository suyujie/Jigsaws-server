package server.node.system.dailyJob;

import java.io.Serializable;

import server.node.system.dailyJob.job.JobBiShaHit;
import server.node.system.dailyJob.job.JobBishaKill;
import server.node.system.dailyJob.job.JobFaceBookShare;
import server.node.system.dailyJob.job.JobGivePower;
import server.node.system.dailyJob.job.JobHireRobot;
import server.node.system.dailyJob.job.JobMoreFight;
import server.node.system.dailyJob.job.JobPaint;
import server.node.system.dailyJob.job.JobPartGet;
import server.node.system.dailyJob.job.JobPartLevelUp;
import server.node.system.dailyJob.job.JobPvp;
import server.node.system.dailyJob.job.JobPvp1vs3;
import server.node.system.dailyJob.job.JobPvpBeatWeapon;
import server.node.system.dailyJob.job.JobPvpWin;
import server.node.system.dailyJob.job.JobRentRobot;
import server.node.system.dailyJob.job.JobRepaireRobot;
import server.node.system.dailyJob.job.JobVisitPlayer;
import server.node.system.dailyJob.job.JobWeaponFight;
import server.node.system.dailyJob.job.JobWinNoHit;
import server.node.system.dailyJob.job.JobWinOne;
import server.node.system.dailyJob.job.JobXuLiHit;
import server.node.system.dailyJob.job.JobXuLiKill;

/**
 * 每日任务包  实体
 */
public class JobBagPO implements Serializable {

	private static final long serialVersionUID = 1815990077977411997L;
	private long createTime;
	private int jobNum;

	private JobWinOne jobWinOne;
	private JobBiShaHit jobBiShaHit;
	private JobBishaKill jobBishaKill;
	private JobFaceBookShare jobFaceBookShare;
	private JobGivePower jobGivePower;
	private JobXuLiKill jobXuLiKill;
	private JobXuLiHit jobXuLiHit;
	private JobMoreFight jobMoreFight;
	private JobWeaponFight jobWeaponFight;
	private JobPartLevelUp jobPartLevelUp;
	private JobPvp jobPvp;
	private JobHireRobot jobHireRobot;
	private JobRentRobot jobRentRobot;
	private JobPartGet jobPartGet;
	private JobRepaireRobot jobRepaireRobot;
	private JobVisitPlayer jobVisitPlayer;
	private JobPaint jobPaint;
	private JobWinNoHit jobWinNoHit;
	private JobPvpWin jobPvpWin;
	private JobPvpBeatWeapon jobPvpBeatWeapon;
	private JobPvp1vs3 jobPvp1vs3;

	public JobBagPO() {
	}

	public JobBagPO(long createTime, int jobNum) {
		super();
		this.createTime = createTime;
		this.jobNum = jobNum;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getJobNum() {
		return jobNum;
	}

	public void setJobNum(int jobNum) {
		this.jobNum = jobNum;
	}

	public JobWinOne getJobWinOne() {
		return jobWinOne;
	}

	public void setJobWinOne(JobWinOne jobWinOne) {
		this.jobWinOne = jobWinOne;
	}

	public JobBiShaHit getJobBiShaHit() {
		return jobBiShaHit;
	}

	public void setJobBiShaHit(JobBiShaHit jobBiShaHit) {
		this.jobBiShaHit = jobBiShaHit;
	}

	public JobBishaKill getJobBishaKill() {
		return jobBishaKill;
	}

	public void setJobBishaKill(JobBishaKill jobBishaKill) {
		this.jobBishaKill = jobBishaKill;
	}

	public JobFaceBookShare getJobFaceBookShare() {
		return jobFaceBookShare;
	}

	public void setJobFaceBookShare(JobFaceBookShare jobFaceBookShare) {
		this.jobFaceBookShare = jobFaceBookShare;
	}

	public JobGivePower getJobGivePower() {
		return jobGivePower;
	}

	public void setJobGivePower(JobGivePower jobGivePower) {
		this.jobGivePower = jobGivePower;
	}

	public JobXuLiKill getJobXuLiKill() {
		return jobXuLiKill;
	}

	public void setJobXuLiKill(JobXuLiKill jobXuLiKill) {
		this.jobXuLiKill = jobXuLiKill;
	}

	public JobXuLiHit getJobXuLiHit() {
		return jobXuLiHit;
	}

	public void setJobXuLiHit(JobXuLiHit jobXuLiHit) {
		this.jobXuLiHit = jobXuLiHit;
	}

	public JobMoreFight getJobMoreFight() {
		return jobMoreFight;
	}

	public void setJobMoreFight(JobMoreFight jobMoreFight) {
		this.jobMoreFight = jobMoreFight;
	}

	public JobWeaponFight getJobWeaponFight() {
		return jobWeaponFight;
	}

	public void setJobWeaponFight(JobWeaponFight jobWeaponFight) {
		this.jobWeaponFight = jobWeaponFight;
	}

	public JobPartLevelUp getJobPartLevelUp() {
		return jobPartLevelUp;
	}

	public void setJobPartLevelUp(JobPartLevelUp jobPartLevelUp) {
		this.jobPartLevelUp = jobPartLevelUp;
	}

	public JobPvp getJobPvp() {
		return jobPvp;
	}

	public void setJobPvp(JobPvp jobPvp) {
		this.jobPvp = jobPvp;
	}

	public JobHireRobot getJobHireRobot() {
		return jobHireRobot;
	}

	public void setJobHireRobot(JobHireRobot jobHireRobot) {
		this.jobHireRobot = jobHireRobot;
	}

	public JobRentRobot getJobRentRobot() {
		return jobRentRobot;
	}

	public void setJobRentRobot(JobRentRobot jobRentRobot) {
		this.jobRentRobot = jobRentRobot;
	}

	public JobPartGet getJobPartGet() {
		return jobPartGet;
	}

	public void setJobPartGet(JobPartGet jobPartGet) {
		this.jobPartGet = jobPartGet;
	}

	public JobRepaireRobot getJobRepaireRobot() {
		return jobRepaireRobot;
	}

	public void setJobRepaireRobot(JobRepaireRobot jobRepaireRobot) {
		this.jobRepaireRobot = jobRepaireRobot;
	}

	public JobVisitPlayer getJobVisitPlayer() {
		return jobVisitPlayer;
	}

	public void setJobVisitPlayer(JobVisitPlayer jobVisitPlayer) {
		this.jobVisitPlayer = jobVisitPlayer;
	}

	public JobPaint getJobPaint() {
		return jobPaint;
	}

	public void setJobPaint(JobPaint jobPaint) {
		this.jobPaint = jobPaint;
	}

	public JobWinNoHit getJobWinNoHit() {
		return jobWinNoHit;
	}

	public void setJobWinNoHit(JobWinNoHit jobWinNoHit) {
		this.jobWinNoHit = jobWinNoHit;
	}

	public JobPvpWin getJobPvpWin() {
		return jobPvpWin;
	}

	public void setJobPvpWin(JobPvpWin jobPvpWin) {
		this.jobPvpWin = jobPvpWin;
	}

	public JobPvpBeatWeapon getJobPvpBeatWeapon() {
		return jobPvpBeatWeapon;
	}

	public void setJobPvpBeatWeapon(JobPvpBeatWeapon jobPvpBeatWeapon) {
		this.jobPvpBeatWeapon = jobPvpBeatWeapon;
	}

	public JobPvp1vs3 getJobPvp1vs3() {
		return jobPvp1vs3;
	}

	public void setJobPvp1vs3(JobPvp1vs3 jobPvp1vs3) {
		this.jobPvp1vs3 = jobPvp1vs3;
	}

}
