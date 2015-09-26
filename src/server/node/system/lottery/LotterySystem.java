package server.node.system.lottery;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.system.AbstractSystem;
import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;
import gamecore.util.BaseWeightPool;
import gamecore.util.Clock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Root;
import server.node.system.player.GoldType;
import server.node.system.player.Player;
import server.node.system.robotPart.Part;
import server.node.system.robotPart.PartMaking;
import server.node.system.robotPart.PartQualityType;
import server.node.system.robotPart.PartSlotType;

/** 
 * 抽奖系统。
 */
public final class LotterySystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(LotterySystem.class.getName());

	//抽奖钻石消耗
	public static Integer ONE = 1;//一次
	public static Integer TEN = 10; //十连抽

	public Map<PartSlotType, Map<Integer, Integer>> goldCost;

	public LotterySystem() {
	}

	@SuppressWarnings("serial")
	@Override
	public boolean startup() {
		System.out.println("LotterySystem start....");

		goldCost = new HashMap<PartSlotType, Map<Integer, Integer>>();
		goldCost.put(PartSlotType.HEAD, new HashMap<Integer, Integer>() {
			{
				put(ONE, 500);
				put(TEN, 4500);
			}
		});
		goldCost.put(PartSlotType.BODY, new HashMap<Integer, Integer>() {
			{
				put(ONE, 500);
				put(TEN, 4500);
			}
		});
		goldCost.put(PartSlotType.ARM, new HashMap<Integer, Integer>() {
			{
				put(ONE, 500);
				put(TEN, 4500);
			}
		});
		goldCost.put(PartSlotType.LEG, new HashMap<Integer, Integer>() {
			{
				put(ONE, 500);
				put(TEN, 4500);
			}
		});
		goldCost.put(PartSlotType.WEAPON, new HashMap<Integer, Integer>() {
			{
				put(ONE, 700);
				put(TEN, 6500);
			}
		});

		boolean b = LotteryLoadData.getInstance().readData();

		System.out.println("LotterySystem start....OK");
		return b;
	}

	@Override
	public void shutdown() {
	}

	public Map<PartSlotType, Map<Integer, Integer>> getGoldCost() {
		return goldCost;
	}

	public LotteryBag getLotteryBag(Player player) {
		LotteryBag lotteryBag = RedisHelperJson.getLotteryBag(player.getId());
		if (lotteryBag == null) {
			lotteryBag = initLotteryBag(player, true);
		}
		return lotteryBag;
	}

	public LotteryBag initLotteryBag(Player player, boolean sync) {
		LotteryBag lotteryBag = new LotteryBag(player.getId());
		if (sync) {
			lotteryBag.synchronize();
		}
		return lotteryBag;
	}

	/**
	 * 抽奖
	 *  type //10：金币抽奖（免费），0：钻石抽头，1钻石身体，2钻石抽胳膊，3钻石抽腿，4钻石抽武器
	 * @throws SQLException 
	 */
	public SystemResult raffle(Player player, int type, int cost) throws SQLException {
		SystemResult result = new SystemResult();

		List<PartMaking> lotteryParts = new ArrayList<PartMaking>();

		ArrayList<Part> parts = new ArrayList<Part>();

		if (type == 10 && cost == 0) {//免费抽奖
			//条件,确认可以免费抽奖
			LotteryBag lotteryBag = getLotteryBag(player);
			if (lotteryBag.readNextFreeTimeLeft() == 0) {
				lotteryParts = raffleFree();
				lotteryBag.setLastFreeT(Clock.currentTimeSecond());
				lotteryBag.synchronize();
				this.publish(new LotteryMessage(LotteryMessage.LOTTERY_FREE, player));

			} else {
				result.setCode(ErrorCode.FREE_LOTTERY_TIME);
				return result;
			}

		} else {//花钻抽奖

			PartSlotType partSlotType = PartSlotType.asEnum(type);

			if (partSlotType != null && cost > 0) {//gold 抽奖
				if (player.getGold() < cost) {//gold 不够
					result.setCode(ErrorCode.GOLD_NOT_ENOUGH);
					return result;
				} else {
					lotteryParts = raffleGold(partSlotType, cost);
				}
			}

		}

		if (lotteryParts != null && !lotteryParts.isEmpty()) {
			for (PartMaking making : lotteryParts) {
				Part part = Root.partSystem.createPart(Root.idsSystem.takePartId(), making.getPartSlotType().asCode(), making.getId(), 1, 0, 0);
				parts.add(part);
			}

			Root.partSystem.addParts(player, parts, true);

			LotteryMessage lotteryMessage = new LotteryMessage(LotteryMessage.LOTTERY_PARTS, player, parts);
			this.publish(lotteryMessage);

		}

		if (cost > 0) {//gold
			Root.playerSystem.changeGold(player, -cost, GoldType.LOTTERY, true);
		}

		result.setBindle(parts);

		return result;
	}

	//free lottery
	private List<PartMaking> raffleFree() {

		List<PartMaking> parts = new ArrayList<PartMaking>();

		BaseWeightPool lotteryCashPool = LotteryLoadData.getInstance().getLotteryFreePool();

		PartMaking partMaking = (PartMaking) lotteryCashPool.getValue();

		parts.add(partMaking);

		return parts;
	}

	/**
	 * gold lottery 钻石抽奖,
	 * 十连抽必出一个金以上的部件
	 */
	private List<PartMaking> raffleGold(PartSlotType partSlotType, int cost) {

		List<PartMaking> parts = new ArrayList<PartMaking>();

		BaseWeightPool lotteryGoldPool = LotteryLoadData.getInstance().getLotteryGoldPool(partSlotType);

		if (cost == goldCost.get(partSlotType).get(ONE)) {
			PartMaking partMaking = (PartMaking) lotteryGoldPool.getValue();
			parts.add(partMaking);
		}

		if (cost == goldCost.get(partSlotType).get(TEN)) {

			boolean allSliver = true;//全银,没有金钛

			while (parts.size() < 10) {

				PartMaking partMaking = (PartMaking) lotteryGoldPool.getValue();
				if (allSliver) {
					if (partMaking.getPartQualityType() != PartQualityType.SILVER) {//这个不是银的,就是金钛
						allSliver = false;
					}
					if (allSliver && parts.size() == 9) {//第九个了,还是全银...
						partMaking = null;//这个不要了,重新来一次
					}
				}
				if (partMaking != null) {
					parts.add(partMaking);
				}
			}

		}

		return parts;
	}

}
