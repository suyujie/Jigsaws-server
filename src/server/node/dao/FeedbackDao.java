package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.task.TaskCenter;
import gamecore.util.Clock;
import server.node.system.feedback.Feedback;
import server.node.system.player.Player;

public class FeedbackDao {

	/**
	 * 反馈信息
	 */
	public void save(Player player, Feedback feedback) {
		String sql = "insert into t_feedback(id,player_id,msg,email,t) values (?,?,?,?,?)";
		Object[] args = { feedback.getId(), player.getId(), feedback.getMsg(), feedback.getEmail(), Clock.currentTimeMillis() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Log, player.getId(), sql, args));
	}

}
