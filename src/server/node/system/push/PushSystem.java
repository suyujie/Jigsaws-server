package server.node.system.push;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.system.AbstractSystem;
import gamecore.task.TaskCenter;
import gamecore.util.Clock;
import gamecore.util.Utils;

import java.sql.SQLException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.ConfigManager;
import server.node.system.Root;
import server.node.system.player.Player;
import server.node.system.push.pushMessage.AbstractPushMessage;
import server.node.system.push.pushMessage.PushCashFull;
import server.node.system.push.pushMessage.PushLotteryFree;
import server.node.system.push.pushMessage.PushPvpBeated;
import server.node.system.push.pushMessage.PushRentOrderHired;
import server.node.system.push.pushMessage.PushRepaired;
import common.microsoft.PushUtils;

public class PushSystem extends AbstractSystem {

	private static int cacheGroupNum = 100;
	private static int cacheGroupIndex = 0;

	private Queue<AbstractPushMessage> sendPushQueue = null;

	private static Logger logger = LogManager.getLogger(PushSystem.class.getName());

	@Override
	public boolean startup() {
		System.out.println("PushSystem start....");

		sendPushQueue = new ArrayBlockingQueue<AbstractPushMessage>(10000000);

		TaskCenter.getInstance().scheduleAtFixedRate(new CheckAndSendPush(), Utils.randomInt(1, 5), 1, TimeUnit.SECONDS);

		TaskCenter.getInstance().scheduleWithFixedDelay(new SendPush(), Utils.randomInt(1, 5), 1, TimeUnit.SECONDS);

		System.out.println("PushSystem start....OK");

		return true;
	}

	@Override
	public void shutdown() {
	}

	private void send(AbstractPushMessage pushMessage) throws SQLException {

		Player player = Root.playerSystem.getPlayer(pushMessage.getPlayerId());

		if (player != null
		//&& !player.checkOnLine() 
				&& player.getPushUri() != null && player.getPushUri().length() > 10) {//pushurl有一定长度才算

			if (pushMessage instanceof PushCashFull) {//push_msg_cashfull=您的金矿已经满仓了
				PushUtils.send(player.getPushUri(), Root.langSystem.getMessage(player.getLang(), "push_msg_cashfull"));
			}
			if (pushMessage instanceof PushLotteryFree) {//push_msg_lotteryfree = 免费抽取零件的机会来了
				PushUtils.send(player.getPushUri(), Root.langSystem.getMessage(player.getLang(), "push_msg_lotteryfree"));
			}
			if (pushMessage instanceof PushRepaired) {//push_msg_repaired = 机器人已修理完成
				PushUtils.send(player.getPushUri(), Root.langSystem.getMessage(player.getLang(), "push_msg_repaired"));
			}
			if (pushMessage instanceof PushRentOrderHired) {//push_msg_rent = {0}雇佣了你的机器人
				PushRentOrderHired pushRentOrderHired = (PushRentOrderHired) pushMessage;
				PushUtils.send(player.getPushUri(), Root.langSystem.getMessage(player.getLang(), "push_msg_rent", pushRentOrderHired.getHirerName()));
			}
			if (pushMessage instanceof PushPvpBeated) {//push_msg_beated = 您的基地被{0}攻击
				PushPvpBeated pushPvpBeated = (PushPvpBeated) pushMessage;
				PushUtils.send(player.getPushUri(), Root.langSystem.getMessage(player.getLang(), "push_msg_beated", pushPvpBeated.getAttackerName()));
			}

		}

	}

	//新加推送信息   push_msg_beated = 您的基地被{0}攻击,直接发送
	public void addPushPvpBeated(PushPvpBeated pushPvpBeated) {
		sendPushQueue.add(pushPvpBeated);
	}

	//新加推送信息   push_msg_rent = {0}雇佣了你的机器人,直接发送
	public void addPushRentOrderHired(PushRentOrderHired pushRentOrderHired) {
		sendPushQueue.add(pushRentOrderHired);
	}

