package server.node.system.lottery;

import java.util.ArrayList;

public class XmlLotteryData {

	private ArrayList<LotteryMaking> lotteryMakingArray = new ArrayList<LotteryMaking>();

	public ArrayList<LotteryMaking> getLotteryMakingArray() {
		return lotteryMakingArray;
	}

	public void setLotteryMakingArray(ArrayList<LotteryMaking> lotteryMakingArray) {
		this.lotteryMakingArray = lotteryMakingArray;
	}

}
