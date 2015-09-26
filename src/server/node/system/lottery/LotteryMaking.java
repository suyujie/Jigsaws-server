package server.node.system.lottery;

import java.io.Serializable;

public class LotteryMaking implements Serializable {

	private static final long serialVersionUID = -8955324866929526483L;

	private String type;//类型,可能是 品质,机器人,身体部件
	private Integer weightCash;//cash抽奖的权重
	private Integer weightGold;//gold抽奖的权重

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getWeightCash() {
		return weightCash;
	}

	public void setWeightCash(Integer weightCash) {
		this.weightCash = weightCash;
	}

	public Integer getWeightGold() {
		return weightGold;
	}

	public void setWeightGold(Integer weightGold) {
		this.weightGold = weightGold;
	}

}
