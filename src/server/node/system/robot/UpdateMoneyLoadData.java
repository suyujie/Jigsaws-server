package server.node.system.robot;

import gamecore.system.AbstractLoadData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.robotPart.PartMaking;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class UpdateMoneyLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(UpdateMoneyLoadData.class.getName());

	private static UpdateMoneyLoadData instance = null;

	HashMap<Integer, UpdateMoneyMaking> updateCashMakings = null;

	public static UpdateMoneyLoadData getInstance() {
		if (instance == null) {
			instance = new UpdateMoneyLoadData();
		}
		return instance;

	}

	public boolean readData() {

		this.updateCashMakings = new HashMap<Integer, UpdateMoneyMaking>();

		boolean b = readData_udpateCash();

		return b;
	}

	private boolean readData_udpateCash() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("updateMoney"));
		if (!xmlFile.exists()) {
			logger.error("read xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("UpdateMoney", UpdateMoneyMaking.class);
			stream.alias("ArrayOfUpdateMoney", XmlUpdateCashData.class);
			stream.addImplicitCollection(XmlUpdateCashData.class, "ArrayOfUpdateMoney");
			XmlUpdateCashData xmlUpdateCashData = (XmlUpdateCashData) stream.fromXML(new FileReader(xmlFile));

			int i = 0;//这个i 正好对应上PartQualityType里面的材质code
			for (UpdateMoneyMaking updateCashMaking : xmlUpdateCashData.getArrayOfUpdateMoney()) {
				updateCashMaking.setTables();
				this.updateCashMakings.put(i++, updateCashMaking);
			}
			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		}
		return false;
	}

	public UpdateMoneyMaking getUpdateCashMaking(int qualityType) {
		return this.updateCashMakings.get(qualityType);
	}

	public int getCash(PartMaking partMaking, int oldLevel, int oldExp, int level, int newExp, boolean moreMaxLevel) {

		//根据了列表的计算的钱数（每级的相加+不够一级的（当前等级钱数*剩余经验/升级经验））
		UpdateMoneyMaking cashMaking = updateCashMakings.get(partMaking.getPartQualityType().asCode());

		//当前等级升到下一级   整级需要的钱 *(总经验-已有经验)/总经验
		int cash = 0;

		if (level > oldLevel) {//升级了
			cash += cashMaking.getUpdateCash(oldLevel) * (partMaking.getExp(oldLevel) - oldExp) / partMaking.getExp(oldLevel) * 3 / 10 + 1;
			cash += cashMaking.getUpdateCash(oldLevel) * 7 / 10 + 1;
			//之后的整级需要cash
			for (int i = oldLevel + 1; i < level; i++) {
				cash += cashMaking.getUpdateCash(i) * 3 / 10 + 1;
				cash += cashMaking.getUpdateCash(i) * 7 / 10 + 1;
			}
			//最后一级后多出的经验
			if (!moreMaxLevel) {
				cash += cashMaking.getUpdateCash(level) * (newExp) / partMaking.getExp(level) * 3 / 10 + 1;
			}
		} else {//没升级
			cash += cashMaking.getUpdateCash(level) * (newExp - oldExp) / partMaking.getExp(level) * 3 / 10 + 1;
		}

		return cash;

	}

	public int getGold(PartMaking partMaking, int oldLevel, int level) {

		UpdateMoneyMaking cashMaking = updateCashMakings.get(partMaking.getPartQualityType().asCode());

		int gold = 0;

		if (level > oldLevel) {//升级了
			//之后的整级需要gold
			for (int i = oldLevel; i < level; i++) {
				gold += cashMaking.getUpdateGold(i);
			}
		}

		return gold;

	}

}