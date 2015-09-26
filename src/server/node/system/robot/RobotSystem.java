package server.node.system.robot;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.serialize.SerializerJson;
import gamecore.system.AbstractSystem;
import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;
import gamecore.util.Clock;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.dao.RobotDao;
import server.node.dao.RobotSlotDao;
import server.node.system.Content;
import server.node.system.Root;
import server.node.system.berg.BergBag;
import server.node.system.chip.ChipBag;
import server.node.system.color.ColorBag;
import server.node.system.expPart.ExpPartBag;
import server.node.system.expPart.ExpPartLoadData;
import server.node.system.expPart.ExpPartMaking;
import server.node.system.gamePrice.GamePriceLoadData;
import server.node.system.player.CashType;
import server.node.system.player.GoldType;
import server.node.system.player.Player;
import server.node.system.robotPart.Part;
import server.node.system.robotPart.PartBag;
import server.node.system.robotPart.PartLoadData;
import server.node.system.robotPart.PartMaking;
import server.node.system.robotPart.PartSlotType;
import server.node.system.robotPart.QualityLoadData;
import server.node.system.robotPart.QualityMaking;
import server.node.system.robotPart.RarityUpgradeLoadData;
import server.node.system.robotPart.RarityUpgradeMaking;

import com.alibaba.fastjson.TypeReference;

/**
 * robot系统。
 */
