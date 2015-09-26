package server.node.system.opponent;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.system.AbstractSystem;
import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;
import gamecore.util.DataUtils;
import gamecore.util.RangeExpansion;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.dao.PlayerDao;
import server.node.system.Content;
import server.node.system.Root;
import server.node.system.player.CashType;
import server.node.system.player.Player;

/**
 * 对手系统。
 */
public final class OpponentSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(OpponentSystem.class.getName());

	@Override
	public boolean startup() {

		System.out.println("OpponentSystem start....");
		int num = initOpponent();
		logger.info("opponent player num : " + num);
		System.out.println("OpponentSystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {
	}

	/**
	 * 获取玩家的重复对手包
	 */
	public RepeatOpponentBag getRepeatOpponentBag(Player player) {
		RepeatOpponentBag repeatOpponentBag = RedisHelperJson.getRepeatOpponentBag(player.getId());
		if (repeatOpponentBag == null) {
			repeatOpponentBag = new RepeatOpponentBag(player.getId());
		}
		return repeatOpponentBag;
	}

	/**
	 * 初始化对手
	 */
	public int initOpponent() {

		PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
		List<Map<String, Object>> list = playerDao.readPlayerAsOpponent();
		DaoFactory.getInstance().returnPlayerDao(playerDao);

		if (list != null) {
			for (Map<String, Object> map : list) {
				if (map != null) {
					Long id = ((BigInteger) map.get("id")).longValue();
					int cupNum = ((Long) map.get("cup_num")).intValue();
					addOpponent(cupNum, id);
				}
			}
			return list.size();
		}

		return 0;

	}

	/**
	 * 加入对手
	 * 
	 * 玩家掉线才加入
	 * 保护时间结束,并且不在线
	 * 
	 * @param cup
	 * @param playerId
	 */
	public void addOpponent(int cup, Player player) {
		if (player.getLevel() >= Content.PvpMinLevel) {
			addOpponent(cup, player.getId());
		}
	}

	private void addOpponent(int cup, Long playerId) {
		RedisHelperJson.addOpponent(cup, playerId);
	}

	/**
	 * 从cup对手列表中移除对手
	 * 玩家上线
	 * 玩家被pvp选中为defender了 ---玩家被打(因为被打加上保护时间)
	 */
	public void removeOpponent(int cup, Long playerId) {
		RedisHelperJson.removeOpponent(cup, playerId);
	}

	//从缓存中随机得到一个对手
	public SystemResult getOpponent(Player player, int useCash) throws SQLException {

		SystemResult result = new SystemResult();

		//刷新对手需要的费用客户端传过来
		if (player.getCash() >= useCash) {
			Root.playerSystem.changeCash(player, -useCash, CashType.PVP_USE, true);

			/**
			 * 在线的玩家和处于保护器的玩家不能被选择。
			 * 概率搜索到：-15%~25%的玩家（功勋比我低15%—功勋比我高25%）
			 */
			RepeatOpponentBag repeatOpponentBag = getRepeatOpponentBag(player);

			Player opponent = getOpponent(player, repeatOpponentBag, player.getPlayerStatistics().getCupNum() * 85 / 100, player.getPlayerStatistics().getCupNum() * 125 / 100);

			if (opponent == null && player.getLevel() > 25) {//当玩家等级超过25级之后，PVP搜索时不再出现假人，只能匹配真人。首先按现在的逻辑搜索真人，如果没有真人，往下扩展范围

				//范围扩展
				RangeExpansion re = new RangeExpansion(85, 125, 0, 125, 5);

				while (opponent == null) {
					int[] downSize = re.down();
					if (downSize[2] == 0) {
						break;
					}
					opponent = getOpponent(player, repeatOpponentBag, player.getPlayerStatistics().getCupNum() * downSize[0] / 100, player.getPlayerStatistics().getCupNum()
							* downSize[1] / 100);
				}

			}

			if (opponent != null) {//加入重复对手包,接下来的n次不会搜索到这个人

				repeatOpponentBag.addOpponent(opponent.getId());
				repeatOpponentBag.synchronize();

				//加入defender列表中,之后会有其他线程安排进入对手列表
				Root.defenderSystem.addDefender(opponent);
			}

			result.setBindle(opponent);

		} else {
			result.setCode(ErrorCode.CASH_NOT_ENOUGH);
		}

		return result;
	}

	//根据勋章数量匹配对手
	private Player getOpponent(Player player, RepeatOpponentBag repeatOpponentBag, Integer min, Integer max) throws SQLException {
		Player opponent = null;

		List<Integer> cupList = DataUtils.combinationIntegerArray(min, max, true);
		for (Integer c : cupList) {
			if (RedisHelperJson.existsOpponent(c)) {
				Long opponentId = RedisHelperJson.getOpponent(c);// 取出  1个
				if (opponentId != null) {
					//是否是重复对手
					if (!repeatOpponentBag.repeat(opponentId)) {//不重复
						opponent = Root.playerSystem.getPlayer(opponentId);
						if (opponent != null && !opponent.checkOnLine() && opponent.getId().longValue() != player.getId().longValue()) {//不在线,并且不是自己.那就是他了.
							break;
						} else {//不符合条件
							opponent = null;
						}
					}
				}
			}
		}

		return opponent;
	}

	//随机匹配对手
	private Player getOpponentRandom(Player player, RepeatOpponentBag repeatOpponentBag) throws SQLException {
		Player opponent = null;

		List<Integer> cupList = DataUtils.combinationIntegerArray(200, 2500, true);
		for (Integer c : cupList) {
			if (RedisHelperJson.existsOpponent(c)) {
				Long opponentId = RedisHelperJson.getOpponent(c);// 取出  1个
				if (opponentId != null) {
					//是否是重复对手
					if (!repeatOpponentBag.repeat(opponentId)) {//不重复
						opponent = Root.playerSystem.getPlayer(opponentId);
						if (opponent != null && !opponent.checkOnLine() && opponent.getId().longValue() != player.getId().longValue()) {//不在线,并且不是自己.那就是他了.
							break;
						} else {//不符合条件
							opponent = null;
						}
					}
				}
			}
		}

		return opponent;
	}

}
