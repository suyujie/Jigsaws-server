package server.node.system.toturial;

import gamecore.system.AbstractLoadData;

import java.io.File;
import java.io.FileReader;

import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;
import javolution.util.FastTable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class ToturialLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(ToturialLoadData.class.getName());

	private static ToturialLoadData instance = null;

	FastMap<Integer, ToturialMaking> toturialMakings = null;
	//有奖励的教学id
	FastTable<Integer> toturialMakingIdsWithReward = null;

	public static ToturialLoadData getInstance() {
		if (instance == null) {
			instance = new ToturialLoadData();
		}
		return instance;

	}

	public boolean readData() {

		if (logger.isDebugEnabled()) {
			logger.info("read ToturialMaking data");
		}

		this.toturialMakings = new FastMap<Integer, ToturialMaking>();
		this.toturialMakingIdsWithReward = new FastTable<Integer>();

		boolean b = readData_teacher();

		return b;
	}

	/**
	 * 读取ToturialMaking
	 */
	private boolean readData_teacher() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("teacher"));
		if (!xmlFile.exists()) {
			logger.error("read teacher xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("TeachEvent", ToturialMaking.class);
			stream.alias("ArrayOfTeachEvent", XmlToturialData.class);
			stream.addImplicitCollection(XmlToturialData.class, "ArrayOfTeachEvent");
			XmlToturialData xmlToturialData = (XmlToturialData) stream.fromXML(new FileReader(xmlFile));

			for (ToturialMaking toturialMaking : xmlToturialData.getToturialMakings()) {
				toturialMaking.initReward();
				this.toturialMakings.put(toturialMaking.getId(), toturialMaking);
				if (toturialMaking.getMoneyType() != null) {
					this.toturialMakingIdsWithReward.add(toturialMaking.getId());
				}
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public ToturialMaking getToturialMaking(Integer id) {
		return this.toturialMakings.get(id);
	}

	public boolean checkHaveReward(Integer id) {
		return this.toturialMakingIdsWithReward.contains(id);
	}

	public FastTable<Integer> getToturialMakingIdsWithReward() {
		return toturialMakingIdsWithReward;
	}

}