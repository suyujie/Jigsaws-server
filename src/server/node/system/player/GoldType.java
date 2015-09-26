package server.node.system.player;

/** 
 * gold 获取  消耗  
 */
public enum GoldType {

	MANAGER_GIVE(0, "系统给予"), BUY(1, "充值"), MISSION_GET(2, "大关卡生产"), TASK_GET(3, "达成成就获得"), DAILYJOB_GET(4, "每日任务获得"), TOTURIAL_GET(5, "教学给予"), NOTICE_GIVE(6, "公告获得"), POINT_GIVE(
			7, "通关获得"), INIT_GIVE(8, "初始给予"), HANDBOOK_REWARD(9, "图鉴奖励"), COMPENSATE(10, "补偿"), MONTH_CARD(11, "月卡"), FIRST_BUY_DOUBLE(12, "首冲翻倍"), PAINT_COST(-1, "涂装"), REPAIR_ROBOT(
			-2, "充电"), CANCEL_RENTORDER_COST(-3, "订单取消"), GET_EGG_PART_COST(-4, "抽蛋"), LOTTERY(-5, "抽奖"), REFLUSH_RENT_ORDER(-6, "刷新订单"), ROBOT_PART_UP_COST(-7, "部件升级"), ROBOT_PART_UP_WITH_GOLD_COST(
			-8, "钻石直接升级"), BUY_BLACK_ITEM(-9, "黑市商店购买"), RESET_DEATHWHEEL(-10, "死亡轮盘重置"), BERG_UPGRADE(-11, "水晶融合"), RESET_BERG_WHEEL(-12, "水晶车轮战重置"), RELIVE_BERG_WHEEL(-13,
			"车轮战复活"), RESET_TREASURE_ISLAND(-14, "金银岛重置"), ROBOT_PART_EVOLUTION_COST(-15, "进化");

	private int sc;
	private String desc;

	private GoldType(int code, String desc) {
		this.sc = code;
		this.desc = desc;
	}

	public int asCode() {
		return this.sc;
	}

}
