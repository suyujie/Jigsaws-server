package server.node.system.lottery;

import gamecore.entity.AbstractEntity;
import gamecore.util.Clock;
import server.node.system.Content;

/**
 * 抽奖实体。
 */
public class LotteryBag extends AbstractEntity {

	private static final long serialVersionUID = -8182736434713802262L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "lottery_bag_";

	public long lastFreeT;

	public LotteryBag() {
	}

	public LotteryBag(Long playerId) {
		super(LotteryBag.generateCacheKey(playerId));//玩家playerId当作存储键值
		lastFreeT = 0;
	}

	public long getLastFreeT() {
		return lastFreeT;
	}

	public void setLastFreeT(long lastFreeT) {
		this.lastFreeT = lastFreeT;
	}

	//下次免费抽奖倒计时,秒为单位
	public long readNextFreeTimeLeft() {
		int now = (int) Clock.currentTimeSecond();
		if (now - lastFreeT >= Content.FreeLotteryTime) {
			return 0;
		} else {
			return Content.FreeLotteryTime - (now - lastFreeT);
		}
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(LotteryBag.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

}
