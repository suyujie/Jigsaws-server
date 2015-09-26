package gamecore.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 基本权重池
 */
public final class BaseWeightPool {

	private int weightSum;//分母

	private List<BaseWeight> baseWeights;

	public BaseWeightPool() {
		this.weightSum = 0;
		this.baseWeights = new ArrayList<BaseWeight>();
	}

	public int getWeightSum() {
		return weightSum;
	}

	public void setWeightSum(int weightSum) {
		this.weightSum = weightSum;
	}

	public List<BaseWeight> getBaseWeights() {
		return baseWeights;
	}

	public void setBaseWeights(List<BaseWeight> baseWeights) {
		this.baseWeights = baseWeights;
	}

	public void addWeight(int num, Object value) {
		baseWeights.add(new BaseWeight(weightSum, weightSum + num, value));
		this.weightSum += num;
	}

	public Object getValue() {
		int base = Utils.randomInt(0, weightSum - 1);
		for (BaseWeight bw : baseWeights) {
			if (base >= bw.getBegin() && base < bw.getEnd()) {//判断区间
				return bw.getValue();
			}
		}
		return null;
	}

	public List<Object> getValueNumNoRepeat(int num) {

		List<Object> result = new ArrayList<Object>();

		if (num >= baseWeights.size()) {
			for (BaseWeight bw : baseWeights) {
				result.add(bw.getValue());
			}
			return result;
		} else {
			//先填充一个队列,然后随机

			List<Object> allItem = new ArrayList<Object>();

			for (BaseWeight bw : baseWeights) {
				for (int i = bw.getBegin(); i < bw.getEnd(); i++) {
					allItem.add(bw.getValue());
				}
			}

			while (result.size() < num) {

				int index = Utils.randomInt(0, allItem.size() - 1);

				Object value = allItem.get(index);

				if (!result.contains(value)) {
					result.add(value);
				}

				allItem.remove(value);

			}

		}

		return result;
	}

}
