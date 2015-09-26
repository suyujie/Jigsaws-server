package server.node.system.gameEvents.treasureIsland;

import gamecore.util.DataUtils;
import javolution.util.FastTable;

public class TreasureIslandBossMaking {

	private Integer id;
	private String suitName;
	private String score;
	private String def;
	private String hp;

	private String ai;
	private String action;
	private String bg;
	private Float animSpeed;
	private Float moveSpeed;
	private Integer roundTime;
	private Integer scale;

	private FastTable<Integer> scoreTable;
	private FastTable<Integer> defTable;
	private FastTable<Integer> hpTable;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public String getAi() {
		return ai;
	}

	public void setAi(String ai) {
		this.ai = ai;
	}

	public String getAction() {
		return action;
	}

	public String getBg() {
		return bg;
	}

	public Float getAnimSpeed() {
		return animSpeed;
	}

	public void setAnimSpeed(Float animSpeed) {
		this.animSpeed = animSpeed;
	}

	public Float getMoveSpeed() {
		return moveSpeed;
	}

	public void setMoveSpeed(Float moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	public Integer getRoundTime() {
		return roundTime;
	}

	public void setRoundTime(Integer roundTime) {
		this.roundTime = roundTime;
	}

	public void setBg(String bg) {
		this.bg = bg;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public FastTable<Integer> getScoreTable() {
		return scoreTable;
	}

	public void setScoreTable(FastTable<Integer> scoreTable) {
		this.scoreTable = scoreTable;
	}

	public String getSuitName() {
		return suitName;
	}

	public void setSuitName(String suitName) {
		this.suitName = suitName;
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
		scoreTable = DataUtils.string2FastTable(score);
		defTable = DataUtils.string2FastTable(def);
		hpTable = DataUtils.string2FastTable(hp);
	}

	public Integer getScoreByLevel(Integer level) {
		return scoreTable.get(level - 1);
	}

	public Integer getDefByLevel(Integer level) {
		return defTable.get(level - 1);
	}

	public Integer getHpByLevel(Integer level) {
		return hpTable.get(level - 1);
	}

}
