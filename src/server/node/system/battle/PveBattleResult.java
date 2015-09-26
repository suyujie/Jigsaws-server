package server.node.system.battle;

import java.util.List;

import server.node.system.egg.EggPartBag;
import server.node.system.robotPart.Part;

public class PveBattleResult {

	private boolean isWin;
	private boolean isNoHurt;
	private int eggNum;
	private boolean firstPass;
	private int gold;
	//获得的钱
	private int cash;
	//得到的经验
	private int exp;
	//小关卡的星级
	private int pointStar;
	private List<Part> parts;
	private List<Integer> expParts;
	private List<Integer> colors;
	private long enterTime;
	private long exitTime;
	private PveBattle pveBattle;
	private EggPartBag eggPartBag;

	public PveBattleResult(boolean isWin, boolean isNoHurt, int eggNum) {
		super();
		this.isWin = isWin;
		this.isNoHurt = isNoHurt;
		this.eggNum = eggNum;
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

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getCash() {
		return cash;
	}

	public void setCash(int cash) {
		this.cash = cash;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getPointStar() {
		return pointStar;
	}

	public void setPointStar(int pointStar) {
		this.pointStar = pointStar;
	}

	public int getEggNum() {
		return eggNum;
	}

	public void setEggNum(int eggNum) {
		this.eggNum = eggNum;
	}

	public List<Part> getParts() {
		return parts;
	}

	public void setParts(List<Part> parts) {
		this.parts = parts;
	}

	public List<Integer> getExpParts() {
		return expParts;
	}

	public void setExpParts(List<Integer> expParts) {
		this.expParts = expParts;
	}

	public List<Integer> getColors() {
		return colors;
	}

	public void setColors(List<Integer> colors) {
		this.colors = colors;
	}

	public long getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(long enterTime) {
		this.enterTime = enterTime;
	}

	public long getExitTime() {
		return exitTime;
	}

	public void setExitTime(long exitTime) {
		this.exitTime = exitTime;
	}

	public boolean isFirstPass() {
		return firstPass;
	}

	public void setFirstPass(boolean firstPass) {
		this.firstPass = firstPass;
	}

	public PveBattle getPveBattle() {
		return pveBattle;
	}

	public void setPveBattle(PveBattle pveBattle) {
		this.pveBattle = pveBattle;
	}

	public void setNoHurt(boolean isNoHurt) {
		this.isNoHurt = isNoHurt;
	}

	public EggPartBag getEggPartBag() {
		return eggPartBag;
	}

	public void setEggPartBag(EggPartBag eggPartBag) {
		this.eggPartBag = eggPartBag;
	}

}
