package server.node.system.robotPart;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.entity.AbstractEntity;
import gamecore.io.ByteArrayGameOutput;
import gamecore.serialize.SerializerJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Content;
import server.node.system.player.Player;

/**
 * 部件包实体。
 */
public class PartBag extends AbstractEntity {

	private static Logger logger = LogManager.getLogger(PartBag.class.getName());

	private static final long serialVersionUID = 8783268910066213103L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "partbag_";

	private HashMap<Long, Part> parts = new HashMap<Long, Part>();

	private List<Long> newParts;//新获得的parts,还没有同步给客户端

	public PartBag() {
	}

	public PartBag(Player player, HashMap<Long, Part> parts) {
		super(PartBag.generateCacheKey(player.getId()));//玩家playerId当作存储键值
		this.parts = parts;
	}

	public HashMap<Long, Part> getParts() {
		return parts;
	}

	public void setParts(HashMap<Long, Part> parts) {
		this.parts = parts;
	}

	/**
	 * 更新 新parts
	 */
	public void addNewParts(long partId, boolean sync) {
		if (newParts == null) {
			newParts = new ArrayList<>();
		}
		newParts.add(partId);
		if (sync) {
			this.synchronize();
		}
	}

	/**
	 * 清除 新parts 信息
	 */
	public void clearNewParts(boolean sync) {
		if (newParts != null) {
			newParts = null;
			if (sync) {
				this.synchronize();
			}
		}
	}

	public void addPart(Part part, boolean sync) {
		if (parts == null) {
			parts = new HashMap<Long, Part>();
		}
		parts.put(part.getStoreId(), part);
		if (sync) {
			this.synchronize();
		}
	}

	public void removePart(Long partStoreId, boolean sync) {
		if (parts != null) {
			this.parts.remove(partStoreId);
			if (sync) {
				this.synchronize();
			}
		}
	}

	public Part takePart(Long partStoreId, boolean sync) {
		if (parts != null) {
			Part part = this.parts.remove(partStoreId);
			if (sync) {
				this.synchronize();
			}
			return part;
		}
		return null;
	}

	public Part getPart(Long storeId) {
		return this.parts.get(storeId);
	}

	/**
	 * 部件列表
	 * @return
	 */
	public List<Part> readAllParts() {
		List<Part> list = new ArrayList<Part>();
		for (Part part : this.parts.values()) {
			if (part != null) {
				list.add(part);
			}
		}
		return list;
	}

	public List<Long> getNewParts() {
		return newParts;
	}

	public void setNewParts(List<Long> newParts) {
		this.newParts = newParts;
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(PartBag.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	public byte[] toByteArray() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			//循环发送组件信息
			if (parts != null) {
				for (Part part : parts.values()) {
					bago.putBytesNoLength(part.toByteArray());
				}
			}

		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}

	public String toStorgeJson() {
		return SerializerJson.serialize(parts);
	}

}
