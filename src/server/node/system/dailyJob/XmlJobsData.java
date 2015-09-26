package server.node.system.dailyJob;

import java.util.ArrayList;

public class XmlJobsData {

	private ArrayList<JobMaking> ArrayOfDailyJobData = new ArrayList<JobMaking>();

	public ArrayList<JobMaking> getJobs() {
		return ArrayOfDailyJobData;
	}

	public void setJobs(ArrayList<JobMaking> jobMakings) {
		this.ArrayOfDailyJobData = jobMakings;
	}

}
