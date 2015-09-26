package server.node.system.mission;

import java.io.Serializable;
import java.util.List;

import javolution.util.FastTable;
import server.node.system.mission.pointAward.DropGood;
import server.node.system.mission.pointAward.PointAwardData;

public class PointMaking implements Serializable {

	private static final long serialVersionUID = 842507115919839528L;

	private Integer id;
	private Integer mission;
	private String moneyStr;
	private String diamondStr;
	private String expStr;
	private String tishiStr;
	private String passMoneyStr;
	private String awardStar1;
	private String awardStar2;
	private String awardStar3;
	private String awardFlush;
	private String awardNum;
	private String bossIdStr;
	private String wearStr;
	private String bjname;
	private Integer isBoss;
	private String flushBossShow;
	private Integer numTotalDay;
	private String eggCost;

	private FastTable<Integer> moneyTable;
	private FastTable<Integer> goldTable;
	private FastTable<Integer> expTable;
	private FastTable<Integer> passMoneyTable;
	private FastTable<Integer> wearTable;
	private FastTable<Integer> eggCostTable;

	private PointAwardData pointAwardData;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMission() {
		return mission;
	}

	public void setMission(Integer mission) {
		this.mission = mission;
	}

	public String getMoneyStr() {
		return moneyStr;
	}

	public void setMoneyStr(String moneyStr) {
		this.moneyStr = moneyStr;
	}

	public String getExpStr() {
		return expStr;
	}

	public void setExpStr(String expStr) {
		this.expStr = expStr;
	}

	public String getTishiStr() {
		return tishiStr;
	}

	public void setTishiStr(String tishiStr) {
		this.tishiStr = tishiStr;
	}

	public String getPassMoneyStr() {
		return passMoneyStr;
	}

	public void setPassMoneyStr(String passMoneyStr) {
		this.passMoneyStr = passMoneyStr;
	}

	public String getBossIdStr() {
		return bossIdStr;
	}

	public void setBossIdStr(String bossIdStr) {
		this.bossIdStr = bossIdStr;
	}

	public String getAwardStar1() {
		return awardStar1;
	}

	public String getAwardStar2() {
		return awardStar2;
	}

	public String getAwardStar3() {
		return awardStar3;
	}

	public String getAwardFlush() {
		return awardFlush;
	}

	public String getAwardNum() {
		return awardNum;
	}

	public String getBjname() {
		return bjname;
	}

	public Integer getIsBoss() {
		return isBoss;
	}

	public String getEggCost() {
		return eggCost;
	}

	public void setEggCost(String eggCost) {
		this.eggCost = eggCost;
	}

	public FastTable<Integer> getMoneyTable() {
		return moneyTable;
	}

	public void setMoneyTable(FastTable<Integer> moneyTable) {
		this.moneyTable = moneyTable;
	}

	public FastTable<Integer> getExpTable() {
		return expTable;
	}

	public void setExpTable(FastTable<Integer> expTable) {
		this.expTable = expTable;
	}

	public FastTable<Integer> getPassMoneyTable() {
		return passMoneyTable;
	}

	public void setPassMoneyTable(FastTable<Integer> passMoneyTable) {
		this.passMoneyTable = passMoneyTable;
	}

	public void setWearTable(FastTable<Integer> wearTable) {
		this.wearTable = wearTable;
	}

	public void setGoldTable(FastTable<Integer> goldTable) {
		this.goldTable = goldTable;
	}

	public void setEggCostTable(FastTable<Integer> eggCostTable) {
		this.eggCostTable = eggCostTable;
	}

	public PointAwardData getPointAwardData() {
		return pointAwardData;
	}

	public void setPointAwardData(PointAwardData pointAwardData) {
		this.pointAwardData = pointAwardData;
	}

	public List<DropGood> getDropGoodStars(int star) {
		return this.pointAwardData.getDropGoodStar(star);
	}

	public List<DropGood> getDropGoodFlushs(Integer num) {
		return this.pointAwardData.getDropGoodFlush(num);
	}

	public String getDiamondStr() {
		return diamondStr;
	}

	public void setDiamondStr(String diamondStr) {
		this.diamondStr = diamondStr;
	}

	public String getWearStr() {
		return wearStr;
	}

	public void setWearStr(String wearStr) {
		this.wearStr = wearStr;
	}

	public String getFlushBossShow() {
		return flushBossShow;
	}

	public void setFlushBossShow(String flushBossShow) {
		this.flushBossShow = flushBossShow;
	}

	public Integer getNumTotalDay() {
		return numTotalDay;
	}

	public void setNumTotalDay(Integer numTotalDay) {
		this.numTotalDay = numTotalDay;
	}

	public void setBjname(String bjname) {
		this.bjname = bjname;
	}

	public void setIsBoss(Integer isBoss) {
		this.isBoss = isBoss;
	}

	public FastTable<Integer> getWearTable() {
		return wearTable;
	}

	public FastTable<Integer> getGoldTable() {
		return goldTable;
	}

	public FastTable<Integer> getEggCostTable() {
		return eggCostTable;
	}

	public int getCash(int star) {
		return getMoneyTable().get(star - 1);
	}

	public int getExp(int star) {
		return getExpTable().get(star - 1);
	}

	public int getWear(int star) {
		return getWearTable().get(star - 1);
	}

	public int getGold(int star) {
		if (getGoldTable() == null) {
			return 0;
		}
		return getGoldTable().get(star - 1);
	}

}
