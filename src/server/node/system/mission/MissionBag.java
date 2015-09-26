package server.node.system.mission;

import gamecore.entity.AbstractEntity;
import gamecore.io.ByteArrayGameOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 大关卡包实体。
 */
public class MissionBag extends AbstractEntity {

	private static final long serialVersionUID = -3174578709321287355L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "missionbag_";

	private HashMap<Integer, Mission> missions = new HashMap<Integer, Mission>();

	public MissionBag() {
	}

	public MissionBag(Long playerId) {
		super(generateCacheKey(playerId));
	}

	public void setMissions(HashMap<Integer, Mission> missions) {
		this.missions = missions;
	}

	public HashMap<Integer, Mission> getMissions() {
		return missions;
	}

	//加入mission
	public void putMission(Mission mission, boolean sync) {
		missions.put(mission.getMakingId(), mission);
		if (sync) {
			this.synchronize();
		}
	}

	//获取mission
	public Mission readMission(Integer missionMakingId) {
		return missions.get(missionMakingId);
	}

	//获取所有的missions
	public List<Mission> readMissions() {
		List<Mission> missionList = new ArrayList<>();
		for (Mission mission : missions.values()) {
			if (mission != null) {
				missionList.add(mission);
			}
		}
		return missionList;
	}

	//获取最大的关卡id
	public int readMaxPoint() {

		Mission maxMission = null;

		int maxPointId = 0;

		List<Mission> missions = readMissions();

		for (Mission mission : missions) {
			if (mission != null) {
				if (maxMission == null) {
					maxMission = mission;
				} else {
					if (mission.getMakingId() > maxMission.getMakingId() && !mission.getPoints().isEmpty()) {
						maxMission = mission;
					}
				}
			}
		}

		if (maxMission != null) {
			for (Point pointPO : maxMission.readPoints()) {
				if (pointPO != null) {
					if (maxPointId == 0) {
						maxPointId = pointPO.getMakingId();
					} else {
						if (pointPO.getMakingId() > maxPointId && pointPO.getPassStar() > 0) {
							maxPointId = pointPO.getMakingId();
						}
					}
				}
			}
		}

		return maxPointId;

	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long playerId) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(MissionBag.CKPrefix).append(playerId).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	public byte[] toByteArray(PointFlushBag pointFlushBag) {

		ByteArrayGameOutput bago = new ByteArrayGameOutput();

		List<Mission> missions = readMissions();

		List<Point> pointPOs = new ArrayList<Point>();

		for (Mission mission : missions) {
			if (mission != null) {
				for (Point pointPO : mission.readPoints()) {
					if (pointPO != null) {
						pointPOs.add(pointPO);
					}
				}
			}
		}
		//小关卡数量
		bago.putInt(pointPOs.size());
		//大关卡的数量
		bago.putInt(missions.size());

		//循环小关卡
		for (Point pointPO : pointPOs) {
			bago.putBytesNoLength(pointPO.toByteArray(pointFlushBag));
		}

		//循环大关卡
		for (Mission mission : missions) {
			bago.putBytesNoLength(mission.toByteArray());
		}
		return bago.toByteArray();

	}

}
