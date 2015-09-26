package server.node.system.rent;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;
import gamecore.io.ByteArrayGameOutput;
import gamecore.util.Clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Content;
import server.node.system.Root;
import server.node.system.player.Player;
import server.node.system.robot.Robot;

/**
 * 机器人出租订单
 */
public class RentOrder extends AbstractEntity {

	private static final Logger logger = LogManager.getLogger(RentOrder.class.getName());

	private static final long serialVersionUID = -111395132739181199L;

	private static final StringBuffer ckBuf = new StringBuffer();
	private static final String CKPrefix = "rent_order_";

	private Long id;
	private int score;
	//出租玩家ID
	private Long playerId;
	//承租玩家ID
	private Long tenantId;
	//机器人ID
	private Long robotId;
	//机器人的位置
	private int robotSlot;
	//租金
	private int cash;
	//订单到期时间
	private Long endTime;
	//订单租赁时间
	private Long rentTime;
	//订单状态
	private int status;//1 已经租出，0 未租出

	public RentOrder() {
	}

	public RentOrder(Long id, int score, Long playerId, int robotSlot, Long tenantId, Long robotId, int cash, Long endTime, Long rentTime, int status) {
		super(generateCacheKey(playerId, robotSlot));
		this.id = id;
		this.score = score;
		this.playerId = playerId;
		this.robotSlot = robotSlot;
		this.tenantId = tenantId;
		this.robotId = robotId;
		this.cash = cash;
		this.endTime = endTime;
		this.rentTime = rentTime;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public int getScore() {
		return score;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public Long getRobotId() {
		return robotId;
	}

	public int getRobotSlot() {
		return robotSlot;
	}

	public int getCash() {
		return cash;
	}

	public Long getEndTime() {
		return endTime;
	}

	public Long getRentTime() {
		return rentTime;
	}

	public int getStatus() {
		return status;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public void setRentTime(Long rentTime) {
		this.rentTime = rentTime;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public void setRobotId(Long robotId) {
		this.robotId = robotId;
	}

	public void putRobotSlot(int robotSlot) {
		this.robotSlot = robotSlot;
	}

	public void setCash(int cash) {
		this.cash = cash;
	}

	//只有等待状态的时候才超时一说
	public boolean checkTimeOut() {
		if (status == RentOrderStatus.Wait.asCode()) {
			if (Clock.currentTimeSecond() + Content.RentOrderEarlierTimeOut - getEndTime() > 0) {//超时了
				return true;
			} else {//没有超时
				return false;
			}
		}
		return false;
	}

	public static String generateCacheKey(Long playerId, int robotSlot) {
		synchronized (ckBuf) {
			String ck = ckBuf.append(CKPrefix).append(playerId).append("_").append(robotSlot).toString();
			ckBuf.delete(0, ck.length());
			return ck;
		}
	}

	public void synchronize(int hour) {
		synchronized (this) {
			JedisUtilJson.getInstance().setForHour(getCacheKey(), this, hour);
		}
	}

	public byte[] toByteArray() {

		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			bago.putInt(robotSlot);
			if (tenantId != null) {
				bago.putInt((int) (cash * (1 - Content.orderAgencyCost)));
				Player player = Root.playerSystem.getPlayer(tenantId);
				bago.putString(player.getAccount().getNameInPlat());
				bago.putInt(player.getLevel());
			} else {
				bago.putInt(0);
				bago.putString("");
				bago.putInt(0);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();

	}

	public byte[] toByteArrayAsWaitOrder() {

		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			bago.putLong(playerId);
			bago.putInt(robotSlot);
			bago.putInt(cash);
			Player player = Root.playerSystem.getPlayer(playerId);
			if (player != null) {
				bago.putString(player.getAccount().getNameInPlat());
				bago.putInt(player.getLevel());
			} else {
				bago.putString("");
				bago.putInt(1);
			}

			Robot robot = Root.robotSystem.getRobotBag(player).readRobot(robotId);
			bago.putBytesNoLength(robot.toByteArrayAsDefender());

		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();

	}
}
