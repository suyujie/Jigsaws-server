package server.node.system.boss;

import gamecore.util.DataUtils;

import java.io.Serializable;

import javolution.util.FastTable;

public class BossMaking implements Serializable {
	private static final long serialVersionUID = 5025485282917904893L;
	private Integer id;
	private String atk;
	private String def;
	private String hp;
	private String crit;

	@SuppressWarnings("unused")
	private String partsIdStr;
	@SuppressWarnings("unused")
	private String partsColorStr;
	@SuppressWarnings("unused")
	private String scale;
	@SuppressWarnings("unused")
	private String aiStr;
	@SuppressWarnings("unused")
	private String actionStr;
	@SuppressWarnings("unused")
	private String levelX;
	@SuppressWarnings("unused")
	private String timeCrazyTime;
	@SuppressWarnings("unused")
	private String timeCrazyScale;
	@SuppressWarnings("unused")
	private String hpCrazyHp;
	@SuppressWarnings("unused")
	private String hpCrazyTime;
	@SuppressWarnings("unused")
	private String hpCrazyScale;
	@SuppressWarnings("unused")
	private String hpCrazySpeed;
	@SuppressWarnings("unused")
	private String hpCrazyActionIndex;
	@SuppressWarnings("unused")
	private String hpCrazyActionAddRate;
	@SuppressWarnings("unused")
	private String isEndure;
	@SuppressWarnings("unused")
	private String isStartCrazy;
	@SuppressWarnings("unused")
	private String animSpeed;

	private FastTable<Integer> atkTable;
	private FastTable<Integer> defTable;
	private FastTable<Integer> hpTable;
	private FastTable<Integer> critTable;

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

	public String getCrit() {
		return crit;
	}

	public void setCrit(String crit) {
		this.crit = crit;
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

	public FastTable<Integer> getCritTable() {
		return critTable;
	}

	public void setCritTable(FastTable<Integer> critTable) {
		this.critTable = critTable;
	}

	public void setTables() {
		setAtkTable(DataUtils.string2FastTable(getAtk()));
		setDefTable(DataUtils.string2FastTable(getDef()));
		setCritTable(DataUtils.string2FastTable(getCrit()));
		setHpTable(DataUtils.string2FastTable(getHp()));
	}

}