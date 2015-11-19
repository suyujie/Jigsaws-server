package server.node.system.ids;

import gamecore.system.AbstractSystem;
import gamecore.task.TaskCenter;
import gamecore.util.Clock;

import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.ConfigManager;

/**
 * 主键id
 */
public final class IdsSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(IdsSystem.class.getName());

	private static final int max = 3000;// 最大阈值
	private static final int numPerTime = 1000;// 每次增加数量

	protected TreeSet<Long> idPool = new TreeSet<Long>();

	public IdsSystem() {
		this.idPool = new TreeSet<Long>();
	}

	@Override
	public boolean startup() {

		System.out.println("IdsSystem start..");

		TaskCenter.getInstance().scheduleWithFixedDelay(new PlayerIdTask(), 1, 10, TimeUnit.SECONDS);

		System.out.println("IdsSystem start..ok");

		return true;
	}

	@Override
	public void shutdown() {
	}

	/**
	 * 消耗一个Id
	 */
	public Long takeId() {

		if (idPool.size() <= max / 10) {
			TaskCenter.getInstance().execute(new PlayerIdTask());
		}

		Long id = null;
		while (id == null) {
			synchronized (idPool) {
				id = idPool.pollFirst();
			}
			if (id == null) {
				try {
					Thread.sleep(10);
					logger.info("waiting for id create");
				} catch (InterruptedException e) {
					logger.error(e);
				}
			}
		}

		return id;
	}

	public static void createIds(Integer hostTag, TreeSet<Long> pool) {
		// 以当前时间戳生成numPerTime个
		Long t = Clock.currentTimeSecond();
		synchronized (pool) {
			for (int i = 0; i < numPerTime; i++) {
				long id = (t * 10 + hostTag) * numPerTime + i;
				pool.add(id);
			}
		}
	}

	class PlayerIdTask implements Runnable {
		public void run() {
			if (idPool.size() < max) {
				createIds(ConfigManager.getInstance().tag, idPool);
			}
		}

	}

}
