package server.node.system.opponent;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import server.node.system.Content;

/**
 * 临时忽略的对手列表
 */
public class RepeatOpponentBag extends AbstractEntity {

	private static final long serialVersionUID = -7959087020950348501L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "repeat_opponents_";

	private List<Long> repeatOpponents = new ArrayList<Long>();

	private int num = 10;

	public RepeatOpponentBag() {
	}

	public RepeatOpponentBag(long playerId) {
		super(RepeatOpponentBag.generateCacheKey(playerId));
	}

	public List<Long> getRepeatOpponents() {
		return repeatOpponents;
	}

	public void setRepeatOpponents(LinkedList<Long> repeatOpponents) {
		this.repeatOpponents = repeatOpponents;
	}

	//取出all
	public List<Long> readRepeatOpponentsAll() {
		List<Long> result = new ArrayList<Long>();
		synchronized (this) {
			if (repeatOpponents == null || repeatOpponents.isEmpty()) {
				return null;
			} else {
				ListIterator<Long> listIterator = repeatOpponents.listIterator();
				while (listIterator.hasNext()) {
					result.add(listIterator.next());
				}
			}
		}
		return result;
	}

	//是否包含,true不包含		false包含
	public boolean repeat(Long id) {
		if (repeatOpponents == null || repeatOpponents.isEmpty()) {
			return false;
		} else {
			return repeatOpponents.contains(id);
		}
	}

	//增加一个对手
	public boolean addOpponent(Long playerId) {
		if (repeatOpponents.contains(playerId)) {
			return false;
		} else {
			repeatOpponents.add(playerId);
		}
		if (repeatOpponents.size() > num) {
			repeatOpponents.remove(0);
		}
		return true;
	}

	//当前多少对手
	public int readOpponentsNum() {
		return repeatOpponents.size();
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(long playerId) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(RepeatOpponentBag.CKPrefix).append(playerId).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

}
