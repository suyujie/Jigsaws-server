package server.node.system.friend;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gamecore.entity.AbstractEntity;
import gamecore.io.ByteArrayGameOutput;
import gamecore.serialize.SerializerJson;
import server.node.system.Root;
import server.node.system.player.Player;

/**
 * 好友背包
 */
public class FriendBag extends AbstractEntity {

	private static Logger logger = LogManager.getLogger(FriendBag.class.getName());

	private static final long serialVersionUID = 3561068634267201050L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "friend_bag_";

	private List<Long> playerIds;

	public FriendBag() {
	}

	public FriendBag(Long playerId) {
		super(generateCacheKey(playerId));
	}

	public FriendBag(Long playerId, List<Long> playerIds) {
		super(generateCacheKey(playerId));
		this.playerIds = playerIds;
	}

	public List<Long> getPlayerIds() {
		return playerIds;
	}

	public void setPlayerIds(List<Long> playerIds) {
		this.playerIds = playerIds;
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long playerId) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(FriendBag.CKPrefix).append(playerId).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	public String toJson() {
		return SerializerJson.serialize(playerIds);
	}

	public byte[] toByteArray() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			bago.putInt(playerIds.size());
			for (Long playerId : playerIds) {
				Player player = Root.playerSystem.getPlayer(playerId);
				bago.putString(player.getAccount().getIdInPlat());
				bago.putLong(playerId);
				bago.putString(player.getAccount().getNameInPlat());
				bago.putInt(player.getPlayerStatistics().getCupNum());
				bago.putInt(player.getLevel());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return bago.toByteArray();
	}

}
