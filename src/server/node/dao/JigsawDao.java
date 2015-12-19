package server.node.dao;

import java.util.List;
import java.util.Map;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;
import server.node.system.jigsaw.Jigsaw;
import server.node.system.jigsaw.JigsawState;

public class JigsawDao {

	public void save(Jigsaw jigsaw) {
		String sql = "insert into t_jigsaw(id,player_id,bucket_name,url,good,bad,drop,state) values (?,?,?,?,?,?,?,?)";
		Object[] args = { jigsaw.getId(), jigsaw.getPlayerId(), jigsaw.getBucketName(), jigsaw.getUrl(),
				jigsaw.getGood(), jigsaw.getBad(), jigsaw.getDrop(), jigsaw.getState().asCode() };
		TaskCenter.getInstance()
				.executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, jigsaw.getPlayerId(), sql, args));
	}

	public List<Map<String, Object>> read(int begin, int num) {
		String sql = "SELECT * from t_jigsaw where state = ? or state = ? limit ?,?";
		Object[] args = { JigsawState.ENABLE.asCode(), JigsawState.REPORT_OK.asCode(), begin, num };
		return SyncDBUtil.readList(DBOperator.Read, sql, args);
	}

	public Map<String, Object> read(Long id) {
		String sql = "SELECT * from t_jigsaw where id = ? limit 0,1";
		Object[] args = { id };
		return SyncDBUtil.readMap(DBOperator.Read, sql, args);
	}

	public void update(Jigsaw jigsaw) {
		String sql = "update t_jigsaw set good = ?,bad = ?,bad = ?,state = ? where id = ?";
		Object[] args = { jigsaw.getGood(), jigsaw.getBad(), jigsaw.getDrop(), jigsaw.getState().asCode(),
				jigsaw.getId() };
		TaskCenter.getInstance()
				.executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, jigsaw.getPlayerId(), sql, args));
	}

	public void delete(Jigsaw jigsaw) {
		String sql = "update t_jigsaw set url = ?,state = ? where id = ?";
		Object[] args = { jigsaw.getUrl(), jigsaw.getState().asCode(), jigsaw.getId() };
		TaskCenter.getInstance()
				.executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, jigsaw.getPlayerId(), sql, args));
	}

	public void deleteTrue(Jigsaw jigsaw) {
		String sql = "delete from t_jigsaw where id = ?";
		Object[] args = { jigsaw.getId() };
		TaskCenter.getInstance()
				.executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, jigsaw.getPlayerId(), sql, args));
	}

}
