package server.node.system.battle;

import java.util.Map;

public class PvpBattleResult {

	private PvpBattle pvpBattle;
	private boolean isWin;
	private boolean isNoHurt;
	private int lootCash;//对手身上可抢的钱
	private int lootGainCash;//对手关卡可抢的钱
	private Map<Integer, Long> defenderMissionLoseSeconds;//防守方每个关卡减少的时间
	private Float pvpLootRate;
	private int winCup;//胜利增加的cup数
	private int loseCup;//失败减少的cup数
	private int attackerWinNum;
	private int defenderWinNum;
	private Integer lootExpId;
	private String lootChipName;
	private Integer lootBergId;

	private long beginTime;
	private long endTime;

	public PvpBattleResult(boolean isWin, boolean isNoHurt, int attackerWinNum, int defenderWinNum) {
		super();
		this.isWin = isWin;
		this.isNoHurt = isNoHurt;
		this.attackerWinNum = attackerWinNum;
		this.defenderWinNum = defenderWinNum;
	}

	//攻击者抢到的钱
	public int attackerWinLootCash() {
		return (int) ((lootCash + lootGainCash) * pvpLootRate);
	}

	//防守者失去的钱
	public int defenderLoseCash() {
		return (int) (lootCash * pvpLootRate);
	}

	public PvpBattle getPvpBattle() {
		return pvpBattle;
	}

	public void setPvpBattle(PvpBattle pvpBattle) {
		this.pvpBattle = pvpBattle;
	}

	public Integer getLootExpId() {
		return lootExpId;
	}

	public void setLootExpId(Integer lootExpId) {
		this.lootExpId = lootExpId;
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

	public Map<Integer, Long> getDefenderMissionLoseSeconds() {
		return defenderMissionLoseSeconds;
	}

	public void setDefenderMissionLoseSeconds(Map<Integer, Long> defenderMissionLoseSeconds) {
		this.defenderMissionLoseSeconds = defenderMissionLoseSeconds;
	}

	public Float getPvpLootRate() {
		return pvpLootRate;
	}

	public void setPvpLootRate(Float pvpLootRate) {
		this.pvpLootRate = pvpLootRate;
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

	public boolean isWin() {
		return isWin;
	}

	public boolean isNoHurt() {
		return isNoHurt;
	}

	public void setWin(boolean isWin) {
		this.isWin = isWin;
	}

	public void setNoHurt(boolean isNoHurt) {
		this.isNoHurt = isNoHurt;
	}

	public int getAttackerWinNum() {
		return attackerWinNum;
	}

	public void setAttackerWinNum(int attackerWinNum) {
		this.attackerWinNum = attackerWinNum;
	}

	public int getDefenderWinNum() {
		return defenderWinNum;
	}

	public void setDefenderWinNum(int defenderWinNum) {
		this.defenderWinNum = defenderWinNum;
	}

	public long getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

}
