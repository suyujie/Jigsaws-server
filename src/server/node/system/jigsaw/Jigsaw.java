package server.node.system.jigsaw;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;
import server.node.system.Content;

/**
 * 图片实体
 */
public class Jigsaw extends AbstractEntity {

	private static final long serialVersionUID = -4828830629858473329L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "jigsaw_";

	private Long id;
	private Long playerId;
	private String url;
	private String bucketName;
	private int good;
	private int bad;
	private int drop;
	private JigsawState state;

	public Jigsaw() {
	}

	public Jigsaw(Long id, Long playerId, String url, String bucketName, int good, int bad, int drop,
			JigsawState state) {
		super(Jigsaw.generateCacheKey(id));
		this.id = id;
		this.playerId = playerId;
		this.url = url;
		this.bucketName = bucketName;
		this.good = good;
		this.bad = bad;
		this.drop = drop;
		this.state = state;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public int getGood() {
		return good;
	}

	public void setGood(int good) {
		this.good = good;
	}

	public int getBad() {
		return bad;
	}

	public void setBad(int bad) {
		this.bad = bad;
	}

	public int getDrop() {
		return drop;
	}

	public void setDrop(int drop) {
		this.drop = drop;
	}

	public JigsawState getState() {
		return state;
	}

	public void setState(JigsawState state) {
		this.state = state;
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(Jigsaw.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	/**
	 * 实例数据写入缓存。
	 */
	public void synchronize(int hour) {
		synchronized (this) {
			JedisUtilJson.getInstance().setForHour(getCacheKey(), this, hour);
		}
	}

	public int getCacheTag() {
		return new Long(id % JigsawSystem.imageIdCacheTagMaxNum).intValue();
	}

}
