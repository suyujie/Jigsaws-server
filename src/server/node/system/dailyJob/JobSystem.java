package server.node.system.dailyJob;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.serialize.SerializerJson;
import gamecore.system.AbstractSystem;
import gamecore.system.SystemResult;
import gamecore.util.Clock;
import gamecore.util.DateUtils;
import gamecore.util.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import server.node.dao.DailyJobDao;
import server.node.system.Root;
import server.node.system.dailyJob.job.JobBiShaHit;
import server.node.system.dailyJob.job.JobBishaKill;
import server.node.system.dailyJob.job.JobFaceBookShare;
import server.node.system.dailyJob.job.JobGivePower;
import server.node.system.dailyJob.job.JobHireRobot;
import server.node.system.dailyJob.job.JobMoreFight;
import server.node.system.dailyJob.job.JobPaint;
import server.node.system.dailyJob.job.JobPartGet;
import server.node.system.dailyJob.job.JobPartLevelUp;
import server.node.system.dailyJob.job.JobPvp;
import server.node.system.dailyJob.job.JobPvp1vs3;
import server.node.system.dailyJob.job.JobPvpBeatWeapon;
import server.node.system.dailyJob.job.JobPvpWin;
import server.node.system.dailyJob.job.JobRentRobot;
import server.node.system.dailyJob.job.JobRepaireRobot;
import server.node.system.dailyJob.job.JobVisitPlayer;
import server.node.system.dailyJob.job.JobWeaponFight;
import server.node.system.dailyJob.job.JobWinNoHit;
import server.node.system.dailyJob.job.JobWinOne;
import server.node.system.dailyJob.job.JobXuLiHit;
import server.node.system.dailyJob.job.JobXuLiKill;
import server.node.system.mission.PointLoadData;
import server.node.system.mission.Point;
import server.node.system.npc.NpcRobot;
import server.node.system.player.GoldType;
import server.node.system.player.Player;
import server.node.system.robot.Robot;
import server.node.system.robotPart.Part;
import server.node.system.robotPart.PartLoadData;
import server.node.system.robotPart.PartMaking;
import server.node.system.robotPart.PartSlotType;
import server.node.system.robotPart.WeaponType;
import common.language.LangType;

/**
 * 每日任务系统
 */
public final class JobSystem extends AbstractSystem {

