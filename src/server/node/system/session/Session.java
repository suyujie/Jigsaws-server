package server.node.system.session;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;
import server.node.system.Content;

/**
 * 可序列化 Session 控制玩家在线
 */
public class Session extends AbstractEntity {

	private static final long serialVersionUID = 3865546682091817876L;

	private final static String CKPrefix = "session_";

	private static final StringBuilder ckBuf = new StringBuilder();

	private Integer nodeTag;// 节点id
	private String sessionId;
	private Long playerId;
	private Long activeT;// 秒

	public Session() {
	}

	public Session(Integer nodeTag, String sessionId, Long playerId, long activeT) {
		super(generateCacheKey(sessionId));
		this.nodeTag = nodeTag;
		this.sessionId = sessionId;
		this.playerId = playerId;
		this.activeT = activeT;
	}

	public Integer getNodeTag() {
		return nodeTag;
	}

	public void setNodeTag(Integer nodeTag) {
		this.nodeTag = nodeTag;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
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
			JedisUtilJson.getInstance().setForSec(getCacheKey(), this, Content.SessionTimeOutSec);
		}
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(String sessionId) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(CKPrefix).append(sessionId).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

}
