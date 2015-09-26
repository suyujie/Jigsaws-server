package server.node.system.opponent;

import gamecore.system.AbstractSystem;
import gamecore.task.TaskCenter;
import gamecore.util.Clock;
import gamecore.util.Utils;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Root;
import server.node.system.player.Player;

/**
 * 防守者系统
 */
public final class DefenderSystem extends AbstractSystem {

	private Queue<Defender> defenders = null;

	private static int defenderTime = 10;//防御时间为10分钟,十分钟后重新进入opponent列表

	@Override
	public boolean startup() {
		System.out.println("DefenderSystem start....");

		defenders = new ArrayBlockingQueue<Defender>(100000);
		TaskCenter.getInstance().scheduleAtFixedRate(new CheckDefenderTimeOut(), Utils.randomInt(1, 20), 60, TimeUnit.SECONDS);

		System.out.println("DefenderSystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {
	}

	/**
	 * pvp中被搜到了,从opponent列表中移除,加入defender列表
	 * 加入防守者
	 */
	public void addDefender(Player player) {

		Root.opponentSystem.removeOpponent(player.getPlayerStatistics().getCupNum(), player.getId());

		Defender defender = new Defender(player.getId(), Clock.currentTimeSecond() + defenderTime);

		defenders.add(defender);
	}

	/**
	 * 守护任务。
	 * 内部线程类,过滤玩家
	 */
	protected class CheckDefenderTimeOut implements Runnable {

		private Logger logger = LogManager.getLogger(CheckDefenderTimeOut.class.getName());

		protected CheckDefenderTimeOut() {
		}

		@Override
		public void run() {

			while (defenders != null && !defenders.isEmpty()) {

				Defender defender = defenders.peek();//获取第一个元素,但是不移除
				if (defender != null) {

					if (defender.getEndT() >= Clock.currentTimeSecond()) {//结束时间>=当前时间,该清理掉了
						try {
							Player player = Root.playerSystem.getPlayer(defender.getPlayerId());

							//在线,或者 被保护
							if (player.getOnLine() == 1 || player.getProtectEndTime() == 0) {//没有被保护,  没到保护条件  战斗压根没发生  战斗中断没有结算....都加回对手列表
								Root.opponentSystem.addOpponent(player.getPlayerStatistics().getCupNum(), player);
							}
						} catch (Exception e) {
							logger.error(e.getMessage());
						}

					} else {
						break;
					}

				} else {
					break;
				}
			}

		}
	}

}
