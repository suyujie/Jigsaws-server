package server.node.system.gameImage;

import gamecore.entity.AbstractEntity;

/**
 * 图片实体
 */
public class GameImage extends AbstractEntity {

	private static final long serialVersionUID = -4828830629858473329L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "g_img_";

	private Long id;
	private Long playerId;
	private String imageUrl;
	private int good;
	private int bad;

	public GameImage() {
	}

	public GameImage(Long id, Long playerId, String imageUrl, int good, int bad) {
		super(GameImage.generateCacheKey(id));
		this.id = id;
		this.playerId = playerId;
		this.imageUrl = imageUrl;
		this.good = good;
		this.bad = bad;
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

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
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

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(GameImage.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

}