package server.node.system.gameEvents.bergWheel;

import server.node.system.berg.BergLoadData;
import server.node.system.berg.BergMaking;
import server.node.system.berg.BergType;
import gamecore.util.DataUtils;
import javolution.util.FastTable;

public class BergWheelMaking {

	private String atk;
	private String def;
	private String hp;
	private String score;
	private String num;
	private String level;

	private FastTable<Integer> atkTable;
	private FastTable<Integer> defTable;
	private FastTable<Integer> hpTable;
	private FastTable<Integer> scoreTable;
	private FastTable<Integer> bergNumTable;
	private FastTable<Integer> bergLevelTable;

	public String getAtk() {
		return atk;
	}

	public void setAtk(String atk) {
		this.atk = atk;
	}

	public String getDef() {
		return def;
	}

	public void setDef(String def) {
		this.def = def;
	}

	public String getHp() {
		return hp;
	}

	public void setHp(String hp) {
		this.hp = hp;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public FastTable<Integer> getScoreTable() {
		return scoreTable;
	}

	public void setScoreTable(FastTable<Integer> scoreTable) {
		this.scoreTable = scoreTable;
	}

	public FastTable<Integer> getAtkTable() {
		return atkTable;
	}

	public void setAtkTable(FastTable<Integer> atkTable) {
		this.atkTable = atkTable;
	}

	public FastTable<Integer> getDefTable() {
		return defTable;
	}

	public void setDefTable(FastTable<Integer> defTable) {
		this.defTable = defTable;
	}

	public FastTable<Integer> getHpTable() {
		return hpTable;
	}

	public void setHpTable(FastTable<Integer> hpTable) {
		this.hpTable = hpTable;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public FastTable<Integer> getBergNumTable() {
		return bergNumTable;
	}

	public void setBergNumTable(FastTable<Integer> bergNumTable) {
		this.bergNumTable = bergNumTable;
	}

	public FastTable<Integer> getBergLevelTable() {
		return bergLevelTable;
	}

	public void setBergLevelTable(FastTable<Integer> bergLevelTable) {
		this.bergLevelTable = bergLevelTable;
	}

	public void setTable() {
		atkTable = DataUtils.string2FastTable(atk);
		defTable = DataUtils.string2FastTable(def);
		hpTable = DataUtils.string2FastTable(hp);
		scoreTable = DataUtils.string2FastTable(score);
		bergNumTable = DataUtils.string2FastTable(num);
		bergLevelTable = DataUtils.string2FastTable(level);
	}

	public int getAtk(int level) {
		int maxLevel = atkTable.size() - 1;
		if (level <= 0) {
			return atkTable.get(0);
		} else if (level > maxLevel) {
			return atkTable.get(maxLevel) * (100 + (level - maxLevel) * 10) / 100;
		} else {
			return atkTable.get(level);
		}
	}

	public int getDef(int level) {
		int maxLevel = defTable.size() - 1;
		if (level <= 0) {
			return defTable.get(0);
		} else if (level > maxLevel) {
			return defTable.get(maxLevel) * (100 + (level - maxLevel) * 10) / 100;
		} else {
			return defTable.get(level);
		}
	}

	public int getHp(int level) {
		int maxLevel = hpTable.size() - 1;
		if (level <= 0) {
			return hpTable.get(0);
		} else if (level > maxLevel) {
			return hpTable.get(maxLevel) * (100 + (level - maxLevel) * 10) / 100;
		} else {
			return hpTable.get(level);
		}
	}

	public int getScore(int level) {
		int maxLevel = scoreTable.size() - 1;
		if (level <= 0) {
			return scoreTable.get(0);
		} else if (level > maxLevel) {
			return scoreTable.get(maxLevel) * (100 + (level - maxLevel) * 10) / 100;
		} else {
			return scoreTable.get(level);
		}
	}

	public int getBergLevel(int level) {
		int maxLevel = bergLevelTable.size() - 1;
		if (level <= 0) {
			return bergLevelTable.get(0);
		} else if (level > maxLevel) {
			return bergLevelTable.get(maxLevel) * (100 + (level - maxLevel) * 10) / 100;
		} else {
			return bergLevelTable.get(level);
		}
	}

	public int getBergNum(int level) {
		int maxLevel = bergNumTable.size() - 1;
		if (level <= 0) {
			return bergNumTable.get(0);
		} else if (level > maxLevel) {
			return bergNumTable.get(maxLevel) * (100 + (level - maxLevel) * 10) / 100;
		} else {
			return bergNumTable.get(level);
		}
	}

	public Integer getBergId(int level) {//随机一个类型的
		BergMaking making = BergLoadData.getInstance().getBergMaking(BergType.randCode(), level);
		return making.getId();
	}
}
