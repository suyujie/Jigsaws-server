package gamecore.system;

/**
 * 错误码。
 */
public final class ErrorCode {

	// 没有错误
	public final static int NO_ERROR = 0;
	// 用户未登陆或者session失效
	public final static int NO_LOGIN = 1;
	// 操作超时
	public final static int REQ_TIMEOUT = 2;
	//校验错误
	public final static int CHECK_ERROR = 3;
	//参数错误
	public final static int PARAM_ERROR = 8;
	// 未知错误
	public final static int UNKNOWN_ERROR = 9;

	//玩家钻石不够
	public final static int GOLD_NOT_ENOUGH = 101;
	//玩家现金不够
	public final static int CASH_NOT_ENOUGH = 102;

	//关卡收获时间还没到,不能收获
	public final static int GAIN_TIME_NOT_OK = 201;
	public final static int GAIN_CASH_NOT_OK = 202;

	//不能免费抽奖
	public final static int FREE_LOTTERY_TIME = 211;

	//没有战斗,不能通关
	public final static int POINT_NO_OPEN = 301;

	//没有战斗,不能通关
	public final static int NO_BATTLE_NO_PASS = 302;

	//换装问题
	public final static int CHANGE_PART_ERROR = 401;
	public final static int CHANGE_PART_NO_PART = 402;

	//修理完成时间误差过大
	public final static int REPAIRTIME_GAP_TO_LARGE = 406;
	//花钻修理,gold不统一
	public final static int REPAIR_GOLD_NOT_PAIR = 409;

	//涂装,颜色瓶不够
	public final static int PAINT_COLOR_NO_ENOUGH = 411;
	//涂装,gold跟前端传过来的不一致
	public final static int PAINT_GOLD_NO_PAIR = 422;

	//part升级
	public final static int PART_UPDATE_ERROR_NO_ROBOT = 501;
	//part升级,没有升级部件
	public final static int PART_UPDATE_ERROR_NO_PART = 502;
	//part升级,没有消耗部件
	public final static int PART_UPDATE_ERROR_NO_USEPART = 503;
	//part卖出
	public final static int PART_SELL_ERROR_NO_PART = 504;
	//eggpag
	public final static int NO_EGG_BAG = 505;
	//eggpag
	public final static int EGG_BAG_NO_THIS_PART = 506;
	//eggpag
	public final static int GET_EGG_GOLD_NOT_ENOUGH = 507;
	//part进化,没有消耗部件
	public final static int PART_EVOLUTION_ERROR_NO_PART = 508;

	//订单已存在
	public final static int RENTORDER_EXIST = 601;
	//租金设定不合适
	public final static int RENTORDER_CASH_NO_SUITABLE = 602;
	//钱不够租不起
	public final static int ORDER_RENT_CASH_NO_ENOUGH = 603;
	//玩家钻石不够
	public final static int CANCEL_RENT_GOLD_NOT_ENOUGH = 604;

	//pvp
	public final static int NO_PVP_BATTLE = 701;

	//check
	public final static int CHECK_BATTLE_ATTACK_ERROR = 1001;//战斗验证数据,attacker的机器人数值不一致
	public final static int CHECK_BATTLE_DEFENDER_ERROR = 1002;//战斗验证数据,defender的机器人数值不一致
	public final static int CHECK_BATTLE_RENT_ERROR = 1003;//战斗验证数据,rent的机器人数值不一致
	public final static int CHECK_BATTLE_DAMAGE_ERROR = 1011;//战斗伤害验证异常

	//充值失败
	public final static int RECHARGE_ERROR = 1100;
	public final static int RECHARGE_VALID_ERROR = 1101;
	public final static int RECHARGE_REPEAT_ERROR = 1102;
	public final static int RECHARGE_WAITING = 1103;
	public final static int RECHARGE_NO_ORDER = 1104;

	//月卡
	public final static int NO_MONTH_CARD = 1200;
	public final static int MONTH_CARD_REPEAT_REWARD = 1201;
	public final static int MONTH_CARD_REPEAT_BUY = 1202;

	//chip
	public final static int CHIP_NO_ENOUGH = 1301;

	public final static int BERG_WHEEL_NULL = 1401;
	public final static int BERG_UPGRADE_NOT_ENOUGH = 1402;

	public final static int DEATH_WHEEL_NULL = 1501;

	public final static int TREASURE_ISLAND_NULL = 1601;

}
