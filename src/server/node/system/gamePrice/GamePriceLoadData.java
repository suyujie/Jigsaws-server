package server.node.system.gamePrice;

import gamecore.system.AbstractLoadData;

import java.io.File;
import java.io.FileReader;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thoughtworks.xstream.XStream;

@XmlRootElement
public class GamePriceLoadData extends AbstractLoadData {

	private static Logger logger = LogManager.getLogger(GamePriceLoadData.class.getName());

	private static GamePriceLoadData instance = null;

	public GamePriceMaking gamePriceMaking = null;

	public static GamePriceLoadData getInstance() {
		if (instance == null) {
			instance = new GamePriceLoadData();
		}
		return instance;

	}

	public boolean readData() {
		return readData_game_price();
	}

	private boolean readData_game_price() {

		if (logger.isDebugEnabled()) {
			logger.info("read GamePrice data");
		}

		XStream stream = new XStream();
		File xmlFile = new File(getNewXmlName("GamePrice"));
		if (!xmlFile.exists()) {
			logger.error("read GamePrice xml error : no this xml :" + xmlFile.getAbsolutePath());
			return false;
		}

		try {
			stream.alias("GamePrice", GamePriceMaking.class);
			gamePriceMaking = (GamePriceMaking) stream.fromXML(new FileReader(xmlFile));

			System.out.println("============" + gamePriceMaking.getProjectPriceStr());
			System.out.println("============" + gamePriceMaking.getColorPrice());
			System.out.println("============" + gamePriceMaking.getProjectTimeStr());
			System.out.println("============" + gamePriceMaking.getWorkerPriceStr());

			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	public Integer getColorPrice() {
		if (gamePriceMaking != null) {
			return gamePriceMaking.getColorPrice();
		}
		return 0;
	}

}