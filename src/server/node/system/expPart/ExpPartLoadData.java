package server.node.system.expPart;

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
public class ExpPartLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(ExpPartLoadData.class.getName());

	private static ExpPartLoadData instance = null;

	FastMap<Integer, ExpPartMaking> part_exps = new FastMap<Integer, ExpPartMaking>();

	public static ExpPartLoadData getInstance() {
		if (instance == null) {
			instance = new ExpPartLoadData();
		}
		return instance;

	}

	public boolean readData() {

		boolean b = readData_exp();

		return b;
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
			stream.alias("PartsData", ExpPartMaking.class);
			stream.alias("ArrayOfPartsData", XmlExpPartData.class);
			stream.addImplicitCollection(XmlExpPartData.class, "ArrayOfPartsData");
			XmlExpPartData xmlExpPartData = (XmlExpPartData) stream.fromXML(new FileReader(xmlFile));

			for (ExpPartMaking expPart : xmlExpPartData.getExpParts()) {
				this.part_exps.put(expPart.getId(), expPart);
			}

			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		}
		return false;
	}

	public ExpPartMaking getMaking(Integer id) {

		return this.part_exps.get(id);

	}

	public FastMap<Integer, ExpPartMaking> getPart_exps() {
		return part_exps;
	}

	public void setPart_exps(FastMap<Integer, ExpPartMaking> part_exps) {
		this.part_exps = part_exps;
	}

}