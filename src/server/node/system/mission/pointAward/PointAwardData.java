package server.node.system.mission.pointAward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import server.node.system.mission.PointMaking;

public class PointAwardData {

	// key:star 星级
	private HashMap<Integer, List<DropGood>> pointDropGoods = new HashMap<Integer, List<DropGood>>();
	//刷新关卡的时候掉落
	private PointDropBean pointDropBeanF = new PointDropBean();

	//构造掉落物品
	private DropGood createDropGood(String type_id_level_rate) {

		String[] t_i_l_r = type_id_level_rate.split("-");

		int type = Integer.parseInt(t_i_l_r[0].trim());
		if (t_i_l_r.length == 4) {//type_id_level_rate
			Integer id = Integer.parseInt(t_i_l_r[1].trim());
			Integer level = Integer.parseInt(t_i_l_r[2].trim());
			Integer rate = Integer.parseInt(t_i_l_r[3].trim());
			return new DropGood(type, id, level, rate);
		}
		if (t_i_l_r.length == 3) {//type_id_rate
			Integer id = Integer.parseInt(t_i_l_r[1].trim());
			Integer rate = Integer.parseInt(t_i_l_r[2].trim());
			return new DropGood(type, id, 1, rate);
		}
		if (t_i_l_r.length == 2) {//type_id
			Integer id = Integer.parseInt(t_i_l_r[1].trim());
			return new DropGood(type, id, 1, null);
		}
		return null;
	}

	//1星,2星,3星 掉落物品
	private List<DropGood> starDropGoods(String good_star_x) {
		if (good_star_x != null && good_star_x != "0" && good_star_x.contains("-")) {
			String[] good_star_x_s = good_star_x.split(",");//x星掉落

			List<DropGood> dropGoods = new ArrayList<DropGood>();
			for (int i = 0; i < good_star_x_s.length; i++) {
				dropGoods.add(createDropGood(good_star_x_s[i]));
			}
			return dropGoods;
		}
		return null;
	}

	//刷关卡
	private void flushDropGoods(String good_flush, String awardNum) {
		if (good_flush != null && good_flush != "0" && good_flush.contains("-")) {

			String[] good_flush_s = good_flush.split(",");

			//掉落的物品
			for (int i = 0; i < good_flush_s.length; i++) {//掉落的多个物品的 type-id-level-rate
				DropGood dropGood = createDropGood(good_flush_s[i]);
				pointDropBeanF.addGoodWeight(dropGood.getRate(), dropGood);
			}

			//掉落物品数量  0-1,1-1,2-3   掉落0个的可能性1,掉落1个的可能性1,掉落2个的可能性3
			String[] numStrs = awardNum.split(",");
			for (int i = 0; i < numStrs.length; i++) {
				String[] num_rate = numStrs[i].split("-");

				int num = Integer.parseInt(num_rate[0].trim());
				int rate = Integer.parseInt(num_rate[1].trim());

				pointDropBeanF.addNumWeight(rate, num);
			}

		}
	}

	public PointAwardData(PointMaking pointMaking) throws Exception {

		pointDropGoods.put(1, starDropGoods(pointMaking.getAwardStar1()));//1星掉落
		pointDropGoods.put(2, starDropGoods(pointMaking.getAwardStar2()));//2星掉落
		pointDropGoods.put(3, starDropGoods(pointMaking.getAwardStar3()));//3星掉落

		flushDropGoods(pointMaking.getAwardFlush(), pointMaking.getAwardNum());
	}

	public List<DropGood> getDropGoodStar(Integer star) {
		return pointDropGoods.get(star);
	}

	public List<DropGood> getDropGoodFlush(Integer num) {
		return pointDropBeanF.getDropGoods(false, false, num);
	}

}