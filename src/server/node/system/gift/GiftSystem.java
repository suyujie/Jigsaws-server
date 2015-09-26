package server.node.system.gift;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.serialize.SerializerJson;
import gamecore.system.AbstractSystem;
import gamecore.system.SystemResult;
import gamecore.util.Clock;
import gamecore.util.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.TypeReference;

import server.node.dao.GiftDao;
import server.node.system.Content;
import server.node.system.Root;
import server.node.system.gift.gift.GiftWear;
import server.node.system.player.Player;

/**
 * 关卡系统。
 */
public final class GiftSystem extends AbstractSystem {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger(GiftSystem.class.getName());

	public GiftSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("GiftSystem start....");

		System.out.println("GiftSystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {
	}

	//获取GiftBag
	public GiftBag getGiftBag(Player player) throws SQLException {
		GiftBag giftBag = RedisHelperJson.getGiftBag(player.getId());
		if (giftBag == null) {
			giftBag = readGiftBagFromDB(player);
			giftBag.synchronize();
		}
		return giftBag;
	}

	public List<GiftWear> getGifts(Player player) throws SQLException {
		GiftBag giftBag = getGiftBag(player);

		List<GiftWear> gifts = new ArrayList<GiftWear>();

		for (GiftWear gift : giftBag.getGiftWears().values()) {
			if (gift.status == GiftStatus.Wait) {//没有接收的礼物
				gifts.add(gift);
			}
		}
		return gifts;

	}

	//数据库读取GiftBag
	private GiftBag readGiftBagFromDB(Player player) throws SQLException {

		GiftDao giftDao = DaoFactory.getInstance().borrowGiftDao();
		Map<String, Object> map = giftDao.readGiftBagPO(player);
		DaoFactory.getInstance().returnGiftDao(giftDao);

		if (map != null) {
			String json = (String) map.get("gifts");
			Map<Long, GiftWear> giftWears = (Map<Long, GiftWear>) SerializerJson.deSerializeMap(json, new TypeReference<HashMap<Long, GiftWear>>() {
			});
			GiftBag giftBag = new GiftBag(player.getId(), giftWears);
			return giftBag;
		} else {
			GiftBag giftBag = initGiftBag(player);
			return giftBag;
		}

	}

	private GiftBag initGiftBag(Player player) {
		GiftBag giftBag = new GiftBag(player.getId(), new HashMap<Long, GiftWear>());
		GiftDao giftDao = DaoFactory.getInstance().borrowGiftDao();
		giftDao.saveGiftBag(player, giftBag);
		DaoFactory.getInstance().returnGiftDao(giftDao);
		return giftBag;
	}

	//可否送耐久
	public boolean canGiveWear(Player giver, Player accepter, GiftBag accepterGiftBag, boolean checkStatus) throws SQLException {
		if (accepterGiftBag == null) {
			accepterGiftBag = getGiftBag(accepter);//接收者的giftbag
		}

		boolean canGive = true;

		if (accepterGiftBag.getGiftWears() != null && accepterGiftBag.getGiftWears().isEmpty() == false) {
			for (GiftWear gift : accepterGiftBag.getGiftWears().values()) {
				//耐久
				if (gift instanceof GiftWear) {
					//只检查  时间是否允许
					if (gift.getGiverId().longValue() == giver.getId().longValue() && Clock.currentTimeSecond() - gift.getGiveTime() < Content.giftTimePeriod) {//之前赠送过,而且还在时间限定内
						canGive = false;
						break;
					}

					//同时检查,是否还没有领,,,,这种情况下也不送了
					if (checkStatus) {
						if (gift.getGiverId().longValue() == giver.getId().longValue() && gift.status == GiftStatus.Wait) {
							canGive = false;
							break;
						}
					}
				}
			}
		}

		return canGive;
	}

	//送耐久
	public SystemResult giveWear(Player giver, Long accepterId, GiftBag accepterGiftBag) throws SQLException {
		Player accepter = Root.playerSystem.getPlayer(accepterId);
		SystemResult result = new SystemResult();
		if (accepterGiftBag == null) {
			accepterGiftBag = getGiftBag(accepter);//接收者的giftbag
		}
		if (canGiveWear(giver, accepter, accepterGiftBag, true)) {
			GiftWear giftWear = new GiftWear(Utils.getOneLongId(), giver.getId(), Content.wearAsGiftNum, Clock.currentTimeSecond(), GiftStatus.Wait);
			accepterGiftBag.addGiftWear(giftWear, true);

			GiftDao giftDao = DaoFactory.getInstance().borrowGiftDao();
			giftDao.updateGiftBag(accepter, accepterGiftBag);
			DaoFactory.getInstance().returnGiftDao(giftDao);
		}

		GiftMessage giftMessage = new GiftMessage(GiftMessage.SEND_GIFT, giver);
		this.publish(giftMessage);
		//如果不能送,不管
		return result;
	}

	//接收礼物
	public SystemResult acceptGift(Player player, Long giftId) throws SQLException {
		GiftBag giftBag = getGiftBag(player);

		Iterator<GiftWear> iter = giftBag.getGiftWears().values().iterator();
		while (iter.hasNext()) {
			//过滤一遍
			GiftWear gift = iter.next();
			//已经接收了,并且已经过了间隔时间,这样的就该删掉了
			if (gift.status == GiftStatus.Accept && Clock.currentTimeSecond() - gift.getGiveTime() > Content.giftTimePeriod) {
				iter.remove();
			}
		}

		if (giftId == null) {
			return acceptAll(player, giftBag);
		} else {
			return acceptOne(player, giftBag, giftId);
		}
	}

	//接受礼物
	private SystemResult acceptAll(Player player, GiftBag giftBag) {
		SystemResult result = new SystemResult();

		for (GiftWear gift : giftBag.getGiftWears().values()) {
			if (gift != null) {
				acceptGift(player, gift);
			}
		}

		giftBag.synchronize();

		updateDB(player, giftBag);

		return result;
	}

	//接受礼物
	private SystemResult acceptOne(Player player, GiftBag giftBag, Long giftId) {
		SystemResult result = new SystemResult();
		GiftWear gift = giftBag.getGiftWears().get(giftId);
		if (gift != null) {
			acceptGift(player, gift);
		}
		giftBag.synchronize();

		updateDB(player, giftBag);

		return result;
	}

	private void acceptGift(Player player, GiftWear gift) {
		GiftWear giftWear = (GiftWear) gift;
		giftWear.accept(player);
	}

	private void updateDB(Player player, GiftBag giftBag) {
		GiftDao giftDao = DaoFactory.getInstance().borrowGiftDao();
		giftDao.updateGiftBag(player, giftBag);
		DaoFactory.getInstance().returnGiftDao(giftDao);
	}

}
