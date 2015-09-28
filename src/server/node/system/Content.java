package server.node.system;

/**
 * 参数配置。
 */
public class Content {

	// 玩家n次无心跳,算掉线
	public static final int HeartBeatOffLine = 10;
	// 每次心跳间隔时间
	public static final int HeartBeatTimePeriod = 2 * 60;
	// 一般缓存时间
	public static final int CacheTimeOutHour = 48;

	public static final int DefaultCash = 10000;
	public static final int DefaultGold = 500;
	public static final int DefaultCup = 200;

	// gift 间隔时间 秒
	public static final int giftTimePeriod = 6 * 60 * 60;

}
