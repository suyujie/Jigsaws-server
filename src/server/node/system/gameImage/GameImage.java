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

	public GameImage() {
	}

	public GameImage(Long id, Long playerId) {
		super(GameImage.generateCacheKey(id));
		this.id = id;
		this.playerId = playerId;
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
