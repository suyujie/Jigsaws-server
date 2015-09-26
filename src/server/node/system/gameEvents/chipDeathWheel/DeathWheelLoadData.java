package server.node.system.gameEvents.chipDeathWheel;

import gamecore.system.AbstractLoadData;
import gamecore.util.Utils;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;
import javolution.util.FastTable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.gameEvents.chipDeathWheel.makingData.DeathWheelBattleMapMaking;
import server.node.system.gameEvents.chipDeathWheel.makingData.DeathWheelBossMaking;
import server.node.system.gameEvents.chipDeathWheel.makingData.DeathWheelExtraBossMaking;
import server.node.system.gameEvents.chipDeathWheel.makingData.XmlDeathWheelBattleFieldData;
import server.node.system.gameEvents.chipDeathWheel.makingData.XmlDeathWheelBattleOtherData;
import server.node.system.gameEvents.chipDeathWheel.makingData.XmlDeathWheelBattleOtherWeaponData;
import server.node.system.gameEvents.chipDeathWheel.makingData.XmlDeathWheelBossData;
import server.node.system.gameEvents.chipDeathWheel.makingData.XmlDeathWheelExtraBossData;
import server.node.system.robotPart.WeaponType;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class DeathWheelLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(DeathWheelLoadData.class.getName());

	private static DeathWheelLoadData instance = null;

	FastMap<Integer, DeathWheelBossMaking> bossMakings = new FastMap<Integer, DeathWheelBossMaking>();
	FastMap<WeaponType, DeathWheelExtraBossMaking> extraBossMakings = new FastMap<WeaponType, DeathWheelExtraBossMaking>();

	FastTable<DeathWheelBattleMapMaking> battleMapMakings = new FastTable<DeathWheelBattleMapMaking>();
	FastTable<String> battleOther = new FastTable<String>();
	FastMap<WeaponType, String> battleOtherWeapon = new FastMap<WeaponType, String>();

	public static DeathWheelLoadData getInstance() {
		if (instance == null) {
			instance = new DeathWheelLoadData();
		}
		return instance;

	}

	public boolean readData() {

		boolean b = readDataDeathWheelBoss();
		b = b & readDataDeathWheelExtraBoss();
		b = b & readDataDeathWheelBattleField();
		b = b & readDataDeathWheelBattleOther();
		b = b & readDataDeathWheelBattleOtherWeapon();
		return b;
	}

	private boolean readDataDeathWheelBoss() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("DeathWheelRobotBossData"));
		if (!xmlFile.exists()) {
			logger.error("read RobotBossData xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("RobotBossData", DeathWheelBossMaking.class);
			stream.alias("ArrayOfRobotBossData", XmlDeathWheelBossData.class);
			stream.addImplicitCollection(XmlDeathWheelBossData.class, "array");
			XmlDeathWheelBossData data = (XmlDeathWheelBossData) stream.fromXML(new FileReader(xmlFile));

			for (DeathWheelBossMaking making : data.getArray()) {
				making.setTable();
				this.bossMakings.put(making.getId(), making);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean readDataDeathWheelExtraBoss() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("DeathWheelExtrabossData"));
		if (!xmlFile.exists()) {
			logger.error("read ExtrabossData xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("ExtrabossData", DeathWheelExtraBossMaking.class);
			stream.alias("ArrayOfExtrabossData", XmlDeathWheelExtraBossData.class);
			stream.addImplicitCollection(XmlDeathWheelExtraBossData.class, "array");
			XmlDeathWheelExtraBossData data = (XmlDeathWheelExtraBossData) stream.fromXML(new FileReader(xmlFile));

			for (DeathWheelExtraBossMaking making : data.getArray()) {
				this.extraBossMakings.put(WeaponType.asEnum(making.getId()), making);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean readDataDeathWheelBattleField() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("DeathWheelBattleField"));
		if (!xmlFile.exists()) {
			logger.error("read DeathWheelBattleField xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("battlemap", DeathWheelBattleMapMaking.class);
			stream.alias("ArrayOfDeathWheelBattleField", XmlDeathWheelBattleFieldData.class);
			stream.addImplicitCollection(XmlDeathWheelBattleFieldData.class, "filedArray");
			XmlDeathWheelBattleFieldData data = (XmlDeathWheelBattleFieldData) stream.fromXML(new FileReader(xmlFile));

			for (DeathWheelBattleMapMaking making : data.getFiledArray()) {
				making.setTable();
				this.battleMapMakings.add(making);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean readDataDeathWheelBattleOther() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("DeathWheelBattleOther"));
		if (!xmlFile.exists()) {
			logger.error("read DeathWheelBattleOther xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("other", String.class);
			stream.alias("ArrayOfDeathWheelBattleOther", XmlDeathWheelBattleOtherData.class);
			stream.addImplicitCollection(XmlDeathWheelBattleOtherData.class, "otherArray");
			XmlDeathWheelBattleOtherData data = (XmlDeathWheelBattleOtherData) stream.fromXML(new FileReader(xmlFile));
			for (String making : data.getOtherArray()) {
				this.battleOther.add(making);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean readDataDeathWheelBattleOtherWeapon() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("DeathWheelBattleOtherWeapon"));
		if (!xmlFile.exists()) {
			logger.error("read DeathWheelBattleOtherWeapon xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("weapon", String.class);
			stream.alias("ArrayOfDeathWheelBattleOtherWeapon", XmlDeathWheelBattleOtherWeaponData.class);
			stream.addImplicitCollection(XmlDeathWheelBattleOtherWeaponData.class, "otherWeaponArray");
			XmlDeathWheelBattleOtherWeaponData data = (XmlDeathWheelBattleOtherWeaponData) stream.fromXML(new FileReader(xmlFile));
			for (String making : data.getOtherWeaponArray()) {
				String weapon[] = making.split(",");
				this.battleOtherWeapon.put(WeaponType.asEnum(weapon[0]), making);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public DeathWheelBossMaking getDeathWheelBossMaking(Integer id) {
		return this.bossMakings.get(id);
	}

	public DeathWheelExtraBossMaking getDeathWheelExtraBossMaking(WeaponType weaponType) {
		return this.extraBossMakings.get(weaponType);
	}

	public DeathWheelBattleMapMaking getDeathWheelBattleMapMaking() {
		return this.battleMapMakings.get(Utils.randomInt(0, battleMapMakings.size() - 1));
	}

	public String getOther(WeaponType weaponType, List<String> notThese) {

		String other = null;

		while (other == null) {
			other = this.battleOther.get(Utils.randomInt(0, battleOther.size() - 1));
			if (other.equals("double-spear-heavy-gun-shield")) {//随机到了武器
				other = this.battleOtherWeapon.get(weaponType);
			}
			if (notThese.contains(other)) {
				other = null;
			}
		}

		return other;

	}

}