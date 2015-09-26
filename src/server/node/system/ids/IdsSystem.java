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

	private static final int max = 3000;//最大阈值
	private static final int numPerTime = 1000;//每次增加数量

	protected TreeSet<Long> playerIdPool = new TreeSet<Long>();
	protected TreeSet<Long> robotIdPool = new TreeSet<Long>();
	protected TreeSet<Long> missionIdPool = new TreeSet<Long>();
	protected TreeSet<Long> partIdPool = new TreeSet<Long>();

	public IdsSystem() {
		this.playerIdPool = new TreeSet<Long>();
		this.robotIdPool = new TreeSet<Long>();
		this.missionIdPool = new TreeSet<Long>();
		this.partIdPool = new TreeSet<Long>();
	}

	@Override
	public boolean startup() {

		System.out.println("IdsSystem start..");

		TaskCenter.getInstance().scheduleWithFixedDelay(new PlayerIdTask(), 1, 10, TimeUnit.SECONDS);
		TaskCenter.getInstance().scheduleWithFixedDelay(new RobotIdTask(), 2, 15, TimeUnit.SECONDS);
		TaskCenter.getInstance().scheduleWithFixedDelay(new MissionIdTask(), 3, 10, TimeUnit.SECONDS);
		TaskCenter.getInstance().scheduleWithFixedDelay(new PartIdTask(), 4, 3, TimeUnit.SECONDS);

		System.out.println("IdsSystem start..ok");

		return true;
	}

	@Override
	public void shutdown() {
	}

	/**
	 * 消耗一个playerId
	 */
	public Long takePlayerId() {

		if (playerIdPool.size() <= max / 10) {
			TaskCenter.getInstance().execute(new PlayerIdTask());
		}

		Long id = null;
		while (id == null) {
			synchronized (playerIdPool) {
				id = playerIdPool.pollFirst();
			}
			if (id == null) {
				try {
					Thread.sleep(10);
					logger.info("waiting for playerId create");
				} catch (InterruptedException e) {
					logger.error(e);
				}
			}
		}

		return id;
	}

	/**
	 * 消耗一个robotId
	 */
	public Long takeRobotId() {
		if (robotIdPool.size() <= max / 10) {
			TaskCenter.getInstance().execute(new RobotIdTask());
		}

		Long id = null;
		while (id == null) {
			synchronized (robotIdPool) {
				id = robotIdPool.pollFirst();
			}
			if (id == null) {
				try {
					Thread.sleep(10);
					logger.info("waiting for robotId create");
				} catch (InterruptedException e) {
					logger.error(e);
				}
			}
		}

		return id;
	}

	/**
	 * 消耗一个missionId
	 */
	public Long takeMissionId() {
		if (missionIdPool.size() <= max / 10) {
			TaskCenter.getInstance().execute(new MissionIdTask());
		}

		Long id = null;
		while (id == null) {
			synchronized (missionIdPool) {
				id = missionIdPool.pollFirst();
			}
			if (id == null) {
				try {
					Thread.sleep(10);
					logger.info("waiting for missionId create");
				} catch (InterruptedException e) {
					logger.error(e);
				}
			}
		}

		return id;
	}

	/**
	 * 消耗一个partId
	 */
	public Long takePartId() {
		if (partIdPool.size() <= max / 10) {
			TaskCenter.getInstance().execute(new PartIdTask());
		}
		Long id = null;
		while (id == null) {
			synchronized (partIdPool) {
				id = partIdPool.pollFirst();
			}
			if (id == null) {
				try {
					Thread.sleep(10);
					logger.info("waiting for partId create");
				} catch (InterruptedException e) {
					logger.error(e);
				}
			}
		}

		return id;

	}

	//生成id,加入pool
	public static void createIds(Integer hostTag, TreeSet<Long> pool) {
		//以当前时间戳生成numPerTime个
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
			if (playerIdPool.size() < max) {
				createIds(ConfigManager.getInstance().tag, playerIdPool);
			}
		}

	}

	class RobotIdTask implements Runnable {
		public void run() {
			if (robotIdPool.size() < max) {
				createIds(ConfigManager.getInstance().tag, robotIdPool);
			}
		}
	}

	class MissionIdTask implements Runnable {
		public void run() {
			if (missionIdPool.size() < max) {
				createIds(ConfigManager.getInstance().tag, missionIdPool);
			}
		}
	}

	class PartIdTask implements Runnable {
		public void run() {
			if (partIdPool.size() < max) {
				createIds(ConfigManager.getInstance().tag, partIdPool);
			}
		}
	}
}
