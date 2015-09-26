package server.node.system.blackMarket;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.system.AbstractSystem;
import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;
import gamecore.util.Clock;
import gamecore.util.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Root;
import server.node.system.berg.BergLoadData;
import server.node.system.berg.BergType;
import server.node.system.expPart.ExpPartLoadData;
import server.node.system.expPart.ExpPartMaking;
import server.node.system.gameEvents.bergWheel.BergWheel;
import server.node.system.gameEvents.chipDeathWheel.SelectChipsInDeathWheel;
import server.node.system.player.CashType;
import server.node.system.player.GoldType;
import server.node.system.player.Player;
import server.node.system.robot.RobotBag;
import server.node.system.robotPart.Part;
import server.node.system.robotPart.PartLoadData;
import server.node.system.robotPart.PartMaking;
import server.node.system.robotPart.PartQualityType;
import server.node.system.robotPart.PartSlotType;

/**
 * 黑市商店系统
 */
public final class BlackMarketSystem extends AbstractSystem {

	private static final Logger logger = LogManager.getLogger(BlackMarketSystem.class);

	public static final int openLevel = 15;//开放等级

	public static int EnableTime = 3;//黑市商店每次开启3小时
	public static int part_num = 5;
	public static int chip_num = 5;
	public static int berg_num = 3;
	public static int exp_num = 5;

	public BlackMarketSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("BlackMarketSystem start....");
		System.out.println("BlackMarketSystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {
	}

	public BlackMarket getBlackMarket(Player player, boolean checkTimeOut, boolean createIfNull) {

		BlackMarket blackMarket = RedisHelperJson.getBlackMarket(player.getId());

		if (blackMarket != null && checkTimeOut && blackMarket.checkTimeOut()) {// 有,需要验证超时,并且超时了
			blackMarket = null;
		}

		if (blackMarket == null && createIfNull) {
			blackMarket = initBlackMarket(player);
		}

		return blackMarket;
	}

	private BlackMarket initBlackMarket(Player player) {
		BlackMarket blackMarket = new BlackMarket(player, Clock.currentTimeSecond());
		createBlackItems(player, blackMarket);
		blackMarket.synchronize();
		return blackMarket;
	}

	private BlackItem createBlackItemOnePart(PartMaking partMaking, int num) {
		BlackItem item = new BlackItem(Utils.getOneLongId(), false, BlackItemType.PART, 250, new StringBuffer("part").append("_").append(partMaking.getPartSlotType().asDesc())
				.append("_").append(partMaking.getId()).toString(), num);
		return item;
	}

	private void createBlackItemPart(Player player, BlackMarket blackMarket) {

		//进化度最高的一个，如果已有钛材质的不推荐
		RobotBag robotBag = Root.robotSystem.getRobotBag(player);
		PartMaking partMaking1 = robotBag.readMostEvolutionPart();
		if (partMaking1 != null) {
			//这是进化度最高的,找它的铜制部件
			partMaking1 = PartLoadData.getInstance().getMaking(partMaking1.getSuitName(), PartQualityType.COPPER, partMaking1.getPartSlotType());
		} else {//如果已有钛材质的不推荐
			partMaking1 = robotBag.readRandomPart(null);
		}

		//没有符合的，比如，玩家全身钛，那就全部件随机
		if (partMaking1 == null) {
			blackMarket.addItem(createBlackItemOnePart(PartLoadData.getInstance().getMaking(null, PartQualityType.COPPER, null), part_num), false);
		} else {
			blackMarket
					.addItem(
							createBlackItemOnePart(PartLoadData.getInstance().getMaking(partMaking1.getSuitName(), PartQualityType.COPPER, partMaking1.getPartSlotType()), part_num),
							false);

		}

		List<PartMaking> makings = new ArrayList<PartMaking>();
		if (partMaking1 != null) {
			makings.add(partMaking1);
		}
		//如果已有钛材质的不推荐
		PartMaking partMaking2 = robotBag.readRandomPart(makings);
		if (partMaking2 == null) {
			blackMarket.addItem(createBlackItemOnePart(PartLoadData.getInstance().getMaking(null, PartQualityType.COPPER, null), part_num), false);
		} else {
			blackMarket
					.addItem(
							createBlackItemOnePart(PartLoadData.getInstance().getMaking(partMaking2.getSuitName(), PartQualityType.COPPER, partMaking2.getPartSlotType()), part_num),
							false);
		}

	}

	private BlackItem createBlackItemExpPart() {
		ExpPartMaking expPartMaking = ExpPartLoadData.getInstance().getMaking(9);
		BlackItem item = new BlackItem(Utils.getOneLongId(), false, BlackItemType.EXP, 180, new StringBuffer("part").append("_").append("exp").append("_")
				.append(expPartMaking.getId()).toString(), exp_num);
		return item;
	}

