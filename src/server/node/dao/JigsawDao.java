package server.node.dao;

import java.util.List;
import java.util.Map;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;
import server.node.system.jigsaw.Jigsaw;

public class JigsawDao {

	public void save(Jigsaw jigsaw) {
		String sql = "insert into t_jigsaw(id,player_id,url,good,bad,enable) values (?,?,?,?,?,?)";
		Object[] args = { jigsaw.getId(), jigsaw.getPlayerId(), jigsaw.getUrl(), jigsaw.getGood(), jigsaw.getBad() };
		TaskCenter.getInstance()
				.executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, jigsaw.getPlayerId(), sql, args));
	}

	public List<Map<String, Object>> read() {
		String sql = "SELECT * from t_jigsaw where enable = ? limit 0,10000";
		Object[] args = { 1 };
		return SyncDBUtil.readList(DBOperator.Read, sql, args);
	}

	public Map<String, Object> read(Long id) {
		String sql = "SELECT * from t_jigsaw where id = ? limit 0,1";
		Object[] args = { id };
		return SyncDBUtil.readMap(DBOperator.Read, sql, args);
	}

	public void update(Jigsaw jigsaw) {
		String sql = "update t_jigsaw set good = ?,bad = ?,enable = ? where id = ?";
		Object[] args = { jigsaw.getGood(), jigsaw.getBad(), jigsaw.getId(), jigsaw.isEnable() ? 1 : 0 };
		TaskCenter.getInstance()
				.executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, jigsaw.getPlayerId(), sql, args));
	}

}
