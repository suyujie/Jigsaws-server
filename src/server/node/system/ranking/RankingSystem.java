package server.node.system.ranking;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.serialize.SerializerJson;
import gamecore.system.AbstractSystem;
import gamecore.task.TaskCenter;
import gamecore.util.Utils;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.TypeReference;

import server.node.dao.PlayerDao;
import server.node.dao.RobotDao;
import server.node.system.Root;
import server.node.system.player.Player;
import server.node.system.robot.Robot;
import server.node.system.robot.RobotBag;
import server.node.system.robot.RobotType;
import server.node.system.robotPart.Part;
import server.node.system.robotPart.PartLoadData;
import server.node.system.robotPart.PartMaking;
import server.node.system.robotPart.PartSlotType;

/**
 * 对手系统。
 */
public final class RankingSystem extends AbstractSystem {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger(RankingSystem.class.getName());
	private static int rankingNum = 100;
	private static List<String> areas;

	private Map<String, List<RankingBean>> rankingListMap;

	@Override
	public boolean startup() {

		System.out.println("RankingSystem start....");

		areas = new ArrayList<String>();
		areas.add("all");

		rankingListMap = new HashMap<String, List<RankingBean>>();

		try {
			init();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}

		TaskCenter.getInstance().scheduleAtFixedRate(new RankingJob(), Utils.randomInt(1, 60), 5 * 60 * 60, TimeUnit.SECONDS);
		//	QuartzManager.addJob("ranking", RankingJob.class, "0 * * * * ?");

		System.out.println("RankingSystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {
	}

	public void init() throws SQLException {
		PlayerDao playerDao = DaoFactory.getInstance().borrowPlayerDao();
		List<Map<String, Object>> playerList = playerDao.readPlayerRanking();
		DaoFactory.getInstance().returnPlayerDao(playerDao);
		if (playerList != null) {
			for (Map<String, Object> map : playerList) {
				if (map != null) {
					Long id = ((BigInteger) map.get("id")).longValue();
					Player player = Root.playerSystem.getPlayer(id);
					if (player != null) {
						addRankingCup(player.getPlayerStatistics().getCupNum(), player);
					}
				}
			}
		}

		RobotDao robotDao = DaoFactory.getInstance().borrowRobotDao();
		List<Map<String, Object>> robotList = robotDao.readRobotsRanking();
		DaoFactory.getInstance().returnPlayerDao(playerDao);
		if (robotList != null) {
			for (Map<String, Object> map : robotList) {
				if (map != null) {
					Long id = ((BigInteger) map.get("id")).longValue();
					Long playerId = ((BigInteger) map.get("player_id")).longValue();
					int bag = ((Long) map.get("bag")).intValue();
					int slot = ((Long) map.get("slot")).intValue();
					int score = ((Long) map.get("score")).intValue();
					String partsJson = (String) map.get("parts");
					HashMap<Integer, Part> parts = (HashMap<Integer, Part>) SerializerJson.deSerializeMap(partsJson, new TypeReference<HashMap<Integer, Part>>() {
					});
					String bergsJson = (String) map.get("bergs");
					HashMap<Integer, Integer> bergs = (HashMap<Integer, Integer>) SerializerJson.deSerializeMap(bergsJson, new TypeReference<HashMap<Integer, Integer>>() {
					});

					//构造机器人实体
					Robot robot = new Robot(id, RobotType.asEnum(bag), slot, parts, bergs);
					addRankingScore(score, Root.playerSystem.getPlayer(playerId), robot);
				}
			}
		}

		new RankingJob().run();
	}

	/**
	 * 加入排名cup
	 */
	public void addRankingCup(int cup, Player player) {
		RedisHelperJson.addRankingCup(player.getAccount().readArea(), cup, player.getId());
		RedisHelperJson.addRankingCup("all", cup, player.getId());
		if (!areas.contains(player.getAccount().readArea())) {
			areas.add(player.getAccount().readArea());
		}
	}

	/**
	 * 加入排名score
	 */
	public void addRankingScore(int score, Player player, Robot robot) {
		if (robot.getRobotType() == RobotType.BATTLE) {
			RedisHelperJson.addRankingScore(player.getAccount().readArea(), score, player.getId(), robot.getId());
			RedisHelperJson.addRankingScore("all", score, player.getId(), robot.getId());
			if (!areas.contains(player.getAccount().readArea())) {
				areas.add(player.getAccount().readArea());
			}
		}
	}

	//读取排行榜
	public List<RankingBean> ranking(Player player, RankingType type) {

		List<RankingBean> list = null;

		if (type == RankingType.CUP || type == RankingType.CUP_AREA) {
			list = rankingCup(player, type);
		}
		if (type == RankingType.SCORE || type == RankingType.SCORE_AREA) {
			list = rankingScore(player, type);
		}

		if (list == null) {
			return new ArrayList<RankingBean>();
		}

		return list;
	}

	//读取排行榜
	private List<RankingBean> rankingCup(Player player, RankingType type) {

		List<RankingBean> rankingList = new ArrayList<RankingBean>();

		if (type == RankingType.CUP) {
			return readRankingBeans(player, "cup", "all");
		}
		if (type == RankingType.CUP_AREA) {
			return readRankingBeans(player, "cup", player.getAccount().readArea());
		}

		return rankingList;
	}

	private List<RankingBean> rankingScore(Player player, RankingType type) {

		List<RankingBean> rankingList = new ArrayList<RankingBean>();

		if (type == RankingType.SCORE) {
			return readRankingBeans(player, "score", "all");
		}
		if (type == RankingType.SCORE_AREA) {
			return readRankingBeans(player, "score", player.getAccount().readArea());
		}

		return rankingList;
	}

	private List<RankingBean> readRankingBeans(Player player, String rank, String area) {

		List<RankingBean> rankingList = rankingListMap.get(rank + "_" + area);
		if (rankingList != null && !rankingList.isEmpty()) {
			return rankingList;
		} else {

			rankingList = new ArrayList<RankingBean>();

			if (rank.equals("cup")) {
				List<Long> cupList = RedisHelperJson.getRankingCup(player.getAccount().readArea(), rankingNum);
				try {
					for (Long rankPlayerId : cupList) {
						Player rankPlayer = Root.playerSystem.getPlayer(rankPlayerId);
						if (rankPlayer != null) {
							RankingBean rankingBean = new RankingBean(rankPlayer.getAccount().getNameInPlat(), rankPlayerId, rankPlayer.getPlayerStatistics().getCupNum(),
									(byte) 0, rankPlayer.getLevel());
							if (rankingList.size() < rankingNum) {
								rankingList.add(rankingBean);
							} else {
								break;
							}
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				rankingListMap.put("cup_" + area, rankingList);
			}

			if (rank.equals("score")) {
				List<String> scoreList = RedisHelperJson.getRankingScore(area, rankingNum);
				try {
					for (String playerId_robotSlot : scoreList) {
						long playerId = Long.parseLong(playerId_robotSlot.split("_")[0]);
						long robotId = Long.parseLong(playerId_robotSlot.split("_")[1]);
						Player rankPlayer = Root.playerSystem.getPlayer(playerId);
						RobotBag robotBag = Root.robotSystem.getRobotBag(playerId);
						if (rankPlayer != null && robotBag != null) {
							Robot robot = robotBag.readRobot(robotId);
							if (robot != null) {
								Part part = robot.readPart(PartSlotType.WEAPON.asCode());
								PartMaking weaponMaking = PartLoadData.getInstance().getMaking(PartSlotType.WEAPON.asCode(), part.getMakingId());
								RankingBean rankingBean = new RankingBean(rankPlayer.getAccount().getNameInPlat(), playerId, robot.refreshFightProperty().getScore(),
										(byte) weaponMaking.getWeaponType().asCode(), rankPlayer.getLevel());
								if (rankingList.size() < rankingNum) {
									rankingList.add(rankingBean);
								} else {
									break;
								}
							}
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				rankingListMap.put("score_" + area, rankingList);
			}

			return rankingList;
		}

	}

	/**
	 * 守护任务。
	 * 内部线程类,过滤玩家
	 */
	protected class RankingJob implements Runnable {

		@Override
		public void run() {

			//cup
			List<Long> cupList = new ArrayList<Long>();
			for (String area : areas) {
				List<RankingBean> rankingList = new ArrayList<RankingBean>();
				cupList = RedisHelperJson.getRankingCup(area, rankingNum);
				try {
					for (Long rankPlayerId : cupList) {
						Player rankPlayer = Root.playerSystem.getPlayer(rankPlayerId);
						if (rankPlayer != null) {
							RankingBean rankingBean = new RankingBean(rankPlayer.getAccount().getNameInPlat(), rankPlayerId, rankPlayer.getPlayerStatistics().getCupNum(),
									(byte) 0, rankPlayer.getLevel());
							if (rankingList.size() < rankingNum) {
								rankingList.add(rankingBean);
							} else {
								break;
							}
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				rankingListMap.put("cup_" + area, rankingList);
			}

			//score
			List<String> scoreList = new ArrayList<String>();
			for (String area : areas) {
				List<RankingBean> rankingList = new ArrayList<RankingBean>();
				scoreList = RedisHelperJson.getRankingScore(area, rankingNum);
				try {
					for (String playerId_robotSlot : scoreList) {
						long playerId = Long.parseLong(playerId_robotSlot.split("_")[0]);
						long robotId = Long.parseLong(playerId_robotSlot.split("_")[1]);
						Player rankPlayer = Root.playerSystem.getPlayer(playerId);
						if (rankPlayer != null) {
							RobotBag robotBag = Root.robotSystem.getRobotBag(rankPlayer);
							if (rankPlayer != null && robotBag != null) {
								Robot robot = robotBag.readRobot(robotId);
								if (robot != null) {
									Part part = robot.readPart(PartSlotType.WEAPON.asCode());
									PartMaking weaponMaking = PartLoadData.getInstance().getMaking(PartSlotType.WEAPON.asCode(), part.getMakingId());
									RankingBean rankingBean = new RankingBean(rankPlayer.getAccount().getNameInPlat(), playerId, robot.refreshFightProperty().getScore(),
											(byte) weaponMaking.getWeaponType().asCode(), rankPlayer.getLevel());
									if (rankingList.size() < rankingNum) {
										rankingList.add(rankingBean);
									} else {
										break;
									}
								}
							}
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				rankingListMap.put("score_" + area, rankingList);
			}

		}
	}

	//	class RankingJob implements Job {
	//
	//		@Override
	//		public void execute(JobExecutionContext arg0) throws JobExecutionException {
	//
	//			System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "★★★★★★★★★★★");
	//
	//			List<RankingBean> rankingList = new ArrayList<RankingBean>();
	//
	//			//cup
	//			List<Long> cupList = new ArrayList<Long>();
	//			for (String area : areas) {
	//				cupList = RedisHelperJson.getRankingCup(area, rankingNum);
	//				for (Long rankPlayerId : cupList) {
	//					Player rankPlayer = Root.playerSystem.getPlayer(rankPlayerId);
	//					if (rankPlayer != null) {
	//						RankingBean rankingBean = new RankingBean(rankPlayer.getAccount().getNameInPlat(), rankPlayerId, rankPlayer.getPlayerStatistics().getCupNum(), (byte) 0,
	//								rankPlayer.getLevel());
	//						rankingList.add(rankingBean);
	//					}
	//				}
	//				rankingListMap.put("cup_" + area, rankingList);
	//			}
	//
	//			//score
	//			List<String> scoreList = new ArrayList<String>();
	//			for (String area : areas) {
	//				scoreList = RedisHelperJson.getRankingScore(area, rankingNum);
	//				for (String playerId_robotSlot : scoreList) {
	//					long playerId = Long.parseLong(playerId_robotSlot.split("_")[0]);
	//					int robotSlot = Integer.parseInt(playerId_robotSlot.split("_")[1]);
	//					Player rankPlayer = Root.playerSystem.getPlayer(playerId);
	//					RobotBag robotBag = Root.robotSystem.getRobotBag(playerId);
	//					if (rankPlayer != null && robotBag != null) {
	//						Robot robot = robotBag.getRobot(robotSlot);
	//						if (robot != null) {
	//							Part part = robot.getPart(PartSlotType.WEAPON.asCode());
	//							PartMaking weaponMaking = PartLoadData.getInstance().getMaking(PartSlotType.WEAPON.asCode(), part.getMakingId());
	//							RankingBean rankingBean = new RankingBean(rankPlayer.getAccount().getNameInPlat(), playerId, robot.refreshFightProperty().getScore(),
	//									(byte) weaponMaking.getWeaponType().asCode(), rankPlayer.getLevel());
	//							rankingList.add(rankingBean);
	//						}
	//					}
	//				}
	//				rankingListMap.put("score_" + area, rankingList);
	//			}
	//
	//		}
	//	}
}
