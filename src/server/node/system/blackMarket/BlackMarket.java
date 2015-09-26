package server.node.system.blackMarket;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;
import gamecore.serialize.SerializerJson;
import gamecore.util.Clock;
import gamecore.util.DateUtils;

import java.util.HashMap;

import server.node.system.player.Player;

public class BlackMarket extends AbstractEntity {

	private static final long serialVersionUID = 4917141412113394723L;

	private static final StringBuilder ckBuf = new StringBuilder();
	public final static String CKPrefix = "black_market_";

	private static final int canBuyTime = 3;

	private long createT;

	private HashMap<Long, BlackItem> items = new HashMap<Long, BlackItem>();

	public BlackMarket() {
	}

	public BlackMarket(Player player, long createT) {
		super(BlackMarket.generateCacheKey(player.getId()));
		this.createT = createT;
	}

	public long getCreateT() {
		return createT;
	}

	public void setCreateT(long createT) {
		this.createT = createT;
	}

	public HashMap<Long, BlackItem> getItems() {
		return items;
	}

	public void setItems(HashMap<Long, BlackItem> items) {
		this.items = items;
	}

	public void addItem(BlackItem blackItem, boolean sync) {
		items.put(blackItem.getId(), blackItem);
		if (sync) {
			this.synchronize();
		}
	}

	public boolean checkTimeOut() {
		if (DateUtils.isSameDay(createT * 1000, Clock.currentTimeMillis())) {//同一天,没过期
			return false;
		} else {
			return true;
		}
	}

	public boolean inBuyTime() {
		return createT + canBuyTime * 60 * 60 >= Clock.currentTimeSecond();
	}

	/*
	 * 可买倒计时
	 * 1，超过三个小时
	 * 2，到0点
	 */
	public long countdown() {

		long countdown3Hour = createT + canBuyTime * 60 * 60 - Clock.currentTimeSecond();
		long countdownFlush = DateUtils.dayEnd(createT * 1000) / 1000 - Clock.currentTimeSecond();

		return countdown3Hour > countdownFlush ? countdownFlush : countdown3Hour;
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(BlackMarket.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	public String toJson() {
		return SerializerJson.serialize(this);
	}

	public void synchronize() {
		synchronized (this) {
			JedisUtilJson.getInstance().setForHour(getCacheKey(), this, 7 * 24);
		}
	}
}
