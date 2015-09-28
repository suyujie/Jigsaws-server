package server.node.system.handbook;

import gamecore.serialize.SerializerJson;
import gamecore.system.AbstractSystem;
import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import server.node.dao.DaoFactory;
import server.node.dao.HandbookDao;
import server.node.system.RedisHelperJson;
import server.node.system.Root;
import server.node.system.player.GoldType;
import server.node.system.player.Player;
import server.node.system.robot.Robot;
import server.node.system.robot.RobotBag;
import server.node.system.robot.RobotType;
import server.node.system.robotPart.Part;
import server.node.system.robotPart.PartBag;
import server.node.system.robotPart.PartLoadData;
import server.node.system.robotPart.PartMaking;
import server.node.system.robotPart.PartSlotType;

import com.alibaba.fastjson.TypeReference;

/**
 * 图鉴系统。
 */
public final class HandbookSystem extends AbstractSystem {

	public HandbookSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("HandbookSystem start....");

		boolean b = HandbookData.getInstance().readData();

		System.out.println("HandbookSystem start....OK");
		return b;
	}

	@Override
	public void shutdown() {
	}

	public HandbookBag getHandbookBag(Player player) throws SQLException {
		HandbookBag handbookBag = RedisHelperJson.getHandbookBag(player.getId());
		if (handbookBag == null) {
			handbookBag = readHandbookBagFromDB(player);
			handbookBag.synchronize();
		}
		return handbookBag;
	}

	//数据库读取HandbookBag
	private HandbookBag readHandbookBagFromDB(Player player) throws SQLException {
		HandbookBag handbookBag = null;

		HandbookDao dao = DaoFactory.getInstance().borrowHandbookDao();
		Map<String, Object> map = dao.readHandbookBag(player);
		DaoFactory.getInstance().returnHandbookDao(dao);

		if (map != null) {
			try {
				String handbooksStr = (String) map.get("handbooks");
				String rewardedStr = (String) map.get("rewarded");

				HashMap<String, HashMap<PartSlotType, Integer>> handbooks = SerializerJson.deSerializeMap(handbooksStr,
						new TypeReference<HashMap<String, HashMap<PartSlotType, Integer>>>() {
						});
				HashMap<String, Boolean> rewarded = (HashMap<String, Boolean>) SerializerJson.deSerializeMap(rewardedStr, new TypeReference<HashMap<String, Boolean>>() {
				});
				//生成handbookBag
				handbookBag = new HandbookBag(player.getId(), handbooks, rewarded);

				handbookBag.synchronize();//一次性更新partBag

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			handbookBag = initHandbookBag(player);
		}

		return handbookBag;
	}

	//初始化HandbookBag
	private HandbookBag initHandbookBag(Player player) throws SQLException {

		HandbookBag handbookBag = new HandbookBag(player.getId());

		//部件包里的先加上
		PartBag partBag = Root.partSystem.getPartBag(player);
		for (Part part : partBag.readAllParts()) {
			PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());
			handbookBag.addNewHandbook(partMaking, false);
		}
		//机器人战斗包里的也加上
		RobotBag robotBag = Root.robotSystem.getRobotBag(player);
		for (Robot robot : robotBag.readRobots(RobotType.BATTLE)) {
			for (Part part : robot.readParts()) {
				PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());
				handbookBag.addNewHandbook(partMaking, false);
			}
		}

		for (Robot robot : robotBag.readRobots(RobotType.STORAGE)) {
			for (Part part : robot.readParts()) {
				PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());
				handbookBag.addNewHandbook(partMaking, false);
			}
		}

		handbookBag.synchronize();

		HandbookDao dao = DaoFactory.getInstance().borrowHandbookDao();
		dao.save(player, handbookBag);
		DaoFactory.getInstance().returnHandbookDao(dao);

		return handbookBag;
	}

	//得到一个part
	public void addPart(Player player, Part part) throws SQLException {

	//	if (part.getPartSlotType() != PartSlotType.COLOR.asCode()) {
			PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());

			HandbookBag handbookBag = getHandbookBag(player);
			boolean update = handbookBag.addNewHandbook(partMaking, true);

			if (update) {
				HandbookDao dao = DaoFactory.getInstance().borrowHandbookDao();
				dao.update(player, handbookBag);
				DaoFactory.getInstance().returnHandbookDao(dao);
			}
	//	}

	}

	//得到多个part
	public void addParts(Player player, List<Part> parts) throws SQLException {
		HandbookBag handbookBag = getHandbookBag(player);

		boolean update = false;

		for (Part part : parts) {
		//	if (part.getPartSlotType() != PartSlotType.COLOR.asCode()) {
				PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());
				update |= handbookBag.addNewHandbook(partMaking, false);
		//	}
		}

		if (update) {
			handbookBag.synchronize();
			HandbookDao dao = DaoFactory.getInstance().borrowHandbookDao();
			dao.update(player, handbookBag);
			DaoFactory.getInstance().returnHandbookDao(dao);
		}

	}

	//得到一个reward
	public SystemResult reward(Player player, String suitName) throws SQLException {

		SystemResult result = new SystemResult();

		HandbookBag handbookBag = getHandbookBag(player);

		if (handbookBag.checkRewardHandbook(suitName)) {//可以领奖励
			HandbookMaking making = HandbookData.getInstance().getHandbookMaking(suitName);

			if (making != null) {
				int gold = making.getDiamons();

				Root.playerSystem.changeGold(player, gold, GoldType.HANDBOOK_REWARD, true);
				handbookBag.rewardHandbook(suitName, true);

				HandbookDao dao = DaoFactory.getInstance().borrowHandbookDao();
				dao.update(player, handbookBag);
				DaoFactory.getInstance().returnHandbookDao(dao);

			} else {
				result.setCode(ErrorCode.PARAM_ERROR);
			}
		} else {
			result.setCode(ErrorCode.PARAM_ERROR);
		}
		return result;
	}

}
