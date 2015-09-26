package server.node.system.robot;

import gamecore.system.AbstractLoadData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;
import javolution.util.FastTable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class LevelRobotData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(LevelRobotData.class.getName());

	private static LevelRobotData instance = null;

	FastTable<LevelRobotMaking> robots = new FastTable<LevelRobotMaking>();
	FastTable<Integer> giveRobotLevel = new FastTable<Integer>();//送机器人的主城等级
	FastMap<Integer, Integer> levelRobotNum = new FastMap<Integer, Integer>();//每个等级对应的机器人数量
	LevelRobotMaking storageRobotMaking = null;

	public static LevelRobotData getInstance() {
		if (instance == null) {
			instance = new LevelRobotData();
		}
		return instance;

	}

	public boolean readData() {

		boolean b = readData_levelRobot();

		int num = 0;
		for (int i = 1; i <= 100; i++) {//多往后加5级
			if (giveRobotLevel.contains(i)) {
				num++;
			}
			levelRobotNum.put(i, num);
		}

		return b;
	}

	private boolean readData_levelRobot() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("levelRobot"));
		if (!xmlFile.exists()) {
			logger.error("read xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("LevelRobot", LevelRobotMaking.class);
			stream.alias("ArrayOfLevelRobotData", XmlLevelRobotsData.class);
			stream.addImplicitCollection(XmlLevelRobotsData.class, "ArrayOfLevelRobotData");
			XmlLevelRobotsData xmlLevelRobotsData = (XmlLevelRobotsData) stream.fromXML(new FileReader(xmlFile));

			for (LevelRobotMaking levelRobot : xmlLevelRobotsData.getLevelRobots()) {

				levelRobot.encapsulateBean();

				if (levelRobot.getLevel() == 0) {//0表示仓库的机器人
					storageRobotMaking = levelRobot;
				} else {
					this.robots.add(levelRobot);
					this.giveRobotLevel.add(levelRobot.getLevel());
				}

			}

			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		}
		return false;
	}

	public LevelRobotMaking getMaking(Integer level, int haveNum) {

		Integer num = this.levelRobotNum.get(level);//应该有这么多个机器人，同时num也是机器人的索引号

		if (num != null && num > haveNum) {//应有的 > 拥有的，，那就给一个
			return this.robots.get(num - 1);//机器人索引号是从 0---3
		}
		return null;

	}

	public LevelRobotMaking getMakingById(Integer id) {
		return this.robots.get(id);
	}

	public LevelRobotMaking getMakingInStorage() {
		return this.storageRobotMaking;
	}

	public byte[] newRobotLevels() {
		byte[] levels = new byte[3];
		for (int i = 0; i < levels.length; i++) {
			levels[i] = giveRobotLevel.get(i).byteValue();
		}
		return levels;
	}

}