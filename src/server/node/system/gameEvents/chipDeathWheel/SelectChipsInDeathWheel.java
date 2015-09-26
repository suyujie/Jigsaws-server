package server.node.system.gameEvents.chipDeathWheel;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;
import gamecore.util.Utils;

import java.util.ArrayList;
import java.util.List;

import server.node.system.player.Player;

public final class SelectChipsInDeathWheel extends AbstractEntity {

	private static final long serialVersionUID = 689957704202680064L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "select_chip_";

	private long id;
	private List<String> chips;

	public SelectChipsInDeathWheel() {
	}

	public SelectChipsInDeathWheel(Player player) {
		super(generateCacheKey(player.getId()));
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<String> getChips() {
		return chips;
	}

	public void setChips(List<String> chips) {
		this.chips = chips;
	}

	public List<String> readRandChips(int num) {
		if (chips == null || chips.isEmpty()) {
			return new ArrayList<String>();
		} else {
			return (List<String>) Utils.randomSelect(chips, num);
		}
	}

	public void addRandChips(List<String> newChips, boolean sync) {
		if (newChips == null || newChips.isEmpty()) {
			return;
		}
		if (chips == null) {
			chips = new ArrayList<String>();
		}
		for (String chip : newChips) {
			if (!chips.contains(chip)) {
				chips.add(chip);
			}
		}
		if (sync) {
			this.synchronize();
		}
	}

	/**
	 * 生成存储键。玩家id为key
	 */
	public static String generateCacheKey(Long playerId) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(SelectChipsInDeathWheel.CKPrefix).append(playerId).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	/** 实例数据写入缓存。
	 */
	public void synchronize() {
		synchronized (this) {
			JedisUtilJson.getInstance().setForHour(getCacheKey(), this, 7 * 24);
		}
	}

}