	//新加推送信息   push_msg_cashfull=您的金矿已经满仓了
	public void addPushCashFull(PushCashFull pushCashFull) {
		//缓存中获取cashfull的信息,如果没有就加入,如果有了,就判断先后
		PushCashFull msgInCache = getPushCashFullFromCache(pushCashFull.getPlayerId());
		if (msgInCache == null) {
			setPushCashFull(pushCashFull);
		} else {
			if (pushCashFull.getSendTime() > msgInCache.getSendTime()) {//新消息要比老消息晚,就要新的,也就是要晚一点的
				setPushCashFull(pushCashFull);
			}
		}

	}

	//新加推送信息  push_msg_repaired = 机器人已修理完成,延期发送
	public void addPushRepaired(PushRepaired pushRepaired) {
		//缓存中获取cashfull的信息,如果没有就加入,如果有了,就判断先后
		PushRepaired msgInCache = getPushRepairedFromCache(pushRepaired.getPlayerId());
		if (msgInCache == null) {
			setPushRepaired(pushRepaired);
		} else {
			if (pushRepaired.getSendTime() > msgInCache.getSendTime()) {//新消息要比老消息晚,就要新的,也就是要晚一点的
				setPushRepaired(pushRepaired);
			}
		}

	}

	//取消推送信息  push_msg_repaired = 机器人已修理完成,延期发送,因为花钻修理了,所以取消掉推送
	public void cancelPushRepaired(Player player) {
		//缓存中获取cashfull的信息,如果没有就加入,如果有了,就判断先后
		PushRepaired msgInCache = getPushRepairedFromCache(player.getId());
		if (msgInCache != null) {
			delPushRepaired(msgInCache);
		}

	}

	//新加推送信息 push_msg_lotteryfree = 免费抽取零件的机会来了,延期发送
	public void addPushLotteryFree(PushLotteryFree pushLotteryFree) {
		//缓存中获取cashfull的信息,如果没有就加入,如果有了,就判断先后
		PushLotteryFree msgInCache = getPushLotteryFreeFromCache(pushLotteryFree.getPlayerId());
		if (msgInCache == null) {
			setPushLotteryFree(pushLotteryFree);
		} else {
			if (msgInCache.getSendTime() > msgInCache.getSendTime()) {//新消息要比老消息晚,就要新的,也就是要晚一点的
				setPushLotteryFree(pushLotteryFree);
			}
		}

	}

	private PushCashFull getPushCashFullFromCache(long playerId) {
		PushCashFull push = (PushCashFull) JedisUtilJson.getInstance().hashGet("push_cashfull_" + (playerId % cacheGroupNum), playerId + "", PushCashFull.class);
		return push;
	}

	private void setPushCashFull(PushCashFull pushCashFull) {
		JedisUtilJson.getInstance().hashAdd("push_cashfull_" + (pushCashFull.getPlayerId() % cacheGroupNum), pushCashFull.getPlayerId() + "", pushCashFull);
	}

	private void delPushCashFull(PushCashFull pushCashFull) {
		JedisUtilJson.getInstance().hashDel("push_cashfull_" + (pushCashFull.getPlayerId() % cacheGroupNum), pushCashFull.getPlayerId() + "");
	}

	private PushRepaired getPushRepairedFromCache(long playerId) {
		PushRepaired push = (PushRepaired) JedisUtilJson.getInstance().hashGet("push_repaired_" + (playerId % cacheGroupNum), playerId + "", PushRepaired.class);
		return push;
	}

	private void setPushRepaired(PushRepaired pushRepaired) {
		JedisUtilJson.getInstance().hashAdd("push_repaired_" + (pushRepaired.getPlayerId() % cacheGroupNum), pushRepaired.getPlayerId() + "", pushRepaired);
	}

