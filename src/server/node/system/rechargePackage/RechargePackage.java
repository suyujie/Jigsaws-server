package server.node.system.rechargePackage;

import gamecore.io.ByteArrayGameOutput;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.monthCard.MonthCardSystem;
import common.coin.CoinType;
import common.language.LangType;

public class RechargePackage implements Serializable {

	private final static Logger logger = LogManager.getLogger(RechargePackage.class);

	private static final long serialVersionUID = -904208530878253202L;

	private String id;
	private HashMap<LangType, String> name;
	private String pic;
	private HashMap<CoinType, Integer> coin;
	private HashMap<ItemType, Integer> item;
	private HashMap<LangType, String> desc;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public HashMap<LangType, String> getName() {
		return name;
	}

	public void setName(HashMap<LangType, String> name) {
		this.name = name;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public HashMap<ItemType, Integer> getItem() {
		return item;
	}

	public void setItem(HashMap<ItemType, Integer> item) {
		this.item = item;
	}

	public HashMap<CoinType, Integer> getCoin() {
		return coin;
	}

	public void setCoin(HashMap<CoinType, Integer> coin) {
		this.coin = coin;
	}

	public HashMap<LangType, String> getDesc() {
		return desc;
	}

	public void setDesc(HashMap<LangType, String> desc) {
		this.desc = desc;
	}

	public String getItemStr(boolean isGoldDouble) {
		StringBuffer sb = new StringBuffer();
		if (item.get(ItemType.GOLD) != null && item.get(ItemType.GOLD) != 0) {
			if (isGoldDouble) {
				sb.append("gold:").append(item.get(ItemType.GOLD) * 2).append(",");
			} else {
				sb.append("gold:").append(item.get(ItemType.GOLD)).append(",");
			}
		}
		if (item.get(ItemType.CASH) != null && item.get(ItemType.CASH) != 0) {
			sb.append("cash:").append(item.get(ItemType.CASH)).append(",");
		}

		if (sb.toString().endsWith(",")) {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}

	public byte[] toByteArray(CoinType coinType, LangType langType, boolean first) {

		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			bago.putString(id);
			bago.putString(name.get(langType));
			bago.putInt(coin.get(coinType));
			bago.putString(desc.get(langType));
			bago.putString(pic);

			//商品详细数据，以“type:num,……”的形式来表示，
			//type为商品类型，暂定如下表示
			//gold，钻石；
			//cash，金币；
			//part开头，组件（含能量块），如part_head_20代表id为20号的头，part_exp_0代表id为0号的能量块；
			//vip，代表vip等级
			//color，代表喷罐，color_1代表id为1的喷罐

			bago.putString(getItemStr(false));
			bago.putBoolean(first);

		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}

	public byte[] toByteArrayAsMonthCard(CoinType coinType, LangType langType) {

		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			bago.putString(id);
			bago.putString(name.get(langType));
			bago.putInt(coin.get(coinType));
			bago.putString(desc.get(langType));
			bago.putString(pic);
			bago.putString(getItemStr(false));
			bago.putInt(MonthCardSystem.GoldEveryDay);
			bago.putInt(MonthCardSystem.DAYS);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bago.toByteArray();
	}
}