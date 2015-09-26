package server.node.system.battle;

import gamecore.entity.AbstractEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Root;
import server.node.system.npc.NpcPlayer;
import server.node.system.player.Player;
import server.node.system.robot.Robot;
import server.node.system.toturial.Toturial;

/**
 * pvp战斗
 */
public final class PvpBattle extends AbstractEntity implements Serializable {

	private static Logger logger = LogManager.getLogger(PvpBattle.class.getName());

	private static final long serialVersionUID = 8381346196692836467L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "pvp_";

	private boolean valid = false;//战场是否有效
	private int type;//0普通战斗记录,1复仇战(被复仇,这个不让再次复仇)
	private Long attackerId;
	private Long defenderId;
	private NpcPlayer pvpNpc;
	private int lootCash;//对手身上可抢的钱
	private int lootGainCash;//关卡里可以抢的钱
	private int[] cashs;
	private Integer lootExpPartId;//对手身上可抢的能量块的id
	private String lootChipName;//可抢的芯片名称
	private Integer lootBergId;//可抢的水晶id
	private int winCup;//胜利增加的cup数
	private int loseCup;//失败减少的cup数
	private long beginTime;
	private Map<Integer, Robot> attackRobots;
	private Map<Integer, Robot> defendRobots;
	private int isPvpWinOne;
	private boolean consumeWear;//是否扣过耐久
	private Map<Integer, Long> defenderMissionLoseSeconds;//防守方每个关卡减少的时间

	public PvpBattle() {
	}

	public PvpBattle(Player player, Player defender, NpcPlayer pvpNpc, int lootCash, int lootGainCash, Map<Integer, Long> defenderMissionLoseSeconds, int[] cashs, int winCup,
			int loseCup, Integer lootExpPartId, String lootChipName, Integer lootBergId, long beginTime) {
		super(PvpBattle.generateCacheKey(player.getId()));
		this.attackerId = player.getId();
		this.defenderId = defender == null ? null : defender.getId();
		this.pvpNpc = pvpNpc;
		this.lootCash = lootCash;
		this.lootGainCash = lootGainCash;
		this.defenderMissionLoseSeconds = defenderMissionLoseSeconds;
		this.cashs = cashs;
		this.winCup = winCup;
		this.loseCup = loseCup;
		this.lootExpPartId = lootExpPartId;
		this.lootChipName = lootChipName;
		this.lootBergId = lootBergId;
		this.beginTime = beginTime;
		this.attackRobots = new HashMap<Integer, Robot>();
		this.defendRobots = new HashMap<Integer, Robot>();

		try {
			Toturial toturial = Root.toturialSystem.getToturial(player);
			this.isPvpWinOne = toturial.getIsPvpWinOne();
		} catch (Exception e) {
			logger.error(e.getMessage());
			this.isPvpWinOne = 0;

		}
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Long getAttackerId() {
		return attackerId;
	}

	public void setAttackerId(Long attackerId) {
		this.attackerId = attackerId;
	}

	public Long getDefenderId() {
		return defenderId;
	}

	public void setDefenderId(Long defenderId) {
		this.defenderId = defenderId;
	}

	public NpcPlayer getPvpNpc() {
		return pvpNpc;
	}

	public void setPvpNpc(NpcPlayer pvpNpc) {
		this.pvpNpc = pvpNpc;
	}

	public int getLootCash() {
		return lootCash;
	}

	public void setLootCash(int lootCash) {
		this.lootCash = lootCash;
	}

	public int getLootGainCash() {
		return lootGainCash;
	}

	public void setLootGainCash(int lootGainCash) {
		this.lootGainCash = lootGainCash;
	}

	public int[] getCashs() {
		return cashs;
	}

	public void setCashs(int[] cashs) {
		this.cashs = cashs;
	}

	public int getWinCup() {
		return winCup;
	}

	public void setWinCup(int winCup) {
		this.winCup = winCup;
	}

	public int getLoseCup() {
		return loseCup;
	}

	public void setLoseCup(int loseCup) {
		this.loseCup = loseCup;
	}

	public Integer getLootExpPartId() {
		return lootExpPartId;
	}

	public void setLootExpPartId(Integer lootExpPartId) {
		this.lootExpPartId = lootExpPartId;
	}

	public String getLootChipName() {
		return lootChipName;
	}

	public void setLootChipName(String lootChipName) {
		this.lootChipName = lootChipName;
	}

	public Integer getLootBergId() {
		return lootBergId;
	}

	public void setLootBergId(Integer lootBergId) {
		this.lootBergId = lootBergId;
	}

	public long getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}

	public Map<Integer, Robot> getAttackRobots() {
		return attackRobots;
	}

	public void setAttackRobots(Map<Integer, Robot> attackRobots) {
		this.attackRobots = attackRobots;
	}

	public Map<Integer, Robot> getDefendRobots() {
		return defendRobots;
	}

	public void setDefendRobots(Map<Integer, Robot> defendRobots) {
		this.defendRobots = defendRobots;
	}

	public int getIsPvpWinOne() {
		return isPvpWinOne;
	}

	public void setIsPvpWinOne(int isPvpWinOne) {
		this.isPvpWinOne = isPvpWinOne;
	}

	public boolean isConsumeWear() {
		return consumeWear;
	}

	public void setConsumeWear(boolean consumeWear) {
		this.consumeWear = consumeWear;
	}

	public Map<Integer, Long> getDefenderMissionLoseSeconds() {
		return defenderMissionLoseSeconds;
	}

	public void setDefenderMissionLoseSeconds(Map<Integer, Long> defenderMissionLoseSeconds) {
		this.defenderMissionLoseSeconds = defenderMissionLoseSeconds;
	}

	public List<Robot> readAttackRobotArray() {
		List<Robot> robots = new ArrayList<Robot>(attackRobots.values());
		return robots;
	}

	/**
	 * 生成存储键。攻击者的id为key
	 */
	public static String generateCacheKey(Long playerId) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(PvpBattle.CKPrefix).append(playerId).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

}
