package server.node.system.gameEvents.chipDeathWheel.makingData;

import gamecore.io.ByteArrayGameOutput;

import java.io.Serializable;

public class DeathWheelExtraBossMaking implements Serializable {

	private static final long serialVersionUID = 5977493803996216201L;

	private Integer id;//武器类型

	private String aiStr;
	private String actionStr;
	private Float hpScale;
	private int timeCrazyTime;
	private Float timeCrazyScale;
	private Float hpCrazyHp;
	private int hpCrazyTime;
	private Float hpCrazyScale;
	private Float hpCrazySpeed;
	private Float animSpeed;
	private boolean isEndure;
	private boolean isStartCrazy;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAiStr() {
		return aiStr;
	}

	public void setAiStr(String aiStr) {
		this.aiStr = aiStr;
	}

	public String getActionStr() {
		return actionStr;
	}

	public void setActionStr(String actionStr) {
		this.actionStr = actionStr;
	}

	public Float getHpScale() {
		return hpScale;
	}

	public void setHpScale(Float hpScale) {
		this.hpScale = hpScale;
	}

	public int getTimeCrazyTime() {
		return timeCrazyTime;
	}

	public void setTimeCrazyTime(int timeCrazyTime) {
		this.timeCrazyTime = timeCrazyTime;
	}

	public Float getHpCrazyHp() {
		return hpCrazyHp;
	}

	public void setHpCrazyHp(Float hpCrazyHp) {
		this.hpCrazyHp = hpCrazyHp;
	}

	public int getHpCrazyTime() {
		return hpCrazyTime;
	}

	public void setHpCrazyTime(int hpCrazyTime) {
		this.hpCrazyTime = hpCrazyTime;
	}

	public Float getHpCrazySpeed() {
		return hpCrazySpeed;
	}

	public void setHpCrazySpeed(Float hpCrazySpeed) {
		this.hpCrazySpeed = hpCrazySpeed;
	}

	public Float getAnimSpeed() {
		return animSpeed;
	}

	public void setAnimSpeed(Float animSpeed) {
		this.animSpeed = animSpeed;
	}

	public boolean isEndure() {
		return isEndure;
	}

	public void setEndure(boolean isEndure) {
		this.isEndure = isEndure;
	}

	public boolean isStartCrazy() {
		return isStartCrazy;
	}

	public void setStartCrazy(boolean isStartCrazy) {
		this.isStartCrazy = isStartCrazy;
	}

	public Float getTimeCrazyScale() {
		return timeCrazyScale;
	}

	public void setTimeCrazyScale(Float timeCrazyScale) {
		this.timeCrazyScale = timeCrazyScale;
	}

	public Float getHpCrazyScale() {
		return hpCrazyScale;
	}

	public void setHpCrazyScale(Float hpCrazyScale) {
		this.hpCrazyScale = hpCrazyScale;
	}

	public byte[] toByteArray() {
		ByteArrayGameOutput go = new ByteArrayGameOutput();
		try {
			go.putString(aiStr);
			go.putString(actionStr);
			go.putInt(timeCrazyTime);
			go.putInt(((Float) (timeCrazyScale * 100)).intValue());
			go.putInt(((Float) (hpCrazyHp * 100)).intValue());
			go.putInt(hpCrazyTime);
			go.putInt(((Float) (hpCrazyScale * 100)).intValue());
			go.putInt(((Float) (hpCrazySpeed * 100)).intValue());
			go.putInt(((Float) (animSpeed * 100)).intValue());
			go.putBoolean(isEndure);
			go.putBoolean(isStartCrazy);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return go.toByteArray();
	}
}
