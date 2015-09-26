package server.node.system.monthCard;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.system.AbstractSystem;
import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;
import gamecore.util.Clock;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.dao.MonthCardDao;
import server.node.system.Root;
import server.node.system.player.GoldType;
import server.node.system.player.Player;

/** 
 * 月卡
 */
public final class MonthCardSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(MonthCardSystem.class.getName());

	//一个月多少天
	//TODO 需要改成30天
	public static Integer DAYS = 3;
	//每天领取gold
	public static Integer GoldEveryDay = 150;

	public static final String MonthCardId = "monthcard";

	public MonthCardSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("MonthCardSystem start....");

		System.out.println("MonthCardSystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {
	}

	public MonthCard getMonthCard(Player player, boolean canReadDB) throws SQLException {
		MonthCard monthCard = RedisHelperJson.getMonthCard(player.getId());
		if (monthCard == null && canReadDB) {
			monthCard = readMonthCardFromDB(player);
		}
		return monthCard;
	}

	private MonthCard readMonthCardFromDB(Player player) throws SQLException {

		MonthCardDao monthCardDao = DaoFactory.getInstance().borrowMonthCardDao();
		Map<String, Object> map = monthCardDao.readMonthCard(player.getId());
		DaoFactory.getInstance().returnMonthCardDao(monthCardDao);

		if (map != null) {
			try {
				Long buyT = ((BigInteger) map.get("buy_t")).longValue();
				Long lastT = ((BigInteger) map.get("last_t")).longValue();
				int rewardNum = ((Long) map.get("reward_num")).intValue();

				MonthCard monthCard = new MonthCard(player, buyT, lastT, rewardNum);
				monthCard.synchronize();

				return monthCard;

			} catch (Exception e) {
				logger.error(e);
			}
		}

		return null;
	}

	public MonthCard buyMonthCard(Player player) {
		MonthCard monthCard = new MonthCard(player, Clock.currentTimeSecond(), 0, 0);
		monthCard.synchronize();

		MonthCardDao monthCardDao = DaoFactory.getInstance().borrowMonthCardDao();
		monthCardDao.save(player, monthCard);
		DaoFactory.getInstance().returnMonthCardDao(monthCardDao);

		Root.logSystem.addMonthCardLog(player, monthCard);

		return monthCard;
	}

	public void monthCardTimeOut(Player player, MonthCard monthCard) {
		delete(player, monthCard);
	}

	public SystemResult receiveMonthCard(Player player) throws SQLException {

		SystemResult result = new SystemResult();

		MonthCard monthCard = getMonthCard(player, false);

		if (monthCard == null) {
			result.setCode(ErrorCode.NO_MONTH_CARD);
			return result;
		}

		if (monthCard.haveDayNum() < 0) {//已经不能领了
			monthCardTimeOut(player, monthCard);
			result.setCode(ErrorCode.NO_MONTH_CARD);
			return result;
		}

		if (!monthCard.checkTodayRewarded()) {//今天还没领取
			//领取gold
			Root.playerSystem.changeGold(player, GoldEveryDay, GoldType.MONTH_CARD, true);
			//更新或者删除月卡
			monthCard.setLastT(Clock.currentTimeSecond());
			monthCard.setRewardNum(monthCard.getRewardNum() + 1);

			//查看之后还有几天
			if (monthCard.haveDayNum() > 0) {//明天还可以领取,更新数据
				update(player, monthCard);
			} else {//明天就没了，删掉这次月卡
				monthCardTimeOut(player, monthCard);
			}

			result.setCode(ErrorCode.NO_ERROR);
			result.setBindle(monthCard);
			return result;
		} else {
			result.setCode(ErrorCode.MONTH_CARD_REPEAT_REWARD);
			return result;
		}

	}

	private void update(Player player, MonthCard monthCard) {
		monthCard.synchronize();
		MonthCardDao monthCardDao = DaoFactory.getInstance().borrowMonthCardDao();
		monthCardDao.update(player, monthCard);
		DaoFactory.getInstance().returnMonthCardDao(monthCardDao);
	}

	private void delete(Player player, MonthCard monthCard) {

		RedisHelperJson.removeEntity(monthCard.getCacheKey());

		MonthCardDao monthCardDao = DaoFactory.getInstance().borrowMonthCardDao();
		monthCardDao.delete(player);
		DaoFactory.getInstance().returnMonthCardDao(monthCardDao);

		Root.logSystem.updateMonthCardLog(player, monthCard);

	}
}
