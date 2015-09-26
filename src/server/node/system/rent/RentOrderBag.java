package server.node.system.rent;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.cache.redis.RedisHelperJson;
import gamecore.entity.AbstractEntity;
import gamecore.io.ByteArrayGameOutput;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Content;
import server.node.system.player.Player;
import server.node.system.robot.Robot;

public class RentOrderBag extends AbstractEntity {

	private static final Logger logger = LogManager.getLogger(RentOrderBag.class.getName());

	private static final long serialVersionUID = 2369489343040568792L;

	private static StringBuffer ckBuf = new StringBuffer();

	public static final String CKPrefix = "order_bag_";

	private int maxSize;
	private List<String> orderKeys = new ArrayList<String>();

	public RentOrderBag() {
	}

	public RentOrderBag(Player player) {
		super(generateCacheKey(player.getId()));
		int level = player.getLevel();
		if (level <= 20) {
			this.maxSize = 1;
		} else {
			this.maxSize = 2;
		}
	}

	public List<String> getOrderKeys() {
		return orderKeys;
	}

	public void setOrderKeys(List<String> orderKeys) {
		this.orderKeys = orderKeys;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setRentOrder(RentOrder order, boolean sync) {
		order.synchronize(Content.RentOrderTime);
		orderKeys.add(order.getCacheKey());
		if (sync) {
			this.synchronize();
		}
	}

	public void removeRentOrder(RentOrder order, boolean sync) {
		RedisHelperJson.removeEntity(order.getCacheKey());
		orderKeys.remove(order.getCacheKey());
		if (sync) {
			this.synchronize();
		}
	}

	/**
	 * order list
	 */
	public List<RentOrder> getRentOrders() {
		List<RentOrder> list = new ArrayList<RentOrder>();
		for (String key : orderKeys) {
			RentOrder rentOrder = RedisHelperJson.get(key, RentOrder.class);
			if (rentOrder != null) {
				if (null != rentOrder) {
					list.add(rentOrder);
				}
			}
		}
		return list;
	}

	/**
	 * 检查机器人是否已经出租状态
	 */
	public boolean checkRobotRented(Player player, Robot robot) {
		String key = RentOrder.generateCacheKey(player.getId(), robot.getSlot());
		if (orderKeys.contains(key)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据机器人的slot 从缓存中获取订单
	 * @param slot
	 * @return
	 */
	public RentOrder getRentOrder(Player player, Integer robotSlot) {
		RentOrder rentOrder = RedisHelperJson.getRentOrder(RentOrder.generateCacheKey(player.getId(), robotSlot));
		return rentOrder;
	}

	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ck = ckBuf.append(CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ck;
		}
	}

	public void synchronize() {
		synchronized (this) {
			JedisUtilJson.getInstance().setForHour(getCacheKey(), this, 7 * 24);
		}
	}

	public byte[] toByteArray() {
		List<RentOrder> rentOrders = getRentOrders();
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			//循环发送组件信息
			if (rentOrders != null) {
				for (RentOrder rentOrder : rentOrders) {
					bago.putBytesNoLength(rentOrder.toByteArray());
				}
			}

		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}

}
