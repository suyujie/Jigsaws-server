package server.node.system.lottery;

import gamecore.system.AbstractLoadData;
import gamecore.util.BaseWeightPool;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.robotPart.PartMaking;
import server.node.system.robotPart.PartQualityType;
import server.node.system.robotPart.PartSlotType;

import com.thoughtworks.xstream.XStream;

public final class LotteryLoadData extends AbstractLoadData {

	private final static Logger logger = LogManager.getLogger(LotteryLoadData.class.getName());

	private static final LotteryLoadData instance = new LotteryLoadData();

	private BaseWeightPool lotteryFreePool = new BaseWeightPool();

	private BaseWeightPool lotteryGoldHeadPool = new BaseWeightPool();
	private BaseWeightPool lotteryGoldBodyPool = new BaseWeightPool();
	private BaseWeightPool lotteryGoldArmPool = new BaseWeightPool();
	private BaseWeightPool lotteryGoldLegPool = new BaseWeightPool();
	private BaseWeightPool lotteryGoldWeaponPool = new BaseWeightPool();

	private HashMap<PartQualityType, LotteryMaking> qualityLotteryMaking = null;
	private HashMap<PartSlotType, LotteryMaking> partSlotLotteryMaking = null;
	private HashMap<String, LotteryMaking> robotLotteryMaking = null;

	private List<PartMaking> partMakings = new ArrayList<PartMaking>();

	public LotteryLoadData() {
	}

	public static LotteryLoadData getInstance() {
		return LotteryLoadData.instance;
	}

	public boolean readData() {

		qualityLotteryMaking = new HashMap<PartQualityType, LotteryMaking>();
		partSlotLotteryMaking = new HashMap<PartSlotType, LotteryMaking>();
		robotLotteryMaking = new HashMap<String, LotteryMaking>();

		//读取三个维度的权重
		boolean b = readLotteryQuality();
		b = b & readLotteryPartSlot();
		b = b & readLotteryRobot();

		//读取所有的组件,构造奖池
		b = b & createLotteryPool();

		return b;
	}

	public void putPartMaking(PartMaking partMaking) {
		this.partMakings.add(partMaking);
	}

	/**
	 * 读取奖池数据
	 * lotteryQuality.xml
	 */
	public boolean readLotteryQuality() {

		if (logger.isDebugEnabled()) {
			logger.info("read lotteryQuality data");
		}

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("lotteryQuality"));
		if (!xmlFile.exists()) {
			logger.error("read lotteryQuality xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("LotteryData", LotteryMaking.class);
			stream.alias("ArrayOfLotteryData", XmlLotteryData.class);
			stream.addImplicitCollection(XmlLotteryData.class, "lotteryMakingArray");
			XmlLotteryData xmlLotteryData = (XmlLotteryData) stream.fromXML(new FileReader(xmlFile));

			for (LotteryMaking lotteryMaking : xmlLotteryData.getLotteryMakingArray()) {
				qualityLotteryMaking.put(PartQualityType.asEnum(lotteryMaking.getType()), lotteryMaking);
			}

			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	/**
	 * 读取奖池数据
	 * lotteryPartSlot.xml
	 */
	public boolean readLotteryPartSlot() {

		if (logger.isDebugEnabled()) {
			logger.info("read lotteryPartSlot data");
		}

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("lotteryPartSlot"));
		if (!xmlFile.exists()) {
			logger.error("read lotteryPartSlot xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("LotteryData", LotteryMaking.class);
			stream.alias("ArrayOfLotteryData", XmlLotteryData.class);
			stream.addImplicitCollection(XmlLotteryData.class, "lotteryMakingArray");
			XmlLotteryData xmlLotteryData = (XmlLotteryData) stream.fromXML(new FileReader(xmlFile));

			for (LotteryMaking lotteryMaking : xmlLotteryData.getLotteryMakingArray()) {
				partSlotLotteryMaking.put(PartSlotType.asEnum(lotteryMaking.getType()), lotteryMaking);
			}

			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	/**
	 * 读取奖池数据
	 * lotteryRobot.xml
	 */
	public boolean readLotteryRobot() {

		if (logger.isDebugEnabled()) {
			logger.info("read lotteryRobot data");
		}

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("lotteryRobot"));
		if (!xmlFile.exists()) {
			logger.error("read lotteryRobot xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("LotteryData", LotteryMaking.class);
			stream.alias("ArrayOfLotteryData", XmlLotteryData.class);
			stream.addImplicitCollection(XmlLotteryData.class, "lotteryMakingArray");
			XmlLotteryData xmlLotteryData = (XmlLotteryData) stream.fromXML(new FileReader(xmlFile));

			for (LotteryMaking lotteryMaking : xmlLotteryData.getLotteryMakingArray()) {
				robotLotteryMaking.put(lotteryMaking.getType(), lotteryMaking);
			}

			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	private boolean createLotteryPool() {

		for (PartMaking partMaking : partMakings) {

			//cash
			//身体组件权重
			int partSlotWeightCash = partSlotLotteryMaking.get(partMaking.getPartSlotType()).getWeightCash();
			int qualityWeightCash = qualityLotteryMaking.get(partMaking.getPartQualityType()).getWeightCash();
			int robotWeightCash = robotLotteryMaking.get(partMaking.getSuitName()).getWeightCash();

			int partSlotWeightGold = partSlotLotteryMaking.get(partMaking.getPartSlotType()).getWeightGold();
			int qualityWeightGold = qualityLotteryMaking.get(partMaking.getPartQualityType()).getWeightGold();
			int robotWeightGold = robotLotteryMaking.get(partMaking.getSuitName()).getWeightGold();

			lotteryFreePool.addWeight(partSlotWeightCash * qualityWeightCash * robotWeightCash, partMaking);

			if (partMaking.getPartSlotType() == PartSlotType.HEAD) {
				lotteryGoldHeadPool.addWeight(qualityWeightGold * robotWeightGold, partMaking);
			}
			if (partMaking.getPartSlotType() == PartSlotType.BODY) {
				lotteryGoldBodyPool.addWeight(partSlotWeightGold * qualityWeightGold * robotWeightGold, partMaking);
			}
			if (partMaking.getPartSlotType() == PartSlotType.ARM) {
				lotteryGoldArmPool.addWeight(partSlotWeightGold * qualityWeightGold * robotWeightGold, partMaking);
			}
			if (partMaking.getPartSlotType() == PartSlotType.LEG) {
				lotteryGoldLegPool.addWeight(partSlotWeightGold * qualityWeightGold * robotWeightGold, partMaking);
			}
			if (partMaking.getPartSlotType() == PartSlotType.WEAPON) {
				lotteryGoldWeaponPool.addWeight(partSlotWeightGold * qualityWeightGold * robotWeightGold, partMaking);
			}

		}

		return true;
	}

	public BaseWeightPool getLotteryFreePool() {
		return lotteryFreePool;
	}

	public BaseWeightPool getLotteryGoldPool(PartSlotType partSlotType) {
		switch (partSlotType) {
		case HEAD:
			return lotteryGoldHeadPool;
		case BODY:
			return lotteryGoldBodyPool;
		case ARM:
			return lotteryGoldArmPool;
		case LEG:
			return lotteryGoldLegPool;
		case WEAPON:
			return lotteryGoldWeaponPool;
		default:
			return null;
		}
	}

}
