package server.node.system.feedback;

import gamecore.system.AbstractSystem;
import gamecore.system.SystemResult;
import gamecore.util.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.dao.DaoFactory;
import server.node.dao.FeedbackDao;
import server.node.system.player.Player;

/** 
 * 抽奖系统。
 */
public final class FeedbackSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(FeedbackSystem.class.getName());

	public FeedbackSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("FeedbackSystem start....");

		System.out.println("FeedbackSystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {
	}

	public SystemResult saveFeedback(Player player, String email, String msg) {
		SystemResult result = new SystemResult();

		Feedback feedback = new Feedback(Utils.getOneLongId(), msg, email);

		FeedbackDao dao = DaoFactory.getInstance().borrowFeedbackDao();
		dao.save(player, feedback);
		DaoFactory.getInstance().returnFeedbackDao(dao);

		return result;
	}

}
