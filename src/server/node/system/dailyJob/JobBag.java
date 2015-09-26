package server.node.system.dailyJob;

import gamecore.entity.AbstractEntity;
import gamecore.serialize.SerializerJson;
import server.node.system.player.Player;

/**
 * 每日任务包  实体
 */
public class JobBag extends AbstractEntity {

	private static final long serialVersionUID = -5414445128063629073L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "job_bag_";

	public JobBagPO jobBagPO;

	public JobBag() {
	}

	public JobBag(Player player, JobBagPO jobBagPO) {
		super(JobBag.generateCacheKey(player.getId()));//玩家playerId当作存储键值
		this.jobBagPO = jobBagPO;
	}

	public JobBagPO getJobBagPO() {
		return jobBagPO;
	}

	public void setJobBagPO(JobBagPO jobBagPO) {
		this.jobBagPO = jobBagPO;
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(JobBag.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	public String toJson() {
		return SerializerJson.serialize(jobBagPO);
	}

}
