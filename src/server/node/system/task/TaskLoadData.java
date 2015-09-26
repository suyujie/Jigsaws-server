package server.node.system.task;

import gamecore.system.AbstractLoadData;
import gamecore.util.DataUtils;

import java.io.File;
import java.io.FileReader;

import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class TaskLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(TaskLoadData.class.getName());

	private static TaskLoadData instance = null;

	FastMap<Integer, TaskMaking> tasks = null;

	public static TaskLoadData getInstance() {
		if (instance == null) {
			instance = new TaskLoadData();
		}
		return instance;

	}

	public boolean readData() {

		if (logger.isDebugEnabled()) {
			logger.info("read taskMaking data");
		}

		this.tasks = new FastMap<Integer, TaskMaking>();

		boolean b = readData_task();

		return b;
	}

	/**
	 * 读取taskMaking
	 */
	private boolean readData_task() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("achieveData"));
		if (!xmlFile.exists()) {
			logger.error("read achieveData xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("AchieveData", TaskMaking.class);
			stream.alias("ArrayOfAchieveData", XmlTasksData.class);
			stream.addImplicitCollection(XmlTasksData.class, "ArrayOfAchieveData");
			XmlTasksData xmlTasksData = (XmlTasksData) stream.fromXML(new FileReader(xmlFile));

			for (TaskMaking taskMaking : xmlTasksData.getTasks()) {

				taskMaking.setNeedNumTable(DataUtils.string2Array(taskMaking.getIfNumStrList()));
				taskMaking.setRewardGoldTable(DataUtils.string2Array(taskMaking.getRewardNumStrList()));

				this.tasks.put(taskMaking.getId(), taskMaking);
			}

			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	public TaskMaking getTaskMaking(Integer id) {
		return this.tasks.get(id);
	}

}