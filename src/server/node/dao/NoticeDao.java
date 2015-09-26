package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.sql.SQLException;
import java.util.Map;

import server.node.system.notice.NoticeBag;
import server.node.system.player.Player;

public class NoticeDao {

	public Map<String, Object> readNoticeBag(Player player) throws SQLException {
		String sql = "select * from t_notice_status where id = ? limit 0,1";
		Object[] args = { player.getId() };
		return SyncDBUtil.readMap(DBOperator.Read, player.getId(), sql, args, false);
	}

	public void saveNoticeBag(Player player, NoticeBag noticeBag) {
		String sql = "INSERT INTO t_notice_status(id, readed_ids,sended_ids) values (?,?,?)";
		Object[] args = { player.getId(), noticeBag.getReadIdsStr(), noticeBag.getSendIdsStr() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void updateNoticeBag(Player player, NoticeBag noticeBag) {
		String sql = "UPDATE t_notice_status set readed_ids = ? , sended_ids = ? where id = ?";
		Object[] args = { noticeBag.getReadIdsStr(), noticeBag.getSendIdsStr(), player.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

}