	private void createBlackItemChip(Player player, BlackMarket blackMarket) {

		List<String> chips = new ArrayList<String>();

		//先按照副本选择的，没有的话，战斗位上的
		SelectChipsInDeathWheel chipsInDeathWheel = RedisHelperJson.getSelectChipsInDeathWheel(player.getId());
		if (chipsInDeathWheel != null && chipsInDeathWheel.getChips() != null && !chipsInDeathWheel.getChips().isEmpty()) {
			chips.addAll(chipsInDeathWheel.readRandChips(2));
		}

		if (chips.size() < 2) {
			RobotBag robotBag = Root.robotSystem.getRobotBag(player);
			chips.addAll((List<String>) Utils.randomSelect(robotBag.readBattleSuits(), 2 - chips.size()));
			if (chips.size() < 2) {
				chips.add(Utils.randomSelectOne(PartLoadData.getInstance().suitNames));
			}
		}

		for (int i = 0; i < chips.size() && i < 2; i++) {
			String chipName = chips.get(i);
			BlackItem item = new BlackItem(Utils.getOneLongId(), false, BlackItemType.CHIP, 70, new StringBuffer("chip").append("_").append(chipName).toString(), chip_num);
			blackMarket.addItem(item, false);
		}
	}

	private void createBlackItemBerg(BlackMarket blackMarket) {
		blackMarket.addItem(
				new BlackItem(Utils.getOneLongId(), false, BlackItemType.BERG, 120, new StringBuffer("berg").append("_")
						.append(BergLoadData.getInstance().getBergMaking(BergType.randCode(), 2).getId()).toString(), berg_num), false);
		blackMarket.addItem(
				new BlackItem(Utils.getOneLongId(), false, BlackItemType.BERG, 120, new StringBuffer("berg").append("_")
						.append(BergLoadData.getInstance().getBergMaking(BergType.randCode(), 2).getId()).toString(), berg_num), false);
	}

	private BlackItem createBlackItemCash(Player player) {

		int cash = Root.missionSystem.gainCashPerHour(player) * 24 * 4 + 1000;//4天产量+1000
		int gold = Root.missionSystem.cash2Gold(player, cash) * 10 / 100;//打1折
		if (gold < 1) {
			gold = 1;
		}

		BlackItem item = new BlackItem(Utils.getOneLongId(), false, BlackItemType.CASH, gold, new StringBuffer("cash").append("_").append(cash).toString(), 1);
		return item;
	}

	private void createBlackItems(Player player, BlackMarket blackMarket) {

		//部件
		createBlackItemPart(player, blackMarket);
		//chip
		createBlackItemChip(player, blackMarket);
		//水晶
		createBlackItemBerg(blackMarket);
		//金币
		blackMarket.addItem(createBlackItemCash(player), false);
		//能量块
		blackMarket.addItem(createBlackItemExpPart(), false);

	}

	public SystemResult buyBlackItem(Player player, long itemId) throws SQLException {

		SystemResult result = new SystemResult(ErrorCode.NO_ERROR);

		BlackMarket blackMarket = getBlackMarket(player, false, false);

		if (blackMarket != null) {

			BlackItem blackItem = blackMarket.getItems().get(itemId);

			if (blackItem != null && blackItem.isBuyed() == false && player.getGold() >= blackItem.getGold()) {

				if (blackItem.getType() == BlackItemType.CASH) {
					//cash_10000
					int cash = Integer.parseInt(blackItem.getItem().split("_")[1]);
					Root.playerSystem.changeCash(player, cash, CashType.BUY_BLACK_ITEM_CASH, true);
					result.setMap("cash", cash);
				}
				if (blackItem.getType() == BlackItemType.CHIP) {
					//chip_cowboy
					String chipName = blackItem.getItem().split("_")[1];
					Root.chipSystem.addChipNum(player, null, chipName, blackItem.getNum());
					result.setMap("chip", chipName);
				}
				if (blackItem.getType() == BlackItemType.BERG) {
					//berg_2
					int bergId = Integer.parseInt(blackItem.getItem().split("_")[1]);
					try {
						Root.bergSystem.addBergNum(player, null, bergId, blackItem.getNum());
					} catch (SQLException e) {
						e.printStackTrace();
					}
					result.setMap("berg", bergId);
				}
				if (blackItem.getType() == BlackItemType.EXP) {
					//part_exp_id
					int expId = Integer.parseInt(blackItem.getItem().split("_")[2]);
					Root.expPartSystem.addExpPart(player, expId, blackItem.getNum());
					result.setMap("exp", expId);
				}
				if (blackItem.getType() == BlackItemType.PART) {
					//part_head_id
					PartSlotType partSlotType = PartSlotType.asEnumByDesc(blackItem.getItem().split("_")[1]);
					int partId = Integer.parseInt(blackItem.getItem().split("_")[2]);
					List<Part> parts = new ArrayList<>();
					for (int i = 0; i < blackItem.getNum(); i++) {
						parts.add(Root.partSystem.createPart(Root.idsSystem.takePartId(), partSlotType.asCode(), partId, 1, 0, 0));
					}
					Root.partSystem.addParts(player, parts, true);
					result.setMap("parts", parts);
				}

				blackItem.setBuyed(true);

				blackMarket.addItem(blackItem, true);

				//花钻
				Root.playerSystem.changeGold(player, -blackItem.getGold(), GoldType.BUY_BLACK_ITEM, true);

			}

		}

		return result;
	}
}
