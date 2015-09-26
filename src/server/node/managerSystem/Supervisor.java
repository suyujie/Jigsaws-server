package server.node.managerSystem;

import gamecore.task.TaskCenter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 服务器监管器。
 */
public final class Supervisor {

	private final static Supervisor instance = new Supervisor();

	private Monitor monitor;

	private ScheduledFuture<?> tickerFuture;

	protected LinkedList<HostPerformance> perfQueue;

	private SupervisorTicker ticker;

	protected int maxQueueSize = 12 * 48; // 最近48小时

	private Supervisor() {
		this.monitor = new Monitor();
		this.perfQueue = new LinkedList<HostPerformance>();

	}

	public final static Supervisor getInstance() {
		return Supervisor.instance;
	}

	public boolean start() {
		if (null == this.ticker) {
			this.ticker = new SupervisorTicker();
		}
		// 间隔 5 分钟执行一次
		this.tickerFuture = TaskCenter.getInstance().scheduleAtFixedRate(this.ticker, 5, 5, TimeUnit.MINUTES);

		return true;
	}

	public void stop() {
		if (null != this.tickerFuture) {
			this.tickerFuture.cancel(true);
			this.tickerFuture = null;
		}

		this.monitor.release();
	}

	/**
	 * 返回信息监视器。
	 * @return
	 */
	public Monitor getMonitor() {
		return this.monitor;
	}

	/**
	 * 截取统计信息。
	 * @return
	 */
	public VMStatistics fetchVMStatistics() {
		VMStatistics ret = new VMStatistics();
		ret.collect();
		return ret;
	}

	/**
	 * 返回指定数量的最近性能数据。
	 * @param num
	 * @return
	 */
	public List<HostPerformance> getHostPerformances(int num) {
		if (this.perfQueue.isEmpty()) {
			return null;
		}

		ArrayList<HostPerformance> list = new ArrayList<HostPerformance>(num);
		synchronized (this.perfQueue) {
			for (int i = this.perfQueue.size() - 1, count = 0; i >= 0 && count < num; --i, ++count) {
				list.add(this.perfQueue.get(i));
			}
		}
		return list;
	}

	public final class SupervisorTicker implements Runnable {

		protected SupervisorTicker() {
		}

		@Override
		public void run() {
			// 记录性能数据
			synchronized (Supervisor.getInstance().perfQueue) {
				// 快照性能指标并记录
				Supervisor.getInstance().perfQueue.offer(Supervisor.getInstance().getMonitor().snapshotPerformance());
				// 判断队列长度
				if (Supervisor.getInstance().perfQueue.size() > Supervisor.getInstance().maxQueueSize) {
					Supervisor.getInstance().perfQueue.poll();
				}
			}

		}
	}

}
