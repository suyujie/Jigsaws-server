package server.node.system.mission;

import gamecore.system.AbstractLoadData;
import gamecore.util.DataUtils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.mission.pointAward.PointAwardData;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class PointLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(PointLoadData.class.getName());

	private static PointLoadData instance = null;

	FastMap<Integer, PointMaking> points = null;
	List<Integer> toturialPointIds = null;
	FastMap<Integer, MissionMaking> missions = null;
	FastMap<Integer, PointMaking> nextPoints = null;

	//教学关卡最后一关
	public final Integer LastToturialPoint = 6;
	//正式关卡的第一关
	public final Integer FirstFormalPoint = 21;

	public static PointLoadData getInstance() {
		if (instance == null) {
			instance = new PointLoadData();
		}
		return instance;

	}

	public boolean readData() {

		if (logger.isDebugEnabled()) {
			logger.info("read mission point data");
		}

		this.points = new FastMap<Integer, PointMaking>();
		this.toturialPointIds = new ArrayList<Integer>();
		this.missions = new FastMap<Integer, MissionMaking>();
		this.nextPoints = new FastMap<Integer, PointMaking>();

		return readData_point();
	}

	/**
	 * 读取point
	 */
	private boolean readData_point() {

		if (logger.isDebugEnabled()) {
			logger.info("read pointData data");
		}

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("pointData"));
		if (!xmlFile.exists()) {
			logger.error("read pointData xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("PointData", PointMaking.class);
			stream.alias("ArrayOfPointData", XmlPointsData.class);
			stream.addImplicitCollection(XmlPointsData.class, "ArrayOfPointData");
			XmlPointsData xmlPointsData = (XmlPointsData) stream.fromXML(new FileReader(xmlFile));

			//组织关卡的开启顺序1
			Integer openIndex = null;

			for (PointMaking pointMaking : xmlPointsData.getPoints()) {

				pointMaking.setMoneyTable(DataUtils.string2FastTable(pointMaking.getMoneyStr()));
				pointMaking.setExpTable(DataUtils.string2FastTable(pointMaking.getExpStr()));
				pointMaking.setPassMoneyTable(DataUtils.string2FastTable(pointMaking.getPassMoneyStr()));
				pointMaking.setWearTable(DataUtils.string2FastTable(pointMaking.getWearStr()));
				pointMaking.setGoldTable(DataUtils.string2FastTable(pointMaking.getDiamondStr()));
				pointMaking.setEggCostTable(DataUtils.string2FastTable(pointMaking.getEggCost()));

				//组织关卡的开启顺序2
				if (pointMaking.getId() >= FirstFormalPoint && pointMaking.getId() != FirstFormalPoint) {//顺序开启的关卡,不含 教学关卡  不含 正式关卡的第一关
					nextPoints.put(openIndex, pointMaking);//
				}

				openIndex = pointMaking.getId();

				//整理奖励
				pointMaking.setPointAwardData(new PointAwardData(pointMaking));

				if (pointMaking.getId() < FirstFormalPoint) {
					this.toturialPointIds.add(pointMaking.getId());
				}

				this.points.put(pointMaking.getId(), pointMaking);

			}

			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	public PointMaking getPointMaking(Integer id) {
		return this.points.get(id);
	}

	public MissionMaking getMissionMaking(Integer id) {
		return this.missions.get(id);
	}

	public PointMaking getNextPointMaking(Integer id) {
		return this.nextPoints.get(id);
	}

}