package server.node.system;

/**
 * 参数配置。
 */
public class Content {

	//玩家n次无心跳,算掉线
	public static final int HeartBeatOffLine = 10;
	//每次心跳间隔时间
	public static final int HeartBeatTimePeriod = 2 * 60;
	//护盾时间 24小时
	public static final int protectTime = 24 * 60 * 60;

	//一般缓存时间
	public static final int CacheTimeOutHour = 48;

	//战斗时间
	public static final int PvpTime = 240;
	public static final int PveTime = 180;
	//pvp可匹配的对手的最小等级
	public static final int PvpMinLevel = 11;

	public static final int DefaultCash = 10000;
	public static final int DefaultGold = 500;
	public static final int DefaultCup = 200;

	//最大耐久
	public static final int maxWear = 100;
	//耐久送多少点
	public static final int wearAsGiftNum = 5;
	//gift 间隔时间  秒
	public static final int giftTimePeriod = 6 * 60 * 60;
	//pvp 战斗消耗耐久
	public static final int pvpUseWear = 25;
	//出租订单 检测周期 
	public static final int CheckRentOrderPeriod = 1 * 60;//一分钟
	//每次检查出租订单情况,同时检查多少,暂定200
	public static final int CheckRentOrderPerTime = 200;

	//订单时间 6小时   6 * 60 * 60;
	public static final int RentOrderTime = 6 * 60 * 60;
	//订单提前到期时间
	public static final int RentOrderEarlierTimeOut = 2 * 60; //服务器提前2分钟到期
	//租赁中介费率
	public static final float orderAgencyCost = 0F;
	//取消订单扣取的钻石
	public static final int cancelOrderCost = 1;
	//租金上限
	public static final int orderMaxPrice = 99999;
	//租金下限
	public static final int orderMinPrice = 10;

	//修理机器人,时间误差容忍
	public static final int repaireGapTime = 60;

	//免费抽奖时间间隔 24 小时
	public static final int FreeLotteryTime = 24 * 60 * 60;

	//验证伤害值的 最大值参数
	public static final int maxDamage = 10;

}
