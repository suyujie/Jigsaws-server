package server.node.system.dailyJob;

import gamecore.system.AbstractLoadData;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class JobLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(JobLoadData.class.getName());

	private static JobLoadData instance = null;

	private FastMap<Integer, JobMaking> jobs = null;

	//分成4档
	private HashMap<Integer, List<Integer>> groupJobs = null;

	public static JobLoadData getInstance() {
		if (instance == null) {
			instance = new JobLoadData();
		}
		return instance;

	}

	public boolean readData() {

		if (logger.isDebugEnabled()) {
			logger.info("read jobMaking data");
		}

		this.jobs = new FastMap<Integer, JobMaking>();
		this.groupJobs = new HashMap<Integer, List<Integer>>();

		boolean b = readData_job();

		return b;

	}

	/**
	 * 读取jobMaking
	 */
	private boolean readData_job() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("dailyJob"));
		if (!xmlFile.exists()) {
			logger.error("read dailyJob xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("DailyJobData", JobMaking.class);
			stream.alias("ArrayOfDailyJobData", XmlJobsData.class);
			stream.addImplicitCollection(XmlJobsData.class, "ArrayOfDailyJobData");
			XmlJobsData xmlJobsData = (XmlJobsData) stream.fromXML(new FileReader(xmlFile));

			for (JobMaking jobMaking : xmlJobsData.getJobs()) {

				this.jobs.put(jobMaking.getId(), jobMaking);

				List<Integer> jobs = this.groupJobs.get(jobMaking.getGroup());
				if (jobs == null) {
					jobs = new ArrayList<Integer>();
				}
				jobs.add(jobMaking.getId());
				this.groupJobs.put(jobMaking.getGroup(), jobs);

			}

			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	public JobMaking getJobMaking(Integer id) {
		return this.jobs.get(id);
	}

	public List<Integer> getGroupJobs(Integer group) {
		return groupJobs.get(group);
	}

}