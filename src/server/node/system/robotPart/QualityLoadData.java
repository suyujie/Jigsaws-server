package server.node.system.robotPart;

import gamecore.system.AbstractLoadData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class QualityLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(QualityLoadData.class.getName());

	private static QualityLoadData instance = null;

	FastMap<Integer, QualityMaking> quality = null;

	public static QualityLoadData getInstance() {
		if (instance == null) {
			instance = new QualityLoadData();
		}
		return instance;

	}

	public boolean readData() {

		if (logger.isDebugEnabled()) {
			logger.info("read QualityData data");
		}

		this.quality = new FastMap<Integer, QualityMaking>();

		boolean b = readData_quality();

		return b;
	}

	/**
	 * 读取 quality
	 * @return
	 */
	private boolean readData_quality() {

		if (logger.isDebugEnabled()) {
			logger.info("read part_arm data");
		}

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("QualityData"));
		if (!xmlFile.exists()) {
			logger.error("read xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("QualityData", QualityMaking.class);
			stream.alias("ArrayOfQualityData", XmlQualityData.class);
			stream.addImplicitCollection(XmlQualityData.class, "array");
			XmlQualityData xmlQualityData = (XmlQualityData) stream.fromXML(new FileReader(xmlFile));

			int i = 0;//这个i 正好对应上PartQualityType里面的材质code
			for (QualityMaking qualityMaking : xmlQualityData.getArray()) {
				this.quality.put(i++, qualityMaking);
			}
			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		}
		return false;
	}

	public QualityMaking getQualityMaking(int qualityType) {
		return this.quality.get(qualityType);
	}

}