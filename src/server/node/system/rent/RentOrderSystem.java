package server.node.system.rent;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.system.AbstractSystem;
import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;
import gamecore.task.TaskCenter;
import gamecore.util.Clock;
import gamecore.util.DataUtils;
import gamecore.util.Utils;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.dao.RentOrderDao;
import server.node.system.Content;
import server.node.system.Root;
import server.node.system.battle.PveBattle;
import server.node.system.npc.NpcPlayer;
import server.node.system.player.CashType;
import server.node.system.player.GoldType;
import server.node.system.player.Player;
import server.node.system.robot.FightProperty;
import server.node.system.robot.Robot;
import server.node.system.robot.RobotBag;
import server.node.system.robot.RobotType;

public class RentOrderSystem extends AbstractSystem {

	private static final Logger logger = LogManager.getLogger(RentOrderSystem.class);

	public static int maxRent = 5;

	//在这台节点上创建的订单的key
	private Queue<String> rentOrderKeys = null;

	@Override
	public boolean startup() {

		System.out.println("RentOrderSystem start....");

		rentOrderKeys = new ArrayBlockingQueue<String>(10000000);

		TaskCenter.getInstance().scheduleWithFixedDelay(new CheckRentOrderTimeOut(), Utils.randomInt(1, 30), Content.CheckRentOrderPeriod, TimeUnit.SECONDS);

		System.out.println("RentOrderSystem start....OK");

		return true;
	}

	@Override
	public void shutdown() {

	}

	/**
	 * 获取玩家的订单包
	 * @param player
	 * @return
	 */
	public RentOrderBag getRentOrderBag(Player player) {
		RentOrderBag rentOrderBag = RedisHelperJson.getRentOrderBag(player.getId());
		if (rentOrderBag == null) {
			rentOrderBag = getRentOrderBagFromDB(player);
			rentOrderBag.synchronize();
		}
		return rentOrderBag;
	}

	/**
	 * 从数据库中获取玩家租赁包
	 * @param player
	 * @return
	 */
	public RentOrderBag getRentOrderBagFromDB(Player player) {
		RentOrderBag rentOrderBag = new RentOrderBag(player);

		RentOrderDao dao = DaoFactory.getInstance().borrowRentOrderDao();
		List<Map<String, Object>> list = dao.readRentOrders(player);
		DaoFactory.getInstance().returnRentOrderDao(dao);

		if (list != null && list.size() > 0) {
			for (Map<String, Object> map : list) {
				Long id = ((BigInteger) map.get("id")).longValue();
				Integer score = map.get("score") == null ? null : ((Long) map.get("score")).intValue();
				Long tenantId = map.get("tenant_id") == null ? null : ((BigInteger) map.get("tenant_id")).longValue();
				Long robotId = map.get("robot_id") == null ? null : ((BigInteger) map.get("robot_id")).longValue();
				int robotSlot = map.get("robot_slot") == null ? null : ((Long) map.get("robot_slot")).intValue();
				Integer cash = map.get("cash") == null ? null : ((Long) map.get("cash")).intValue();
				Long endTime = map.get("end_time") == null ? null : ((BigInteger) map.get("end_time")).longValue();
				Long rentTime = map.get("rent_time") == null ? null : ((BigInteger) map.get("rent_time")).longValue();
				Integer status = tenantId == null ? RentOrderStatus.Wait.asCode() : RentOrderStatus.Rent.asCode();

				RentOrder order = new RentOrder(id, score, player.getId(), robotSlot, tenantId, robotId, cash, endTime, rentTime, status);
				rentOrderBag.setRentOrder(order, false);
			}
		}
		return rentOrderBag;
	}

