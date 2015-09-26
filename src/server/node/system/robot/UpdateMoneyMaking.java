package server.node.system.robot;

import gamecore.util.DataUtils;

import java.io.Serializable;

import javolution.util.FastTable;

//升级需要的钱
public class UpdateMoneyMaking implements Serializable {

	private static final long serialVersionUID = -7144549058222192128L;

	private String moneystr;
	private String updateRmb;
	private FastTable<Integer> cashTable;
	private FastTable<Integer> goldTable;

	public UpdateMoneyMaking(String moneystr) {
		super();
		this.moneystr = moneystr;
	}

	public String getMoneystr() {
		return moneystr;
	}

	public void setMoneystr(String moneystr) {
		this.moneystr = moneystr;
	}

	public FastTable<Integer> getCashTable() {
		return cashTable;
	}

	public void setCashTable(FastTable<Integer> cashTable) {
		this.cashTable = cashTable;
	}

	public String getUpdateRmb() {
		return updateRmb;
	}

	public void setUpdateRmb(String updateRmb) {
		this.updateRmb = updateRmb;
	}

	public FastTable<Integer> getGoldTable() {
		return goldTable;
	}

	public void setGoldTable(FastTable<Integer> goldTable) {
		this.goldTable = goldTable;
	}

	public void setTables() {
		setCashTable(DataUtils.string2FastTable(getMoneystr()));
		setGoldTable(DataUtils.string2FastTable(getUpdateRmb()));
	}

	public int getUpdateCash(int level) {
		if (cashTable == null) {
			return 0;
		} else {
			return cashTable.get(level - 1);
		}
	}

	public int getUpdateGold(int level) {
		if (goldTable == null) {
			return 0;
		} else {
			return goldTable.get(level - 1);
		}
	}

}