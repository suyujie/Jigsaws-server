package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.util.List;
import java.util.Map;

import server.node.system.player.Player;
import server.node.system.record.AttackRecord;
import server.node.system.record.DefenceRecord;
import server.node.system.record.RentRecord;

public class RecordDao {

	public List<Map<String, Object>> readAttackRecord(Player player) {
		String sql = "select * from t_record_attack where player_id = ? order by t desc";
		Object[] args = { player.getId() };
		return SyncDBUtil.readList(DBOperator.Read, player.getId(), sql, args, false);
	}

	public List<Map<String, Object>> readDefenceRecord(Player player) {
		String sql = "select * from t_record_defence where player_id = ? order by t desc";
		Object[] args = { player.getId() };
		return SyncDBUtil.readList(DBOperator.Read, player.getId(), sql, args, false);
	}

	public List<Map<String, Object>> readRentRecord(Player player) {
		String sql = "select * from t_record_rent where player_id = ? order by t desc";
		Object[] args = { player.getId() };
		return SyncDBUtil.readList(DBOperator.Read, player.getId(), sql, args, false);
	}

	public void saveAttackRecord(Player attacker, AttackRecord attackRecord) {
		String sql = "INSERT INTO t_record_attack(id, player_id,opponent_id,is_npc,is_win, win_num,change_cup, change_cash, t) values (?,?,?,?,?,?,?,?,?)";
		Object[] args = { attackRecord.getId(), attacker.getId(), attackRecord.getOpponentId(), attackRecord.getIsNpc(), attackRecord.getIsWin(), attackRecord.getWinNum(),
				attackRecord.getChangeCup(), attackRecord.getChangeCash(), attackRecord.getT() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, attacker.getId(), sql, args));
	}

	public void saveDefenceRecord(Player defender, DefenceRecord defenceRecord) {
		String sql = "INSERT INTO t_record_defence(id, defence_type,revenge_num,player_id,opponent_id,is_win, win_num,change_cup, change_cash, status, t) values (?,?,?,?,?,?,?,?,?,?,?)";
		Object[] args = { defenceRecord.getId(), defenceRecord.getType(), defenceRecord.getRevengeNum(), defender.getId(), defenceRecord.getOpponentId(), defenceRecord.getIsWin(),
				defenceRecord.getWinNum(), defenceRecord.getChangeCup(), defenceRecord.getChangeCash(), defenceRecord.getStatus(), defenceRecord.getT() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, defender.getId(), sql, args));
	}

	public void updateDefenceRecord(Player player, DefenceRecord defenceRecord) {
		String sql = "update t_record_defence set revenge_num = ? where id = ? ";
		Object[] args = { defenceRecord.getRevengeNum(), defenceRecord.getId() };

		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void updateDefenceRecords(Player player, List<DefenceRecord> defenceRecords) {

		StringBuffer idsSb = new StringBuffer();
		for (DefenceRecord defenceRecord : defenceRecords) {
			idsSb.append(defenceRecord.getId()).append(",");
		}
		if (idsSb.toString().endsWith(",")) {
			idsSb.deleteCharAt(idsSb.length() - 1);
		}

		String sql = "update t_record_defence set status = ? where id in ( " + idsSb.toString() + " ) ";
		Object[] args = { 1 };

		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));

	}

	public void saveRentRecord(Player player, RentRecord rentRecord) {
		String sql = "INSERT INTO t_record_rent(id, player_id,opponent_id, change_cash, t) values (?,?,?,?,?)";
		Object[] args = { rentRecord.getId(), player.getId(), rentRecord.getTenantId(), rentRecord.getChangeCash(), rentRecord.getT() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void deleteAttackRecord(Player player, Long id) {
		String sql = "delete from t_record_attack where id = ? ";
		Object[] args = { id };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void deleteDefenceRecord(Player defender, Long id) {
		String sql = "delete from t_record_defence where id = ? ";
		Object[] args = { id };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, defender.getId(), sql, args));
	}

	public void deleteRentRecord(Player player, Long id) {
		String sql = "delete from t_record_rent where id = ? ";
		Object[] args = { id };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void deleteAttackRecords(Player player, List<Long> ids) {

		StringBuffer idsSb = new StringBuffer();
		for (Long id : ids) {
			idsSb.append(id).append(",");
		}
		if (idsSb.toString().endsWith(",")) {
			idsSb.deleteCharAt(idsSb.length() - 1);
		}

		String sql = "DELETE from t_record_attack where id in ( " + idsSb.toString() + " ) ";
		Object[] args = {};

		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void deleteDefenceRecords(Player player, List<Long> ids) {
		StringBuffer idsSb = new StringBuffer();
		for (Long id : ids) {
			idsSb.append(id).append(",");
		}
		if (idsSb.toString().endsWith(",")) {
			idsSb.deleteCharAt(idsSb.length() - 1);
		}

		String sql = "DELETE from t_record_defence where id in ( " + idsSb.toString() + " ) ";
		Object[] args = {};

		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	public void deleteRentRecords(Player player, List<Long> ids) {
		StringBuffer idsSb = new StringBuffer();
		for (Long id : ids) {
			idsSb.append(id).append(",");
		}
		if (idsSb.toString().endsWith(",")) {
			idsSb.deleteCharAt(idsSb.length() - 1);
		}

		String sql = "DELETE from t_record_rent where id in ( " + idsSb.toString() + " ) ";
		Object[] args = {};

		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

}
