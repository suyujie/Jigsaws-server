package server.node.system.task;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.serialize.SerializerJson;
import gamecore.system.AbstractSystem;
import gamecore.system.SystemResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import server.node.dao.TaskDao;
import server.node.system.Root;
import server.node.system.battle.PveBattleResult;
import server.node.system.battle.PvpBattleResult;
import server.node.system.friend.FriendBag;
import server.node.system.mission.Mission;
import server.node.system.mission.MissionBag;
import server.node.system.npc.NpcPlayer;
import server.node.system.npc.NpcRobot;
import server.node.system.player.GoldType;
import server.node.system.player.Player;
import server.node.system.robot.FightProperty;
import server.node.system.robot.Robot;
import server.node.system.robotPart.Part;
import server.node.system.robotPart.PartLoadData;
import server.node.system.robotPart.PartMaking;
import server.node.system.robotPart.PartSlotType;
import server.node.system.task.task.TaskAttackWinNum;
import server.node.system.task.task.TaskCupNum;
import server.node.system.task.task.TaskDefenceWinNum;
import server.node.system.task.task.TaskFriendNum;
import server.node.system.task.task.TaskGainCashNum;
import server.node.system.task.task.TaskGivePowerNum;
import server.node.system.task.task.TaskHireRobotNum;
import server.node.system.task.task.TaskHitNum;
import server.node.system.task.task.TaskHomeLevel;
import server.node.system.task.task.TaskRentRobotNum;
import server.node.system.task.task.TaskRobotQualityNum;
import server.node.system.task.task.TaskStarNum;
import server.node.system.task.task.battle.TaskWinWithWeapon1Num;
import server.node.system.task.task.battle.TaskWinWithWeapon2Num;
import server.node.system.task.task.battle.TaskWinWithWeapon3Num;
import server.node.system.task.task.battle.TaskWinWithWeapon4Num;
import server.node.system.task.task.battle.TaskWinWithWeapon5Num;
import server.node.system.task.task.paint.TaskPaintNum;
import server.node.system.task.task.paint.TaskPaintPartQuality0Num;
import server.node.system.task.task.paint.TaskPaintPartQuality1Num;
import server.node.system.task.task.paint.TaskPaintPartQuality2Num;
import server.node.system.task.task.paint.TaskPaintPartQuality3Num;
import server.node.system.task.task.paint.TaskPaintPartQuality4Num;
import server.node.system.task.task.partLevel.TaskPartLevel;
import server.node.system.task.task.partLevel.TaskPartQuality0Level;
import server.node.system.task.task.partLevel.TaskPartQuality1Level;
import server.node.system.task.task.partLevel.TaskPartQuality2Level;
import server.node.system.task.task.partLevel.TaskPartQuality3Level;
import server.node.system.task.task.partLevel.TaskPartQuality4Level;
import server.node.system.task.task.partNum.TaskPartNum;
import server.node.system.task.task.partNum.TaskPartQuality2Num;
import server.node.system.task.task.partNum.TaskPartQuality3Num;
import server.node.system.task.task.partNum.TaskPartQuality4Num;
import server.node.system.task.task.pvp.TaskPvpBeatQuality2RobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatQuality3RobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatQuality4RobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatQualityRobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatRobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatSuitRobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatWeapon1RobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatWeapon2RobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatWeapon3RobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatWeapon4RobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatWeapon5RobotNum;
import server.node.system.task.task.pvp.TaskPvpGetCash;
import server.node.system.task.task.pvp.TaskPvpWinLevel;

/**
 * 任务系统
 */
public final class TaskSystem extends AbstractSystem {

	public TaskSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("TaskSystem start....");

		boolean b = TaskLoadData.getInstance().readData();

		System.out.println("TaskSystem start....OK");

