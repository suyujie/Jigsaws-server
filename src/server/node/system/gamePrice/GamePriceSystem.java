package server.node.system.gamePrice;

import gamecore.system.AbstractSystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 道具价格系统
 */
public final class GamePriceSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(GamePriceSystem.class.getName());

	public GamePriceSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("GamePriceSystem start....");
		run = true;
		boolean b = GamePriceLoadData.getInstance().readData();
		System.out.println("GamePriceSystem start....OK");

		return b;
	}

	@Override
	public void shutdown() {

	}

}
