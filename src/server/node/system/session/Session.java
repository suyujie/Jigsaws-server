package server.node.system.session;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;
import server.node.system.Content;

/** 
 * 可序列化 Session
 * 控制玩家在线
 */
public class Session extends AbstractEntity {

	private static final long serialVersionUID = 3865546682091817876L;

	private final static String CKPrefix = "session_";

	private static final StringBuilder ckBuf = new StringBuilder();

	private Integer nodeTag;//节点id
	private String mobileId;
	private Long playerId;
	private Long activeT;//秒

	public Session() {
	}

	public Session(Integer nodeTag, String mobileId, Long playerId, long activeT) {
		super(generateCacheKey(mobileId));
		this.nodeTag = nodeTag;
		this.mobileId = mobileId;
		this.playerId = playerId;
		this.activeT = activeT;
	}

	public Integer getNodeTag() {
		return nodeTag;
	}

	public void setNodeTag(Integer nodeTag) {
		this.nodeTag = nodeTag;
	}

	public String getMobileId() {
		return mobileId;
	}

	public void setMobileId(String mobileId) {
		this.mobileId = mobileId;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public Long getActiveT() {
		return activeT;
	}

	public void setActiveT(Long activeT) {
		this.activeT = activeT;
	}

	public void synchronize() {
		synchronized (this) {
			JedisUtilJson.getInstance().setForSec(getCacheKey(), this, Content.HeartBeatOffLine * Content.HeartBeatTimePeriod);
		}
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(String mobileId) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(CKPrefix).append(mobileId).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

}