		return b;
	}

	@Override
	public void shutdown() {
	}

	public TaskBag getTaskBag(Player player) throws SQLException {
		TaskBag taskBag = RedisHelperJson.getTaskBag(player.getId());
		if (taskBag == null) {
			taskBag = readTaskFromDB(player);
			taskBag.synchronize();
		}
		return taskBag;
	}

	private TaskBag readTaskFromDB(Player player) throws SQLException {
		TaskBag taskBag = null;

		TaskDao taskDao = DaoFactory.getInstance().borrowTaskDao();
		Map<String, Object> map = taskDao.readTask(player);
		DaoFactory.getInstance().returnTaskDao(taskDao);

		if (map != null) {
			int titleTaskId = ((Long) map.get("task_title_id")).intValue();
			String taskJson = (String) map.get("tasks");
			TaskBagPO taskBagPO = (TaskBagPO) SerializerJson.deSerialize(taskJson, TaskBagPO.class);
			taskBag = new TaskBag(player, titleTaskId, taskBagPO);

			taskBag.synchronize();

		} else {
			taskBag = initTaskBag(player);
		}

		return taskBag;
	}

	//初始化TaskBag
	private TaskBag initTaskBag(Player player) {

		TaskBagPO taskBagPO = new TaskBagPO();

		TaskBag taskBag = new TaskBag(player, -1, taskBagPO);//titleTaskId 默认 -1;

		TaskDao taskDao = DaoFactory.getInstance().borrowTaskDao();
		taskDao.saveTaskBag(player, taskBag);
		DaoFactory.getInstance().returnTaskDao(taskDao);

		return taskBag;
	}

	private void updateTasks(Player player, TaskBag taskBag) {
		TaskDao taskDao = DaoFactory.getInstance().borrowTaskDao();
		taskDao.updateTasks(player, taskBag);
		DaoFactory.getInstance().returnTaskDao(taskDao);
	}

	private void updateTaskTitleId(Player player, TaskBag taskBag) {
		TaskDao taskDao = DaoFactory.getInstance().borrowTaskDao();
		taskDao.updateTaskTitleId(player, taskBag);
		DaoFactory.getInstance().returnTaskDao(taskDao);
	}

	public SystemResult changeTitleTaskId(Player player, Integer titleTaskId) throws SQLException {
		SystemResult result = new SystemResult();
		TaskBag taskBag = getTaskBag(player);
		taskBag.setTitleTaskId(titleTaskId);
		taskBag.synchronize();
		updateTaskTitleId(player, taskBag);
		return result;
	}

	//领取奖励
	public SystemResult rewardTask(Player player, int taskMakingId, int level) throws SQLException {

		SystemResult result = new SystemResult();

		TaskBag taskBag = getTaskBag(player);
		TaskBagPO taskBagPO = taskBag.getTaskBagPO();

		int gold = 0;

		switch (TaskType.asEnum(taskMakingId)) {
		//		HomeLevel(0), //	0	运筹帷幄			升级主城至N级
		case HomeLevel:
			gold = taskBagPO.getTaskHomeLevel() == null ? 0 : taskBagPO.getTaskHomeLevel().reward(level);
			break;
		//		StarNum(1), //	1	邪恶终结者			在单人模式取得N颗星
		case StarNum:
			gold = taskBagPO.getTaskStarNum() == null ? 0 : taskBagPO.getTaskStarNum().reward(level);
			break;
		//		PartNum(2), //	2	收藏家				获得过N个零件
		case PartNum:
			gold = taskBagPO.getTaskPartNum() == null ? 0 : taskBagPO.getTaskPartNum().reward(level);
			break;
		//		PartQuality2Num(3), //	3	秘银爱好者			获得过N个银制零件
		case PartQuality2Num:
			gold = taskBagPO.getTaskPartQuality2Num() == null ? 0 : taskBagPO.getTaskPartQuality2Num().reward(level);
			break;
		//		PartQuality3Num(4), //	4	黄金爱好者			获得过N个金制零件
		case PartQuality3Num:
			gold = taskBagPO.getTaskPartQuality3Num() == null ? 0 : taskBagPO.getTaskPartQuality3Num().reward(level);
			break;
		//		PartQuality4Num(5), //	5	钛爱好者			获得过N个钛制零件
		case PartQuality4Num:
			gold = taskBagPO.getTaskPartQuality4Num() == null ? 0 : taskBagPO.getTaskPartQuality4Num().reward(level);
			break;
		//		PvpWinLevel(6), //	6	以小博大			多人游戏中，击败过主城N级的玩家
		case PvpWinLevel:
			gold = taskBagPO.getTaskPvpWinLevel() == null ? 0 : taskBagPO.getTaskPvpWinLevel().reward(level);
			break;
		//		PartLevel(7), //	7	改装大师			最高将零件改装至N级
		case PartLevel:
			gold = taskBagPO.getTaskPartLevel() == null ? 0 : taskBagPO.getTaskPartLevel().reward(level);
			break;
		//		PartQuality0Level(8), //	8	铁之魂				最高将铁质零件改装至N级
		case PartQuality0Level:
			gold = taskBagPO.getTaskPartQuality0Level() == null ? 0 : taskBagPO.getTaskPartQuality0Level().reward(level);
			break;
		//		PartQuality1Level(9), //	9	钢之魂				最高将铜质零件改装至N级
		case PartQuality1Level:
			gold = taskBagPO.getTaskPartQuality1Level() == null ? 0 : taskBagPO.getTaskPartQuality1Level().reward(level);
			break;
		//		PartQuality2Level(10), //	10	银之魂				最高将银质零件改装至N级
		case PartQuality2Level:
			gold = taskBagPO.getTaskPartQuality2Level() == null ? 0 : taskBagPO.getTaskPartQuality2Level().reward(level);
			break;
		//		PartQuality3Level(11), //	11	金之魂				最高将金质零件改装至N级
		case PartQuality3Level:
			gold = taskBagPO.getTaskPartQuality3Level() == null ? 0 : taskBagPO.getTaskPartQuality3Level().reward(level);
			break;
		//		PartQuality4Level(12), //	12	钛之魂				最高将钛质零件改装至N级
		case PartQuality4Level:
			gold = taskBagPO.getTaskPartQuality4Level() == null ? 0 : taskBagPO.getTaskPartQuality4Level().reward(level);
			break;
		//		CupNum(13), //	13	常胜将军			最高获得了N个勋章
		case CupNum:
			gold = taskBagPO.getTaskCupNum() == null ? 0 : taskBagPO.getTaskCupNum().reward(level);
			break;
		//		AttackWinNum(14), //	14	战无不胜			多人游戏中，进攻对手获得N场胜利
		case AttackWinNum:
			gold = taskBagPO.getTaskAttackWinNum() == null ? 0 : taskBagPO.getTaskAttackWinNum().reward(level);
			break;
		//		DefenceWinNum(15), //	15	坚不可摧			多人游戏中，成功防御N波进攻
		case DefenceWinNum:
			gold = taskBagPO.getTaskDefenceWinNum() == null ? 0 : taskBagPO.getTaskDefenceWinNum().reward(level);
			break;
		//		PvpBeatRobotNum(16), //	16	毁灭者				多人游戏中，摧毁N个敌方机器人
		case PvpBeatRobotNum:
			gold = taskBagPO.getTaskPvpBeatRobotNum() == null ? 0 : taskBagPO.getTaskPvpBeatRobotNum().reward(level);
			break;
		//		PvpBeatRobotWeapon3Num(17), //	17	双持炼狱			多人游戏中，摧毁N个双持机器人
		case PvpBeatRobotWeapon3Num:
			gold = taskBagPO.getTaskPvpBeatWeapon3RobotNum() == null ? 0 : taskBagPO.getTaskPvpBeatWeapon3RobotNum().reward(level);
			break;
		//		PvpBeatRobotWeapon1Num(18), //	18	长柄炼狱			多人游戏中，摧毁N个长柄机器人
		case PvpBeatRobotWeapon1Num:
			gold = taskBagPO.getTaskPvpBeatWeapon1RobotNum() == null ? 0 : taskBagPO.getTaskPvpBeatWeapon1RobotNum().reward(level);
			break;
		//		PvpBeatRobotWeapon5Num(19), //	19	盾剑炼狱			多人游戏中，摧毁N个盾剑机器人
		case PvpBeatRobotWeapon5Num:
			gold = taskBagPO.getTaskPvpBeatWeapon5RobotNum() == null ? 0 : taskBagPO.getTaskPvpBeatWeapon5RobotNum().reward(level);
			break;
		//		PvpBeatRobotWeapon4Num(20), //	20	枪械炼狱			多人游戏中，摧毁N个枪械机器人
		case PvpBeatRobotWeapon4Num:
			gold = taskBagPO.getTaskPvpBeatWeapon4RobotNum() == null ? 0 : taskBagPO.getTaskPvpBeatWeapon4RobotNum().reward(level);
			break;
		//		PvpBeatRobotWeapon2Num(21), //	21	重击炼狱			多人游戏中，摧毁N个重击机器人
		case PvpBeatRobotWeapon2Num:
			gold = taskBagPO.getTaskPvpBeatWeapon2RobotNum() == null ? 0 : taskBagPO.getTaskPvpBeatWeapon2RobotNum().reward(level);
			break;
		//		PvpBeatRobotSuitNum(22), //	22	美貌终结者			多人游戏中，消灭N个外形套装机器人
		case PvpBeatRobotSuitNum:
			gold = taskBagPO.getTaskPvpBeatSuitRobotNum() == null ? 0 : taskBagPO.getTaskPvpBeatSuitRobotNum().reward(level);
			break;
		//		PvpBeatRobotQualityNum(23), //	23	健身终结者			多人游戏中，消灭N个材质套装机器人
		case PvpBeatRobotQualityNum:
			gold = taskBagPO.getTaskPvpBeatQualityRobotNum() == null ? 0 : taskBagPO.getTaskPvpBeatQualityRobotNum().reward(level);
			break;
		//		PvpBeatRobotQuality2Num(24), //	24	秘银终结者			多人游戏中，消灭N个银质套装机器人
		case PvpBeatRobotQuality2Num:
			gold = taskBagPO.getTaskPvpBeatQuality2RobotNum() == null ? 0 : taskBagPO.getTaskPvpBeatQuality2RobotNum().reward(level);
			break;
		//		PvpBeatRobotQuality3Num(25), //	25	黄金终结者			多人游戏中，消灭N个金质套装机器人
		case PvpBeatRobotQuality3Num:
			gold = taskBagPO.getTaskPvpBeatQuality3RobotNum() == null ? 0 : taskBagPO.getTaskPvpBeatQuality3RobotNum().reward(level);
			break;
		//		PvpBeatRobotQuality4Num(26), //	26	钛终结者			多人游戏中，消灭N个钛质套装机器人
		case PvpBeatRobotQuality4Num:
			gold = taskBagPO.getTaskPvpBeatQuality4RobotNum() == null ? 0 : taskBagPO.getTaskPvpBeatQuality4RobotNum().reward(level);
			break;
		//		HitNum(27), //	27	杀戮机器			使用N次大招击中敌人
		case HitNum:
			gold = taskBagPO.getTaskHitNum() == null ? 0 : taskBagPO.getTaskHitNum().reward(level);
			break;
		//		PvpGetCash(28), //	28	淘金者				盗取N金币
		case PvpGetCash:
			gold = taskBagPO.getTaskPvpGetCash() == null ? 0 : taskBagPO.getTaskPvpGetCash().reward(level);
			break;
		//		RentRobotSuccessNum(29), //	29	造物者				成功出租N个机器人
		case RentRobotNum:
			gold = taskBagPO.getTaskRentRobotNum() == null ? 0 : taskBagPO.getTaskRentRobotNum().reward(level);
			break;
		//		HireRobotNum(30), //	30	机械总动员			租赁N个机器人
		case HireRobotNum:
			gold = taskBagPO.getTaskHireRobotNum() == null ? 0 : taskBagPO.getTaskHireRobotNum().reward(level);
			break;
		//		FriendNum(31), //	31	正义联盟			好友数量
		case FriendNum:
			gold = taskBagPO.getTaskFriendNum() == null ? 0 : taskBagPO.getTaskFriendNum().reward(level);
			break;
		//		GainCashNum(32), //	32	黄金矿主			在单人游戏的关卡矿点里收获N金钱
		case GainCashNum:
			gold = taskBagPO.getTaskGainCashNum() == null ? 0 : taskBagPO.getTaskGainCashNum().reward(level);
			break;
		//		RobotQualityNum(33), //	33	健身爱好者			拼出N种不同的材质套
		case RobotQualityNum:
			gold = taskBagPO.getTaskRobotQualityNum() == null ? 0 : taskBagPO.getTaskRobotQualityNum().reward(level);
			break;

		//		WinWithWeapon3Num(34), //	34	双持之魂			出战包含双持武器时，获得N场胜利
		case WinWithWeapon3Num:
			gold = taskBagPO.getTaskWinWithWeapon3Num() == null ? 0 : taskBagPO.getTaskWinWithWeapon3Num().reward(level);
			break;
		//		WinWithWeapon2Num(35), //	35	重击之魂			出战包含重击武器时，获得N场胜利
		case WinWithWeapon2Num:
			gold = taskBagPO.getTaskWinWithWeapon2Num() == null ? 0 : taskBagPO.getTaskWinWithWeapon2Num().reward(level);
			break;
		//		WinWithWeapon5Num(36), //	36	盾剑之魂			出战包含盾剑武器时，获得N场胜利
		case WinWithWeapon5Num:
			gold = taskBagPO.getTaskWinWithWeapon5Num() == null ? 0 : taskBagPO.getTaskWinWithWeapon5Num().reward(level);
			break;
		//		WinWithWeapon4Num(37), //	37	枪械之魂			出战包含枪械武器时，获得N场胜利
		case WinWithWeapon4Num:
			gold = taskBagPO.getTaskWinWithWeapon4Num() == null ? 0 : taskBagPO.getTaskWinWithWeapon4Num().reward(level);
			break;
		//		WinWithWeapon1Num(38), //	38	长柄之魂			出战包含长柄武器时，获得N场胜利
		case WinWithWeapon1Num:
			gold = taskBagPO.getTaskWinWithWeapon1Num() == null ? 0 : taskBagPO.getTaskWinWithWeapon1Num().reward(level);
			break;
		//		PaintNum(39), //	39	五彩缤纷			进行过N次色彩喷涂
		case PaintNum:
			gold = taskBagPO.getTaskPaintNum() == null ? 0 : taskBagPO.getTaskPaintNum().reward(level);
			break;
		//		PaintPartQuality0Num(40), //	40	绚丽的铁			共对铁质零件进行过N次色彩喷涂
		case PaintPartQuality0Num:
			gold = taskBagPO.getTaskPaintPartQuality0Num() == null ? 0 : taskBagPO.getTaskPaintPartQuality0Num().reward(level);
			break;
		//		PaintPartQuality1Num(41), //	41	绚丽的铜			共对铜质零件进行过N次色彩喷涂
		case PaintPartQuality1Num:
			gold = taskBagPO.getTaskPaintPartQuality1Num() == null ? 0 : taskBagPO.getTaskPaintPartQuality1Num().reward(level);
			break;
		//		PaintPartQuality2Num(42), //	42	绚丽的银			共对银质零件进行过N次色彩喷涂
		case PaintPartQuality2Num:
			gold = taskBagPO.getTaskPaintPartQuality2Num() == null ? 0 : taskBagPO.getTaskPaintPartQuality2Num().reward(level);
			break;
		//		PaintPartQuality3Num(43), //	43	绚丽的金			共对金质零件进行过N次色彩喷涂
		case PaintPartQuality3Num:
			gold = taskBagPO.getTaskPaintPartQuality3Num() == null ? 0 : taskBagPO.getTaskPaintPartQuality3Num().reward(level);
			break;
		//		PaintPartQuality4Num(44), //	44	绚丽的钛			共对钛质零件进行过N次色彩喷涂
		case PaintPartQuality4Num:
			gold = taskBagPO.getTaskPaintPartQuality4Num() == null ? 0 : taskBagPO.getTaskPaintPartQuality4Num().reward(level);
			break;
		//		GivePowerNum(45), //	45	友情至上			对好友赠送N次体力
		case GivePowerNum:
			gold = taskBagPO.getTaskGivePowerNum() == null ? 0 : taskBagPO.getTaskGivePowerNum().reward(level);
			break;

		default:
			break;
		}

		taskBag.synchronize();
		updateTasks(player, taskBag);

		if (gold > 0) {
			Root.playerSystem.changeGold(player, gold, GoldType.TASK_GET, false);
			player.synchronize();
		}

		return result;

	}

	private void addCurrentStatus(List<TaskCurrentStatus> currentStatuses, TaskCurrentStatus cs) {
		if (cs != null) {
			currentStatuses.add(cs);
		}
	}

	public List<TaskCurrentStatus> getCurrentStatus(Player player) throws SQLException {
		TaskBag taskBag = getTaskBag(player);
		return getCurrentStatus(taskBag);
	}

	public List<TaskCurrentStatus> getCurrentStatus(TaskBag taskBag) {

		List<TaskCurrentStatus> currentStatuses = new ArrayList<TaskCurrentStatus>();

		TaskBagPO taskBagPO = taskBag.getTaskBagPO();

		//		getTaskHomeLevel
		addCurrentStatus(currentStatuses, taskBagPO.getTaskHomeLevel() == null ? null : taskBagPO.getTaskHomeLevel().readCurrent());
		//		getTaskStarNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskStarNum() == null ? null : taskBagPO.getTaskStarNum().readCurrent());
		//		getTaskPartNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPartNum() == null ? null : taskBagPO.getTaskPartNum().readCurrent());
		//		getTaskPartQuality2Num
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPartQuality2Num() == null ? null : taskBagPO.getTaskPartQuality2Num().readCurrent());
		//		getTaskPartQuality3Num
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPartQuality3Num() == null ? null : taskBagPO.getTaskPartQuality3Num().readCurrent());
		//		getTaskPartQuality4Num
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPartQuality4Num() == null ? null : taskBagPO.getTaskPartQuality4Num().readCurrent());
		//		getTaskPvpWinLevel
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPvpWinLevel() == null ? null : taskBagPO.getTaskPvpWinLevel().readCurrent());
		//		getTaskPartLevel
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPartLevel() == null ? null : taskBagPO.getTaskPartLevel().readCurrent());
		//		getTaskPartQuality0Level
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPartQuality0Level() == null ? null : taskBagPO.getTaskPartQuality0Level().readCurrent());
		//		getTaskPartQuality1Level
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPartQuality1Level() == null ? null : taskBagPO.getTaskPartQuality1Level().readCurrent());
		//		getTaskPartQuality2Level
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPartQuality2Level() == null ? null : taskBagPO.getTaskPartQuality2Level().readCurrent());
		//		getTaskPartQuality3Level
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPartQuality3Level() == null ? null : taskBagPO.getTaskPartQuality3Level().readCurrent());
		//		getTaskPartQuality4Level
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPartQuality4Level() == null ? null : taskBagPO.getTaskPartQuality4Level().readCurrent());
		//		getTaskCupNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskCupNum() == null ? null : taskBagPO.getTaskCupNum().readCurrent());
		//		getTaskAttackWinNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskAttackWinNum() == null ? null : taskBagPO.getTaskAttackWinNum().readCurrent());
		//		getTaskDefenceWinNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskDefenceWinNum() == null ? null : taskBagPO.getTaskDefenceWinNum().readCurrent());
		//		getTaskPvpBeatRobotNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPvpBeatRobotNum() == null ? null : taskBagPO.getTaskPvpBeatRobotNum().readCurrent());
		//		getTaskPvpBeatWeapon3RobotNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPvpBeatWeapon3RobotNum() == null ? null : taskBagPO.getTaskPvpBeatWeapon3RobotNum().readCurrent());
		//		getTaskPvpBeatWeapon1RobotNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPvpBeatWeapon1RobotNum() == null ? null : taskBagPO.getTaskPvpBeatWeapon1RobotNum().readCurrent());
		//		getTaskPvpBeatWeapon5RobotNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPvpBeatWeapon5RobotNum() == null ? null : taskBagPO.getTaskPvpBeatWeapon5RobotNum().readCurrent());
		//		getTaskPvpBeatWeapon4RobotNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPvpBeatWeapon4RobotNum() == null ? null : taskBagPO.getTaskPvpBeatWeapon4RobotNum().readCurrent());
		//		getTaskPvpBeatWeapon2RobotNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPvpBeatWeapon2RobotNum() == null ? null : taskBagPO.getTaskPvpBeatWeapon2RobotNum().readCurrent());
		//		getTaskPvpBeatSuitRobotNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPvpBeatSuitRobotNum() == null ? null : taskBagPO.getTaskPvpBeatSuitRobotNum().readCurrent());
		//		getTaskPvpBeatQualityRobotNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPvpBeatQualityRobotNum() == null ? null : taskBagPO.getTaskPvpBeatQualityRobotNum().readCurrent());
		//		getTaskPvpBeatQuality2RobotNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPvpBeatQuality2RobotNum() == null ? null : taskBagPO.getTaskPvpBeatQuality2RobotNum().readCurrent());
		//		getTaskPvpBeatQuality3RobotNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPvpBeatQuality3RobotNum() == null ? null : taskBagPO.getTaskPvpBeatQuality3RobotNum().readCurrent());
		//		getTaskPvpBeatQuality4RobotNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPvpBeatQuality4RobotNum() == null ? null : taskBagPO.getTaskPvpBeatQuality4RobotNum().readCurrent());
		//		getTaskHitNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskHitNum() == null ? null : taskBagPO.getTaskHitNum().readCurrent());
		//		getTaskPvpGetCash
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPvpGetCash() == null ? null : taskBagPO.getTaskPvpGetCash().readCurrent());
		//		getTaskRentRobotSuccessNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskRentRobotNum() == null ? null : taskBagPO.getTaskRentRobotNum().readCurrent());
		//		getTaskHireRobotNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskHireRobotNum() == null ? null : taskBagPO.getTaskHireRobotNum().readCurrent());
		//		getTaskFriendNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskFriendNum() == null ? null : taskBagPO.getTaskFriendNum().readCurrent());
		//		getTaskGainCashNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskGainCashNum() == null ? null : taskBagPO.getTaskGainCashNum().readCurrent());
		//		getTaskRobotQualityNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskRobotQualityNum() == null ? null : taskBagPO.getTaskRobotQualityNum().readCurrent());
		//		getTaskWinWithWeapon3Num
		addCurrentStatus(currentStatuses, taskBagPO.getTaskWinWithWeapon3Num() == null ? null : taskBagPO.getTaskWinWithWeapon3Num().readCurrent());
		//		getTaskWinWithWeapon2Num
		addCurrentStatus(currentStatuses, taskBagPO.getTaskWinWithWeapon2Num() == null ? null : taskBagPO.getTaskWinWithWeapon2Num().readCurrent());
		//		getTaskWinWithWeapon5Num
		addCurrentStatus(currentStatuses, taskBagPO.getTaskWinWithWeapon5Num() == null ? null : taskBagPO.getTaskWinWithWeapon5Num().readCurrent());
		//		getTaskWinWithWeapon4Num
		addCurrentStatus(currentStatuses, taskBagPO.getTaskWinWithWeapon4Num() == null ? null : taskBagPO.getTaskWinWithWeapon4Num().readCurrent());
		//		getTaskWinWithWeapon1Num
		addCurrentStatus(currentStatuses, taskBagPO.getTaskWinWithWeapon1Num() == null ? null : taskBagPO.getTaskWinWithWeapon1Num().readCurrent());
		//		getTaskPaintNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPaintNum() == null ? null : taskBagPO.getTaskPaintNum().readCurrent());
		//		getTaskPaintPartQuality0Num
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPaintPartQuality0Num() == null ? null : taskBagPO.getTaskPaintPartQuality0Num().readCurrent());
		//		getTaskPaintPartQuality1Num
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPaintPartQuality1Num() == null ? null : taskBagPO.getTaskPaintPartQuality1Num().readCurrent());
		//		getTaskPaintPartQuality2Num
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPaintPartQuality2Num() == null ? null : taskBagPO.getTaskPaintPartQuality2Num().readCurrent());
		//		getTaskPaintPartQuality3Num
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPaintPartQuality3Num() == null ? null : taskBagPO.getTaskPaintPartQuality3Num().readCurrent());
		//		getTaskPaintPartQuality4Num
		addCurrentStatus(currentStatuses, taskBagPO.getTaskPaintPartQuality4Num() == null ? null : taskBagPO.getTaskPaintPartQuality4Num().readCurrent());
		//		getTaskGivePowerNum
		addCurrentStatus(currentStatuses, taskBagPO.getTaskGivePowerNum() == null ? null : taskBagPO.getTaskGivePowerNum().readCurrent());

		return currentStatuses;

	}

	//pve战斗结束后需要处理的任务[获得零件,获得不同材质的零件,持有武器]
	public void doTaskWhenExitPve(Player player, PveBattleResult pveBattleResult) throws SQLException {
		TaskBag taskBag = getTaskBag(player);
		//获取的新part
		List<Part> newParts = pveBattleResult.getParts();
		if (newParts != null && !newParts.isEmpty() && newParts.size() > 0) {
			doTaskPartNum(player, taskBag, newParts, false);
		}

		doTaskWinWithWeapon(pveBattleResult, null, player, taskBag, false);

		taskBag.synchronize();
		updateTasks(player, taskBag);
	}

	//pvp战斗结束后需要处理的任务
	public void doTaskWhenExitPvp(Player attacker, Player defender, PvpBattleResult pvpBattleResult) throws SQLException {

		TaskBag taskBag = getTaskBag(attacker);

		if (pvpBattleResult.isWin()) {
			doTaskCupNum(attacker, taskBag, false);
			doTaskAttackWinNum(attacker, taskBag, false);
			doTaskPvpWinLevel(attacker, taskBag, defender.getLevel(), false);
			doTaskPvpGetCash(attacker, taskBag, pvpBattleResult.attackerWinLootCash(), false);
			doTaskWinWithWeapon(null, pvpBattleResult, attacker, taskBag, false);

			taskBag.synchronize();
			updateTasks(attacker, taskBag);

		} else {
			//输了也会得到cash
			doTaskPvpGetCash(attacker, taskBag, pvpBattleResult.attackerWinLootCash(), false);
			Root.taskSystem.doTaskDefenceWinNum(defender);
		}

	}

	//pvp  npc 战斗结束后需要处理的任务
	public void doTaskWhenExitPvpNpc(Player attacker, NpcPlayer pvpNpc, PvpBattleResult pvpBattleResult) throws SQLException {

		TaskBag taskBag = getTaskBag(attacker);

		if (pvpBattleResult.isWin()) {
			doTaskCupNum(attacker, taskBag, false);
			doTaskAttackWinNum(attacker, taskBag, false);
			doTaskPvpWinLevel(attacker, taskBag, pvpNpc.getLevel(), false);
			doTaskPvpGetCash(attacker, taskBag, pvpBattleResult.attackerWinLootCash(), false);
			doTaskWinWithWeapon(null, pvpBattleResult, attacker, taskBag, false);

			taskBag.synchronize();
			updateTasks(attacker, taskBag);
		} else {
			//输了也会得到cash
			doTaskPvpGetCash(attacker, taskBag, pvpBattleResult.attackerWinLootCash(), false);
		}

	}

	//	HomeLevel(0), //	0	运筹帷幄			升级主城至N级
	public void doTaskHomeLevel(Player player) throws SQLException {
		TaskBag taskBag = getTaskBag(player);
		TaskHomeLevel task = taskBag.getTaskBagPO().getTaskHomeLevel();
		if (task == null) {
			task = new TaskHomeLevel();
			taskBag.getTaskBagPO().setTaskHomeLevel(task);
		}
		task.doTask(player);
		taskBag.synchronize();
		updateTasks(player, taskBag);
	}

	//	StarNum(1), //	1	邪恶终结者			在单人模式取得N颗星
	public void doTaskStarNum(Player player, MissionBag missionBag) throws SQLException {
		TaskBag taskBag = getTaskBag(player);
		TaskStarNum task = taskBag.getTaskBagPO().getTaskStarNum();
		if (task == null) {
			task = new TaskStarNum();
			taskBag.getTaskBagPO().setTaskStarNum(task);
		}

		//总星星数量
		int star = 0;
		for (Mission mission : missionBag.readMissions()) {
			star += mission.flushStarNum();
		}

		task.doTask(star);
		taskBag.synchronize();
		updateTasks(player, taskBag);
	}

	public void doTaskPartNum(Player player, TaskBag taskBag, List<Part> newPartPos, boolean sync) throws SQLException {
		if (taskBag == null) {
			taskBag = getTaskBag(player);
		}
		//		PartNum(2), //	2	收藏家				获得过N个零件
		TaskPartNum task = taskBag.getTaskBagPO().getTaskPartNum();
		if (task == null) {
			task = new TaskPartNum();
			taskBag.getTaskBagPO().setTaskPartNum(task);
		}
		task.doTask(newPartPos.size());

		//材质task
		int quality2Num = 0;
		int quality3Num = 0;
		int quality4Num = 0;
		for (Part part : newPartPos) {
			PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());
			if (partMaking != null && partMaking.getPartQualityType() != null) {

				switch (partMaking.getPartQualityType()) {
				case SILVER:
					quality2Num++;
					break;
				case GOLD:
					quality3Num++;
					break;
				case TITANIUM:
					quality4Num++;
					break;

				default:
					break;
				}

			}
		}

		//PartQuality2Num(3), //	3	秘银爱好者			获得过N个银制零件
		if (quality2Num > 0) {
			TaskPartQuality2Num taskQ2 = taskBag.getTaskBagPO().getTaskPartQuality2Num();
			if (taskQ2 == null) {
				taskQ2 = new TaskPartQuality2Num();
				taskBag.getTaskBagPO().setTaskPartQuality2Num(taskQ2);
			}
			taskQ2.doTask(quality2Num);
		}
		//PartQuality3Num(4), //	4	黄金爱好者			获得过N个金制零件
		if (quality3Num > 0) {
			TaskPartQuality3Num taskQ3 = taskBag.getTaskBagPO().getTaskPartQuality3Num();
			if (taskQ3 == null) {
				taskQ3 = new TaskPartQuality3Num();
				taskBag.getTaskBagPO().setTaskPartQuality3Num(taskQ3);
			}
			taskQ3.doTask(quality3Num);
		}
		//PartQuality4Num(5), //	5	钛爱好者			获得过N个钛制零件
		if (quality4Num > 0) {
			TaskPartQuality4Num taskQ4 = taskBag.getTaskBagPO().getTaskPartQuality4Num();
			if (taskQ4 == null) {
				taskQ4 = new TaskPartQuality4Num();
				taskBag.getTaskBagPO().setTaskPartQuality4Num(taskQ4);
			}
			taskQ4.doTask(quality4Num);
		}

		if (sync) {
			taskBag.synchronize();
			updateTasks(player, taskBag);
		}
	}

	//	PvpWinLevel(6), //	6	以小博大			多人游戏中，击败过主城N级的玩家
	public void doTaskPvpWinLevel(Player player, TaskBag taskBag, int defenderLevel, boolean sync) throws SQLException {
		if (taskBag == null) {
			taskBag = getTaskBag(player);
		}
		TaskPvpWinLevel task = taskBag.getTaskBagPO().getTaskPvpWinLevel();
		if (task == null) {
			task = new TaskPvpWinLevel();
			taskBag.getTaskBagPO().setTaskPvpWinLevel(task);
		}

		task.doTask(player, defenderLevel);
		if (sync) {
			taskBag.synchronize();
			updateTasks(player, taskBag);
		}
	}

	public void doTaskPartLevel(Player player, Part part) throws SQLException {
		TaskBag taskBag = getTaskBag(player);
		//		PartLevel(7), //	7	改装大师			最高将零件改装至N级
		TaskPartLevel task = taskBag.getTaskBagPO().getTaskPartLevel();
		if (task == null) {
			task = new TaskPartLevel();
			taskBag.getTaskBagPO().setTaskPartLevel(task);
		}
		task.doTask(part.getLevel());

		PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());
		switch (partMaking.getPartQualityType()) {
		case IRON:
			//			PartQuality0Level(8), //	8	铁之魂				最高将铁质零件改装至N级
			TaskPartQuality0Level taskQ0 = taskBag.getTaskBagPO().getTaskPartQuality0Level();
			if (taskQ0 == null) {
				taskQ0 = new TaskPartQuality0Level();
				taskBag.getTaskBagPO().setTaskPartQuality0Level(taskQ0);
			}
			taskQ0.doTask(part.getLevel());
			break;
		case COPPER:
			//	PartQuality1Level(9), //	9	钢之魂				最高将铜质零件改装至N级
			TaskPartQuality1Level taskQ1 = taskBag.getTaskBagPO().getTaskPartQuality1Level();
			if (taskQ1 == null) {
				taskQ1 = new TaskPartQuality1Level();
				taskBag.getTaskBagPO().setTaskPartQuality1Level(taskQ1);
			}
			taskQ1.doTask(part.getLevel());
			break;
		case SILVER:
			//	PartQuality2Level(10), //	10	银之魂				最高将银质零件改装至N级
			TaskPartQuality2Level taskQ2 = taskBag.getTaskBagPO().getTaskPartQuality2Level();
			if (taskQ2 == null) {
				taskQ2 = new TaskPartQuality2Level();
				taskBag.getTaskBagPO().setTaskPartQuality2Level(taskQ2);
			}
			taskQ2.doTask(part.getLevel());
			break;
		case GOLD:
			//	PartQuality3Level(11), //	11	金之魂				最高将金质零件改装至N级
			TaskPartQuality3Level taskQ3 = taskBag.getTaskBagPO().getTaskPartQuality3Level();
			if (taskQ3 == null) {
				taskQ3 = new TaskPartQuality3Level();
				taskBag.getTaskBagPO().setTaskPartQuality3Level(taskQ3);
			}
			taskQ3.doTask(part.getLevel());
			break;
		case TITANIUM:
			//	PartQuality4Level(12), //	12	钛之魂				最高将钛质零件改装至N级
			TaskPartQuality4Level taskQ4 = taskBag.getTaskBagPO().getTaskPartQuality4Level();
			if (taskQ4 == null) {
				taskQ4 = new TaskPartQuality4Level();
				taskBag.getTaskBagPO().setTaskPartQuality4Level(taskQ4);
			}
			taskQ4.doTask(part.getLevel());
			break;

		default:
			break;
		}

		taskBag.synchronize();
		updateTasks(player, taskBag);
	}

	//	CupNum(13), //	13	常胜将军			最高获得了N个勋章
	public void doTaskCupNum(Player player, TaskBag taskBag, boolean sync) throws SQLException {
		if (taskBag == null) {
			taskBag = getTaskBag(player);
		}
		TaskCupNum task = taskBag.getTaskBagPO().getTaskCupNum();
		if (task == null) {
			task = new TaskCupNum();
			taskBag.getTaskBagPO().setTaskCupNum(task);
		}
		task.doTask(player);
		if (sync) {
			taskBag.synchronize();
			updateTasks(player, taskBag);
		}
	}

	//	AttackWinNum(14), //	14	战无不胜			多人游戏中，进攻对手获得N场胜利
	public void doTaskAttackWinNum(Player player, TaskBag taskBag, boolean sync) throws SQLException {
		if (taskBag == null) {
			taskBag = getTaskBag(player);
		}
		TaskAttackWinNum task = taskBag.getTaskBagPO().getTaskAttackWinNum();
		if (task == null) {
			task = new TaskAttackWinNum();
			taskBag.getTaskBagPO().setTaskAttackWinNum(task);
		}
		task.doTask(player);
		if (sync) {
			taskBag.synchronize();
			updateTasks(player, taskBag);
		}
	}

	//	DefenceWinNum(15), //	15	坚不可摧			多人游戏中，成功防御N波进攻
	public void doTaskDefenceWinNum(Player player) throws SQLException {
		TaskBag taskBag = getTaskBag(player);
		TaskDefenceWinNum task = taskBag.getTaskBagPO().getTaskDefenceWinNum();
		if (task == null) {
			task = new TaskDefenceWinNum();
			taskBag.getTaskBagPO().setTaskDefenceWinNum(task);
		}
		task.doTask(player);
		taskBag.synchronize();
		updateTasks(player, taskBag);
	}

	public void doTaskPvpBeatRobot(Player player, Robot beatedRobot, NpcRobot beatedNpcRobot) throws SQLException {
		if (beatedRobot != null) {
			doTaskPvpBeatRobotNum(player, beatedRobot);
		}
		if (beatedNpcRobot != null) {
			doTaskPvpBeatRobotNpcNum(player, beatedNpcRobot);
		}
	}

	private void doTaskPvpBeatRobotNum(Player player, Robot beatedRobot) throws SQLException {
		TaskBag taskBag = getTaskBag(player);

		//		PvpBeatRobotNum(16), //	16	毁灭者				多人游戏中，摧毁N个敌方机器人
		TaskPvpBeatRobotNum task = taskBag.getTaskBagPO().getTaskPvpBeatRobotNum();
		if (task == null) {
			task = new TaskPvpBeatRobotNum();
			taskBag.getTaskBagPO().setTaskPvpBeatRobotNum(task);
		}
		task.doTask(player);

		FightProperty fightProperty = beatedRobot.refreshFightProperty();
		//外形套装
		if (fightProperty.isSameSuitType()) {
			//	PvpBeatRobotSuitNum(22), //	22	美貌终结者			多人游戏中，消灭N个外形套装机器人
			TaskPvpBeatSuitRobotNum taskSuit = taskBag.getTaskBagPO().getTaskPvpBeatSuitRobotNum();
			if (taskSuit == null) {
				taskSuit = new TaskPvpBeatSuitRobotNum();
				taskBag.getTaskBagPO().setTaskPvpBeatSuitRobotNum(taskSuit);
			}
			taskSuit.doTask();
		}
		//材质套装
		doTaskBeatQualityRobotNum(taskBag, fightProperty);
		//击败持有XX武器的机器人
		Part partPOWeapon = beatedRobot.readParts().get(PartSlotType.WEAPON.asCode());
		doTaskBeatWeaponRobotNum(taskBag, partPOWeapon);

		taskBag.synchronize();
		updateTasks(player, taskBag);
	}

	private void doTaskPvpBeatRobotNpcNum(Player player, NpcRobot beatedNpcRobot) throws SQLException {
		TaskBag taskBag = getTaskBag(player);

		//		PvpBeatRobotNum(16), //	16	毁灭者				多人游戏中，摧毁N个敌方机器人
		TaskPvpBeatRobotNum task = taskBag.getTaskBagPO().getTaskPvpBeatRobotNum();
		if (task == null) {
			task = new TaskPvpBeatRobotNum();
			taskBag.getTaskBagPO().setTaskPvpBeatRobotNum(task);
		}
		task.doTask(player);

		FightProperty fightProperty = beatedNpcRobot.refreshFightProperty();

		//外形套装
		if (fightProperty.isSameSuitType()) {
			//	PvpBeatRobotSuitNum(22), //	22	美貌终结者			多人游戏中，消灭N个外形套装机器人
			TaskPvpBeatSuitRobotNum taskSuit = taskBag.getTaskBagPO().getTaskPvpBeatSuitRobotNum();
			if (taskSuit == null) {
				taskSuit = new TaskPvpBeatSuitRobotNum();
				taskBag.getTaskBagPO().setTaskPvpBeatSuitRobotNum(taskSuit);
			}
			taskSuit.doTask();
		}
		//材质套装
		if (fightProperty.isSameQualityType()) {
			doTaskBeatQualityRobotNum(taskBag, fightProperty);
		}

		//击败持有XX武器的机器人
		Part partPOWeapon = beatedNpcRobot.getParts().get(PartSlotType.WEAPON.asCode());
		if (partPOWeapon != null) {
			doTaskBeatWeaponRobotNum(taskBag, partPOWeapon);
		}

		taskBag.synchronize();
		updateTasks(player, taskBag);
	}

	private void doTaskBeatQualityRobotNum(TaskBag taskBag, FightProperty fightProperty) {
		//材质套装
		if (fightProperty.isSameQualityType()) {
			//			PvpBeatRobotQualityNum(23), //	23	健身终结者			多人游戏中，消灭N个材质套装机器人
			TaskPvpBeatQualityRobotNum taskQ = taskBag.getTaskBagPO().getTaskPvpBeatQualityRobotNum();
			if (taskQ == null) {
				taskQ = new TaskPvpBeatQualityRobotNum();
				taskBag.getTaskBagPO().setTaskPvpBeatQualityRobotNum(taskQ);
			}
			taskQ.doTask();

			switch (fightProperty.getQualityType()) {
			case SILVER:
				//	PvpBeatRobotQuality2Num(24), //	24	秘银终结者			多人游戏中，消灭N个银质套装机器人
				TaskPvpBeatQuality2RobotNum taskQ2 = taskBag.getTaskBagPO().getTaskPvpBeatQuality2RobotNum();
				if (taskQ2 == null) {
					taskQ2 = new TaskPvpBeatQuality2RobotNum();
					taskBag.getTaskBagPO().setTaskPvpBeatQuality2RobotNum(taskQ2);
				}
				taskQ2.doTask();
				break;
			case GOLD:
				//	PvpBeatRobotQuality3Num(25), //	25	黄金终结者			多人游戏中，消灭N个金质套装机器人
				TaskPvpBeatQuality3RobotNum taskQ3 = taskBag.getTaskBagPO().getTaskPvpBeatQuality3RobotNum();
				if (taskQ3 == null) {
					taskQ3 = new TaskPvpBeatQuality3RobotNum();
					taskBag.getTaskBagPO().setTaskPvpBeatQuality3RobotNum(taskQ3);
				}
				taskQ3.doTask();
				break;

			case TITANIUM:
				//	PvpBeatRobotQuality4Num(26), //	26	钛终结者			多人游戏中，消灭N个钛质套装机器人
				TaskPvpBeatQuality4RobotNum taskQ4 = taskBag.getTaskBagPO().getTaskPvpBeatQuality4RobotNum();
				if (taskQ4 == null) {
					taskQ4 = new TaskPvpBeatQuality4RobotNum();
					taskBag.getTaskBagPO().setTaskPvpBeatQuality4RobotNum(taskQ4);
				}
				taskQ4.doTask();
				break;

			default:
				break;
			}
		}
	}

	private void doTaskBeatWeaponRobotNum(TaskBag taskBag, Part partPOWeapon) {

		PartMaking weaponMaking = PartLoadData.getInstance().getMaking(PartSlotType.WEAPON.asCode(), partPOWeapon.getMakingId());

		switch (weaponMaking.getWeaponType()) {
		//	NONE(0), GUN(1), SPEAR(2), DOUBLE(3), HEAVY(4), SHIELD(5);
		case DOUBLE:
			//	PvpBeatRobotWeapon3Num(17), //	17	双持炼狱			多人游戏中，摧毁N个双持机器人
			TaskPvpBeatWeapon3RobotNum taskW3 = taskBag.getTaskBagPO().getTaskPvpBeatWeapon3RobotNum();
			if (taskW3 == null) {
				taskW3 = new TaskPvpBeatWeapon3RobotNum();
				taskBag.getTaskBagPO().setTaskPvpBeatWeapon3RobotNum(taskW3);
			}
			taskW3.doTask();
			break;
		case SPEAR:
			//	PvpBeatRobotWeapon1Num(18), //	18	长柄炼狱			多人游戏中，摧毁N个长柄机器人
			TaskPvpBeatWeapon1RobotNum taskW1 = taskBag.getTaskBagPO().getTaskPvpBeatWeapon1RobotNum();
			if (taskW1 == null) {
				taskW1 = new TaskPvpBeatWeapon1RobotNum();
				taskBag.getTaskBagPO().setTaskPvpBeatWeapon1RobotNum(taskW1);
			}
			taskW1.doTask();
			break;
		case SHIELD:
			//	PvpBeatRobotWeapon5Num(19), //	19	盾剑炼狱			多人游戏中，摧毁N个盾剑机器人
			TaskPvpBeatWeapon5RobotNum taskW5 = taskBag.getTaskBagPO().getTaskPvpBeatWeapon5RobotNum();
			if (taskW5 == null) {
				taskW5 = new TaskPvpBeatWeapon5RobotNum();
				taskBag.getTaskBagPO().setTaskPvpBeatWeapon5RobotNum(taskW5);
			}
			taskW5.doTask();
			break;
		case GUN:
			//	PvpBeatRobotWeapon4Num(20), //	20	枪械炼狱			多人游戏中，摧毁N个枪械机器人
			TaskPvpBeatWeapon4RobotNum taskW4 = taskBag.getTaskBagPO().getTaskPvpBeatWeapon4RobotNum();
			if (taskW4 == null) {
				taskW4 = new TaskPvpBeatWeapon4RobotNum();
				taskBag.getTaskBagPO().setTaskPvpBeatWeapon4RobotNum(taskW4);
			}
			taskW4.doTask();
			break;
		case HEAVY:
			//	PvpBeatRobotWeapon2Num(21), //	21	重击炼狱			多人游戏中，摧毁N个重击机器人
			TaskPvpBeatWeapon2RobotNum taskW2 = taskBag.getTaskBagPO().getTaskPvpBeatWeapon2RobotNum();
			if (taskW2 == null) {
				taskW2 = new TaskPvpBeatWeapon2RobotNum();
				taskBag.getTaskBagPO().setTaskPvpBeatWeapon2RobotNum(taskW2);
			}
			taskW2.doTask();
			break;

		default:
			break;
		}
	}

	//	HitNum(27), //	27	杀戮机器			使用N次大招击中敌人
	public void doTaskHitNum(Player player, int hitNum) throws SQLException {
		TaskBag taskBag = getTaskBag(player);
		TaskHitNum task = taskBag.getTaskBagPO().getTaskHitNum();
		if (task == null) {
			task = new TaskHitNum();
			taskBag.getTaskBagPO().setTaskHitNum(task);
		}
		task.doTask(hitNum);
		taskBag.synchronize();
		updateTasks(player, taskBag);
	}

	//	PvpGetCash(28), //	28	淘金者				盗取N金币
	public void doTaskPvpGetCash(Player player, TaskBag taskBag, int cash, boolean sync) throws SQLException {
		if (taskBag == null) {
			taskBag = getTaskBag(player);
		}
		TaskPvpGetCash task = taskBag.getTaskBagPO().getTaskPvpGetCash();
		if (task == null) {
			task = new TaskPvpGetCash();
			taskBag.getTaskBagPO().setTaskPvpGetCash(task);
		}
		task.doTask(cash);
		if (sync) {
			taskBag.synchronize();
			updateTasks(player, taskBag);
		}
	}

	//	RentRobotSuccessNum(29), //	29	造物者				成功出租N个机器人
	public void doTaskRentRobotNum(Player player) throws SQLException {
		TaskBag taskBag = getTaskBag(player);
		TaskRentRobotNum task = taskBag.getTaskBagPO().getTaskRentRobotNum();
		if (task == null) {
			task = new TaskRentRobotNum();
			taskBag.getTaskBagPO().setTaskRentRobotNum(task);
		}
		task.doTask();
		taskBag.synchronize();
		updateTasks(player, taskBag);
	}

	//	HireRobotNum(30), //	30	机械总动员			租赁N个机器人
	public void doTaskHireRobotNum(Player player) throws SQLException {
		TaskBag taskBag = getTaskBag(player);
		TaskHireRobotNum task = taskBag.getTaskBagPO().getTaskHireRobotNum();
		if (task == null) {
			task = new TaskHireRobotNum();
			taskBag.getTaskBagPO().setTaskHireRobotNum(task);
		}
		task.doTask();
		taskBag.synchronize();
		updateTasks(player, taskBag);
	}

	//	FriendNum(31), //	31	正义联盟			好友数量
	public void doTaskFriendNum(Player player, FriendBag friendBag) throws SQLException {
		TaskBag taskBag = getTaskBag(player);
		TaskFriendNum task = taskBag.getTaskBagPO().getTaskFriendNum();
		if (task == null) {
			task = new TaskFriendNum();
			taskBag.getTaskBagPO().setTaskFriendNum(task);
		}
		task.doTask(player, friendBag);
		taskBag.synchronize();
		updateTasks(player, taskBag);
	}

	//	GainCashNum(32), //	32	黄金矿主			在单人游戏的关卡矿点里收获N金钱
	public void doTaskGainCashNum(Player player, int cash) throws SQLException {
		TaskBag taskBag = getTaskBag(player);
		TaskGainCashNum task = taskBag.getTaskBagPO().getTaskGainCashNum();
		if (task == null) {
			task = new TaskGainCashNum();
			taskBag.getTaskBagPO().setTaskGainCashNum(task);
		}
		task.doTask(player, cash);
		taskBag.synchronize();
		updateTasks(player, taskBag);
	}

	//	RobotQualityNum(33), //	33	健身爱好者			拼出N种不同的材质套
	public void doTaskRobotQualityNum(Player player, Robot robot) throws SQLException {
		FightProperty fightProperty = robot.refreshFightProperty();
		if (fightProperty.isSameQualityType()) {
			TaskBag taskBag = getTaskBag(player);
			TaskRobotQualityNum task = taskBag.getTaskBagPO().getTaskRobotQualityNum();
			if (task == null) {
				task = new TaskRobotQualityNum();
				taskBag.getTaskBagPO().setTaskRobotQualityNum(task);
			}
			task.doTask(fightProperty.getQualityType());
			taskBag.synchronize();
			updateTasks(player, taskBag);
		}

	}

	public void doTaskWinWithWeapon(PveBattleResult pveBattleResult, PvpBattleResult pvpBattleResult, Player player, TaskBag taskBag, boolean sync) throws SQLException {
		if (taskBag == null) {
			taskBag = getTaskBag(player);
		}

		List<Robot> robots;
		NpcRobot npcRobot = null;
		if (pveBattleResult != null) {
			robots = pveBattleResult.getPveBattle().readAttackRobotArrayAll();
			npcRobot = pveBattleResult.getPveBattle().getAttackNpcRobot();
		} else {
			robots = pvpBattleResult.getPvpBattle().readAttackRobotArray();
		}

		if (robots != null && !robots.isEmpty()) {

			boolean withDOUBLE = false;
			boolean withSPEAR = false;
			boolean withSHIELD = false;
			boolean withHEAVY = false;
			boolean withGUN = false;

			for (Robot robot : robots) {

				//weapon
				Part part = robot.readPart(PartSlotType.WEAPON.asCode());

				if (part != null) {
					PartMaking partMaking = PartLoadData.getInstance().getMaking(PartSlotType.WEAPON.asCode(), part.getMakingId());

					switch (partMaking.getWeaponType()) {
					case DOUBLE:
						withDOUBLE = true;
						break;
					case SPEAR:
						withSPEAR = true;
						break;
					case SHIELD:
						withSHIELD = true;
						break;
					case HEAVY:
						withHEAVY = true;
						break;
					case GUN:
						withGUN = true;
						break;

					default:
						break;
					}
				}

			}

			if (npcRobot != null) {
				//weapon
				Part part = npcRobot.getParts().get(PartSlotType.WEAPON.asCode());

				PartMaking partMaking = PartLoadData.getInstance().getMaking(PartSlotType.WEAPON.asCode(), part.getMakingId());

				switch (partMaking.getWeaponType()) {
				case DOUBLE:
					withDOUBLE = true;
					break;
				case SPEAR:
					withSPEAR = true;
					break;
				case SHIELD:
					withSHIELD = true;
					break;
				case HEAVY:
					withHEAVY = true;
					break;
				case GUN:
					withGUN = true;
					break;

				default:
					break;
				}
			}

			if (withDOUBLE) {
				//				TaskWinWithWeapon3Num(35), //	35	双持之魂			出战包含双持武器时，获得N场胜利
				TaskWinWithWeapon3Num taskW3 = taskBag.getTaskBagPO().getTaskWinWithWeapon3Num();
				if (taskW3 == null) {
					taskW3 = new TaskWinWithWeapon3Num();
					taskBag.getTaskBagPO().setTaskWinWithWeapon3Num(taskW3);
				}
				taskW3.doTask();
			}

			if (withHEAVY) {
				//					TaskWinWithWeapon2Num(36), //	36	重击之魂			出战包含重击武器时，获得N场胜利
				TaskWinWithWeapon2Num taskW2 = taskBag.getTaskBagPO().getTaskWinWithWeapon2Num();
				if (taskW2 == null) {
					taskW2 = new TaskWinWithWeapon2Num();
					taskBag.getTaskBagPO().setTaskWinWithWeapon2Num(taskW2);
				}
				taskW2.doTask();
			}

			if (withSHIELD) {
				//					TaskWinWithWeapon5Num(37), //	37	盾剑之魂			出战包含盾剑武器时，获得N场胜利
				TaskWinWithWeapon5Num taskW5 = taskBag.getTaskBagPO().getTaskWinWithWeapon5Num();
				if (taskW5 == null) {
					taskW5 = new TaskWinWithWeapon5Num();
					taskBag.getTaskBagPO().setTaskWinWithWeapon5Num(taskW5);
				}
				taskW5.doTask();
			}

			if (withGUN) {
				//					TaskWinWithWeapon4Num(38), //	38	枪械之魂			出战包含枪械武器时，获得N场胜利
				TaskWinWithWeapon4Num taskW4 = taskBag.getTaskBagPO().getTaskWinWithWeapon4Num();
				if (taskW4 == null) {
					taskW4 = new TaskWinWithWeapon4Num();
					taskBag.getTaskBagPO().setTaskWinWithWeapon4Num(taskW4);
				}
				taskW4.doTask();
			}

			if (withSPEAR) {
				//					TaskWinWithWeapon1Num(39), //	39	长柄之魂			出战包含长柄武器时，获得N场胜利
				TaskWinWithWeapon1Num taskW1 = taskBag.getTaskBagPO().getTaskWinWithWeapon1Num();
				if (taskW1 == null) {
					taskW1 = new TaskWinWithWeapon1Num();
					taskBag.getTaskBagPO().setTaskWinWithWeapon1Num(taskW1);
				}
				taskW1.doTask();
			}

		}

		if (sync) {
			taskBag.synchronize();
			updateTasks(player, taskBag);
		}
	}

	//涂装task
	public void doTaskPaintNum(Player player, List<Part> parts) throws SQLException {
		TaskBag taskBag = getTaskBag(player);
		//	PaintNum(40), //	40	五彩缤纷			进行过N次色彩喷涂
		TaskPaintNum task = taskBag.getTaskBagPO().getTaskPaintNum();
		if (task == null) {
			task = new TaskPaintNum();
			taskBag.getTaskBagPO().setTaskPaintNum(task);
		}
		task.doTask(parts.size());

		for (Part part : parts) {

			PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());

			switch (partMaking.getPartQualityType()) {
			case IRON:
				//			PaintPartQuality0Num(41), //	41	绚丽的铁			共对铁质零件进行过N次色彩喷涂
				TaskPaintPartQuality0Num taskQ0 = taskBag.getTaskBagPO().getTaskPaintPartQuality0Num();
				if (taskQ0 == null) {
					taskQ0 = new TaskPaintPartQuality0Num();
					taskBag.getTaskBagPO().setTaskPaintPartQuality0Num(taskQ0);
				}
				taskQ0.doTask();
				break;
			case COPPER:
				//			PaintPartQuality1Num(42), //	42	绚丽的铜			共对铜质零件进行过N次色彩喷涂
				TaskPaintPartQuality1Num taskQ1 = taskBag.getTaskBagPO().getTaskPaintPartQuality1Num();
				if (taskQ1 == null) {
					taskQ1 = new TaskPaintPartQuality1Num();
					taskBag.getTaskBagPO().setTaskPaintPartQuality1Num(taskQ1);
				}
				taskQ1.doTask();
				break;
			case SILVER:
				//			PaintPartQuality2Num(43), //	43	绚丽的银			共对银质零件进行过N次色彩喷涂
				TaskPaintPartQuality2Num taskQ2 = taskBag.getTaskBagPO().getTaskPaintPartQuality2Num();
				if (taskQ2 == null) {
					taskQ2 = new TaskPaintPartQuality2Num();
					taskBag.getTaskBagPO().setTaskPaintPartQuality2Num(taskQ2);
				}
				taskQ2.doTask();
				break;
			case GOLD:
				//			PaintPartQuality3Num(44), //	44	绚丽的金			共对金质零件进行过N次色彩喷涂
				TaskPaintPartQuality3Num taskQ3 = taskBag.getTaskBagPO().getTaskPaintPartQuality3Num();
				if (taskQ3 == null) {
					taskQ3 = new TaskPaintPartQuality3Num();
					taskBag.getTaskBagPO().setTaskPaintPartQuality3Num(taskQ3);
				}
				taskQ3.doTask();
				break;
			case TITANIUM:
				//			PaintPartQuality4Num(45), //	45	绚丽的钛			共对钛质零件进行过N次色彩喷涂
				TaskPaintPartQuality4Num taskQ4 = taskBag.getTaskBagPO().getTaskPaintPartQuality4Num();
				if (taskQ4 == null) {
					taskQ4 = new TaskPaintPartQuality4Num();
					taskBag.getTaskBagPO().setTaskPaintPartQuality4Num(taskQ4);
				}
				taskQ4.doTask();
				break;

			default:
				break;
			}

		}

		taskBag.synchronize();
		updateTasks(player, taskBag);
	}

	//	GivePowerNum(46), //	46	友情至上			对好友赠送N次体力
	public void doTaskGivePowerNum(Player player) throws SQLException {
		TaskBag taskBag = getTaskBag(player);
		TaskGivePowerNum task = taskBag.getTaskBagPO().getTaskGivePowerNum();
		if (task == null) {
			task = new TaskGivePowerNum();
			taskBag.getTaskBagPO().setTaskGivePowerNum(task);
		}
		task.doTask();
		taskBag.synchronize();
		updateTasks(player, taskBag);
	}

}
