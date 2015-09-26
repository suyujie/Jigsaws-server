package server.node.system.monthCard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gamecore.entity.AbstractEntity;
import gamecore.util.Clock;
import gamecore.util.DateUtils;
import server.node.system.player.Player;

/**
 * 月卡
 */
public class MonthCard extends AbstractEntity {

	private static final Logger logger = LogManager.getLogger(MonthCard.class);

	private static final long serialVersionUID = -7690593920600973156L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "month_card_";

	public long buyT;
	public long lastT;
	public int rewardNum;

	public MonthCard() {
	}

	public MonthCard(Player player, long buyT, long lastT, int rewardNum) {
		super(MonthCard.generateCacheKey(player.getId()));
		this.buyT = buyT;
		this.lastT = lastT;
		this.rewardNum = rewardNum;
	}

	public long getBuyT() {
		return buyT;
	}

	public void setBuyT(long buyT) {
		this.buyT = buyT;
	}

	public long getLastT() {
		return lastT;
	}

	public void setLastT(long lastT) {
		this.lastT = lastT;
	}

	public int getRewardNum() {
		return rewardNum;
	}

	public void setRewardNum(int rewardNum) {
		this.rewardNum = rewardNum;
	}

	//到期时间
	private long readTimeOutTime() {
		return DateUtils.dayEnd(buyT * 1000) / 1000 + (MonthCardSystem.DAYS - 1) * 24 * 60 * 60;
	}

	//检查今天是否已经领取了
	public boolean checkTodayRewarded() {
		if (DateUtils.isSameDay(lastT * 1000, Clock.currentTimeMillis())) {//上次领取是今天.
			return true;
		} else {
			return false;
		}
	}

	public boolean checkTimeOut(Long t) {//单位 秒
		if (t == null) {
			t = Clock.currentTimeSecond();
		}
		//买月卡的那天 结束的时间点(秒)
		if (t > readTimeOutTime()) {//已到期
			return true;
		}
		//未到期
		return false;
	}

	public int readNextTimeSecond() {

		//现在是否已经过期
		if (checkTimeOut(Clock.currentTimeSecond())) {
			return -1;
		} else if (!checkTodayRewarded()) {//上次领取不是今天.
			return 0;//0 表示现在就可以领取
		} else {//第二天凌晨,就是可以领的时间,但是得判断第二天凌晨有没有过期
			//第二天凌晨,秒
			long nextDayBegin = DateUtils.dayEnd(Clock.currentTimeMillis()) / 1000 + 1;
			//到期
			if (checkTimeOut(nextDayBegin)) {
				return -1;
			} else {//未到期,明天还可以领取...明早凌晨-现在 = 倒计时
				return (int) (nextDayBegin - Clock.currentTimeSecond());
			}
		}
	}

	public int haveDayNum() {
		//现在是否已经过期
		if (checkTimeOut(Clock.currentTimeSecond())) {
			logger.debug("player[" + getCacheKey() + "] time out");
			return 0;
		} else {
			//买月卡的那天的即将结束的最后秒数 + 29天...
			long lastDayEnd = DateUtils.dayEnd(buyT * 1000) + MonthCardSystem.DAYS * 24 * 60 * 60 * 1000L;
			int haveDay = DateUtils.getIntervalDaysOfTwoDays(Clock.currentTimeMillis(), lastDayEnd);

			if (checkTodayRewarded()) {//上次领取不是今天.
				return haveDay - 1;
			} else {
				return haveDay;
			}
		}
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(MonthCard.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

}
