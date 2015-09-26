package server.node.system.gameEvents.chipDeathWheel.makingData;

import gamecore.util.DataUtils;

import java.io.Serializable;

import javolution.util.FastTable;

public class DeathWheelBossMaking implements Serializable {

	private static final long serialVersionUID = -1971251805062586805L;

	private Integer id;//难度

	private String atk;
	private String def;
	private String hp;
	private String crt;
	private String score;
	private int scale;
	private int chipnum;

	private FastTable<Integer> atkTable;
	private FastTable<Integer> defTable;
	private FastTable<Integer> hpTable;
	private FastTable<Integer> crtTable;
	private FastTable<Integer> scoreTable;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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

	public String getCrt() {
		return crt;
	}

	public void setCrt(String crt) {
		this.crt = crt;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public int getChipnum() {
		return chipnum;
	}

	public void setChipnum(int chipnum) {
		this.chipnum = chipnum;
	}

	public FastTable<Integer> getScoreTable() {
		return scoreTable;
	}

	public void setScoreTable(FastTable<Integer> scoreTable) {
		this.scoreTable = scoreTable;
	}

	public FastTable<Integer> getCrtTable() {
		return crtTable;
	}

	public void setCrtTable(FastTable<Integer> crtTable) {
		this.crtTable = crtTable;
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

	public void setTable() {
		atkTable = DataUtils.string2FastTable(atk);
		defTable = DataUtils.string2FastTable(def);
		hpTable = DataUtils.string2FastTable(hp);
		crtTable = DataUtils.string2FastTable(crt);
		scoreTable = DataUtils.string2FastTable(score);
	}

}