	public JobSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("JobSystem start....");
		boolean b = JobLoadData.getInstance().readData();
		System.out.println("JobSystem start....OK");
		return b;
	}

	@Override
	public void shutdown() {
	}

	public JobBag getJobBag(Player player) throws SQLException {

		JobBag jobBag = RedisHelperJson.getJobBag(player.getId());

		if (jobBag == null) {
			jobBag = readJobBagFromDB(player);
		}

		//如果时间是0,表示之前create的时候还没有通过教学,那么就检查是否通过了教学
		if (jobBag.getJobBagPO().getCreateTime() == 0) {
			Point pointPO = Root.missionSystem.getPointPO(player, PointLoadData.getInstance().LastToturialPoint);
			if (pointPO != null && pointPO.getPassStar() > 0) {//通过第八关,通过教学了,生成每日任务
				createjobBagPO(jobBag);
				updateDB(player, jobBag);
				jobBag.synchronize();
				this.publish(new DailyJobMessage(DailyJobMessage.NewJob, player));
			}
		} else {//早就通过了教学,重新生成 每日任务
			if (!DateUtils.isSameDay(Clock.currentTimeMillis(), jobBag.getJobBagPO().getCreateTime() * 1000)) {
				Root.logSystem.addDailyJobLog(player, jobBag);
				createjobBagPO(jobBag);
				updateDB(player, jobBag);
				jobBag.synchronize();
				this.publish(new DailyJobMessage(DailyJobMessage.NewJob, player));
			}
		}

		return jobBag;
	}

	private JobBag readJobBagFromDB(Player player) throws SQLException {
		JobBag jobBag = null;

		DailyJobDao dailyJobDao = DaoFactory.getInstance().borrowDailyJobDao();
		Map<String, Object> map = dailyJobDao.readDailyJob(player);
		DaoFactory.getInstance().returnDailyJobDao(dailyJobDao);

		if (map != null) {
			String json = (String) map.get("jobs");
			JobBagPO jobBagPO = (JobBagPO) SerializerJson.deSerialize(json, JobBagPO.class);
			jobBag = new JobBag(player, jobBagPO);
		} else {
			jobBag = initJobBag(player);
		}
		return jobBag;
	}

	//初始化JobBag
	private JobBag initJobBag(Player player) {

		//初始化的时候,生成一个空的每日任务包
		JobBagPO jobBagPO = null;

		jobBagPO = new JobBagPO();
		jobBagPO.setCreateTime(0);

		JobBag jobBag = new JobBag(player, jobBagPO);
		jobBag.synchronize();

		saveDB(player, jobBag);

		return jobBag;
	}

	//生成每日任务
	@SuppressWarnings("unchecked")
	private void createjobBagPO(JobBag jobBag) {

		JobBagPO jobBagPO = new JobBagPO();
		jobBagPO.setCreateTime(Clock.currentTimeSecond());

		List<Integer> ids = new ArrayList<Integer>();
		ids.addAll((List<Integer>) Utils.randomSelect(JobLoadData.getInstance().getGroupJobs(1), 1));
		ids.addAll((List<Integer>) Utils.randomSelect(JobLoadData.getInstance().getGroupJobs(2), 2));
		ids.addAll((List<Integer>) Utils.randomSelect(JobLoadData.getInstance().getGroupJobs(3), 1));
		ids.addAll((List<Integer>) Utils.randomSelect(JobLoadData.getInstance().getGroupJobs(4), 1));

		for (Integer jobId : ids) {
			switch (JobType.asEnum(jobId)) {
			case WinOne:
				jobBagPO.setJobWinOne(new JobWinOne());
				break;
			case MoreFight:
				jobBagPO.setJobMoreFight(new JobMoreFight());
				break;
			case XuLiHit:
				jobBagPO.setJobXuLiHit(new JobXuLiHit());
				break;
			case BiShaHit:
				jobBagPO.setJobBiShaHit(new JobBiShaHit());
				break;
			case WeaponFight:
				jobBagPO.setJobWeaponFight(new JobWeaponFight(WeaponType.randomOneWithOutNone()));
				break;
			case GivePower:
				jobBagPO.setJobGivePower(new JobGivePower());
				break;
			case BishaKill:
				jobBagPO.setJobBishaKill(new JobBishaKill());
				break;
			case XuLiKill:
				jobBagPO.setJobXuLiKill(new JobXuLiKill());
				break;
			case FaceBookShare:
				jobBagPO.setJobFaceBookShare(new JobFaceBookShare());
				break;
			case PartLevelUp:
				jobBagPO.setJobPartLevelUp(new JobPartLevelUp());
				break;
			case PVP:
				jobBagPO.setJobPvp(new JobPvp());
				break;
			case HireRobot:
				jobBagPO.setJobHireRobot(new JobHireRobot());
				break;
			case RentRobot:
				jobBagPO.setJobRentRobot(new JobRentRobot());
				break;
			case PartGet:
				jobBagPO.setJobPartGet(new JobPartGet());
				break;
			case RepaireRobot:
				jobBagPO.setJobRepaireRobot(new JobRepaireRobot());
				break;
			case VisitPlayer:
				jobBagPO.setJobVisitPlayer(new JobVisitPlayer());
				break;
			case Paint:
				jobBagPO.setJobPaint(new JobPaint());
				break;
			case WinNoHit:
				jobBagPO.setJobWinNoHit(new JobWinNoHit());
				break;
			case PvpWin:
				jobBagPO.setJobPvpWin(new JobPvpWin());
				break;
			case PvpBeatWeapon:
				jobBagPO.setJobPvpBeatWeapon(new JobPvpBeatWeapon(WeaponType.randomOneWithOutNone()));
				break;
			case Pvp1vs3:
				jobBagPO.setJobPvp1vs3(new JobPvp1vs3());
				break;
			default:
				break;
			}
		}

		jobBag.setJobBagPO(jobBagPO);

	}

	//领取奖励
	public SystemResult rewardJob(Player player, int jobMakingId) throws SQLException {

		SystemResult result = new SystemResult();

		JobBag jobBag = getJobBag(player);
		JobBagPO jobBagPO = jobBag.getJobBagPO();

		int gold = 0;

		switch (JobType.asEnum(jobMakingId)) {
		case WinOne:
			gold = jobBagPO.getJobWinOne() == null ? 0 : jobBagPO.getJobWinOne().reward();
			break;
		case MoreFight:
			gold = jobBagPO.getJobMoreFight() == null ? 0 : jobBagPO.getJobMoreFight().reward();
			break;
		case XuLiHit:
			gold = jobBagPO.getJobXuLiHit() == null ? 0 : jobBagPO.getJobXuLiHit().reward();
			break;
		case BiShaHit:
			gold = jobBagPO.getJobBiShaHit() == null ? 0 : jobBagPO.getJobBiShaHit().reward();
			break;
		case WeaponFight:
			gold = jobBagPO.getJobWeaponFight() == null ? 0 : jobBagPO.getJobWeaponFight().reward();
			break;
		case GivePower:
			gold = jobBagPO.getJobGivePower() == null ? 0 : jobBagPO.getJobGivePower().reward();
			break;
		case BishaKill:
			gold = jobBagPO.getJobBishaKill() == null ? 0 : jobBagPO.getJobBishaKill().reward();
			break;
		case XuLiKill:
			gold = jobBagPO.getJobXuLiKill() == null ? 0 : jobBagPO.getJobXuLiKill().reward();
			break;
		case FaceBookShare:
			gold = jobBagPO.getJobFaceBookShare() == null ? 0 : jobBagPO.getJobFaceBookShare().reward();
			break;
		case PartLevelUp:
			gold = jobBagPO.getJobPartLevelUp() == null ? 0 : jobBagPO.getJobPartLevelUp().reward();
			break;
		case PVP:
			gold = jobBagPO.getJobPvp() == null ? 0 : jobBagPO.getJobPvp().reward();
			break;
		case HireRobot:
			gold = jobBagPO.getJobHireRobot() == null ? 0 : jobBagPO.getJobHireRobot().reward();
			break;
		case RentRobot:
			gold = jobBagPO.getJobRentRobot() == null ? 0 : jobBagPO.getJobRentRobot().reward();
			break;
		case PartGet:
			gold = jobBagPO.getJobPartGet() == null ? 0 : jobBagPO.getJobPartGet().reward();
			break;
		case RepaireRobot:
			gold = jobBagPO.getJobRepaireRobot() == null ? 0 : jobBagPO.getJobRepaireRobot().reward();
			break;
		case VisitPlayer:
			gold = jobBagPO.getJobVisitPlayer() == null ? 0 : jobBagPO.getJobVisitPlayer().reward();
			break;
		case Paint:
			gold = jobBagPO.getJobPaint() == null ? 0 : jobBagPO.getJobPaint().reward();
			break;
		case WinNoHit:
			gold = jobBagPO.getJobWinNoHit() == null ? 0 : jobBagPO.getJobWinNoHit().reward();
			break;
		case PvpWin:
			gold = jobBagPO.getJobPvpWin() == null ? 0 : jobBagPO.getJobPvpWin().reward();
			break;
		case PvpBeatWeapon:
			gold = jobBagPO.getJobPvpBeatWeapon() == null ? 0 : jobBagPO.getJobPvpBeatWeapon().reward();
			break;
		case Pvp1vs3:
			gold = jobBagPO.getJobPvp1vs3() == null ? 0 : jobBagPO.getJobPvp1vs3().reward();
			break;

		default:
			break;
		}

		if (gold > 0) {

			updateDB(player, jobBag);
			jobBag.synchronize();

			Root.playerSystem.changeGold(player, gold, GoldType.DAILYJOB_GET, false);
			player.synchronize();
		}

		return result;

	}

	//save to db
	private void saveDB(Player player, JobBag jobBag) {
		DailyJobDao dailyJobDao = DaoFactory.getInstance().borrowDailyJobDao();
		dailyJobDao.saveDailyJob(player, jobBag);
		DaoFactory.getInstance().returnDailyJobDao(dailyJobDao);
	}

	//save to db
	private void updateDB(Player player, JobBag jobBag) {
		DailyJobDao dailyJobDao = DaoFactory.getInstance().borrowDailyJobDao();
		dailyJobDao.updateDailyJob(player, jobBag);
		DaoFactory.getInstance().returnDailyJobDao(dailyJobDao);
	}

	//		旗开得胜	取得一场战斗胜利。
	public void doJobWinOne(Player player, JobBag jobBag, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobWinOne job = (JobWinOne) jobBag.getJobBagPO().getJobWinOne();
		if (job != null) {
			job.doJob();
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//		越战越勇	进行三场比赛。
	public void doJobMoreFight(Player player, JobBag jobBag, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobMoreFight job = (JobMoreFight) jobBag.getJobBagPO().getJobMoreFight();
		if (job != null) {
			job.doJob();
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//	蓄力高手	战斗中使用10次蓄力攻击击中敌人。
	public void doJobXuLiHit(Player player, JobBag jobBag, int hitXuLiNum, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobXuLiHit job = (JobXuLiHit) jobBag.getJobBagPO().getJobXuLiHit();
		if (job != null) {
			job.doJob(hitXuLiNum);
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//	能量溢出	战斗中使用5次超必杀击中敌人。
	public void doJobBiShaHit(Player player, JobBag jobBag, int hitBiShaNum, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobBiShaHit job = (JobBiShaHit) jobBag.getJobBagPO().getJobBiShaHit();
		if (job != null) {
			job.doJob(hitBiShaNum);
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//	武器大师	使用X系武器参加一场战斗。［可租赁］(这个武器需要随一种，另外特殊限定条件见旁边说明)
	public void doJobWeaponFight(Player player, JobBag jobBag, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobWeaponFight job = (JobWeaponFight) jobBag.getJobBagPO().getJobWeaponFight();
		if (job != null) {
			job.doJob();
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//	互相帮助	为好友送n点体力。
	public void doJobGivePower(Player player, JobBag jobBag, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobGivePower job = (JobGivePower) jobBag.getJobBagPO().getJobGivePower();
		if (job != null) {
			job.doJob();
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//	超级杀手	使用超必杀摧毁一名敌人。
	public void doJobBishaKill(Player player, JobBag jobBag, int biShaKill, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobBishaKill job = (JobBishaKill) jobBag.getJobBagPO().getJobBishaKill();
		if (job != null) {
			job.doJob(biShaKill);
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//	冷血杀手	使用蓄力攻击摧毁一名敌人。
	public void doJobXuLiKill(Player player, JobBag jobBag, int xuliKill, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobXuLiKill job = (JobXuLiKill) jobBag.getJobBagPO().getJobXuLiKill();
		if (job != null) {
			job.doJob(xuliKill);
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//	亲友同乐	使用FB分享一次战果。
	public void doJobFaceBookShare(Player player, JobBag jobBag, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobFaceBookShare job = (JobFaceBookShare) jobBag.getJobBagPO().getJobFaceBookShare();
		if (job != null) {
			job.doJob();
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//		节节高升	升级一个零件。
	public void doJobPartLevelUp(Player player, JobBag jobBag, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobPartLevelUp job = (JobPartLevelUp) jobBag.getJobBagPO().getJobPartLevelUp();
		if (job != null) {
			job.doJob();
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//		竞技达人	进行三次pvp对战。
	public void doJobPVP(Player player, JobBag jobBag, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobPvp job = (JobPvp) jobBag.getJobBagPO().getJobPvp();
		if (job != null) {
			job.doJob();
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//		租赁机甲	租赁机器人参加一次战斗
	public void doJobHireRobot(Player player, JobBag jobBag, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobHireRobot job = (JobHireRobot) jobBag.getJobBagPO().getJobHireRobot();
		if (job != null) {
			job.doJob();
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//		机甲商人	出租的机器人被别人使用一次。
	public void doJobRentRobot(Player player, JobBag jobBag, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobRentRobot job = (JobRentRobot) jobBag.getJobBagPO().getJobRentRobot();
		if (job != null) {
			job.doJob();
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//	16			获得一个部件。（不包括能量块）
	public void doJobPartGet(Player player, JobBag jobBag, int num, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobPartGet job = (JobPartGet) jobBag.getJobBagPO().getJobPartGet();
		if (job != null) {
			job.doJob(num);
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//	17			进行一次修理。
	public void doJobRepaireRobot(Player player, JobBag jobBag, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobRepaireRobot job = (JobRepaireRobot) jobBag.getJobBagPO().getJobRepaireRobot();
		if (job != null) {
			job.doJob();
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//	18			参观一次其他玩家基地。(PVP进场不算，好友和log算)。
	public void doJobVisitPlayer(Player player, JobBag jobBag, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobVisitPlayer job = (JobVisitPlayer) jobBag.getJobBagPO().getJobVisitPlayer();
		if (job != null) {
			job.doJob();
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//	19			使用一次喷灌进行涂装改色。
	public void doJobPaint(Player player, JobBag jobBag, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobPaint job = (JobPaint) jobBag.getJobBagPO().getJobPaint();
		if (job != null) {
			job.doJob();
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//	20			在任意一场战斗中无伤胜利。
	public void doJobWinNoHit(Player player, JobBag jobBag, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobWinNoHit job = (JobWinNoHit) jobBag.getJobBagPO().getJobWinNoHit();
		if (job != null) {
			job.doJob();
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//	21			获得两场竞技对战胜利。
	public void doJobPvpWin(Player player, JobBag jobBag, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobPvpWin job = (JobPvpWin) jobBag.getJobBagPO().getJobPvpWin();
		if (job != null) {
			job.doJob();
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	//	22			竞技对战摧毁三名XX武器的敌人。
	public void doJobPvpBeatWeapon(Player player, JobBag jobBag, Robot beatedRobot, NpcRobot beatedNpcRobot, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobPvpBeatWeapon job = (JobPvpBeatWeapon) jobBag.getJobBagPO().getJobPvpBeatWeapon();
		if (job != null) {

			Part partWeapon = null;

			if (beatedRobot != null) {
				partWeapon = beatedRobot.readParts().get(PartSlotType.WEAPON.asCode());
			}

			if (beatedNpcRobot != null) {
				partWeapon = beatedNpcRobot.getParts().get(PartSlotType.WEAPON.asCode());
			}

			if (partWeapon != null) {

				partWeapon.getMakingId();

				PartMaking weaponMaking = PartLoadData.getInstance().getMaking(PartSlotType.WEAPON.asCode(), partWeapon.getMakingId());

				if (job.getWeaponType().asCode() == weaponMaking.getWeaponType().asCode()) {
					job.doJob();
					if (sync) {
						updateJobBagToCacheAndDB(player, jobBag);
					}
				}
			}
		}
	}

	//	23			在一场竞技对战中一挑三胜利。
	public void doJobPvp1vs3(Player player, JobBag jobBag, boolean sync) throws SQLException {
		if (jobBag == null) {
			jobBag = getJobBag(player);
		}
		JobPvp1vs3 job = (JobPvp1vs3) jobBag.getJobBagPO().getJobPvp1vs3();
		if (job != null) {
			job.doJob();
			if (sync) {
				updateJobBagToCacheAndDB(player, jobBag);
			}
		}
	}

	public void updateJobBagToCacheAndDB(Player player, JobBag jobBag) {
		updateDB(player, jobBag);
		jobBag.synchronize();
	}

	private void addCurrentStatus(List<JobCurrentStatus> currentStatuses, JobCurrentStatus cs) {
		if (cs != null) {
			currentStatuses.add(cs);
		}
	}

	public List<JobCurrentStatus> getJobBagStatus(JobBag jobBag, LangType lang) {

		List<JobCurrentStatus> currentStatuses = new ArrayList<JobCurrentStatus>();

		JobBagPO jobBagPO = jobBag.getJobBagPO();

		addCurrentStatus(currentStatuses, jobBagPO.getJobBiShaHit() == null ? null : jobBagPO.getJobBiShaHit().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobBishaKill() == null ? null : jobBagPO.getJobBishaKill().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobFaceBookShare() == null ? null : jobBagPO.getJobFaceBookShare().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobGivePower() == null ? null : jobBagPO.getJobGivePower().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobMoreFight() == null ? null : jobBagPO.getJobMoreFight().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobWinOne() == null ? null : jobBagPO.getJobWinOne().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobXuLiHit() == null ? null : jobBagPO.getJobXuLiHit().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobXuLiKill() == null ? null : jobBagPO.getJobXuLiKill().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobPartLevelUp() == null ? null : jobBagPO.getJobPartLevelUp().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobPvp() == null ? null : jobBagPO.getJobPvp().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobRentRobot() == null ? null : jobBagPO.getJobRentRobot().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobHireRobot() == null ? null : jobBagPO.getJobHireRobot().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobPartGet() == null ? null : jobBagPO.getJobPartGet().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobRepaireRobot() == null ? null : jobBagPO.getJobRepaireRobot().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobVisitPlayer() == null ? null : jobBagPO.getJobVisitPlayer().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobPaint() == null ? null : jobBagPO.getJobPaint().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobWinNoHit() == null ? null : jobBagPO.getJobWinNoHit().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobPvpWin() == null ? null : jobBagPO.getJobPvpWin().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobPvp1vs3() == null ? null : jobBagPO.getJobPvp1vs3().readCurrent());
		addCurrentStatus(currentStatuses, jobBagPO.getJobPvpBeatWeapon() == null ? null : jobBagPO.getJobPvpBeatWeapon().readCurrent(lang));
		addCurrentStatus(currentStatuses, jobBagPO.getJobWeaponFight() == null ? null : jobBagPO.getJobWeaponFight().readCurrent(lang));

		return currentStatuses;

	}

}
