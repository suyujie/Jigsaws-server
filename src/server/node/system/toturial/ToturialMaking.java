package server.node.system.toturial;

import java.io.Serializable;

import server.node.system.player.MoneyType;

public class ToturialMaking implements Serializable {

	private static final long serialVersionUID = -4764662260702856123L;
	private Integer id;
	private Integer index;
	private Integer type;
	private Integer endIfType;
	private String ifStr;
	private String eventContStr;
	private String isSave;
	private String rewardStr;
	private MoneyType moneyType;
	private int rewardNum;
	private int isStartCheck;

	public ToturialMaking(Integer id, Integer index, Integer type, Integer endIfType, String ifStr, String eventContStr, String isSave, String rewardStr, int isStartCheck) {
		super();
		this.id = id;
		this.index = index;
		this.type = type;
		this.endIfType = endIfType;
		this.ifStr = ifStr;
		this.eventContStr = eventContStr;
		this.isSave = isSave;
		this.rewardStr = rewardStr;
		this.isStartCheck = isStartCheck;
	}

	public void initReward() {
		if (rewardStr != null && rewardStr.contains("|")) {
			String[] rewards = rewardStr.split("\\|");
			//0是无奖励，1是金币，2是钻石
			switch (Integer.parseInt(rewards[0])) {
			case 0:
				this.moneyType = null;
				break;
			case 1:
				this.moneyType = MoneyType.CASH;
				break;
			case 2:
				this.moneyType = MoneyType.GOLD;
				break;
			default:
				break;
			}

			this.rewardNum = Integer.parseInt(rewards[1]);
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getEndIfType() {
		return endIfType;
	}

	public void setEndIfType(Integer endIfType) {
		this.endIfType = endIfType;
	}

	public String getIfStr() {
		return ifStr;
	}

	public void setIfStr(String ifStr) {
		this.ifStr = ifStr;
	}

	public String getEventContStr() {
		return eventContStr;
	}

	public void setEventContStr(String eventContStr) {
		this.eventContStr = eventContStr;
	}

	public String getIsSave() {
		return isSave;
	}

	public void setIsSave(String isSave) {
		this.isSave = isSave;
	}

	public String getRewardStr() {
		return rewardStr;
	}

	public void setRewardStr(String rewardStr) {
		this.rewardStr = rewardStr;
	}

	public MoneyType getMoneyType() {
		return moneyType;
	}

	public void setMoneyType(MoneyType moneyType) {
		this.moneyType = moneyType;
	}

	public int getRewardNum() {
		return rewardNum;
	}

	public void setRewardNum(int rewardNum) {
		this.rewardNum = rewardNum;
	}

	public int getIsStartCheck() {
		return isStartCheck;
	}

	public void setIsStartCheck(int isStartCheck) {
		this.isStartCheck = isStartCheck;
	}

}
