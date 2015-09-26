package server.node.system.npc;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.system.AbstractSystem;
import gamecore.util.RangeExpansion;
import gamecore.util.Utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Root;
import server.node.system.player.Player;
import server.node.system.robot.FightProperty;
import server.node.system.robot.Robot;
import server.node.system.robot.RobotBag;
import server.node.system.robot.RobotType;
import server.node.system.robotPart.Part;
import server.node.system.robotPart.PartSlotType;

/**
 * npc系统。
 */
public final class NpcSystem extends AbstractSystem {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger(NpcSystem.class.getName());

	private static int[] firstRentNeedCash = { 30, 70, 80, 180, 400 };

	@Override
	public boolean startup() {
		System.out.println("NpcSystem start....");

		boolean b = NpcLoadData.getInstance().readData();

		System.out.println("NpcSystem start....OK");

		return b;
	}

	@Override
	public void shutdown() {
	}

	public NpcPlayer getPvpNpc(Player player, boolean first) {

		//随机取一个npc
		NpcMaking npcMaking = (NpcMaking) Utils.randomSelectOne(NpcLoadData.getInstance().npcMakings);

		int npcLevel = player.getLevel();

		if (first) {
			npcLevel = 1;
		} else {
			npcLevel = Utils.randomInt(player.getLevel() - 2, player.getLevel() + 2);
		}

		//我方机器人的平均分
		RobotBag robotBag = Root.robotSystem.getRobotBag(player);

		int avgScore = 0;
		int maxScore = 0;
		List<Robot> myRobots = robotBag.readRobots(RobotType.BATTLE);
		for (Robot robot : myRobots) {
			FightProperty fightProperty = robot.refreshFightProperty();
			avgScore += fightProperty.getScore();
			if (fightProperty.getScore() > maxScore) {
				maxScore = fightProperty.getScore();
			}
		}
		avgScore = avgScore / myRobots.size();

		List<NpcRobot> npcRobots = new ArrayList<NpcRobot>();

		if (first) {
			NpcLoadData.getInstance().getNpcRobotFirst(npcRobots, 3);
		} else {
			RangeExpansion re = new RangeExpansion((int) (avgScore * 0.9F), (int) (avgScore * 1.1F), 0, avgScore * 10, (int) (avgScore * 0.1F));

			int[] init = re.init();
			NpcLoadData.getInstance().getNpcRobots(npcRobots, init[0], init[1], 3);

			while (npcRobots.size() < 3) {

				int[] redown = re.down();
				int[] reup = re.up();
				if (redown[2] == 0 && reup[2] == 0) {
					break;
				}

				NpcLoadData.getInstance().getNpcRobots(npcRobots, redown[0], redown[1], 3);

				NpcLoadData.getInstance().getNpcRobots(npcRobots, reup[0], reup[1], 3);

			}
		}

		int cash = npcLevel * 10 + avgScore * 2;

		//构造npcPlayer
		NpcPlayer npcPlayer = new NpcPlayer(-Utils.getOneLongId(), npcMaking.getName(), npcLevel, cash, npcRobots, null);

		//先放一小时
		npcPlayer.synchronize(1);

		return npcPlayer;

	}

	@SuppressWarnings("unchecked")
	public List<NpcPlayer> getRentNpc(Player player, int num, boolean isFirst) {

		List<NpcPlayer> list = new ArrayList<NpcPlayer>();

		//随机取num个npc
		List<NpcMaking> npcMakings = (List<NpcMaking>) Utils.randomSelect(NpcLoadData.getInstance().npcMakings, num);

		//我方机器人的最大score
		RobotBag robotBag = Root.robotSystem.getRobotBag(player);

		int maxScore = 0;
		List<Robot> myRobots = robotBag.readRobots(RobotType.BATTLE);
		for (Robot robot : myRobots) {
			FightProperty fightProperty = robot.refreshFightProperty();
			if (maxScore < fightProperty.getScore()) {
				maxScore = fightProperty.getScore();
			}
		}

		//当玩家等级超过25级之后，租赁机器人时，不再出现比自己厉害的假人。
		//首先按现在的逻辑搜索真人，如果没有真人，出现假人的时候，只按自己的分值的60%~80%生成假人。
		RangeExpansion re = null;

		if (player.getLevel() >= 25) {
			re = new RangeExpansion((int) (maxScore * 0.3), (int) (maxScore * 0.5), (int) (maxScore * 0.1), (int) (maxScore * 0.8), maxScore / 10 + 1);
		} else {
			re = new RangeExpansion((int) (maxScore * 0.9), (int) (maxScore * 1.1), (int) (maxScore * 0.1), (int) (maxScore * 10), maxScore / 10 + 1);
		}

		//robot的数量要跟num一样
		List<NpcRobot> npcRobots = new ArrayList<NpcRobot>();

		int[] init = re.init();
		NpcLoadData.getInstance().getNpcRobots(npcRobots, init[0], init[1], num);

		while (npcRobots.size() < num) {
			int[] redown = re.down();
			int[] reup = re.up();
			if (redown[2] == 0 && reup[2] == 0) {
				break;
			}
			NpcLoadData.getInstance().getNpcRobots(npcRobots, redown[0], redown[1], num);

			NpcLoadData.getInstance().getNpcRobots(npcRobots, reup[0], reup[1], num);

		}

		for (int i = 0; i < npcMakings.size(); i++) {

			NpcMaking npcMaking = npcMakings.get(i);
			NpcRobot npcRobot = npcRobots.get(i);
			if (npcMaking != null && npcRobot != null) {

				FightProperty npcFightProperty = npcRobot.refreshFightProperty();

				int npcLevel = Utils.randomInt(player.getLevel() - 2, player.getLevel() + 2);
				int cash = npcFightProperty.getScore() * Utils.randomInt(80, 120) / 100;//租借需要的钱  战力的80%~120%浮动
				if (isFirst) {//如果第一次，钱是固定的
					cash = firstRentNeedCash[i];
				}

				NpcPlayer npcPlayer = new NpcPlayer(Utils.getOneLongId(), npcMaking.getName(), npcLevel, cash, null, npcRobot);

				npcPlayer.synchronize(1);
				list.add(npcPlayer);
			}
		}

		return list;

	}

	public NpcPlayer getNpcPlayer(Long id) {
		return RedisHelperJson.getNpcPlayer(id);
	}

	/**
	 * 获取出战的三个机器人使用的武器的id
	 */
	public String getWeaponIds(long npcId) {
		StringBuffer weaponStr = new StringBuffer();

		NpcPlayer npcPlayer = getNpcPlayer(npcId);

		for (NpcRobot npcRobot : npcPlayer.getAttackRobots()) {
			if (npcRobot != null) {
				Part part = npcRobot.getParts().get(PartSlotType.WEAPON.asCode());
				if (part != null) {
					weaponStr.append(part.getMakingId()).append("-");
				}
			}
		}
		return weaponStr.toString();
	}

}