	private void delPushRepaired(PushRepaired pushRepaired) {
		JedisUtilJson.getInstance().hashDel("push_repaired_" + (pushRepaired.getPlayerId() % cacheGroupNum), pushRepaired.getPlayerId() + "");
	}

	private PushLotteryFree getPushLotteryFreeFromCache(long playerId) {
		PushLotteryFree push = (PushLotteryFree) JedisUtilJson.getInstance().hashGet("push_lotteryfree_" + (playerId % cacheGroupNum), playerId + "", PushLotteryFree.class);
		return push;
	}

	private void setPushLotteryFree(PushLotteryFree pushLotteryFree) {
		JedisUtilJson.getInstance().hashAdd("push_lotteryfree_" + (pushLotteryFree.getPlayerId() % cacheGroupNum), pushLotteryFree.getPlayerId() + "", pushLotteryFree);
	}

	private void delPushLotteryFree(PushLotteryFree pushLotteryFree) {
		JedisUtilJson.getInstance().hashDel("push_lotteryfree_" + (pushLotteryFree.getPlayerId() % cacheGroupNum), pushLotteryFree.getPlayerId() + "");
	}

	/**
	 * 守护任务。
	 * 内部线程类,过滤推送消息
	 */
	protected class CheckAndSendPush implements Runnable {

		protected CheckAndSendPush() {
		}

		@Override
		public void run() {

			if (cacheGroupIndex++ >= cacheGroupNum) {
				cacheGroupIndex = 0;
			}

			//只有一台node,或者能跟自己机器号整除的 那些
			boolean isThisNodeDo = cacheGroupIndex % ConfigManager.getInstance().nodeNum.intValue() == ConfigManager.getInstance().tag.intValue() - 1;

			if (isThisNodeDo) {
				//push_msg_repaired = 机器人已修理完成,延期发送
				List<PushCashFull> pushCashFulls = JedisUtilJson.getInstance().hashAllValues("push_cashfull_" + cacheGroupIndex, PushCashFull.class);
				long time = Clock.currentTimeSecond();
				for (PushCashFull pushCashFull : pushCashFulls) {
					if (time >= pushCashFull.getSendTime()) {//发送时间已到
						sendPushQueue.add(pushCashFull);
						delPushCashFull(pushCashFull);
					}
				}

				//push_msg_lotteryfree = 免费抽取零件的机会来了,延期发送
				List<PushLotteryFree> pushLotteryFrees = JedisUtilJson.getInstance().hashAllValues("push_lotteryfree_" + cacheGroupIndex, PushLotteryFree.class);
				time = Clock.currentTimeSecond();
				for (PushLotteryFree pushLotteryFree : pushLotteryFrees) {
					if (time >= pushLotteryFree.getSendTime()) {//发送时间已到
						sendPushQueue.add(pushLotteryFree);
						delPushLotteryFree(pushLotteryFree);
					}
				}

				//push_msg_cashfull=您的金矿已经满仓了,延期发送
				List<PushRepaired> pushRepaireds = JedisUtilJson.getInstance().hashAllValues("push_repaired_" + cacheGroupIndex, PushRepaired.class);
				time = Clock.currentTimeSecond();
				for (PushRepaired pushRepaired : pushRepaireds) {
					if (time >= pushRepaired.getSendTime()) {//发送时间已到
						sendPushQueue.add(pushRepaired);
						delPushRepaired(pushRepaired);
					}
				}
			}

		}
	}

	/**
	 * 守护任务。
	 * 内部线程类,发送消息
	 */
	protected class SendPush implements Runnable {

		protected SendPush() {
		}

		@Override
		public void run() {

			while (sendPushQueue != null && !sendPushQueue.isEmpty()) {
				AbstractPushMessage pushMessage = sendPushQueue.poll();//获取并移除第一个元素
				if (pushMessage != null) {
					synchronized (sendPushQueue) {
						try {
							send(pushMessage);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}

		}
	}
}
