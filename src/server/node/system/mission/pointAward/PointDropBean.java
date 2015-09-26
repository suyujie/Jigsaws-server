package server.node.system.mission.pointAward;

import gamecore.util.BaseWeight;
import gamecore.util.BaseWeightPool;

import java.util.ArrayList;
import java.util.List;

/**
 * 某个星级下的掉落情况 
 */
public class PointDropBean {
	// 掉落的物品的数量
	private BaseWeightPool dropNumPool = new BaseWeightPool();

	// 掉落的物品,以及百分比
	private BaseWeightPool dropRatePool = new BaseWeightPool();//某个物品掉落的百分比

	public void addNumWeight(int num, int value) {
		dropNumPool.addWeight(num, value);
	}

	public void addGoodWeight(int num, DropGood value) {
		dropRatePool.addWeight(num, value);
	}

	public List<DropGood> getDropGoods(boolean allDrop, boolean repeat, Integer num) {

		List<DropGood> list = new ArrayList<DropGood>();

		if (allDrop) {//特殊情况
			for (BaseWeight bw : dropRatePool.getBaseWeights()) {
				list.add((DropGood) bw.getValue());
			}
		} else {

			if (num == null) {
				num = (int) dropNumPool.getValue();
			}
			if (repeat) {//可重复
				//获得数量
				while (list.size() < num) {
					DropGood dropGood = (DropGood) dropRatePool.getValue();
					list.add(dropGood);
				}
			} else {//不可重复
				for (Object o : dropRatePool.getValueNumNoRepeat(num)) {
					list.add((DropGood) o);
				}
			}
		}

		return list;

	}
}
