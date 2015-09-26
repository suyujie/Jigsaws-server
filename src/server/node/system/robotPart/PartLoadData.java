package server.node.system.robotPart;

import gamecore.system.AbstractLoadData;
import gamecore.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.lottery.LotteryLoadData;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class PartLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(PartLoadData.class.getName());

	private static PartLoadData instance = null;

	FastMap<Integer, PartMaking> part_arms = new FastMap<Integer, PartMaking>();
	FastMap<Integer, PartMaking> part_legs = new FastMap<Integer, PartMaking>();
	FastMap<Integer, PartMaking> part_heads = new FastMap<Integer, PartMaking>();
	FastMap<Integer, PartMaking> part_bodys = new FastMap<Integer, PartMaking>();
	FastMap<Integer, PartMaking> part_weapons = new FastMap<Integer, PartMaking>();
	FastMap<Integer, PartMaking> part_exps = new FastMap<Integer, PartMaking>();
	public List<String> suitNames = new ArrayList<String>();

	public FastMap<String, PartMaking> name_quality_slot_id = new FastMap<String, PartMaking>();

	HashMap<PartSlotType, List<PartMaking>> partArray = null;

	public static PartLoadData getInstance() {
		if (instance == null) {
			instance = new PartLoadData();
		}
		return instance;

	}

	public boolean readData() {

		this.partArray = new HashMap<PartSlotType, List<PartMaking>>();
		this.partArray.put(PartSlotType.ARM, new ArrayList<PartMaking>());
		this.partArray.put(PartSlotType.LEG, new ArrayList<PartMaking>());
		this.partArray.put(PartSlotType.HEAD, new ArrayList<PartMaking>());
		this.partArray.put(PartSlotType.BODY, new ArrayList<PartMaking>());
		this.partArray.put(PartSlotType.WEAPON, new ArrayList<PartMaking>());

		boolean b = readData_arm();
		b = b & readData_leg();
		b = b & readData_body();
		b = b & readData_head();
		b = b & readData_weapon();
		b = b & readData_exp();

		return b;
	}

	/**
	 * 读取arm
	 * @return
	 */
	private boolean readData_arm() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("arm"));
		if (!xmlFile.exists()) {
			logger.error("read xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("PartsData", PartMaking.class);
			stream.alias("ArrayOfPartsData", XmlPartsData.class);
			stream.addImplicitCollection(XmlPartsData.class, "ArrayOfPartsData");
			XmlPartsData xmlPartsData = (XmlPartsData) stream.fromXML(new FileReader(xmlFile));

			for (PartMaking part : xmlPartsData.getParts()) {
				part.setTables();
				part.setSlotQualitySuitType();
				this.part_arms.put(part.getId(), part);
				this.partArray.get(PartSlotType.ARM).add(part);
				part.checkPartMaking();
				name_quality_slot_id(part);
				suitName(part);
				LotteryLoadData.getInstance().putPartMaking(part);
			}
			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		}
		return false;
	}

	/**
	 * 读取leg
	 * @return
	 */
	private boolean readData_leg() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("leg"));
		if (!xmlFile.exists()) {
			logger.error("read xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("PartsData", PartMaking.class);
			stream.alias("ArrayOfPartsData", XmlPartsData.class);
			stream.addImplicitCollection(XmlPartsData.class, "ArrayOfPartsData");
			XmlPartsData xmlPartsData = (XmlPartsData) stream.fromXML(new FileReader(xmlFile));

			for (PartMaking part : xmlPartsData.getParts()) {
				part.setTables();
				part.setSlotQualitySuitType();
				this.part_legs.put(part.getId(), part);
				this.partArray.get(PartSlotType.LEG).add(part);
				part.checkPartMaking();
				name_quality_slot_id(part);
				suitName(part);
				LotteryLoadData.getInstance().putPartMaking(part);
			}
			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		}
		return false;
	}

	/**
	 * 读取body
	 * @return
	 */
	private boolean readData_body() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("body"));
		if (!xmlFile.exists()) {
			logger.error("read xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("PartsData", PartMaking.class);
			stream.alias("ArrayOfPartsData", XmlPartsData.class);
			stream.addImplicitCollection(XmlPartsData.class, "ArrayOfPartsData");
			XmlPartsData xmlPartsData = (XmlPartsData) stream.fromXML(new FileReader(xmlFile));

			for (PartMaking part : xmlPartsData.getParts()) {
				part.setTables();
				part.setSlotQualitySuitType();
				this.part_bodys.put(part.getId(), part);
				this.partArray.get(PartSlotType.BODY).add(part);
				part.checkPartMaking();
				name_quality_slot_id(part);
				suitName(part);
				LotteryLoadData.getInstance().putPartMaking(part);
			}
			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		}
		return false;
	}

	/**
	 * 读取head
	 * @return
	 */
	private boolean readData_head() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("head"));
		if (!xmlFile.exists()) {
			logger.error("read xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("PartsData", PartMaking.class);
			stream.alias("ArrayOfPartsData", XmlPartsData.class);
			stream.addImplicitCollection(XmlPartsData.class, "ArrayOfPartsData");
			XmlPartsData xmlPartsData = (XmlPartsData) stream.fromXML(new FileReader(xmlFile));

			for (PartMaking part : xmlPartsData.getParts()) {
				part.setTables();
				part.setSlotQualitySuitType();
				this.part_heads.put(part.getId(), part);
				this.partArray.get(PartSlotType.HEAD).add(part);
				part.checkPartMaking();
				name_quality_slot_id(part);
				suitName(part);
				LotteryLoadData.getInstance().putPartMaking(part);
			}
			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		}
		return false;
	}

	/**
	 * 读取weapon
	 * @return
	 */
	private boolean readData_weapon() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("weapon"));
		if (!xmlFile.exists()) {
			logger.error("read xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("PartsData", PartMaking.class);
			stream.alias("ArrayOfPartsData", XmlPartsData.class);
			stream.addImplicitCollection(XmlPartsData.class, "ArrayOfPartsData");
			XmlPartsData xmlPartsData = (XmlPartsData) stream.fromXML(new FileReader(xmlFile));

			for (PartMaking part : xmlPartsData.getParts()) {
				part.setTables();
				part.setSlotQualitySuitType();
				this.part_weapons.put(part.getId(), part);
				this.partArray.get(PartSlotType.WEAPON).add(part);
				part.checkPartMaking();
				name_quality_slot_id(part);
				suitName(part);
				LotteryLoadData.getInstance().putPartMaking(part);
			}
			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		}
		return false;
	}

	/**
	 * 读取exp 道具
	 * @return
	 */
	private boolean readData_exp() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("exp"));
		if (!xmlFile.exists()) {
			logger.error("read xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("PartsData", PartMaking.class);
			stream.alias("ArrayOfPartsData", XmlPartsData.class);
			stream.addImplicitCollection(XmlPartsData.class, "ArrayOfPartsData");
			XmlPartsData xmlPartsData = (XmlPartsData) stream.fromXML(new FileReader(xmlFile));

			for (PartMaking part : xmlPartsData.getParts()) {
				part.setTables();
				part.setSlotQualitySuitType();
				this.part_exps.put(part.getId(), part);
			}

			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		}
		return false;
	}

	private void suitName(PartMaking part) {
		if (!suitNames.contains(part.getSuitName())) {
			suitNames.add(part.getSuitName());
		}
	}

	private void name_quality_slot_id(PartMaking part) {
		name_quality_slot_id.put(new StringBuffer(part.getSuitName()).append("_").append(part.getPartQualityType().asCode()).append(part.getPartSlotType().asCode()).toString(),
				part);
	}

	public PartMaking getMaking(String suitName, PartQualityType partQualityType, PartSlotType partSlotType) {
		if (suitName == null) {
			suitName = Utils.randomSelectOne(suitNames);
		}
		if (partQualityType == null) {
			partQualityType = PartQualityType.rand();
		}
		if (partSlotType == null) {
			partSlotType = PartSlotType.randAsRobotPart();
		}
		return name_quality_slot_id.get(new StringBuffer(suitName).append("_").append(partQualityType.asCode()).append(partSlotType.asCode()).toString());
	}

	public WeaponType getWeaponTypeBySuitName(String suitName) {
		return name_quality_slot_id.get(new StringBuffer(suitName).append("_").append(PartQualityType.IRON.asCode()).append(PartSlotType.WEAPON.asCode()).toString())
				.getWeaponType();
	}

	public PartMaking getMaking(int partType, Integer id) {

		switch (partType) {
		case 0:
			return this.part_heads.get(id);
		case 1:
			return this.part_bodys.get(id);
		case 2:
			return this.part_arms.get(id);
		case 3:
			return this.part_legs.get(id);
		case 4:
			return this.part_weapons.get(id);
		case 5:
			return this.part_exps.get(id);
		default:
			return null;
		}

	}

	public FastMap<Integer, PartMaking> getPart_arms() {
		return part_arms;
	}

	public void setPart_arms(FastMap<Integer, PartMaking> part_arms) {
		this.part_arms = part_arms;
	}

	public FastMap<Integer, PartMaking> getPart_legs() {
		return part_legs;
	}

	public void setPart_legs(FastMap<Integer, PartMaking> part_legs) {
		this.part_legs = part_legs;
	}

	public FastMap<Integer, PartMaking> getPart_heads() {
		return part_heads;
	}

	public void setPart_heads(FastMap<Integer, PartMaking> part_heads) {
		this.part_heads = part_heads;
	}

	public FastMap<Integer, PartMaking> getPart_bodys() {
		return part_bodys;
	}

	public void setPart_bodys(FastMap<Integer, PartMaking> part_bodys) {
		this.part_bodys = part_bodys;
	}

	public FastMap<Integer, PartMaking> getPart_weapons() {
		return part_weapons;
	}

	public void setPart_weapons(FastMap<Integer, PartMaking> part_weapons) {
		this.part_weapons = part_weapons;
	}

	public FastMap<Integer, PartMaking> getPart_exps() {
		return part_exps;
	}

	public void setPart_exps(FastMap<Integer, PartMaking> part_exps) {
		this.part_exps = part_exps;
	}

	public HashMap<PartSlotType, List<PartMaking>> getPartArray() {
		return partArray;
	}

}