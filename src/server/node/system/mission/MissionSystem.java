package server.node.system.mission;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.serialize.SerializerJson;
import gamecore.system.AbstractSystem;
import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;
import gamecore.util.Clock;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.dao.MissionDao;
import server.node.system.Root;
import server.node.system.boss.BossLoadData;
import server.node.system.player.CashType;
import server.node.system.player.Player;

import com.alibaba.fastjson.TypeReference;

/**
 * 关卡系统。
 */
public final class MissionSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(MissionSystem.class.getName());

	public MissionSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("MissionSystem start....");
		// 读取制作数据
		boolean b = PointLoadData.getInstance().readData();
		b = b & MissionGainCashLoadData.getInstance().readData();
		b = b & MissionGainExpLoadData.getInstance().readData();
		b = b & BossLoadData.getInstance().readData();

		System.out.println("MissionSystem start....OK");
		return b;
	}

	@Override
	public void shutdown() {
	}

	//获取MissionBag
	public MissionBag getMissionBag(Player player) {
		MissionBag missionBag = RedisHelperJson.getMissionBag(player.getId());
		if (missionBag == null) {
			missionBag = readMissionBagFromDB(player);
			missionBag.synchronize();
		}
		return missionBag;
	}

	//获取PointFlushBag
	public PointFlushBag getPointFlushBag(Player player) {
		PointFlushBag pointFlushBag = RedisHelperJson.getPointFlushBag(player.getId());
		if (pointFlushBag == null) {
			pointFlushBag = new PointFlushBag(player.getId());
		}
		return pointFlushBag;
	}

	//获取大关卡
	public Mission getMission(Player player, Integer missionMakingId) {
		MissionBag missionBag = getMissionBag(player);
		return missionBag.readMission(missionMakingId);
	}

	//获取一个小关卡,根据小关卡的原型,得到大关卡的makingId,根据大关卡的id,得到大关卡,从大关卡中拿到小关卡
	public Point getPointPO(Player player, Integer pointMakingId) {
		Integer missionMakingId = getMissionMakingIdByPoint(pointMakingId);
		Mission mission = getMission(player, missionMakingId);
		if (mission == null) {//mission 是空的,point自然没有,直接返回null
			return null;
		} else {
			Point pointPO = mission.readPoint(pointMakingId);
			return pointPO;
		}
	}

	//数据库读取MissionBag
	private MissionBag readMissionBagFromDB(Player player) {
		MissionBag missionBag = new MissionBag(player.getId());
		MissionDao missionDao = DaoFactory.getInstance().borrowMissionDao();
		List<Map<String, Object>> list = missionDao.readMissions(player);
		DaoFactory.getInstance().returnMissionDao(missionDao);

		if (list != null && list.size() > 0) {
			for (Map<String, Object> map : list) {
				try {
					int makingId = ((Long) map.get("making_id")).intValue();
					Long cashTime = ((BigInteger) map.get("gain_cash_t")).longValue();
					Long expTime = ((BigInteger) map.get("gain_exp_t")).longValue();
					int clientP = ((Integer) map.get("client_p")).intValue();
					String points = (String) map.get("points");
					HashMap<Integer, Point> pointPOs = (HashMap<Integer, Point>) SerializerJson.deSerializeMap(points, new TypeReference<HashMap<Integer, Point>>() {
					});//小关卡的集合

					Mission mission = new Mission(player, makingId, cashTime, expTime, clientP, pointPOs);
					mission.flushStarNum();

					missionBag.putMission(mission, false);

				} catch (Exception e) {
					logger.error(e);
				}
			}
		}

		return missionBag;
	}

	//开启默认关卡
	public void openDefaultPoint(Player player, MissionBag missionBag) {
		if (missionBag == null) {
			missionBag = getMissionBag(player);
		}
		//开启教学关卡
		openToturialPointPOs(player, missionBag, PointLoadData.getInstance().toturialPointIds);

		//开启正常关卡的第一关
		PointMaking pointMaking = PointLoadData.getInstance().getPointMaking(PointLoadData.getInstance().FirstFormalPoint);
		openPoint(player, missionBag, pointMaking);

	}

	//开启全部的教学关卡
	private void openToturialPointPOs(Player player, MissionBag missionBag, List<Integer> pointMakingIds) {

		Mission mission = null;

		for (Integer pointMakingId : pointMakingIds) {
			PointMaking pointMaking = PointLoadData.getInstance().getPointMaking(pointMakingId);
			if (pointMaking != null) {
				if (mission == null) {
					mission = openMission(player, pointMaking.getMission());
				}
				Point point = new Point(pointMaking.getId(), 1, 0);
				mission.putPoint(point, false);
			}
		}

		mission.flushStarNum();

		missionBag.putMission(mission, true);

		//存入数据库
		MissionDao missionDao = DaoFactory.getInstance().borrowMissionDao();
		missionDao.updatePoint(player, mission);
		DaoFactory.getInstance().returnMissionDao(missionDao);

	}

	//point 打开新星级
	public void openPointNewStar(Player player, MissionBag missionBag, Point pointPO) {

		//小关卡原型
		PointMaking pointMaking = PointLoadData.getInstance().getPointMaking(pointPO.getMakingId());
		//关卡背包
		if (missionBag == null) {
			missionBag = getMissionBag(player);
		}
		//获取大关卡,大关卡的id是构造的
		Mission mission = missionBag.readMission(pointMaking.getMission());

		pointPO.setPassStar(pointPO.getStar());

		if (pointPO.getStar() < 3) {
			pointPO.setStar(pointPO.getStar() + 1);
			mission.flushStarNum();
		}

		//检查 关卡收钱是否没开启[上次收钱时间是-1],如果没开的话,就开  (教学关卡除外)
		if ((mission.getLastGainCashTime() == -1 || mission.getLastGainExpTime() == -1) && pointPO.getMakingId() >= PointLoadData.getInstance().FirstFormalPoint) {
			mission.setLastGainCashTime(Clock.currentTimeSecond());//开始产钱
			mission.setLastGainExpTime(Clock.currentTimeSecond());//开始产经验块

			MissionDao missionDao = DaoFactory.getInstance().borrowMissionDao();
			missionDao.updateGain(player, mission);
			DaoFactory.getInstance().returnMissionDao(missionDao);
		}

		mission.putPoint(pointPO, true);

		missionBag.putMission(mission, true);

		this.publish(new MissionMessage(MissionMessage.AddStar, player, missionBag));

		//存入数据库
		MissionDao missionDao = DaoFactory.getInstance().borrowMissionDao();
		missionDao.updatePoint(player, mission);
		DaoFactory.getInstance().returnMissionDao(missionDao);

	}

	//开启一个正常关卡point
	public Point openPoint(Player player, MissionBag missionBag, PointMaking pointMaking) {

		//小关卡原型
		if (pointMaking != null) {
			Point pointPO = new Point(pointMaking.getId(), 1, 0);
			//关卡背包
			if (missionBag == null) {
				missionBag = getMissionBag(player);
			}
			//获取大关卡,大关卡的id是构造的
			Mission mission = missionBag.readMission(pointMaking.getMission());

			if (mission == null) {//如果还没有大关卡,先开启大关卡
				mission = openMission(player, pointMaking.getMission());
				missionBag.putMission(mission, false);
			}

			mission.putPoint(pointPO, true);

			mission.flushStarNum();

			missionBag.putMission(mission, true);

			this.publish(new MissionMessage(MissionMessage.AddStar, player, missionBag));

			//存入数据库
			MissionDao missionDao = DaoFactory.getInstance().borrowMissionDao();
			missionDao.updatePoint(player, mission);
			DaoFactory.getInstance().returnMissionDao(missionDao);

			return pointPO;
		}

		return null;

	}

	//open mission
	private Mission openMission(Player player, int missionMakingId) {

		HashMap<Integer, Point> pointPOs = new HashMap<Integer, Point>();
		//上次收钱时间为-1,表示还没有开始产钱
		Mission mission = new Mission(player, missionMakingId, -1, -1, 0, pointPOs);

		MissionDao missionDao = DaoFactory.getInstance().borrowMissionDao();
		missionDao.save(player, mission);
		DaoFactory.getInstance().returnMissionDao(missionDao);

		return mission;
	}

	private int calculateGainCash(Mission mission) {
		MissionGainCashMaking missionGainMaking = MissionGainCashLoadData.getInstance().getMissionGainMaking(mission.getMakingId());
		Long t = (Clock.currentTimeSecond() - mission.getLastGainCashTime()) / 60;//分钟
		t = t < missionGainMaking.getMaxTime() ? t : missionGainMaking.getMaxTime();
		int cash = mission.flushGainCashPerTime() * t.intValue();
		if (cash > 0) {
			cash = Math.round((float) cash / (float) missionGainMaking.getCorrection()) + 1;
		}
		return cash;
	}

	private int calculateGainCash(Mission mission, Long min) {
		MissionGainCashMaking missionGainMaking = MissionGainCashLoadData.getInstance().getMissionGainMaking(mission.getMakingId());
		min = missionGainMaking.getMaxTime() > min ? min : missionGainMaking.getMaxTime();
		int cash = mission.flushGainCashPerTime() * min.intValue();
		if (cash > 0) {
			cash = Math.round((float) cash / (float) missionGainMaking.getCorrection()) + 1;
		}
		return cash;
	}

	private Integer calculateGainExp(Mission mission, int errorTimeSec) {
		MissionGainExpMaking making = MissionGainExpLoadData.getInstance().getMissionGainExpMaking(mission.getMakingId());
		Long t = (Clock.currentTimeSecond() - mission.getLastGainExpTime()) / 60;//分钟

		if (t >= making.getTimeMinute()) {//时间足够
			int starNum = mission.flushStarNum();
			return making.getExpIdByStarNum(starNum);
		} else {
			t = (Clock.currentTimeSecond() - mission.getLastGainExpTime() + errorTimeSec) / 60;//分钟,算上误差时间
			if (t < making.getTimeMinute()) {
				return null;
			} else {
				int starNum = mission.flushStarNum();
				return making.getExpIdByStarNum(starNum);
			}
		}

	}

	public Integer calculateGainMaxExpId(Player player) {
		MissionBag missionBag = getMissionBag(player);
		Integer expId = null;
		for (Mission mission : missionBag.readMissions()) {
			Integer gainExpId = calculateGainExpId(mission);
			if (expId == null || expId < gainExpId) {
				expId = gainExpId;
			}
		}
		return expId;
	}

	private Integer calculateGainExpId(Mission mission) {
		MissionGainExpMaking making = MissionGainExpLoadData.getInstance().getMissionGainExpMaking(mission.getMakingId());
		int starNum = mission.flushStarNum();
		return making.getExpIdByStarNum(starNum);
	}

	public int cash2Gold(Player player, long lackCash) {
		//当前的产钱效率,每小时产出
		int gainCashPerHour = gainCashPerHour(player);
		//每个钻石的价格
		int oneGold = (3000 + gainCashPerHour * 6) / 100;
		int needGold = (int) lackCash / oneGold;
		if (needGold == 0) {
			needGold = 1;
		}
		return needGold;
	}

	//计算产钱速度,小时
	public int gainCashPerHour(Player player) {
		MissionBag missionBag = getMissionBag(player);
		int cash = 0;
		for (Mission mission : missionBag.readMissions()) {
			cash += mission.flushGainCashPerHour();
		}
		return cash;
	}

	//mission 预算收钱多少
	public int previewGainMission(Player player, MissionBag missionBag) {
		if (missionBag == null) {
			missionBag = getMissionBag(player);
		}
		int cash = 0;
		for (Mission mission : missionBag.readMissions()) {
			long t = (Clock.currentTimeSecond() - mission.getLastGainCashTime()) / 60;
			//计算该收的钱
			cash = calculateGainCash(mission, t);
		}
		return cash;
	}

	//防守方 每个关卡 被抢 会减少的时间
	public HashMap<Integer, Long> missionLoseTime(Player player, MissionBag missionBag, Float pvpLootMissionCashRate) {

		HashMap<Integer, Long> result = new HashMap<Integer, Long>();

		if (missionBag == null) {
			missionBag = getMissionBag(player);
		}

		long now = Clock.currentTimeSecond();
		for (Mission mission : missionBag.readMissions()) {
			result.put(mission.getMakingId(), (long) ((now - mission.getLastGainCashTime()) * pvpLootMissionCashRate));
		}

		return result;

	}

	//mission 收获    收获后,clientCash客户端算的cash,以cash为准
	public SystemResult gainMission(Player player, MissionBag missionBag, Integer missionMakingId, int clientCash, Integer clientExpId) throws SQLException {

		SystemResult result = new SystemResult();

		if (missionBag == null) {
			missionBag = getMissionBag(player);
		}

		Mission mission = missionBag.readMission(missionMakingId);

		//计算该收的钱
		int cash = calculateGainCash(mission);
		//验证结果,客户端的钱数应该等于或者小于服务器，以客户端为准，大于时候报错。
		if (clientCash != cash) {
			//result.setCode(ErrorCode.GAIN_CASH_NOT_OK);
			logger.error("clientCash:[" + clientCash + "]  serverCash:[" + cash + "]");
			//return result;
		}
		//cash
		Root.playerSystem.changeCash(player, clientCash, CashType.MISSION_GAIN, false);

		player.synchronize();

		mission.setLastGainCashTime(Clock.currentTimeSecond());//更新收钱时间

		//收钱消息
		MissionMessage message = new MissionMessage(MissionMessage.GainCash, player, mission, clientCash);
		this.publish(message);

		//经验块
		if (clientExpId != null) {
			Integer expPartId = calculateGainExp(mission, 3 * 60);//允许误差3分钟
			if (expPartId != null && clientExpId == expPartId) {
				Root.expPartSystem.addExpPart(player, clientExpId, 1);
			} else {//按照客户端的来算,但是给出报错日志
				Root.expPartSystem.addExpPart(player, clientExpId, 1);
				logger.error("client server expId is not same    client [" + clientExpId + "]  server [" + expPartId + "]");
			}
			mission.setLastGainExpTime(Clock.currentTimeSecond());//更新收取exp的时间
		}

		missionBag.synchronize();

		MissionDao missionDao = DaoFactory.getInstance().borrowMissionDao();
		missionDao.updateGain(player, mission);
		DaoFactory.getInstance().returnMissionDao(missionDao);

		result.setBindle(mission);

		return result;

	}

	//这个关卡损失时间
	private void missionLoseTime(Player player, Mission mission, long loseTime) {

		if (Clock.currentTimeSecond() > mission.getLastGainCashTime() + loseTime) {//时间后延之后,还大于当前时间.
			mission.setLastGainCashTime(mission.getLastGainCashTime() + loseTime);
		} else {
			mission.setLastGainCashTime(Clock.currentTimeSecond());
		}

		MissionDao missionDao = DaoFactory.getInstance().borrowMissionDao();
		missionDao.updateGain(player, mission);
		DaoFactory.getInstance().returnMissionDao(missionDao);

		MissionMessage message = new MissionMessage(MissionMessage.LoseTime, player, mission);
		this.publish(message);
	}

	//损失收获时间，即 上次收获时间延后,秒为单位,减钱关卡从前往后的减少
	public SystemResult loseGainTime(Player player, MissionBag missionBag, Map<Integer, Long> defenderMissionLoseSeconds) {

		SystemResult result = new SystemResult();

		if (missionBag == null) {
			missionBag = getMissionBag(player);
		}

		for (Mission mission : missionBag.readMissions()) {
			if (defenderMissionLoseSeconds.get(mission.getMakingId()) != null && defenderMissionLoseSeconds.get(mission.getMakingId()) > 0) {
				missionLoseTime(player, mission, defenderMissionLoseSeconds.get(mission.getMakingId()));
			}
		}
		missionBag.synchronize();

		return result;

	}

	//根据小关卡得到大关卡的id
	public int getMissionMakingIdByPoint(int pointMakingId) {
		PointMaking pointMaking = PointLoadData.getInstance().getPointMaking(pointMakingId);
		return pointMaking.getMission();
	}

	//更新mission的一个属性
	public void updateMissionClientP(Player player, int missionId, int clientP) {
		MissionBag missionBag = getMissionBag(player);
		Mission mission = missionBag.readMission(missionId);
		if (mission.getClientP() != clientP) {
			mission.setClientP(clientP);

			MissionDao missionDao = DaoFactory.getInstance().borrowMissionDao();
			missionDao.updateClientP(player, mission);
			DaoFactory.getInstance().returnMissionDao(missionDao);
		}
		missionBag.synchronize();
	}

}
