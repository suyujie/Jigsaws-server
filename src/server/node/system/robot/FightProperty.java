package server.node.system.robot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import server.node.system.buff.BuffAndValue;
import server.node.system.robotPart.PartQualityType;

public class FightProperty implements Serializable {

	public static final long serialVersionUID = 3702324363466423837L;
	public int score;//分数
	public int atk;//攻击
	public int def;//防御
	public int hp;//血量
	public int crit;//暴击

	private boolean sameQualityType = true;
	private boolean sameSuitType = true;
	private PartQualityType qualityType = null;
	private String suitMakingName = null;

	private List<BuffAndValue> buffAndValues = null;

	public FightProperty(int atk, int def, int hp, int crit) {
		super();
		this.atk = atk;
		this.def = def;
		this.hp = hp;
		this.crit = crit;
	}

	public FightProperty(int score, int atk, int def, int hp, int crit, boolean sameQualityType, boolean sameSuitType, PartQualityType qualityType, String suitMakingName) {
		super();
		this.score = score;
		this.atk = atk;
		this.def = def;
		this.hp = hp;
		this.crit = crit;
		this.sameQualityType = sameQualityType;
		this.sameSuitType = sameSuitType;
		this.qualityType = qualityType;
		this.suitMakingName = suitMakingName;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getAtk() {
		return atk;
	}

	public void setAtk(int atk) {
		this.atk = atk;
	}

	public int getDef() {
		return def;
	}

	public void setDef(int def) {
		this.def = def;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getCrit() {
		return crit;
	}

	public void setCrit(int crit) {
		this.crit = crit;
	}

	public boolean isSameQualityType() {
		return sameQualityType;
	}

	public void setSameQualityType(boolean sameQualityType) {
		this.sameQualityType = sameQualityType;
	}

	public boolean isSameSuitType() {
		return sameSuitType;
	}

	public void setSameSuitType(boolean sameSuitType) {
		this.sameSuitType = sameSuitType;
	}

	public PartQualityType getQualityType() {
		return qualityType;
	}

	public void setQualityType(PartQualityType qualityType) {
		this.qualityType = qualityType;
	}

	public String getSuitMakingName() {
		return suitMakingName;
	}

	public void setSuitMakingName(String suitMakingName) {
		this.suitMakingName = suitMakingName;
	}

	public List<BuffAndValue> getBuffAndValues() {
		return buffAndValues;
	}

	public void setBuffAndValues(List<BuffAndValue> buffAndValues) {
		this.buffAndValues = buffAndValues;
	}

	public void addBuffAndValue(BuffAndValue buffAndValue) {
		if (buffAndValues == null) {
			buffAndValues = new ArrayList<BuffAndValue>();
		}
		buffAndValues.add(buffAndValue);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("score:" + getScore()).append("  ");
		sb.append("atk:" + getAtk()).append("  ");
		sb.append("def:" + getDef()).append("  ");
		sb.append("hp:" + getHp()).append("  ");
		sb.append("crit:" + getCrit());
		return sb.toString();
	}

}