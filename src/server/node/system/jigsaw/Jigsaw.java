package server.node.system.jigsaw;

import gamecore.entity.AbstractEntity;

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
	private int good;
	private int bad;
	private boolean enable;

	public Jigsaw() {
	}

	public Jigsaw(Long id, Long playerId, String url, int good, int bad, boolean enable) {
		super(Jigsaw.generateCacheKey(id));
		this.id = id;
		this.playerId = playerId;
		this.url = url;
		this.good = good;
		this.bad = bad;
		this.enable = enable;
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

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
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

}