public final class RobotSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(RobotSystem.class.getName());

	public RobotSystem() {

	}

	@Override
	public boolean startup() {
		System.out.println("RobotSystem start....");
		// 读取制作数据
		boolean b = LevelRobotData.getInstance().readData();
		b = b & UpdateMoneyLoadData.getInstance().readData();

		System.out.println("RobotSystem start....OK");
		return b;
	}

	@Override
	public void shutdown() {
	}

	//读取robotbag
	public RobotBag getRobotBag(long playerId) throws SQLException {
		return getRobotBag(Root.playerSystem.getPlayer(playerId));
	}

	//读取robotbag
	public RobotBag getRobotBag(Player player) {
		RobotBag robotBag = RedisHelperJson.getRobotBag(player.getId());
		if (robotBag == null) {
			robotBag = (RobotBag) getRobotBagFromDB(player);
		}
		return robotBag;
	}

	//从数据库中读取Robot
	public RobotBag getRobotBagFromDB(Player player) {

		if (player == null) {
			return null;
		}

		RobotBag robotBag = new RobotBag(player.getId());

		RobotSlotDao robotSlotDao = DaoFactory.getInstance().borrowRobotSlotDao();
		List<Map<String, Object>> robotSlots = robotSlotDao.readRobotSlots(player);
		DaoFactory.getInstance().returnRobotSlotDao(robotSlotDao);

		RobotDao robotDao = DaoFactory.getInstance().borrowRobotDao();
		List<Map<String, Object>> robots = robotDao.readRobots(player);
		DaoFactory.getInstance().returnRobotDao(robotDao);

		if (robotSlots != null && !robotSlots.isEmpty()) {
			for (Map<String, Object> map : robotSlots) {
				int slot = ((Long) map.get("slot")).intValue();
				int wear = ((Long) map.get("wear")).intValue();
				long repairBeginTime = ((BigInteger) map.get("repair_b_t")).longValue();
				long repairEndTime = ((BigInteger) map.get("repair_e_t")).longValue();

				RobotSlot robotSlot = new RobotSlot(slot, wear, repairBeginTime, repairEndTime);

				robotBag.putRobotSlot(robotSlot, false);
			}
		} else {//初始化robotSlot 3 个
			for (int i = 0; i <= 2; i++) {
				createRobotSlot(player, robotBag, i);
			}
		}

		if (robots != null && !robots.isEmpty()) {
			for (Map<String, Object> map : robots) {
				try {

					Long id = ((BigInteger) map.get("id")).longValue();
					int bag = ((Long) map.get("bag")).intValue();
					int slot = ((Long) map.get("slot")).intValue();
					String partsJson = (String) map.get("parts");
					HashMap<Integer, Part> parts = (HashMap<Integer, Part>) SerializerJson.deSerializeMap(partsJson, new TypeReference<HashMap<Integer, Part>>() {
					});
					String bergsJson = (String) map.get("bergs");
					HashMap<Integer, Integer> bergs = (HashMap<Integer, Integer>) SerializerJson.deSerializeMap(bergsJson, new TypeReference<HashMap<Integer, Integer>>() {
					});

					//构造机器人实体
					Robot robot = new Robot(id, RobotType.asEnum(bag), slot, parts, bergs);

					robotBag.putRobot(robot, false);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		robotBag.synchronize();

		return robotBag;

	}

	/**
	 * 保存robot
	 * @param player
	 * @param robot
	 */
	private void saveRobotDB(Player player, Robot robot) {
		RobotDao robotDao = DaoFactory.getInstance().borrowRobotDao();
		robotDao.save(player, robot, robot.refreshFightProperty().getScore());
		DaoFactory.getInstance().returnRobotDao(robotDao);
	}

	private void updateRobotDB(Player player, Robot robot) {
		RobotDao robotDao = DaoFactory.getInstance().borrowRobotDao();
		robotDao.updateRobot(player, robot, robot.refreshFightProperty().getScore());
		DaoFactory.getInstance().returnRobotDao(robotDao);
	}

	private void saveRobotSlotDB(Player player, RobotSlot robotSlot) {
		RobotSlotDao robotSlotDao = DaoFactory.getInstance().borrowRobotSlotDao();
		robotSlotDao.save(player, robotSlot);
		DaoFactory.getInstance().returnRobotSlotDao(robotSlotDao);
	}

	private void updateRobotSlotDB(Player player, RobotSlot robotSlot) {
		RobotSlotDao robotSlotDao = DaoFactory.getInstance().borrowRobotSlotDao();
		robotSlotDao.updateRobot(player, robotSlot);
		DaoFactory.getInstance().returnRobotSlotDao(robotSlotDao);
	}

	//创建robotSlot
	public void createRobotSlot(Player player, RobotBag robotBag, Integer slot) {
		RobotSlot robotSlot = new RobotSlot(slot, Content.maxWear, 0, 0);
		//缓存
		robotBag.putRobotSlot(robotSlot, true);
		//插入数据库
		saveRobotSlotDB(player, robotSlot);
	}

	//创建robot
	public void createRobot(Player player, RobotBag robotBag, RobotType type, Integer slot, HashMap<Integer, Part> parts) {
		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}
		//构造机器人实体
		Robot robot = new Robot(Root.idsSystem.takeRobotId(), type, slot, parts, new HashMap<Integer, Integer>());

		//加入机器人包,同时 同步缓存,同步robot,同步robotbag
		robotBag.putRobot(robot, false);
		robotBag.putNewRobot(slot, false);
		robotBag.synchronize();

		saveRobotDB(player, robot);

		RobotMessage robotMessage = new RobotMessage(RobotMessage.ROBOT_GET_NEW, player, robot);
		this.publish(robotMessage);

	}

	//根据等级,加入机器人,只会在机器人战斗包里添加
	public void createRobotByLevel(Player player, RobotBag robotBag) {

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}

		int haveNum = robotBag.readRobotNum(RobotType.BATTLE);

		LevelRobotMaking levelRobotMaking = LevelRobotData.getInstance().getMaking(player.getLevel(), haveNum);

		if (levelRobotMaking != null) {
			HashMap<Integer, Part> partMap = new HashMap<>();

			PartLevelBean partLevel = null;
			Part part = null;

			partLevel = levelRobotMaking.getParts().get(PartSlotType.HEAD.asCode());
			part = Root.partSystem.createPart(Root.idsSystem.takePartId(), PartSlotType.HEAD.asCode(), partLevel.getId(), partLevel.getLevel(), 0, 0);
			partMap.put(PartSlotType.HEAD.asCode(), part);

			partLevel = levelRobotMaking.getParts().get(PartSlotType.BODY.asCode());
			part = Root.partSystem.createPart(Root.idsSystem.takePartId(), PartSlotType.BODY.asCode(), partLevel.getId(), partLevel.getLevel(), 0, 0);
			partMap.put(PartSlotType.BODY.asCode(), part);

			partLevel = levelRobotMaking.getParts().get(PartSlotType.ARM.asCode());
			part = Root.partSystem.createPart(Root.idsSystem.takePartId(), PartSlotType.ARM.asCode(), partLevel.getId(), partLevel.getLevel(), 0, 0);
			partMap.put(PartSlotType.ARM.asCode(), part);

			partLevel = levelRobotMaking.getParts().get(PartSlotType.LEG.asCode());
			part = Root.partSystem.createPart(Root.idsSystem.takePartId(), PartSlotType.LEG.asCode(), partLevel.getId(), partLevel.getLevel(), 0, 0);
			partMap.put(PartSlotType.LEG.asCode(), part);

			partLevel = levelRobotMaking.getParts().get(PartSlotType.WEAPON.asCode());
			part = Root.partSystem.createPart(Root.idsSystem.takePartId(), PartSlotType.WEAPON.asCode(), partLevel.getId(), partLevel.getLevel(), 0, 0);
			partMap.put(PartSlotType.WEAPON.asCode(), part);

			//目前拥有的数量正好是获得的新机器人的位置
			createRobot(player, robotBag, RobotType.BATTLE, haveNum, partMap);

			if (robotBag.getRobotSlots().get(haveNum) == null) {
				createRobotSlot(player, robotBag, haveNum);
			}
		}

	}

	//给一个仓库机器人
	public void createDefaultRobotInStorage(Player player, RobotBag robotBag) {

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}

		LevelRobotMaking making = LevelRobotData.getInstance().getMakingInStorage();

		if (making != null) {
			HashMap<Integer, Part> partMap = new HashMap<>();

			PartLevelBean partLevel = null;
			Part part = null;

			partLevel = making.getParts().get(PartSlotType.HEAD.asCode());
			part = Root.partSystem.createPart(Root.idsSystem.takePartId(), PartSlotType.HEAD.asCode(), partLevel.getId(), partLevel.getLevel(), 0, 0);
			partMap.put(PartSlotType.HEAD.asCode(), part);

			partLevel = making.getParts().get(PartSlotType.BODY.asCode());
			part = Root.partSystem.createPart(Root.idsSystem.takePartId(), PartSlotType.BODY.asCode(), partLevel.getId(), partLevel.getLevel(), 0, 0);
			partMap.put(PartSlotType.BODY.asCode(), part);

			partLevel = making.getParts().get(PartSlotType.ARM.asCode());
			part = Root.partSystem.createPart(Root.idsSystem.takePartId(), PartSlotType.ARM.asCode(), partLevel.getId(), partLevel.getLevel(), 0, 0);
			partMap.put(PartSlotType.ARM.asCode(), part);

			partLevel = making.getParts().get(PartSlotType.LEG.asCode());
			part = Root.partSystem.createPart(Root.idsSystem.takePartId(), PartSlotType.LEG.asCode(), partLevel.getId(), partLevel.getLevel(), 0, 0);
			partMap.put(PartSlotType.LEG.asCode(), part);

			partLevel = making.getParts().get(PartSlotType.WEAPON.asCode());
			part = Root.partSystem.createPart(Root.idsSystem.takePartId(), PartSlotType.WEAPON.asCode(), partLevel.getId(), partLevel.getLevel(), 0, 0);
			partMap.put(PartSlotType.WEAPON.asCode(), part);

			//目前拥有的数量正好是获得的新机器人的位置
			createRobot(player, robotBag, RobotType.STORAGE, 0, partMap);

		}

	}

	/**
	 * 换装
	 */
	public SystemResult changePart(Player player, RobotBag robotBag, RobotType robotType, int robotSlot, long partStoreId, int partType) throws SQLException {
		SystemResult result = new SystemResult(ErrorCode.NO_ERROR);

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}

		Robot robot = robotBag.readRobot(robotType, robotSlot);

		PartBag partBag = Root.partSystem.getPartBag(player);
		Part newPart = partBag.takePart(partStoreId, false);

		if (newPart == null) {
			result.setCode(ErrorCode.CHANGE_PART_NO_PART);
			return result;
		}

		if (robot == null) {//创建新的机器人
			robot = new Robot(Root.idsSystem.takeRobotId(), robotType, robotSlot, new HashMap<Integer, Part>(), new HashMap<Integer, Integer>());
			robot.putPart(newPart);
			robotBag.putRobot(robot, true);
			partBag.synchronize();
			saveRobotDB(player, robot);
		} else {

			Part oldPart = robot.readPart(partType);

			if (newPart.getPartSlotType() == partType) {
				robot.putPart(newPart);
				if (oldPart != null) {
					partBag.addPart(oldPart, true);
					Root.partSystem.updatePartBag(player, partBag);
				} else {
					partBag.synchronize();
					Root.partSystem.updatePartBag(player, partBag);
				}
				robotBag.putRobot(robot, true);
				updateRobotDB(player, robot);
			} else {
				partBag.addPart(newPart, true);
				result.setCode(ErrorCode.CHANGE_PART_ERROR);
				return result;
			}

		}

		this.publish(new RobotMessage(RobotMessage.ROBOT_CHANGE_PART, player, robot));

		result.setBindle(robot.refreshFightProperty());
		return result;

	}

	/**
	 * 镶嵌水晶
	 */
	public SystemResult changeBerg(Player player, RobotBag robotBag, RobotType robotType, int robotSlot, Integer bergSlot, Integer bergId) throws SQLException {
		SystemResult result = new SystemResult(ErrorCode.NO_ERROR);

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}

		Robot robot = robotBag.readRobot(robotType, robotSlot);
		if (robot == null) {//robot没找到,直接返回了
			logger.error("no this robot");
			result.setCode(ErrorCode.PARAM_ERROR);
			return result;
		} else {

			BergBag bergBag = Root.bergSystem.getBergBag(player);
			boolean haveNew = bergBag.removeBerg(bergId, 1, false);

			if (haveNew) {//成功从bergbag中取出一个
				//机器人身上的老的水晶
				if (robot.getBergIds() != null && !robot.getBergIds().isEmpty()) {
					Integer oldBerg = robot.getBergIds().get(bergSlot);
					if (oldBerg != null) {
						bergBag.addBerg(oldBerg, 1, false);
					}
				}

				bergBag.synchronize();
				Root.bergSystem.updateBergBag(player, bergBag);

				//新水晶嵌入robot
				robot.putBerg(bergSlot, bergId);
				robotBag.putRobot(robot, true);
				updateRobotDB(player, robot);

			} else {//水晶不够一个
				logger.error("no this new berg");
				result.setCode(ErrorCode.PARAM_ERROR);
				return result;
			}

		}

		result.setBindle(robot.refreshFightProperty());
		return result;

	}

	/**
	 * 解除水晶
	 */
	public SystemResult removeBerg(Player player, RobotBag robotBag, RobotType robotType, int robotSlot, Integer bergSlot) throws SQLException {
		SystemResult result = new SystemResult(ErrorCode.NO_ERROR);

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}

		Robot robot = robotBag.readRobot(robotType, robotSlot);
		if (robot == null) {//robot没找到,直接返回了
			logger.error("no this robot");
			result.setCode(ErrorCode.PARAM_ERROR);
			return result;
		} else {

			BergBag bergBag = Root.bergSystem.getBergBag(player);

			//机器人身上的老的水晶
			if (robot.getBergIds() != null && !robot.getBergIds().isEmpty()) {
				Integer oldBerg = robot.getBergIds().get(bergSlot);
				if (oldBerg != null) {
					bergBag.addBerg(oldBerg, 1, false);
					bergBag.synchronize();
					Root.bergSystem.updateBergBag(player, bergBag);
				}
			} else {
				logger.error("this robot have no berg");
			}

			bergBag.synchronize();

			//robot 解除
			robot.removeBerg(bergSlot);

			robotBag.putRobot(robot, true);//robot更新缓存

			updateRobotDB(player, robot);//更新机器人db

		}

		result.setBindle(robot.refreshFightProperty());
		return result;

	}

	/**
	 * 免费涂装
	 */
	public SystemResult paintFree(Player player, RobotBag robotBag, Robot robot, int partType, int color) throws SQLException {
		SystemResult result = new SystemResult();
		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}
		List<Part> paintParts = new ArrayList<>();
		if (partType == 5) {//全部涂装,4个颜色瓶,武器不涂装
			boolean takeOk = Root.colorSystem.removeColor(player, null, color, robot.readPaidNum());
			if (takeOk) {//颜色瓶足够
				for (Part part : robot.readParts()) {
					//如果是武器，跳过
					if (part.getPartSlotType() == PartSlotType.WEAPON.asCode()) {
						continue;
					}
					part.setColor(color);
					robot.putPart(part);
					paintParts.add(part);
				}
				updateRobotDB(player, robot);
			} else {
				result.setCode(ErrorCode.PAINT_COLOR_NO_ENOUGH);
				return result;
			}
		} else {
			boolean takeOk = Root.colorSystem.removeColor(player, null, color, 1);
			if (takeOk) {//颜色瓶足够
				Part part = robot.readPart(partType);
				part.setColor(color);
				robot.putPart(part);
				paintParts.add(part);
				//更新数据库
				updateRobotDB(player, robot);
			} else {
				result.setCode(ErrorCode.PAINT_COLOR_NO_ENOUGH);
				return result;
			}
		}
		robotBag.synchronize();
		if (!paintParts.isEmpty()) {
			RobotMessage robotMessage = new RobotMessage(RobotMessage.ROBOT_PAINT, player, paintParts);
			this.publish(robotMessage);
		}

		return result;
	}

	/**
	 * 付费涂装
	 */
	public SystemResult paintWithGold(Player player, RobotBag robotBag, Robot robot, int partType, Integer color, int gold) throws SQLException {
		SystemResult result = new SystemResult();
		if (player.getGold() < gold) {//gold 不够
			result.setCode(ErrorCode.GOLD_NOT_ENOUGH);
			return result;
		}
		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}
		List<Part> paintParts = new ArrayList<>();
		if (partType == 6) {//全部涂装,4个颜色瓶,武器不涂装
			ColorBag colorBag = Root.colorSystem.getColorBag(player);
			//当前有多少个此颜色的瓶子
			int haveNum = colorBag.getColors().get(color);
			if ((robot.readPaidNum() - haveNum) * GamePriceLoadData.getInstance().getColorPrice() == gold) {//验证gold是否对付
				boolean takeOk = Root.colorSystem.removeColor(player, colorBag, color);//取出所有
				if (takeOk) {//所有的全用上,不够的花钱
					for (Part part : robot.readParts()) {
						if (part.getPartSlotType() != PartSlotType.WEAPON.asCode()) {//如果是武器，跳过
							part.setColor(color);
							robot.putPart(part);
							paintParts.add(part);
						}
					}
					//更新数据库
					updateRobotDB(player, robot);
					//消费
					Root.playerSystem.changeGold(player, -gold, GoldType.PAINT_COST, true);
				} else {
					result.setCode(ErrorCode.PAINT_COLOR_NO_ENOUGH);
					return result;
				}
			} else {
				result.setCode(ErrorCode.PAINT_GOLD_NO_PAIR);
				return result;
			}
		} else {//单个涂装
			//当前有多少个此颜色的瓶子
			ColorBag colorBag = Root.colorSystem.getColorBag(player);
			int haveNum = colorBag.getColors().get(color);
			if (haveNum > 0) {
				result.setCode(ErrorCode.PAINT_GOLD_NO_PAIR);
			} else {
				if (GamePriceLoadData.getInstance().getColorPrice() == gold) {//验证gold是否对付
					Part part = robot.readPart(partType);
					part.setColor(color);
					robot.putPart(part);
					paintParts.add(part);
					//更新数据库
					updateRobotDB(player, robot);
					//消费
					Root.playerSystem.changeGold(player, -gold, GoldType.PAINT_COST, true);

				} else {
					result.setCode(ErrorCode.PAINT_GOLD_NO_PAIR);
					return result;
				}
			}

		}
		robotBag.synchronize();
		if (!paintParts.isEmpty()) {
			this.publish(new RobotMessage(RobotMessage.ROBOT_PAINT, player, paintParts));
		}
		return result;
	}

	/**
	 * 机器人增加进化经验
	 * @throws SQLException 
	 */
	public SystemResult robotEvolution(Player player, RobotBag robotBag, Robot robot, int partSlot, Long usePartId) throws SQLException {
		SystemResult result = new SystemResult();

		Part part = robot.readPart(partSlot);
		if (part != null) {
			PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());
			PartBag partBag = Root.partSystem.getPartBag(player);
			int addEvExp = 0;//进化点数
			Part usePart = partBag.takePart(usePartId, false);
			//消耗部件
			if (usePart == null) {
				result.setCode(ErrorCode.PART_UPDATE_ERROR_NO_USEPART);
				return result;
			}
			PartMaking usePartMaking = PartLoadData.getInstance().getMaking(usePart.getPartSlotType(), usePart.getMakingId());

			if (part.getPartSlotType() == usePart.getPartSlotType()) {//同部件才能进化
				QualityMaking qualityMaking = QualityLoadData.getInstance().getQualityMaking(usePartMaking.getPartQualityType().asCode());
				addEvExp += qualityMaking.getUseEvolveExp();
				addEvExp += usePart.getEvExp();
			}

			//更新进化经验,到最大值的时候不加了
			QualityMaking qualityMaking = QualityLoadData.getInstance().getQualityMaking(partMaking.getPartQualityType().asCode());
			if (part.getEvExp() + addEvExp >= qualityMaking.getEvolveExp()) {
				part.setEvExp(qualityMaking.getEvolveExp());
			} else {
				part.setEvExp(part.getEvExp() + addEvExp);
			}

			robot.putPart(part);

			updateRobotDB(player, robot);

			robotBag.putRobot(robot, true);

			partBag.synchronize();
			Root.partSystem.updatePartBag(player, partBag);

			//操作成功
			if (result.getCode() == ErrorCode.NO_ERROR) {
				//进化消息
				this.publish(new RobotMessage(RobotMessage.ROBOT_PART_EVOLUTION, player, robot, part, true));
				result.setBindle(part);
			}
		}

		return result;
	}

	/**
	 * 机器人进化
	 */
	public SystemResult robotEvolution(Player player, RobotBag robotBag, Robot robot, int partSlot) {
		SystemResult result = new SystemResult();
		Part part = robot.readPart(partSlot);
		if (part != null) {

			//进化前的原型
			PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());
			//进化后的原型
			PartMaking partMakingAfter = PartLoadData.getInstance().getMaking(part.getPartSlotType(), partMaking.getEvolveId());
			//进化了
			part.setMakingId(partMakingAfter.getId());
			part.setEvExp(0);
			//如果是武器，重新计算buff value
			if (part.getPartSlotType() == PartSlotType.WEAPON.asCode()) {
				List<Integer> bufVals = Root.buffSystem.calculateBuffValue(partMakingAfter);
				part.setBufVal(bufVals);
			}

			QualityMaking qualityMaking = QualityLoadData.getInstance().getQualityMaking(partMaking.getPartQualityType().asCode());

			int cash = qualityMaking.getEvolveMoney();
			//花钱
			if (player.getCash() >= cash) {
				Root.playerSystem.changeCash(player, -cash, CashType.ROBOT_PART_EVOLUTIN, true);
			} else {
				long lackCash = cash - player.getCash();//缺少的cash,需要钻石来补充

				int needGold = Root.missionSystem.cash2Gold(player, lackCash);

				if (player.getGold() > needGold) {//gold足够
					Root.playerSystem.changeCash(player, -(int) player.getCash(), CashType.ROBOT_PART_EVOLUTIN, false);
					Root.playerSystem.changeGold(player, -needGold, GoldType.ROBOT_PART_EVOLUTION_COST, false);
					player.synchronize();
				} else {
					result.setCode(ErrorCode.GOLD_NOT_ENOUGH);
				}
			}

			//操作成功
			if (result.getCode() == ErrorCode.NO_ERROR) {
				//更新缓存part 和  robot
				robot.putPart(part);
				robotBag.putRobot(robot, true);
				//更新数据库
				updateRobotDB(player, robot);

				//进化
				this.publish(new RobotMessage(RobotMessage.ROBOT_PART_EVOLUTION, player, robot, part, true));
				result.setBindle(part);
			}
		} else {
			result.setCode(ErrorCode.PART_EVOLUTION_ERROR_NO_PART);
		}

		return result;
	}

	public SystemResult rarityUpgradePart(Player player, RobotBag robotBag, Robot robot, int partSlot) throws SQLException {
		SystemResult result = new SystemResult();
		Part part = robot.readPart(partSlot);
		if (part != null) {
			PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());

			int rarity = partMaking.getRarity() + part.getAddRarity();

			RarityUpgradeMaking rarityUpgradeMaking = RarityUpgradeLoadData.getInstance().getRarityUpgradeMaking(rarity);

			int needChip = rarityUpgradeMaking.getChip();
			int needCash = rarityUpgradeMaking.getCash();

			ChipBag chipBag = Root.chipSystem.getChipBag(player);
			boolean enoughChip = chipBag.removeChip(partMaking.getSuitName(), needChip, false);

			if (enoughChip) {
				if (player.getCash() >= needCash) {
					part.setAddRarity(part.getAddRarity() + 1);
					robot.putPart(part);
					robotBag.putRobot(robot, true);
					Root.playerSystem.changeCash(player, -needCash, CashType.PART_RARITY_UPGRADE, true);
					Root.chipSystem.update(player, chipBag);
				} else {
					int needGold = Root.missionSystem.cash2Gold(player, needCash - player.getCash());
					if (player.getGold() > needGold) {//gold足够
						Root.playerSystem.changeCash(player, -(int) player.getCash(), CashType.ROBOT_PART_UP_COST, false);
						Root.playerSystem.changeGold(player, -needGold, GoldType.ROBOT_PART_UP_COST, false);

						player.synchronize();

						part.setAddRarity(part.getAddRarity() + 1);
						robot.putPart(part);
						robotBag.putRobot(robot, true);
						Root.chipSystem.update(player, chipBag);

					} else {
						result.setCode(ErrorCode.GOLD_NOT_ENOUGH);
						logger.error("rarityUpgradePart error ,gold is not enough");
					}
				}
			} else {
				result.setCode(ErrorCode.CHIP_NO_ENOUGH);
				logger.error("rarityUpgradePart error ,chip is not enough");
			}

			return result;
		}

		return result;
	}

	private int canUseExp(PartBag partBag, List<Long> useParts) {
		int addExp = 0;
		for (Long usePartId : useParts) {
			Part usePart = partBag.takePart(usePartId, false);
			if (usePart != null) {
				PartMaking usePartMaking = PartLoadData.getInstance().getMaking(usePart.getPartSlotType(), usePart.getMakingId());
				addExp += usePartMaking.getUseExp(usePart.getLevel());
			}
		}
		return addExp;
	}

	private int canUseExp(ExpPartBag expPartBag, List<Integer> useExpParts) {
		int addExp = 0;
		for (Integer useExpPartId : useExpParts) {
			boolean b = expPartBag.removeExpPart(useExpPartId, 1, false);
			if (b) {
				ExpPartMaking expPartMaking = ExpPartLoadData.getInstance().getMaking(useExpPartId);
				addExp += expPartMaking.getUseExpStr();
			}
		}
		return addExp;
	}

	/**
	 * 机器人升级
	 * @throws SQLException 
	 */
	public SystemResult upgrade(Player player, RobotBag robotBag, RobotType robotType, int robotSlot, int partSlot, List<Long> useParts, List<Integer> expParts)
			throws SQLException {
		SystemResult result = new SystemResult();
		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}
		Robot robot = robotBag.readRobot(robotType, robotSlot);
		if (robot != null) {
			//升级一个机器人身上的组件
			Part part = robot.readPart(partSlot);
			if (part != null) {
				//要升级的部件原型
				PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());
				//可用经验
				PartBag partBag = Root.partSystem.getPartBag(player);
				ExpPartBag expPartBag = Root.expPartSystem.getExpPartBag(player);

				int addExp = canUseExp(partBag, useParts) + canUseExp(expPartBag, expParts);//经验

				int qualityRatio = QualityLoadData.getInstance().getQualityMaking(partMaking.getPartQualityType().asCode()).getRatio();//材质系数
				//升级前的等级
				int oldLevel = part.getLevel();
				int oldExp = part.getExp();

				int level = oldLevel;
				int canUseExp = addExp + part.getExp();
				while (canUseExp > 0) {
					int needExp = partMaking.getExp(level);
					if (canUseExp >= needExp) {
						level++;
						canUseExp -= needExp;
					} else {
						break;
					}
				}

				//判断是否达到部件的最大等级,超过最大等级,,,level=max,exp=0;
				int maxLevel = (partMaking.getRarity() + part.getAddRarity()) * 10;

				boolean moreMaxLevel = false;//超过了最大等级

				if (level >= maxLevel) {
					//更新等级和经验
					part.setExp(0);
					part.setLevel(maxLevel);
					moreMaxLevel = true;
				} else {
					//更新等级和经验
					part.setExp(canUseExp);
					part.setLevel(level);
				}

				//升级所需金钱公式：INT(材质系数*主材质等级/3+提供的经验*(材质系数/2+4)*(2+(主材质等级/20)))-100
				//固定的钱数 =INT((主材质材质系数*INT((主材质当前等级/5)+1))*(1+INT(主材质当前等级/20)*2))
				//根据了列表的计算的钱数（每级的相加+不够一级的（当前等级钱数*剩余经验/升级经验））+固定钱数

				//固定钱数
				int cash = (qualityRatio * (oldLevel / 5 + 1)) * (1 + oldLevel / 20 * 2);

				logger.debug("-1---cash:[" + cash + "]  qualityRatio:[" + qualityRatio + "] oldLevel:[" + oldLevel + "]");

				cash += UpdateMoneyLoadData.getInstance().getCash(partMaking, oldLevel, oldExp, part.getLevel(), part.getExp(), moreMaxLevel);

				logger.debug("-2---cash:[" + cash + "] ");

				//花钱
				if (player.getCash() >= cash) {
					Root.playerSystem.changeCash(player, -cash, CashType.ROBOT_PART_UP_COST, true);

					//更新缓存part 和  robot
					robot.putPart(part);
					robotBag.putRobot(robot, true);
					//更新数据库
					updateRobotDB(player, robot);

					//存储partbag更新
					partBag.synchronize();//部件包同步缓存
					Root.partSystem.updatePartBag(player, partBag);

					//expPartBag 更新
					expPartBag.synchronize();
					Root.expPartSystem.updateDB(player, expPartBag);

				} else {

					int needGold = Root.missionSystem.cash2Gold(player, cash - player.getCash());

					if (player.getGold() > needGold) {//gold足够
						Root.playerSystem.changeCash(player, -(int) player.getCash(), CashType.ROBOT_PART_UP_COST, false);
						Root.playerSystem.changeGold(player, -needGold, GoldType.ROBOT_PART_UP_COST, false);

						player.synchronize();
						//更新缓存part 和  robot
						robot.putPart(part);
						robotBag.putRobot(robot, true);
						//更新数据库
						updateRobotDB(player, robot);

						//存储partbag更新
						partBag.synchronize();//部件包同步缓存
						Root.partSystem.updatePartBag(player, partBag);

						//expPartBag 更新
						expPartBag.synchronize();
						Root.expPartSystem.updateDB(player, expPartBag);

					} else {
						result.setCode(ErrorCode.PART_UPDATE_ERROR_NO_PART);
						logger.error("upgrade error ,gold is not enough");
					}
				}

				//操作成功
				if (result.getCode() == ErrorCode.NO_ERROR) {
					//发现送升级消息,但是不一定成功的升级了,可能只是长了经验
					this.publish(new RobotMessage(RobotMessage.ROBOT_PART_LEVELUP, player, robot, part, level > oldLevel));
					result.setBindle(part);
				}

			} else {
				result.setCode(ErrorCode.PART_UPDATE_ERROR_NO_PART);
			}
		} else {
			result.setCode(ErrorCode.PART_UPDATE_ERROR_NO_ROBOT);
		}
		return result;
	}

	/**
	 * 机器人升级 一键gold升级
	 */
	public SystemResult upgradeWithGold(Player player, RobotBag robotBag, RobotType robotType, int robotSlot, int partSlot) {
		SystemResult result = new SystemResult();

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}
		Robot robot = robotBag.readRobot(robotType, robotSlot);
		if (robot != null) {
			//升级一个机器人身上的组件
			Part part = robot.readPart(partSlot);

			if (part != null) {
				//要升级的部件原型
				PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());

				//花gold
				int needGold = UpdateMoneyLoadData.getInstance().getGold(partMaking, part.getLevel(), part.getLevel() + 1);

				if (player.getGold() >= needGold) {
					//更新等级
					part.setLevel(part.getLevel() + 1);
					//更新缓存part 和  robot
					robot.putPart(part);
					//更新数据库
					updateRobotDB(player, robot);
					robotBag.putRobot(robot, true);

					Root.playerSystem.changeGold(player, -needGold, GoldType.ROBOT_PART_UP_WITH_GOLD_COST, false);

					player.synchronize();

					RobotMessage robotMessage = new RobotMessage(RobotMessage.ROBOT_PART_LEVELUP, player, robot, part, true);
					this.publish(robotMessage);

					result.setBindle(part);

				} else {
					result.setCode(ErrorCode.GOLD_NOT_ENOUGH);
				}
			}

		} else {
			result.setCode(ErrorCode.PART_UPDATE_ERROR_NO_PART);
		}

		return result;
	}

	/**
	 * 消耗耐久度
	 */
	public void consumeWearByRobots(Player player, RobotBag robotBag, List<Robot> robots, int consumeNum) {

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}

		List<RobotSlot> robotSlots = new ArrayList<RobotSlot>();

		for (Robot robot : robots) {
			RobotSlot robotSlot = robotBag.getRobotSlots().get(robot.getSlot());
			robotSlots.add(robotSlot);
		}

		consumeWear(player, robotBag, robotSlots, consumeNum);
	}

	/**
	 * 消耗耐久度
	 */
	public void consumeWearByRobots(Player player, RobotBag robotBag, HashMap<Integer, Robot> robots, int consumeNum) {

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}

		List<RobotSlot> robotSlots = new ArrayList<RobotSlot>();

		for (Robot robot : robots.values()) {
			RobotSlot robotSlot = robotBag.getRobotSlots().get(robot.getSlot());
			robotSlots.add(robotSlot);
		}

		consumeWear(player, robotBag, robotSlots, consumeNum);
	}

	/**
	 * 消耗耐久度
	 */
	private void consumeWear(Player player, RobotBag robotBag, List<RobotSlot> robotSlots, int consumeNum) {

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}

		for (RobotSlot robotSlot : robotSlots) {

			int wear = robotSlot.getWear() - consumeNum;

			robotSlot.setWear(wear > 0 ? wear : 0);

			robotBag.putRobotSlot(robotSlot, false);
			//更新数据库
			updateRobotSlotDB(player, robotSlot);
		}
		robotBag.synchronize();

	}

	//计算修理时间
	private int calculateRepairTime(Player player, RobotSlot robotSlot) {
		//	T=INT(主城等级/5)*2+INT(主城等级/12)*3+INT(主城等级/13)*8+INT(主城等级/14)*15+INT(主城等级/15)*28+INT(主城等级/16)*30+INT(主城等级/17)*30+INT(主城等级/18)*30，当T>150时，T=150。

		int t = (player.getLevel() / 5) * 2 + (player.getLevel() / 12) * 3 + (player.getLevel() / 13) * 8 + (player.getLevel() / 14) * 15 + (player.getLevel() / 15) * 28
				+ (player.getLevel() / 16) * 30 + (player.getLevel() / 17) * 30 + (player.getLevel() / 18) * 30;
		t = t > 150 ? 150 : t;

		return t * (Content.maxWear - robotSlot.getWear()) * 60 / Content.maxWear;
	}

	//计算修理需要的钻石
	private int calculateRepairGold(int leftTime) {

		int needGold = 0;
		if (leftTime <= 1800) {
			needGold = leftTime / 60 + 1;
		} else if (leftTime <= 5400) {
			needGold = (leftTime - 1800) / 120 + 30;
		} else {
			needGold = (leftTime - 5400) / 180 + 60;
		}
		return needGold;
	}

	private void repairOk(RobotSlot robotSlot) {
		robotSlot.setRepairBeginTime(0);
		robotSlot.setRepairEndTime(0);
		robotSlot.setWear(Content.maxWear);
	}

	/**
	 * 全部维修,加耐久
	 */
	public SystemResult repairRobotAll(Player player, RobotBag robotBag, int wear) {

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}

		SystemResult result = new SystemResult();

		long lastRepairEndTime = 0;//最后一个机器人修理ok后的时间

		for (RobotSlot robotSlot : robotBag.getRobotSlots().values()) {

			int beforeWear = robotSlot.getWear();

			//如果加上耐久后,耐久满,不管是否处于修理状态.
			if (beforeWear < Content.maxWear) {//耐久不满

				//修理中的
				if (robotSlot.isRepairing()) {//正在修理
					//先处理之前的时间回复的耐久
					checkRepairRobotSlot(player, robotBag, robotSlot, false);
					//可能这时候 耐久满了,也可能没有满,如果满了,那这次增加的耐久点数就浪费掉了
					//还没满,也就是还在修理中
					if (robotSlot.isRepairing()) {
						if (robotSlot.getWear() + wear >= Content.maxWear) {//加上这点就能满
							repairOk(robotSlot);
						} else {//加上这些依然没有满,重新计算修理时间
							robotSlot.setWear(robotSlot.getWear() + wear);
							//相当于是从现在开始修理
							int needTime = calculateRepairTime(player, robotSlot);
							robotSlot.setRepairBeginTime(Clock.currentTimeSecond());
							robotSlot.setRepairEndTime(Clock.currentTimeSecond() + needTime);

							if (robotSlot.getRepairEndTime() > lastRepairEndTime) {
								lastRepairEndTime = robotSlot.getRepairEndTime();
							}

						}
						//放回robotSlot,同步cache
						robotBag.putRobotSlot(robotSlot, false);
						//更新数据库
						updateRobotSlotDB(player, robotSlot);
					}
				} else {
					//加耐久
					int afterWear = beforeWear + wear;
					afterWear = afterWear > Content.maxWear ? Content.maxWear : afterWear;
					robotSlot.setWear(afterWear);
					//放回robotSlot,同步cache
					robotBag.putRobotSlot(robotSlot, false);
					//更新数据库
					updateRobotSlotDB(player, robotSlot);
				}
			}

		}

		//发送修理消息
		this.publish(new RobotMessage(RobotMessage.ROBOT_REPAIRE, player, lastRepairEndTime));

		robotBag.synchronize();

		return result;

	}

	/**
	 * 全部回满
	 */
	public SystemResult recoverRobotWearAllFull(Player player, RobotBag robotBag) {

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}

		SystemResult result = new SystemResult();

		for (RobotSlot robotSlot : robotBag.getRobotSlots().values()) {

			int beforeWear = robotSlot.getWear();

			//如果加上耐久后,耐久满,不管是否处于修理状态.
			if (beforeWear < Content.maxWear) {//耐久不满
				if (robotSlot.isRepairing()) {//正在修理
					repairOk(robotSlot);
				} else {
					//加耐久
					robotSlot.setWear(Content.maxWear);
				}
				//放回robotSlot,同步cache
				robotBag.putRobotSlot(robotSlot, false);
				//更新数据库
				updateRobotSlotDB(player, robotSlot);
			}

		}

		robotBag.synchronize();

		return result;

	}

	/**
	 * 维修
	 */
	public SystemResult repairRobot(Player player, RobotBag robotBag, int slot) {

		SystemResult result = new SystemResult();

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}

		RobotSlot robotSlot = robotBag.getRobotSlots().get(slot);

		int needTime = calculateRepairTime(player, robotSlot);

		robotSlot.setRepairBeginTime(Clock.currentTimeSecond());
		robotSlot.setRepairEndTime(Clock.currentTimeSecond() + needTime);

		//放回robotSlot,同步cache
		robotBag.putRobotSlot(robotSlot, true);
		//更新数据库
		updateRobotSlotDB(player, robotSlot);

		//发送修理消息
		this.publish(new RobotMessage(RobotMessage.ROBOT_REPAIRE, player, robotSlot.getRepairEndTime()));

		result.setMap("needTime", needTime);

		return result;

	}

	/**
	 * 维修完成
	 */
	public SystemResult repairRobotOk(Player player, RobotBag robotBag, Integer slot) {
		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}
		RobotSlot robotSlot = robotBag.getRobotSlots().get(slot);
		return repairRobotOk(player, robotBag, robotSlot);
	}

	/**
	 * 维修完成
	 */
	public SystemResult repairRobotOk(Player player, RobotBag robotBag, RobotSlot robotSlot) {

		SystemResult result = new SystemResult();

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}

		if (robotSlot.getWear() == Content.maxWear) {//耐久度已满
			return result;
		} else {
			//修理完成时间
			long repairEndTime = robotSlot.getRepairEndTime();
			//修理完成时间跟现在时间相比,现在时间  > 修理完成时间-60秒,算ok,>60秒,算作弊或者数据不同步
			if (Clock.currentTimeSecond() + Content.repaireGapTime < repairEndTime) {
				result.setCode(ErrorCode.REPAIRTIME_GAP_TO_LARGE);
				return result;
			} else {
				//修理完成
				repairOk(robotSlot);
				//放回robotSlot,同步cache
				robotBag.putRobotSlot(robotSlot, true);
				//更新数据库
				updateRobotSlotDB(player, robotSlot);
				return result;
			}
		}

	}

	/**
	 * 检查是否修理完成,如果到时了,就自动修理完成
	 */
	public void checkRepairRobotSlots(Player player, RobotBag robotBag, List<RobotSlot> robotSlots) {

		for (RobotSlot robotSlot : robotSlots) {
			checkRepairRobotSlot(player, robotBag, robotSlot, false);
		}
		robotBag.synchronize();

	}

	/**
	 * 检查是否修理完成,如果到时了,就自动修理完成
	 */
	public void checkRepairRobotSlot(Player player, RobotBag robotBag, RobotSlot robotSlot, boolean sync) {

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}

		if (robotSlot.isRepairing()) {//正在修着
			if (robotSlot.getRepairEndTime() < Clock.currentTimeSecond()) {//时间上修理完成了
				//修理完成
				repairOk(robotSlot);
				//放回robot,同步cache
				robotBag.putRobotSlot(robotSlot, sync);
				//更新数据库
				updateRobotSlotDB(player, robotSlot);
			} else {//没修理完,加上耐久,继续修理
				//增加的wear = 总要修的耐久 * float(已修时间   / 修理所需时间)
				//增加的wear =  (总耐久-当前耐久) * float((当前时间-开始时间)/(结束时间-开始时间))

				//(总耐久-当前耐久)
				int needRepairWear = Content.maxWear - robotSlot.getWear();
				//(当前时间-开始时间)
				long haveRepairTime = Clock.currentTimeSecond() - robotSlot.getRepairBeginTime();
				//(结束时间-开始时间)
				long needRepairTime = robotSlot.getRepairEndTime() - robotSlot.getRepairBeginTime();

				int addWear = (int) (needRepairWear * (float) haveRepairTime / (float) needRepairTime);

				robotSlot.setWear(robotSlot.getWear() + addWear);//增加了wear
				//相当于是从现在开始修理
				robotSlot.setRepairBeginTime(Clock.currentTimeSecond());
				robotBag.putRobotSlot(robotSlot, sync);
				//更新数据库
				updateRobotSlotDB(player, robotSlot);
			}
		}

	}

	/**
	 * 花钻修理
	 */
	public SystemResult repairRobotUseGold(Player player, RobotBag robotBag, int slot, int leftTime, int useGold) {

		SystemResult result = new SystemResult();

		//客户端传过来的gold <=0 不对
		if (useGold <= 0) {
			result.setCode(ErrorCode.GOLD_NOT_ENOUGH);
			return result;
		}

		//玩家钱不够,应该数据不同步导致
		if (player.getGold() < useGold) {
			result.setCode(ErrorCode.GOLD_NOT_ENOUGH);
			return result;
		}

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}

		RobotSlot robotSlot = robotBag.getRobotSlots().get(slot);

		//修理完成还需要的时间 秒
		long repairTime = robotSlot.getRepairEndTime() - Clock.currentTimeSecond();

		if (Math.abs(repairTime - leftTime) > Content.repaireGapTime) {
			result.setCode(ErrorCode.REPAIRTIME_GAP_TO_LARGE);
			return result;
		} else {
			if (leftTime > 0) {
				//服务器计算花多少钻石
				int needGold = calculateRepairGold(leftTime);

				if (needGold != useGold) {//客户端跟服务器计算的需要钻石数量不一样
					result.setCode(ErrorCode.REPAIR_GOLD_NOT_PAIR);
					return result;
				}
			}

			//修理完成
			repairOk(robotSlot);

			robotBag.putRobotSlot(robotSlot, true);
			//更新数据库
			updateRobotSlotDB(player, robotSlot);

			//花gold
			Root.playerSystem.changeGold(player, -useGold, GoldType.REPAIR_ROBOT, true);

			//发送修理消息
			this.publish(new RobotMessage(RobotMessage.ROBOT_REPAIREOK_WITH_GOLD_HAVE_BEGIN, player));

			return result;
		}

	}

	/**
	 * 花钻修理多个
	 */
	public SystemResult repairRobotsUseGold(Player player, RobotBag robotBag, int useGold, List<RobotRepairBean> robotRepairBeans) {

		SystemResult result = new SystemResult();

		//客户端传过来的gold <=0 不对
		if (useGold <= 0) {
			result.setCode(ErrorCode.GOLD_NOT_ENOUGH);
			return result;
		}

		//玩家钱不够
		if (player.getGold() < useGold) {
			result.setCode(ErrorCode.GOLD_NOT_ENOUGH);
			return result;
		}

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}

		//算钱
		//服务器计算花多少钻石
		int needGold = 0;
		List<RobotSlot> repairRobots = new ArrayList<RobotSlot>();
		for (RobotRepairBean robotRepairBean : robotRepairBeans) {

			RobotSlot robotSlot = robotBag.getRobotSlots().get(robotRepairBean.getSlot());

			long repairTime = 0;
			//已经开始维修的机器人
			if (robotSlot.isRepairing()) {
				//修理完成还需要的时间 秒
				repairTime = robotSlot.getRepairEndTime() - Clock.currentTimeSecond();
			} else {
				repairTime = calculateRepairTime(player, robotSlot);
			}

			if (Math.abs(repairTime - robotRepairBean.getNeedTime()) > Content.repaireGapTime) {
				result.setCode(ErrorCode.REPAIRTIME_GAP_TO_LARGE);
				return result;
			} else {
				if (robotRepairBean.getNeedTime() > 0) {
					needGold += calculateRepairGold(robotRepairBean.getNeedTime());
				}
			}

			repairRobots.add(robotSlot);
		}

		//gold 算的不对
		if (needGold != useGold) {
			logger.error("needGold from server : " + needGold);
			result.setCode(ErrorCode.REPAIR_GOLD_NOT_PAIR);
			return result;
		}

		//花gold
		Root.playerSystem.changeGold(player, -useGold, GoldType.REPAIR_ROBOT, true);

		for (RobotSlot robotSlot : repairRobots) {
			//修理完成
			repairOk(robotSlot);
			//放回robot,同步cache
			robotBag.putRobotSlot(robotSlot, false);
			//更新数据库
			updateRobotSlotDB(player, robotSlot);

			if (robotSlot.isRepairing()) {//修理中
				//发送修理消息
				this.publish(new RobotMessage(RobotMessage.ROBOT_REPAIREOK_WITH_GOLD_HAVE_BEGIN, player));
			} else {
				//发送修理消息
				this.publish(new RobotMessage(RobotMessage.ROBOT_REPAIREOK_WITH_GOLD_NO_BEGIN, player));
			}

		}

		robotBag.synchronize();

		return result;
	}

	/**
	 * 机器人换位置
	 */
	public SystemResult changeSlot(Player player, RobotBag robotBag, RobotType fromType, int fromSlot, RobotType toType, int toSlot) {

		SystemResult result = new SystemResult();

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}

		Robot robotForm = robotBag.readRobot(fromType, fromSlot);
		Robot robotTo = robotBag.readRobot(toType, toSlot);

		robotForm.setRobotType(toType);
		robotForm.setSlot(toSlot);
		robotBag.putRobot(robotForm, false);
		if (robotTo != null) {
			robotTo.setRobotType(fromType);
			robotTo.setSlot(fromSlot);
			robotBag.putRobot(robotTo, false);
		}

		robotBag.synchronize();

		//更新数据库
		RobotDao robotDao = DaoFactory.getInstance().borrowRobotDao();
		robotDao.updateRobot(player, robotForm, robotForm.refreshFightProperty().getScore());
		robotDao.updateRobot(player, robotTo, robotTo.refreshFightProperty().getScore());
		DaoFactory.getInstance().returnRobotDao(robotDao);

		//机器人换位置,可能影响pvp,因为,有可能是在pvp的时候换的位置
		Root.pvpSystem.changeSlot(player, robotBag);

		return result;

	}

	/**
	 * 机器人解体
	 * @throws SQLException 
	 */
	public SystemResult breakUp(Player player, RobotBag robotBag, int robotSlot) throws SQLException {

		SystemResult result = new SystemResult();

		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}

		Robot robot = robotBag.readRobot(RobotType.STORAGE, robotSlot);

		if (robot != null) {
			Root.partSystem.addParts(player, robot.readParts(), false);
			robotBag.removeRobot(RobotType.STORAGE, robotSlot, true);
			//删除机器人
			RobotDao robotDao = DaoFactory.getInstance().borrowRobotDao();
			robotDao.deleteRobot(player, robot);
			DaoFactory.getInstance().returnRobotDao(robotDao);
		}

		robotBag.synchronize();

		return result;

	}

	/**
	 * 出战的三个机器人
	 * @param player
	 * @param robotBag
	 * @return
	 */
	public List<Robot> getFightRobots(Player player, RobotBag robotBag) {
		if (robotBag == null) {
			robotBag = getRobotBag(player);
		}
		List<Robot> robots = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Robot robot = robotBag.readRobot(RobotType.BATTLE, i);
			if (robot != null) {
				robots.add(robot);
			}
		}
		return robots;
	}

	/**
	 * 获取出战的三个机器人使用的武器的id
	 */
	public String getWeaponIds(Player player) {
		//本次战斗都用了哪些武器
		RobotBag robotBag = Root.robotSystem.getRobotBag(player);
		StringBuffer weaponStr = new StringBuffer();
		//0 1 2  三个位置上的机器人
		for (int i = 0; i < 3; i++) {
			Robot robot = robotBag.readRobot(RobotType.BATTLE, i);
			if (robot != null) {
				Part weapon = robot.readPart(PartSlotType.WEAPON.asCode());
				if (weapon != null) {
					weaponStr.append(weapon.getMakingId()).append("-");
				}
			}
		}
		return weaponStr.toString();
	}

	public String getWeaponIds(Collection<Robot> robots) {
		StringBuffer weaponStr = new StringBuffer();
		for (Robot robot : robots) {
			if (robot != null) {
				Part weapon = robot.readPart(PartSlotType.WEAPON.asCode());
				if (weapon != null) {
					weaponStr.append(weapon.getMakingId()).append("-");
				}
			}
		}
		return weaponStr.toString();
	}

}
