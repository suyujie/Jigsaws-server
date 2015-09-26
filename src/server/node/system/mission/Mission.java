package server.node.system.mission;

import gamecore.io.ByteArrayGameOutput;
import gamecore.serialize.SerializerJson;
import gamecore.util.Clock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import server.node.system.player.Player;

/**
 * 大关卡
 */
public final class Mission {

	private int makingId;//大关卡的制作id
	private long lastGainCashTime;//上次收取cash时间,单位 second
	private long lastGainExpTime;//上次收取exp时间,单位 second
	private HashMap<Integer, Point> points;
	private int clientP = 0;//给前端的一个属性，判断切换关卡的动画是否播放了，对服务器无意义,

	public Mission() {
	}

	public Mission(Player player, int makingId, long lastGainCashTime, long lastGainExpTime, int clientP, HashMap<Integer, Point> points) {
		this.makingId = makingId;
		this.lastGainCashTime = lastGainCashTime;
		this.lastGainExpTime = lastGainExpTime;
		this.clientP = clientP;
		this.points = points;
	}

	public int getMakingId() {
		return makingId;
	}

	public void setMakingId(int makingId) {
		this.makingId = makingId;
	}

	public int getClientP() {
		return clientP;
	}

	public void setClientP(int clientP) {
		this.clientP = clientP;
	}

	public void putPoint(Point point, boolean sync) {
		points.put(point.getMakingId(), point);
	}

	public Point readPoint(Integer pointMakingId) {
		return points.get(pointMakingId);
	}

	public long getLastGainCashTime() {
		return lastGainCashTime;
	}

	public void setLastGainCashTime(long lastGainCashTime) {
		this.lastGainCashTime = lastGainCashTime;
	}

	public long getLastGainExpTime() {
		return lastGainExpTime;
	}

	public void setLastGainExpTime(long lastGainExpTime) {
		this.lastGainExpTime = lastGainExpTime;
	}

	public HashMap<Integer, Point> getPoints() {
		return points;
	}

	public void setPoints(HashMap<Integer, Point> points) {
		this.points = points;
	}

	//获取所有的points
	public List<Point> readPoints() {
		List<Point> pointPoList = new ArrayList<>();
		for (Point pointPO : points.values()) {
			if (pointPO != null) {
				pointPoList.add(pointPO);
			}
		}
		return pointPoList;
	}

	//统计mission的星数
	public int flushStarNum() {
		int starNum = 0;
		//训练关卡不算
		for (Point point : points.values()) {
			if (point.getMakingId() >= PointLoadData.getInstance().FirstFormalPoint) {
				starNum += point.getPassStar();
			}
		}
		return starNum;
	}

	//统计mission的产钱效率,每小时产钱多少
	public int flushGainCashPerHour() {
		int gainCashPerHour = 0;
		//训练关卡不算
		for (Point pointPO : points.values()) {
			if (pointPO.getMakingId() >= PointLoadData.getInstance().FirstFormalPoint) {
				PointMaking pointMaking = PointLoadData.getInstance().getPointMaking(pointPO.getMakingId());
				if (pointPO.getPassStar() > 0) {
					gainCashPerHour += pointMaking.getPassMoneyTable().get(pointPO.getPassStar() - 1);//星级为 0 1 2  ,正好对应上索引,产cash速度以 分钟 为单位
				}
			}
		}

		gainCashPerHour *= 60;

		MissionGainCashMaking missionGainMaking = MissionGainCashLoadData.getInstance().getMissionGainMaking(makingId);

		if (gainCashPerHour > 0) {
			gainCashPerHour = Math.round((float) gainCashPerHour / (float) missionGainMaking.getCorrection()) + 1;
		}

		return gainCashPerHour;
	}

	public int flushGainCashPerTime() {
		int gainCashPerTime = 0;
		//训练关卡不算
		for (Point pointPO : points.values()) {
			if (pointPO.getMakingId() >= PointLoadData.getInstance().FirstFormalPoint) {
				PointMaking pointMaking = PointLoadData.getInstance().getPointMaking(pointPO.getMakingId());
				if (pointPO.getPassStar() > 0) {
					gainCashPerTime += pointMaking.getPassMoneyTable().get(pointPO.getPassStar() - 1);//星级为 0 1 2  ,正好对应上索引,产cash速度以 分钟 为单位
				}
			}
		}
		return gainCashPerTime;
	}

	public byte[] toByteArray() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		bago.putInt(makingId);
		bago.put((byte) clientP);
		MissionGainCashMaking missionGainMaking = MissionGainCashLoadData.getInstance().getMissionGainMaking(makingId);
		bago.putInt(missionGainMaking.getMaxTime());//关卡产钱最大时间
		if (lastGainCashTime == -1) {
			bago.putInt(-1); //每大关收钱过了多长时间，没开始就是-1（以秒为单位）
		} else {
			int gainCashTimeLength = (int) (Clock.currentTimeSecond() - lastGainCashTime);//已经产钱多长时间[当前时间-上次收获的时间]
			bago.putInt(gainCashTimeLength < 0 ? -1 : gainCashTimeLength); //每大关收钱过了多长时间，没开始就是-1（以秒为单位）
		}
		if (lastGainExpTime == -1) {
			bago.putInt(-1); //每大关收经验块过了多长时间，没开始就是-1（以秒为单位）
		} else {
			int gainExpTimeLength = (int) (Clock.currentTimeSecond() - lastGainExpTime);//已经产exp多长时间[当前时间-上次收获的时间]
			bago.putInt(gainExpTimeLength < 0 ? -1 : gainExpTimeLength); //每大关收exp过了多长时间，没开始就是-1（以秒为单位）
		}
		bago.putInt(missionGainMaking.getCorrection()); //公式里的修正系数
		return bago.toByteArray();
	}

	public String pointsToJson() {
		return SerializerJson.serialize(points);
	}
}
