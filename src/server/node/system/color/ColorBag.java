package server.node.system.color;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;
import gamecore.io.ByteArrayGameOutput;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Content;

/**
 * 颜色包实体。
 */
public class ColorBag extends AbstractEntity {

	private static final Logger logger = LogManager.getLogger(ColorBag.class.getName());

	private static final long serialVersionUID = -8032368084805647131L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "colorbag_";

	//key 颜色code,value:num
	private HashMap<Integer, Integer> colors = new HashMap<Integer, Integer>();

	public ColorBag() {
	}

	public ColorBag(Long playerId, HashMap<Integer, Integer> colors) {
		super(ColorBag.generateCacheKey(playerId));//玩家playerId当作存储键值
		this.colors = colors;
	}

	public HashMap<Integer, Integer> getColors() {
		return colors;
	}

	public void setColors(HashMap<Integer, Integer> colors) {
		this.colors = colors;
	}

	public void addColor(Integer colorKey, int num, boolean sync) {
		if (colors.get(colorKey) == null) {
			colors.put(colorKey, num);
		} else {
			colors.put(colorKey, colors.get(colorKey) + num);
		}
		if (sync) {
			this.synchronize();
		}
	}

	/**
	 * 消耗num个某种颜色的涂装瓶
	 */
	public boolean removeColor(Integer colorKey, int num, boolean sync) {

		if (colors.get(colorKey) >= num) {
			colors.put(colorKey, colors.get(colorKey) - num);
			if (sync) {
				this.synchronize();
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 消耗全部某种颜色的涂装瓶
	 */
	public boolean removeColor(Integer colorKey, boolean sync) {
		colors.put(colorKey, 0);
		if (sync) {
			this.synchronize();
		}
		return true;
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(ColorBag.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	public String toStrArray() {
		StringBuffer sb = new StringBuffer();
		//涂装瓶的颜色数量
		List<Integer> colorIds = ColorLoadData.getInstance().colorIds;

		for (Integer colorId : colorIds) {
			sb.append(colors.get(colorId) == null ? 0 : colors.get(colorId)).append("-");
		}

		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}

	public byte[] toByteArray() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			//涂装瓶的颜色数量
			List<Integer> colorIds = ColorLoadData.getInstance().colorIds;

			bago.putShort((short) colorIds.size());
			for (Integer colorId : colorIds) {
				bago.putInt(colors.get(colorId) == null ? 0 : colors.get(colorId));
			}

		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}

}
