package server.node.system.log;

import server.node.system.gameEvents.treasureIsland.TreasureIsland;
import server.node.system.gameEvents.treasureIsland.TreasureIslandType;

public class TreasureIslandLog extends AbstractLog {

	private TreasureIslandType treasureIslandType;

	private TreasureIsland treasureIsland;
	private Integer cash;
	private Integer expId;
	private Integer expNum;
	private Integer killNum;
	private Integer resetIndex;//今天这个副本的第几次重置

	public TreasureIslandLog(TreasureIsland treasureIsland, TreasureIslandType treasureIslandType, Integer cash, Integer expId, Integer expNum, Integer killNum, Integer resetIndex) {
		super();
		this.treasureIsland = treasureIsland;
		this.treasureIslandType = treasureIslandType;
		this.cash = cash;
		this.expId = expId;
		this.expNum = expNum;
		this.killNum = killNum;
		this.resetIndex = resetIndex;
	}

	public TreasureIsland getTreasureIsland() {
		return treasureIsland;
	}

	public void setTreasureIsland(TreasureIsland treasureIsland) {
		this.treasureIsland = treasureIsland;
	}

	public TreasureIslandType getTreasureIslandType() {
		return treasureIslandType;
	}

	public void setTreasureIslandType(TreasureIslandType treasureIslandType) {
		this.treasureIslandType = treasureIslandType;
	}

	public Integer getCash() {
		return cash;
	}

	public void setCash(Integer cash) {
		this.cash = cash;
	}

	public Integer getExpId() {
		return expId;
	}

	public void setExpId(Integer expId) {
		this.expId = expId;
	}

	public Integer getExpNum() {
		return expNum;
	}

	public void setExpNum(Integer expNum) {
		this.expNum = expNum;
	}

	public Integer getKillNum() {
		return killNum;
	}

	public void setKillNum(Integer killNum) {
		this.killNum = killNum;
	}

	public Integer getResetIndex() {
		return resetIndex;
	}

	public void setResetIndex(Integer resetIndex) {
		this.resetIndex = resetIndex;
	}
}