	public void checkOrderTimeOut(RentOrderBag rentOrderBag) {
		for (RentOrder rentOrder : rentOrderBag.getRentOrders()) {
			if (rentOrder.checkTimeOut()) {//超时了
				try {
					rentOrderTimeOut(rentOrder);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
	}

	/**
	 * 创建出租订单
	 */
	public SystemResult createRentOrder(Player player, Integer robotSlot, int cash) {
		SystemResult result = new SystemResult();

		RobotBag robotBag = Root.robotSystem.getRobotBag(player);
		Robot robot = robotBag.readRobot(RobotType.STORAGE, robotSlot);

		//机器人存在
		if (robot != null) {
			RentOrderBag rentOrderBag = getRentOrderBag(player);
			RentOrder rentOrder = rentOrderBag.getRentOrder(player, robotSlot);

			//如果订单位置为空，则可以生成订单
			if (rentOrder == null) {

				if (!rentOrderBag.checkRobotRented(player, robot)) {//robot是否已经被出租
					int minPrice = Content.orderMinPrice;
					int maxPrice = Content.orderMaxPrice;

					if (cash >= minPrice && cash <= maxPrice) {

						FightProperty property = robot.refreshFightProperty();

						rentOrder = new RentOrder(robot.getId(), property.getScore(), player.getId(), robotSlot, null, robot.getId(), cash, Clock.currentTimeSecond()
								+ Content.RentOrderTime, 0L, RentOrderStatus.Wait.asCode());
						//缓存
						rentOrderBag.setRentOrder(rentOrder, true);

						addToWaitForRentOrders(rentOrder.getScore(), rentOrder);

					} else {
						result.setCode(ErrorCode.RENTORDER_CASH_NO_SUITABLE);
					}
				}
			} else {
				result.setCode(ErrorCode.RENTORDER_EXIST);
			}
		} else {
			result.setCode(ErrorCode.PARAM_ERROR);
		}

		return result;
	}

	/**
	 * 增加订单到系统出租订单列表
	 */
	public void addToWaitForRentOrders(Integer score, RentOrder order) {
		if (order != null) {
			rentOrderKeys.add(order.getCacheKey());
			RedisHelperJson.addWaitRentOrder(score, order.getCacheKey());
		}
	}

	/**
	 * 取消订单
	 * @param playerId
	 * @param orderId
	 * @return
	 */
	public SystemResult cancelRentOrder(Player player, Integer robotSlot) {
		SystemResult result = new SystemResult();

		RentOrderBag rentOrderBag = Root.rentOrderSystem.getRentOrderBag(player);
		RentOrder rentOrder = rentOrderBag.getRentOrder(player, robotSlot);

		if (rentOrder != null) {

			if (player.getGold() >= Content.cancelOrderCost) {
				//修改玩家钻石数据，更新数据库，更新缓存
				Root.playerSystem.changeGold(player, -Content.cancelOrderCost, GoldType.CANCEL_RENTORDER_COST, true);

				rentOrderBag.removeRentOrder(rentOrder, false);
				rentOrderBag.synchronize();

				removeFromWaitForRentOrders(rentOrder.getScore(), rentOrder);

			} else {
				result.setCode(ErrorCode.CANCEL_RENT_GOLD_NOT_ENOUGH);
			}
		} else {
			logger.error("rent order is not exist,or rent order has been canceled");
			result.setCode(ErrorCode.PARAM_ERROR);
		}

		return result;
	}

	/**
	 * 从待租列表中删除订单
	 */
	public void removeFromWaitForRentOrders(Integer score, RentOrder order) {
		if (logger.isDebugEnabled()) {
			logger.info("remove rentOrder");
		}
		RedisHelperJson.removeWaitRentOrder(score, order.getCacheKey());
	}

	/**
	 * 获取n个可租赁的订单
	 */
	public List<RentOrder> getWaitRentOrderListByCupNum(Player player) {
		List<RentOrder> orders = new ArrayList<RentOrder>();

		//自己机器人的最高分
		RobotBag robotBag = Root.robotSystem.getRobotBag(player);
		int score = 0;
		for (Robot robot : robotBag.readRobots(RobotType.BATTLE)) {
			FightProperty property = robot.refreshFightProperty();
			if (property.score > score) {
				score = property.score;
			}
		}

		orders.addAll(getWaitRentOrderListByScore(player, score * 6 / 10, score * 8 / 10, 1));//		第1个机器人：是自己战力分数最高的机器人的60%~80%
		orders.addAll(getWaitRentOrderListByScore(player, score * 9 / 10, score * 11 / 10, 2));//		第2~3个机器人：是自己战力分数最高机器人的90%~110%
		orders.addAll(getWaitRentOrderListByScore(player, score * 13 / 10, score * 16 / 10, 2));//		第4个机器人：是自己分战力数最高机器人的150%~180%

		return orders;
	}

	//从一定范围内的列表中取一定数量的orderId
	private List<RentOrder> getWaitRentOrderListByScore(Player player, Integer min, Integer max, int num) {
		List<RentOrder> result = new ArrayList<RentOrder>();

		//构造 列表, 乱序
		List<Integer> scoreList = DataUtils.combinationIntegerArray(min, max, true);

		for (Integer score : scoreList) {
			if (result.size() >= num) {//够数了
				break;
			}

			List<String> orderKeysFromCache = RedisHelperJson.getWaitRentOrder(score, 1);
			for (String rentOrderKey : orderKeysFromCache) {

				RentOrder rentOrder = RedisHelperJson.getRentOrder(rentOrderKey);

				//先看是否过期
				if (rentOrder != null) {
					if (rentOrder.checkTimeOut()) {//超时了,删掉
						try {
							rentOrderTimeOut(rentOrder);
						} catch (Exception e) {
							logger.error(e.getMessage());
						}
					} else {
						if (rentOrder.getStatus() == RentOrderStatus.Wait.asCode()) {
							try {
								Player orderOwner = Root.playerSystem.getPlayer(rentOrder.getPlayerId());
								if (orderOwner != null && rentOrder.getPlayerId().longValue() != player.getId().longValue()) {//不能是自己的
									result.add(rentOrder);
								}
							} catch (Exception e) {
								logger.error(e.getMessage());
							}
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * 租赁机器人
	 * @throws SQLException 
	 */
	public SystemResult hire(Player player, Long lessorId, int robotSlot) throws SQLException {
		//是否是npc
		NpcPlayer npcPlayer = Root.npcSystem.getNpcPlayer(lessorId);
		if (npcPlayer != null) {
			return hireFromNpc(player, npcPlayer, robotSlot);
		} else {
			Player lessorPlayer = Root.playerSystem.getPlayer(lessorId);
			return hireFromPlayer(player, lessorPlayer, robotSlot);
		}
	}

	/**
	 * 租赁其他玩家的机器人
	 * @throws SQLException 
	 */
	public SystemResult hireFromPlayer(Player player, Player lessorPlayer, int robotSlot) throws SQLException {
		SystemResult result = new SystemResult();

		RentOrderBag rentOrderBag = Root.rentOrderSystem.getRentOrderBag(lessorPlayer);
		RentOrder rentOrder = rentOrderBag.getRentOrder(lessorPlayer, robotSlot);

		PveBattle pveBattle = RedisHelperJson.getPveBattle(player.getId());

		//在订单背包中，说明订单尚未被关闭
		if (rentOrder != null && pveBattle != null) {
			//订单未被租出
			if (rentOrder.getStatus() == RentOrderStatus.Wait.asCode()) {

				if (player.getCash() >= rentOrder.getCash()) {

					//扣取租金，更新数据库，更新缓存
					Root.playerSystem.changeCash(player, -rentOrder.getCash(), CashType.HIRE_ROBOT_COST, true);

					//修改订单状态，更新数据库，更新缓存
					rentOrder.setStatus(RentOrderStatus.Rent.asCode());
					rentOrder.setTenantId(player.getId());
					rentOrder.setRentTime(Clock.currentTimeSecond());//分钟
					rentOrder.setEndTime(0L);//到期时间清零

					//订单同步
					rentOrder.synchronize(7 * 24);

					//同步db,数据库中只记录成功出租的订单
					RentOrderDao dao = DaoFactory.getInstance().borrowRentOrderDao();
					dao.save(lessorPlayer, rentOrder);
					DaoFactory.getInstance().returnRentOrderDao(dao);

					removeFromWaitForRentOrders(rentOrder.getScore(), rentOrder);

					RobotBag rentRobotBag = Root.robotSystem.getRobotBag(rentOrder.getPlayerId());
					Robot robot = rentRobotBag.readRobot(RobotType.STORAGE, rentOrder.getRobotSlot());

					pveBattle.setAttackHireRobot(robot);
					pveBattle.synchronize();

					//发送租赁订单的消息
					RentOrderMessage rentOrderMessage = new RentOrderMessage(RentOrderMessage.Hire, lessorPlayer, player, rentOrder);
					this.publish(rentOrderMessage);

				} else {
					result.setCode(ErrorCode.ORDER_RENT_CASH_NO_ENOUGH);
				}
			} else {//订单已经出租，仍允许租赁，租赁方扣钱，出租方不再收钱

				if (player.getCash() >= rentOrder.getCash()) {

					//扣取租金，更新数据库，更新缓存
					Root.playerSystem.changeCash(player, -rentOrder.getCash(), CashType.HIRE_ROBOT_COST, true);

				} else {
					result.setCode(ErrorCode.ORDER_RENT_CASH_NO_ENOUGH);
				}
			}

		} else {
			//直接从缓存中取，缓存中没有，数据库肯定也没有了
			rentOrder = getRentOrderFromCache(lessorPlayer.getId(), robotSlot);
			//订单已经失效，仍允许租赁，租赁方扣钱，出租方不再收钱
			if (rentOrder != null) {
				if (player.getCash() >= rentOrder.getCash()) {
					//扣取租金，更新数据库，更新缓存
					Root.playerSystem.changeCash(player, -rentOrder.getCash(), CashType.HIRE_ROBOT_COST, true);
				} else {
					result.setCode(ErrorCode.ORDER_RENT_CASH_NO_ENOUGH);
				}

			} else {
				result.setCode(ErrorCode.PARAM_ERROR);
			}
		}

		return result;
	}

	/**
	 * 租用npcRobot
	 */
	public SystemResult hireFromNpc(Player player, NpcPlayer npcPlayer, int robotSlot) {
		SystemResult result = new SystemResult();

		PveBattle pveBattle = RedisHelperJson.getPveBattle(player.getId());

		if (pveBattle != null) {

			if (player.getCash() > npcPlayer.getCash()) {

				//扣取租金，更新数据库，更新缓存
				Root.playerSystem.changeCash(player, -(int) npcPlayer.getCash(), CashType.HIRE_ROBOT_COST, true);

				RentOrderMessage rentOrderMessage = new RentOrderMessage(RentOrderMessage.Hire, null, player, npcPlayer);
				this.publish(rentOrderMessage);

				//延长npc的缓存时间
				npcPlayer.synchronize(7 * 24);

				pveBattle.setAttackNpcRobot(npcPlayer.getRentRobot());

				pveBattle.synchronize();

			} else {
				result.setCode(ErrorCode.ORDER_RENT_CASH_NO_ENOUGH);
			}
		} else {
			logger.error("no pvebattle");
		}

		return result;
	}

	/**
	 * 收取租金
	 */
	public SystemResult gainRent(Player player, Integer robotSlot) {
		SystemResult result = new SystemResult();

		RentOrderBag rentOrderBag = Root.rentOrderSystem.getRentOrderBag(player);
		RentOrder rentOrder = (RentOrder) rentOrderBag.getRentOrder(player, robotSlot);

		if (rentOrder != null && rentOrder.getStatus() == RentOrderStatus.Rent.asCode()) {
			int getCash = (int) (rentOrder.getCash() * (1 - Content.orderAgencyCost));

			//修改player cash，更新数据库，更新缓存
			Root.playerSystem.changeCash(player, getCash, CashType.RENT_ROBOT_GET, true);

			//关闭订单，更新缓存，删除数据库
			RentOrderDao dao = DaoFactory.getInstance().borrowRentOrderDao();
			dao.delete(rentOrder);
			DaoFactory.getInstance().returnRentOrderDao(dao);

			rentOrderBag.removeRentOrder(rentOrder, true);

			removeFromWaitForRentOrders(rentOrder.getScore(), rentOrder);

			//发送关闭订单的消息
			RentOrderMessage message = new RentOrderMessage(RentOrderMessage.Close, player, null, rentOrder);
			this.publish(message);

		} else {
			result.setCode(ErrorCode.PARAM_ERROR);
		}

		return result;
	}

	public RentOrder getRentOrderFromCache(Long id, int slot) {
		return RedisHelperJson.getRentOrder(id, slot);
	}

	//超时处理
	public void rentOrderTimeOut(RentOrder rentOrder) throws SQLException {

		//remove waitingOrder
		removeFromWaitForRentOrders(rentOrder.getScore(), rentOrder);
		//remove from player rentOrderBag
		Player player = Root.playerSystem.getPlayer(rentOrder.getPlayerId());
		RentOrderBag rentOrderBag = Root.rentOrderSystem.getRentOrderBag(player);
		rentOrderBag.removeRentOrder(rentOrder, true);

	}

	/**
	 * 守护任务。
	 * 内部线程类,过滤 超时 订单
	 */
	protected class CheckRentOrderTimeOut implements Runnable {

		@Override
		public void run() {

			int indexNum = 0;

			while (rentOrderKeys != null && !rentOrderKeys.isEmpty() && indexNum <= Content.CheckRentOrderPerTime) {
				indexNum++;
				String key = rentOrderKeys.peek();//获取第一个元素,但是不移除

				if (key != null) {
					RentOrder rentOrder = RedisHelperJson.getRentOrder(key);

					if (rentOrder == null) {
						rentOrderKeys.remove(key);//移除
					} else {
						synchronized (rentOrderKeys) {
							//提前两分钟到期
							if (rentOrder.checkTimeOut()) {//超时了
								rentOrderKeys.remove(key);//移除
								try {
									rentOrderTimeOut(rentOrder);
								} catch (Exception e) {
									logger.error(e.getMessage());
								}
							} else {//没有超时
								break;//终止此次检查,因为后面的肯定还没到时间
							}
						}
					}

				}
			}

		}
	}

}
