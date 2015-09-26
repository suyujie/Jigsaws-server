package server.node.system.task;

import gamecore.entity.AbstractEntity;
import gamecore.serialize.SerializerJson;
import server.node.system.player.Player;

/**
 * 任务task 包  实体
 */
public class TaskBag extends AbstractEntity {

	private static final long serialVersionUID = -6956816268709434424L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "task_bag_";

	public Integer titleTaskId;//称号id

	public TaskBagPO taskBagPO;

	public TaskBag() {
	}

	public TaskBag(Player player, Integer titleTaskId, TaskBagPO taskBagPO) {
		super(TaskBag.generateCacheKey(player.getId()));//玩家playerId当作存储键值
		this.titleTaskId = titleTaskId;
		this.taskBagPO = taskBagPO;
	}

	public Integer getTitleTaskId() {
		return titleTaskId;
	}

	public void setTitleTaskId(Integer titleTaskId) {
		this.titleTaskId = titleTaskId;
	}

	public TaskBagPO getTaskBagPO() {
		return taskBagPO;
	}

	public void setTaskBagPO(TaskBagPO taskBagPO) {
		this.taskBagPO = taskBagPO;
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(TaskBag.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	public String toStorageJson() {
		return SerializerJson.serialize(taskBagPO);
	}

}
