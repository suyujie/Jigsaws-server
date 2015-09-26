package server.node.system.buff;

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
public class BuffLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(BuffLoadData.class.getName());

	private static BuffLoadData instance = new BuffLoadData();

	FastMap<Integer, BuffMaking> buffs = new FastMap<Integer, BuffMaking>();
	FastMap<String, SuitBuffMaking> suitBuffMakings = new FastMap<String, SuitBuffMaking>();

	public static BuffLoadData getInstance() {
		return instance;
	}

	public boolean readData() {

		boolean b = readData_buff();
		b = b & readData_suitBuff();
		return b;

	}

	/**
	 * 读取
	 */
	private boolean readData_buff() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("buffer"));
		if (!xmlFile.exists()) {
			logger.error("read xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("BufferData", BuffMaking.class);
			stream.alias("ArrayOfBufferData", XmlBuffData.class);
			stream.addImplicitCollection(XmlBuffData.class, "ArrayOfBufferData");
			XmlBuffData xmlBuffData = (XmlBuffData) stream.fromXML(new FileReader(xmlFile));

			for (BuffMaking buff : xmlBuffData.getBuffs()) {
				this.buffs.put(buff.getId(), buff);
			}
			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		}

		return false;
	}

	/**
	 * 读取
	 */
	private boolean readData_suitBuff() {

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("sameNamebuffer"));
		if (!xmlFile.exists()) {
			logger.error("read xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("SameNameBuffer", SuitBuffMaking.class);
			stream.alias("ArrayOfSameNameBuffer", XmlSuitBuffData.class);
			stream.addImplicitCollection(XmlSuitBuffData.class, "ArrayOfSameNameBuffer");
			XmlSuitBuffData xmlSuitBuffData = (XmlSuitBuffData) stream.fromXML(new FileReader(xmlFile));

			for (SuitBuffMaking sb : xmlSuitBuffData.getBuffs()) {
				this.suitBuffMakings.put(sb.getName(), sb);
			}
			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		}

		return false;
	}

	public BuffMaking getMaking(Integer id) {
		return this.buffs.get(id);
	}

	public BuffMaking getSuitBuffMaking(String name) {
		if (name != null) {
			SuitBuffMaking suitBuffMaking = suitBuffMakings.get(name);
			if (suitBuffMaking != null) {
				int buffId = suitBuffMaking.getBufferId();
				return getMaking(buffId);
			}
		}
		return null;
	}

}