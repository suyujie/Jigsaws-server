package server.node.system.dailyJob;

import server.node.system.Root;
import common.language.LangType;
import gamecore.io.ByteArrayGameOutput;

public class JobCurrentStatus {

	private int id;
	private int state;//任务状态0：未完成，1：完成未领钻，2：已领钻
	private int rewardRmb;
	private int value; //当前数值
	private int needValue; //完成条件数值
	private int weapon = -1;//武器类型,有两个任务有这个属性,干脆全都有,默认-1
	private String[] needStr;//条件文字,比如 武器类型

	public JobCurrentStatus(int id, int state, int rewardRmb, int value, int needValue, int weapon) {
		super();
		this.id = id;
		this.state = state;
		this.rewardRmb = rewardRmb;
		this.value = value;
		this.needValue = needValue;
		this.weapon = weapon;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getRewardRmb() {
		return rewardRmb;
	}

	public void setRewardRmb(int rewardRmb) {
		this.rewardRmb = rewardRmb;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getNeedValue() {
		return needValue;
	}

	public void setNeedValue(int needValue) {
		this.needValue = needValue;
	}

	public int getWeapon() {
		return weapon;
	}

	public void setWeapon(int weapon) {
		this.weapon = weapon;
	}

	public String[] getNeedStr() {
		return needStr;
	}

	public void setNeedStr(String... needStr) {
		this.needStr = needStr;
	}

	public byte[] toByteArray(LangType langCode) {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		bago.putInt(id);
		bago.putInt(state);
		bago.putInt(rewardRmb);
		bago.putInt(value);
		bago.putInt(needValue);
		bago.putInt(weapon);

		bago.putString(Root.langSystem.getMessage(langCode, "daily_job_desc_" + id, needStr));

		return bago.toByteArray();
	}

}
