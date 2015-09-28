package server.node.system.friend;

import gamecore.serialize.SerializerJson;
import gamecore.system.AbstractSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import server.node.dao.DaoFactory;
import server.node.dao.FriendDao;
import server.node.system.RedisHelperJson;
import server.node.system.Root;
import server.node.system.account.Account;
import server.node.system.player.Player;

/**
 * 好友系统。
 */
public final class FriendSystem extends AbstractSystem {

	public FriendSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("FriendSystem start....");

		System.out.println("FriendSystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {
	}

	//获取FriendBag
	public FriendBag getFriendBag(Player player) {
		FriendBag friendBag = RedisHelperJson.getFriendBag(player.getId());
		if (friendBag == null) {
			friendBag = readFriendBagFromDB(player);
			friendBag.synchronize();
		}
		return friendBag;
	}

	//数据库读取FriendBag
	private FriendBag readFriendBagFromDB(Player player) {

		FriendDao friendDao = DaoFactory.getInstance().borrowFriendDao();
		Map<String, Object> map = friendDao.readFriendBagPO(player);
		DaoFactory.getInstance().returnFriendDao(friendDao);

		if (map != null) {
			String json = (String) map.get("friends");
			List<Long> playerIds = (List<Long>) SerializerJson.deSerializeList(json, Long.class);
			FriendBag friendBag = new FriendBag(player.getId(), playerIds);
			return friendBag;
		} else {
			FriendBag friendBag = initFriendBag(player);
			return friendBag;
		}

	}

	private FriendBag initFriendBag(Player player) {
		FriendBag friendBag = new FriendBag(player.getId(), new ArrayList<Long>());
		saveDB(player, friendBag);
		return friendBag;
	}

	//更新FriendBag
	public FriendBag syncFriendBag(Player player, String plat, List<String> friendIdInPlats) {
		FriendBag friendBag = getFriendBag(player);

		List<Long> friendIds = new ArrayList<Long>();

		for (String id : friendIdInPlats) {
			Account account = Root.accountSystem.getAccountFromDB(plat, id);
			if (account != null && account.getPlayerId() != null) {
				friendIds.add(account.getPlayerId());
			}
		}

		friendBag.setPlayerIds(friendIds);

		friendBag.synchronize();

		updateDB(player, friendBag);

		//发送同步好友的消息
		FriendMessage friendMessage = new FriendMessage(FriendMessage.SYNC_FRIENDS, player, friendBag);
		this.publish(friendMessage);

		return friendBag;
	}

	private void saveDB(Player player, FriendBag friendBag) {
		FriendDao friendDao = DaoFactory.getInstance().borrowFriendDao();
		friendDao.saveFriendBag(player, friendBag);
		DaoFactory.getInstance().returnFriendDao(friendDao);
	}

	private void updateDB(Player player, FriendBag friendBag) {
		FriendDao friendDao = DaoFactory.getInstance().borrowFriendDao();
		friendDao.updateFriendBag(player, friendBag);
		DaoFactory.getInstance().returnFriendDao(friendDao);
	}
}
