package server.node.system.player;

/** 
 * cash 获取  消耗  
 */
public enum CashType {

	MANAGER_GIVE(0, "系统给予"), POINT_GET(1, "关卡通过获得"), RENT_ROBOT_GET(2, "出租获得"), TOTURIAL_GET(3, "教学获得"), PVP_GET(4, "pvp抢的"), SELL_PART(5, "卖出部件"), INIT_GIVE(6, "初始给予"), MISSION_GAIN(
			7, "关卡产出"), BUY_BLACK_ITEM_CASH(8, "黑市商店购买"), TREASURE_ISLAND_GET(9, "金银岛获得"), HIRE_ROBOT_COST(-1, "租赁消费"), ROBOT_PART_UP_COST(-2, "部件升级"), PVP_LOSE(-3, "pvp损失"), LOTTERY(
			-4, "抽奖消耗"), PVP_USE(-5, "进行pvp消费"), PART_RARITY_UPGRADE(-6, "星级突破"), BERG_UPGRADE(-7, "融合"), ROBOT_PART_EVOLUTIN(-8, "进化"),

	;

	private int sc;
	private String desc;

	private CashType(int code, String desc) {
		this.sc = code;
		this.desc = desc;
	}

	public int asCode() {
		return this.sc;
	}

}
