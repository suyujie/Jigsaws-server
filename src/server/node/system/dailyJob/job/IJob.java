package server.node.system.dailyJob.job;

import server.node.system.dailyJob.JobCurrentStatus;

public interface IJob {

	public JobCurrentStatus readCurrent();

	public void doJob();

	public int reward();

}
