package server.node.dao;

import java.util.List;
import java.util.Map;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;
import server.node.system.gameImage.GameImage;
import server.node.system.player.Player;

public class ImageDao {

	public void saveImage(Player player, GameImage gameImage) {
		String sql = "insert into t_image(id,player_id) values (?,?)";
		Object[] args = { gameImage.getId(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public List<Map<String, Object>> readImages() {
		String sql = "SELECT * from t_image limit 0,10000";
		Object[] args = {};
		return SyncDBUtil.readList(DBOperator.Read, sql, args);
	}

}
