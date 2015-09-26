package server.node.system.robot;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;
import gamecore.io.ByteArrayGameOutput;
import gamecore.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import server.node.system.Content;
import server.node.system.robotPart.Part;
import server.node.system.robotPart.PartLoadData;
import server.node.system.robotPart.PartMaking;
import server.node.system.robotPart.PartQualityType;
import server.node.system.robotPart.QualityLoadData;
import server.node.system.robotPart.QualityMaking;

/**
 * 战斗机器人包
 */
public class RobotBag extends AbstractEntity {

	private static final long serialVersionUID = 8783268910066213103L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "robot_bag_";

	private Long id;

	private Integer newRobotSlot = null;//新获得的机器人,还没有同步到客户端

	private HashMap<Integer, RobotSlot> robotSlots = new HashMap<Integer, RobotSlot>();

	private HashMap<Integer, Robot> battleRobots = new HashMap<Integer, Robot>();

	private HashMap<Integer, Robot> storageRobots = new HashMap<Integer, Robot>();

	public RobotBag() {
	}

	public RobotBag(Long id) {
		super(RobotBag.generateCacheKey(id));
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNewRobotSlot() {
		return newRobotSlot;
	}

	public void setNewRobotSlot(Integer newRobotSlot) {
		this.newRobotSlot = newRobotSlot;
	}

	public HashMap<Integer, RobotSlot> getRobotSlots() {
		return robotSlots;
	}

	public void setRobotSlots(HashMap<Integer, RobotSlot> robotSlots) {
		this.robotSlots = robotSlots;
	}

	public HashMap<Integer, Robot> getBattleRobots() {
		return battleRobots;
	}

	public void setBattleRobots(HashMap<Integer, Robot> battleRobots) {
		this.battleRobots = battleRobots;
	}

	public HashMap<Integer, Robot> getStorageRobots() {
		return storageRobots;
	}

	public void setStorageRobots(HashMap<Integer, Robot> storageRobots) {
		this.storageRobots = storageRobots;
	}

	/**
	 * 更新新机器人信息
	 */
	public void putNewRobot(int slot, boolean sync) {
		newRobotSlot = slot;
		if (sync) {
			this.synchronize();
		}
	}

	/**
	 * 清除新机器人信息
	 */
	public void clearNewRobot(boolean sync) {
		if (newRobotSlot != null) {
			newRobotSlot = null;
			if (sync) {
				this.synchronize();
			}
		}
	}

	public void putRobot(Robot robot, boolean sync) {
		if (robot.getRobotType() == RobotType.BATTLE) {
			battleRobots.put(robot.getSlot(), robot);
		} else {
			storageRobots.put(robot.getSlot(), robot);
		}
		if (sync) {
			this.synchronize();
		}
	}

	public void putRobotSlot(RobotSlot robotSlot, boolean sync) {
		robotSlots.put(robotSlot.getSlot(), robotSlot);
		if (sync) {
			this.synchronize();
		}
	}

	public List<RobotSlot> robotSlotArray() {
		List<RobotSlot> list = new ArrayList<RobotSlot>();
		for (int i = 0; i <= 2; i++) {
			RobotSlot robotSlot = robotSlots.get(i);
			list.add(robotSlot);
		}
		return list;
	}

	public void removeRobot(RobotType robotType, int robotSlot, boolean sync) {
		if (robotType == RobotType.BATTLE) {
			battleRobots.remove(robotSlot);
		} else {
			storageRobots.remove(robotSlot);
		}
		if (sync) {
			this.synchronize();
		}
	}

	/**
	 * 数量
	 */
	public int readRobotNum(RobotType robotType) {
		if (robotType == RobotType.BATTLE) {
			return battleRobots.size();
		} else {
			return storageRobots.size();
		}
	}

	public Robot readRobot(RobotType robotType, Integer slot) {
		if (robotType == RobotType.BATTLE) {
			return battleRobots.get(slot);
		} else {
			return storageRobots.get(slot);
		}
	}

	public Robot readRobot(Long robotId) {
		for (Robot robot : battleRobots.values()) {
			if (null != robot && robot.getId().longValue() == robotId.longValue()) {
				return robot;
			}
		}
		for (Robot robot : storageRobots.values()) {
			if (null != robot && robot.getId().longValue() == robotId.longValue()) {
				return robot;
			}
		}
		return null;
	}

	/**
	 * robot list
	 */
	public List<Robot> readRobots(RobotType robotType) {
		List<Robot> list = new ArrayList<Robot>();

		if (robotType == RobotType.BATTLE) {
			for (int i = 0; i < 5; i++) {
				Robot robot = battleRobots.get(i);
				if (null != robot) {
					list.add(robot);
				}
			}
		}
		if (robotType == RobotType.STORAGE) {
			for (Robot robot : storageRobots.values()) {
				if (null != robot) {
					list.add(robot);
				}
			}
		}

		return list;
	}

	/**
	 * fight robots
	 */
	public HashMap<Integer, Robot> readFightRobots(boolean canFight) {
		HashMap<Integer, Robot> map = new HashMap<Integer, Robot>();
		for (int i = 0; i < 3; i++) {
			Robot robot = battleRobots.get(i);
			if (null != robot) {
				if (canFight) {
					RobotSlot robotSlot = robotSlots.get(i);
					if (!robotSlot.isRepairing()) {
						map.put(robot.getSlot(), robot);
					}
				} else {
					map.put(robot.getSlot(), robot);
				}
			}
		}
		return map;
	}

	/**
	 * 进化度最高的机器人部件的id
	 */
	public PartMaking readMostEvolutionPart() {
		List<Robot> robots = readRobots(RobotType.BATTLE);
		PartMaking resultPartMaking = null;
		Float maxEvoPercent = 0F;
		for (Robot robot : robots) {
			for (Part part : robot.readParts()) {
				//进化度最高的,进化经验/下次进化经验==进化度
				PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());
				if (partMaking.getPartQualityType() != PartQualityType.TITANIUM) {//TITANIUM的不推荐
					QualityMaking qualityMaking = QualityLoadData.getInstance().getQualityMaking(partMaking.getPartQualityType().asCode());

					Float evoPercent = part.getEvExp() * 100F / qualityMaking.getEvolveExp();
					if (evoPercent > maxEvoPercent) {
						maxEvoPercent = evoPercent;
						resultPartMaking = partMaking;
					}
				}
			}
		}

		return resultPartMaking;
	}

