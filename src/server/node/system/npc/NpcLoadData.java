package server.node.system.npc;

import gamecore.system.AbstractLoadData;
import gamecore.util.DataUtils;
import gamecore.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Root;
import server.node.system.robot.FightProperty;
import server.node.system.robotPart.Part;
import server.node.system.robotPart.PartLoadData;
import server.node.system.robotPart.PartMaking;
import server.node.system.robotPart.PartQualityType;
import server.node.system.robotPart.PartSlotType;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class NpcLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(NpcLoadData.class.getName());

	private static NpcLoadData instance = null;

	List<NpcMaking> npcMakings = null;
	List<NpcRobot> npcRobotFirst = null;
	//score ,making
	FastMap<Integer, NpcRobot> scoreNpcRobots = null;
	//id ,making
	FastMap<Long, NpcRobot> idNpcRobots = null;

	//npcrobot的最大id
	private int npcRobotNum = 300;

	public static NpcLoadData getInstance() {
		if (instance == null) {
			instance = new NpcLoadData();
		}
		return instance;

	}

	public boolean readData() {

		this.npcMakings = new ArrayList<NpcMaking>();
		this.npcRobotFirst = new ArrayList<NpcRobot>();
		this.scoreNpcRobots = new FastMap<Integer, NpcRobot>();
		this.idNpcRobots = new FastMap<Long, NpcRobot>();

		boolean b = readData_npc();
		b = b & readData_npcRobot();
		b = b & readData_npcRobotFirst();

		logger.info("npcMakings.size : " + npcMakings.size());
		logger.info("npcRobotMakingFirst.size : " + npcRobotFirst.size());
		logger.info("scoreNpcRobots.size : " + scoreNpcRobots.size());
		logger.info("idNpcRobots.size : " + idNpcRobots.size());

		return b;
	}

	private boolean readData_npc() {

		if (logger.isDebugEnabled()) {
			logger.info("read npc_player data");
		}

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("NpcData"));
		if (!xmlFile.exists()) {
			logger.error("read xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("NpcData", NpcMaking.class);
			stream.alias("ArrayOfNpcData", XmlNpcData.class);
			stream.addImplicitCollection(XmlNpcData.class, "ArrayOfNpcData");
			XmlNpcData xmlPartsData = (XmlNpcData) stream.fromXML(new FileReader(xmlFile));

			for (NpcMaking npcMaking : xmlPartsData.getNpcMakings()) {
				this.npcMakings.add(npcMaking);
			}
			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		}
		return false;
	}

	//随机构造npcrobot
	private boolean readData_npcRobot() {
		long id = 0;
		List<NpcRobot> npcRobots = createNpcRobots(npcRobotNum);
		Collections.sort(npcRobots, new ScoreComparable());
		for (NpcRobot npcRobot : npcRobots) {
			if (id++ < npcRobotNum) {
				npcRobot.setId(id);
				FightProperty fightProperty = npcRobot.refreshFightProperty();
				this.idNpcRobots.put(npcRobot.getId(), npcRobot);
				this.scoreNpcRobots.put(fightProperty.getScore(), npcRobot);
			}
		}

		return true;
	}

	//第一次pvp使用的npcrobot
	private boolean readData_npcRobotFirst() {

		if (logger.isDebugEnabled()) {
			logger.info("read npc_robot_first data");
		}

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("NpcRobot"));
		if (!xmlFile.exists()) {
			logger.error("read xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("NpcRobotData", NpcRobotMaking.class);
			stream.alias("ArrayOfNpcRobotData", XmlNpcRobotData.class);
			stream.addImplicitCollection(XmlNpcRobotData.class, "ArrayOfNpcRobotData");
			XmlNpcRobotData xmlNpcRobotData = (XmlNpcRobotData) stream.fromXML(new FileReader(xmlFile));

			long id = 10000;
			for (NpcRobotMaking npcRobotMaking : xmlNpcRobotData.getNpcRobotMakings()) {
				NpcRobot npcRobot = npcRobotMaking.createNpcRobots();
				npcRobot.setId(id++);
				this.idNpcRobots.put(npcRobot.getId(), npcRobot);
				this.npcRobotFirst.add(npcRobot);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}

	public List<NpcMaking> getNpcMakings() {
		return npcMakings;
	}

	public void setNpcMakings(List<NpcMaking> npcMakings) {
		this.npcMakings = npcMakings;
	}

	public FastMap<Integer, NpcRobot> getScoreNpcRobots() {
		return scoreNpcRobots;
	}

	public void setScoreNpcRobots(FastMap<Integer, NpcRobot> scoreNpcRobots) {
		this.scoreNpcRobots = scoreNpcRobots;
	}

	public FastMap<Long, NpcRobot> getIdNpcRobots() {
		return idNpcRobots;
	}

	public void setIdNpcRobots(FastMap<Long, NpcRobot> idNpcRobots) {
		this.idNpcRobots = idNpcRobots;
	}

	private Part randomPart(PartSlotType partSlotType, int minLevel, int maxLevel) {

		List<PartMaking> list = PartLoadData.getInstance().getPartArray().get(partSlotType);//所有的arm 或者所有的腿...
		Collections.shuffle(list);//乱序

		PartMaking pm = null;

		while (pm == null) {
			for (PartMaking partMaking : list) {
				if (partMaking.getPartQualityType() != PartQualityType.GOLD && partMaking.getPartQualityType() != PartQualityType.TITANIUM) {//不要 金 钛
					//判断是否在等级区间内
					int partMaxLevel = partMaking.getScoreTable().size();
					if (maxLevel <= partMaxLevel) {//需要等级超过这个部件的最大等级
						pm = partMaking;
						break;
					}
				}
			}

			if (pm == null) {
				minLevel -= 10;
				maxLevel -= 10;
			}

		}

		Part part = Root.partSystem.createPart(0L, partSlotType.asCode(), pm, Utils.randomInt(minLevel, maxLevel), 0, 0);

		return part;
	}

	private NpcRobot randomNpcRobot(int minLevel, int maxLevel) {

		HashMap<Integer, Part> partPOs = new HashMap<>();

		partPOs.put(PartSlotType.HEAD.asCode(), randomPart(PartSlotType.HEAD, minLevel, maxLevel));
		partPOs.put(PartSlotType.ARM.asCode(), randomPart(PartSlotType.ARM, minLevel, maxLevel));
		partPOs.put(PartSlotType.LEG.asCode(), randomPart(PartSlotType.LEG, minLevel, maxLevel));
		partPOs.put(PartSlotType.BODY.asCode(), randomPart(PartSlotType.BODY, minLevel, maxLevel));
		partPOs.put(PartSlotType.WEAPON.asCode(), randomPart(PartSlotType.WEAPON, minLevel, maxLevel));

		//构造机器人
		NpcRobot npcRobot = new NpcRobot(partPOs);

		return npcRobot;
	}

	public List<NpcRobot> createNpcRobots(int num) {
		List<NpcRobot> robots = new ArrayList<>();

		//假设 所有的战力分布在150----2000 之间,那么,score/10为一档,每档最多10个
		Map<Integer, Integer> scoreNum = new HashMap<Integer, Integer>();

		while (robots.size() < num) {
			for (int i = 0; i < 19; i++) {
				for (int j = 0; j < 10; j++) {
					NpcRobot npcRobot = randomNpcRobot(i * 5 + 1, i * 5 + 5);
					try {
						FightProperty fightProperty = npcRobot.refreshFightProperty();
						Integer scoreLevel = fightProperty.getScore() / 10;
						if (!scoreNum.containsKey(scoreLevel) || scoreNum.get(scoreLevel) < 3) {//没有或者数量小于10个
							scoreNum.put(scoreLevel, scoreNum.get(scoreLevel) == null ? 0 : scoreNum.get(scoreLevel) + 1);
							robots.add(npcRobot);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		return robots;
	}

	public void getNpcRobots(List<NpcRobot> npcRobots, int scoreMin, int scoreMax, int num) {

		List<Integer> scores = DataUtils.combinationIntegerArray(scoreMin, scoreMax, true);

		for (Integer score : scores) {
			NpcRobot npcRobot = scoreNpcRobots.get(score);
			if (npcRobot != null && !npcRobots.contains(npcRobot.getId())) {
				npcRobots.add(npcRobot);
				if (npcRobots.size() >= num) {
					break;
				}
			}
		}
	}

	public void getNpcRobotFirst(List<NpcRobot> npcRobotIds, int num) {
		for (NpcRobot npcRobot : npcRobotFirst) {
			npcRobotIds.add(npcRobot);
			if (npcRobotIds.size() >= num) {
				break;
			}
		}
	}

}