package server.node.system.jigsaw;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import gamecore.entity.AbstractEntity;

/**
 * 已经玩过的图片列表
 */
public class PlayedJigsawBag extends AbstractEntity {

	private static final long serialVersionUID = 6536927576173373278L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "played_ids_";

	private List<Long> playedIds = new ArrayList<Long>();

	private int num = 10;

	public PlayedJigsawBag() {
	}

	public PlayedJigsawBag(long playerId) {
		super(PlayedJigsawBag.generateCacheKey(playerId));
	}

	// 取出all
	public List<Long> readPlayedIds() {
		List<Long> result = new ArrayList<Long>();
		synchronized (this) {
			if (playedIds == null || playedIds.isEmpty()) {
				return null;
			} else {
				ListIterator<Long> listIterator = playedIds.listIterator();
				while (listIterator.hasNext()) {
					result.add(listIterator.next());
				}
			}
		}
		return result;
	}

	// 是否包含,true不包含 false包含
	public boolean repeat(Long id) {
		if (playedIds == null || playedIds.isEmpty()) {
			return false;
		} else {
			return playedIds.contains(id);
		}
	}

	// 增加一个
	public boolean addJigsawId(Long id) {
		if (playedIds.contains(id)) {
			return false;
		} else {
			playedIds.add(id);
		}
		if (playedIds.size() > num) {
			playedIds.remove(0);
		}
		return true;
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(long playerId) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(PlayedJigsawBag.CKPrefix).append(playerId).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

}
