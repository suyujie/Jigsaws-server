package server.node.system.handbook;

import gamecore.system.AbstractLoadData;
import gamecore.util.DataUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;
import javolution.util.FastTable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class HandbookData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(HandbookData.class.getName());

	private static HandbookData instance = null;

	FastMap<String, HandbookMaking> handbookMakingMap = null;
	FastTable<HandbookMaking> handbookMakings = null;

	public static HandbookData getInstance() {
		if (instance == null) {
			instance = new HandbookData();
		}
		return instance;

	}

	public boolean readData() {

		if (logger.isDebugEnabled()) {
			logger.info("read handbookMakings data");
		}

		this.handbookMakingMap = new FastMap<String, HandbookMaking>();
		this.handbookMakings = new FastTable<HandbookMaking>();

		boolean b = readData_handbookMakings();

		return b;
	}

	private boolean readData_handbookMakings() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("handbook"));
		if (!xmlFile.exists()) {
			logger.error("read xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("HandbookData", HandbookMaking.class);
			stream.alias("ArrayOfHandbookData", XmlHandbookData.class);
			stream.addImplicitCollection(XmlHandbookData.class, "ArrayOfHandbookData");
			XmlHandbookData xmlHandbookData = (XmlHandbookData) stream.fromXML(new FileReader(xmlFile));

			for (HandbookMaking making : xmlHandbookData.getHandbooks()) {
				making.setQualityIds(DataUtils.string2Array(making.getQualityId()));
				this.handbookMakingMap.put(making.getName(), making);
				this.handbookMakings.add(making);
			}

			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		}
		return false;
	}

	public FastTable<HandbookMaking> getHandbookMakings() {
		return handbookMakings;
	}

	public HandbookMaking getHandbookMaking(String suitName) {
		return handbookMakingMap.get(suitName);
	}

}