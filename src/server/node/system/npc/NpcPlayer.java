package server.node.system.npc;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;
import gamecore.io.ByteArrayGameOutput;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.robot.FightProperty;

public class NpcPlayer extends AbstractEntity {

	private static final Logger logger = LogManager.getLogger(NpcPlayer.class.getName());

	private static final long serialVersionUID = 4716638653620162336L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "npc_player_";

	private Long id;
	private String name;
	private int level;

	private long cash;

	private String cashs;

	//战斗的
	private List<NpcRobot> attackRobots;
	//租借的
	private NpcRobot rentRobot;

	public NpcPlayer() {
	}

	public NpcPlayer(Long id, String name, int level, long cash, List<NpcRobot> attackRobots, NpcRobot rentRobot) {
		super(generateCacheKey(id));
		this.id = id;
		this.name = name;
		this.level = level;
		this.cash = cash;
		this.attackRobots = attackRobots;
		this.rentRobot = rentRobot;

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getCash() {
		return cash;
	}

	public void setCash(long cash) {
		this.cash = cash;
	}

	public String getCashs() {
		return cashs;
	}

	public void setCashs(String cashs) {
		this.cashs = cashs;
	}

	public List<NpcRobot> getAttackRobots() {
		return attackRobots;
	}

	public void setAttackRobots(List<NpcRobot> attackRobots) {
		this.attackRobots = attackRobots;
	}

	public NpcRobot getRentRobot() {
		return rentRobot;
	}

	public void setRentRobot(NpcRobot rentRobot) {
		this.rentRobot = rentRobot;
	}

	public byte[] npcRobotsToByteArrayAsDefender(int atkerLevel) {

		ByteArrayGameOutput bago = new ByteArrayGameOutput();

		for (NpcRobot robot : attackRobots) {
			if (robot != null) {
				bago.putBytesNoLength(robot.toByteArrayAsDefender());

				//额外的攻防值
				FightProperty addFightProperty = robot.additionFightProperty(atkerLevel, robot.refreshFightProperty());
				bago.putInt(addFightProperty.getAtk());
				bago.putInt(addFightProperty.getDef());
				bago.putInt(addFightProperty.getHp());
				bago.putInt(addFightProperty.getCrit());
			}
		}

		return bago.toByteArray();
	}

	public byte[] npcRobotsToByteArrayAsRent() {

		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		if (rentRobot != null) {
			bago.putBytesNoLength(rentRobot.toByteArrayAsDefender());
		}

		return bago.toByteArray();
	}

	public byte[] npcPlayerToByteArrayAsRent() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			bago.putLong(id);
			bago.putInt(1);
			bago.putInt((int) cash);
			bago.putString(name);
			bago.putInt(level);
			bago.putBytesNoLength(npcRobotsToByteArrayAsRent());

		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(NpcPlayer.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	public void synchronize(int hour) {
		synchronized (this) {
			JedisUtilJson.getInstance().setForHour(super.getCacheKey(), this, hour);
		}
	}

}