	/**
	 * 随机一个部件
	 */
	public PartMaking readRandomPart(List<PartMaking> excludes) {
		List<Robot> robots = readRobots(RobotType.BATTLE);
		List<PartMaking> makings = new ArrayList<PartMaking>();
		for (Robot robot : robots) {
			for (Part part : robot.readParts()) {
				PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());
				if (excludes != null) {
					if (!makings.contains(partMaking) && !excludes.contains(excludes)) {
						if (partMaking.getPartQualityType() != PartQualityType.TITANIUM) {//TITANIUM的不推荐
							makings.add(partMaking);
						}
					}
				} else {
					if (!makings.contains(partMaking)) {
						if (partMaking.getPartQualityType() != PartQualityType.TITANIUM) {//TITANIUM的不推荐
							makings.add(partMaking);
						}
					}
				}
			}
		}

		return Utils.randomSelectOne(makings);
	}

	/**
	 * suit
	 */
	public List<String> readBattleSuits() {
		List<String> suits = new ArrayList<String>();
		for (Robot robot : readRobots(RobotType.BATTLE)) {
			for (Part part : robot.readParts()) {
				PartMaking making = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());
				if (!suits.contains(making.getSuitName())) {
					suits.add(making.getSuitName());
				}
			}
		}
		return suits;
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(RobotBag.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	public byte[] toByteArrayAsDefender(int atkerLevel) {

		ByteArrayGameOutput bago = new ByteArrayGameOutput();

		synchronized (this) {
			for (Robot robot : battleRobots.values()) {
				if (null != robot) {
					bago.putBytesNoLength(robot.toByteArrayAsDefender());
					//额外的攻防值
					FightProperty addFightProperty = robot.additionFightProperty(atkerLevel, robot.refreshFightProperty());
					bago.putInt(addFightProperty.getAtk());
					bago.putInt(addFightProperty.getDef());
					bago.putInt(addFightProperty.getHp());
					bago.putInt(addFightProperty.getCrit());

				}
			}
		}

		return bago.toByteArray();
	}
}
