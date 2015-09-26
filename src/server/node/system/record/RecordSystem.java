package server.node.system.record;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.db.DaoFactory;
import gamecore.system.AbstractSystem;
import gamecore.system.SystemResult;
import gamecore.util.Clock;
import gamecore.util.Utils;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import server.node.dao.RecordDao;
import server.node.system.battle.PvpBattleResult;
import server.node.system.player.Player;
import server.node.system.rent.RentOrder;

/**
 * 记录系统。
 */
public final class RecordSystem extends AbstractSystem {

	public RecordSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("RecordSystem start....");

		System.out.println("RecordSystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {
	}

	/**
	 * 读取 记录包 实体
	 */
	public RecordBag getRecordBag(Player player) {
		RecordBag recordBag = RedisHelperJson.getRecordBag(player.getId());
		if (recordBag == null) {
			recordBag = readRecordBagFromDB(player);
		}
		return recordBag;
	}

	/**
	 * 从数据库中读取 记录包
	 */
	private RecordBag readRecordBagFromDB(Player player) {

		RecordBag recordBag = new RecordBag(player.getId());

		RecordDao recordDao = DaoFactory.getInstance().borrowRecordDao();
		List<Map<String, Object>> attackRecordList = recordDao.readAttackRecord(player);
		List<Map<String, Object>> defenceRecordList = recordDao.readDefenceRecord(player);
		List<Map<String, Object>> rentRecordList = recordDao.readRentRecord(player);
		DaoFactory.getInstance().returnRecordDao(recordDao);

		if (attackRecordList != null && !attackRecordList.isEmpty()) {
			for (Map<String, Object> map : attackRecordList) {
				Long id = ((BigInteger) map.get("id")).longValue();
				Long opponentId = ((BigInteger) map.get("opponent_id")).longValue();
				int isNpc = ((Long) map.get("is_npc")).intValue();
				int win_num = ((Long) map.get("win_num")).intValue();
				int isWin = ((Long) map.get("is_win")).intValue();
				int changeCup = ((Long) map.get("change_cup")).intValue();
				int changeCash = ((Long) map.get("change_cash")).intValue();
				Integer lootExpId = map.get("loot_exp_id") == null ? null : ((Long) map.get("loot_exp_id")).intValue();
				String lootChip = (String) map.get("loot_chip");
				Integer lootBergId = map.get("loot_berg_id") == null ? null : ((Long) map.get("loot_berg_id")).intValue();
				Long t = ((BigInteger) map.get("t")).longValue();
				AttackRecord attackRecord = new AttackRecord(id, opponentId, isNpc, isWin, win_num, changeCup, changeCash, lootExpId, lootChip, lootBergId, t);
				recordBag.putAttackRecord(attackRecord, false, false);
			}
		}
		if (defenceRecordList != null && !defenceRecordList.isEmpty()) {
			for (Map<String, Object> map : defenceRecordList) {
				Long id = ((BigInteger) map.get("id")).longValue();
				int type = ((Long) map.get("defence_type")).intValue();
				int revengeNum = ((Long) map.get("revenge_num")).intValue();
				Long opponentId = ((BigInteger) map.get("opponent_id")).longValue();
				int win_num = ((Long) map.get("win_num")).intValue();
				int isWin = ((Long) map.get("is_win")).intValue();
				int changeCup = ((Long) map.get("change_cup")).intValue();
				int changeCash = ((Long) map.get("change_cash")).intValue();
				Integer lootExpId = map.get("loot_exp_id") == null ? null : ((Long) map.get("loot_exp_id")).intValue();
				String lootChip = (String) map.get("loot_chip");
				Integer lootBergId = map.get("loot_berg_id") == null ? null : ((Long) map.get("loot_berg_id")).intValue();
				int status = ((Long) map.get("status")).intValue();
				Long t = ((BigInteger) map.get("t")).longValue();
				DefenceRecord defenceRecord = new DefenceRecord(id, type, revengeNum, opponentId, isWin, win_num, changeCup, changeCash, lootExpId, lootChip, lootBergId, status, t);
				recordBag.putDefenceRecord(defenceRecord, false, false);
			}
		}
		if (rentRecordList != null && !rentRecordList.isEmpty()) {
			for (Map<String, Object> map : rentRecordList) {
				Long id = ((BigInteger) map.get("id")).longValue();
				Long opponentId = ((BigInteger) map.get("opponent_id")).longValue();
				int changeCash = ((Long) map.get("change_cash")).intValue();
				Long t = ((BigInteger) map.get("t")).longValue();
				RentRecord rentRecord = new RentRecord(id, opponentId, changeCash, t);
				recordBag.putRentRecord(rentRecord, false, false);
			}
		}

		//判断是否超量,并删除
		removeExcess(player, recordBag);

		recordBag.synchronize();

		return recordBag;
	}

	/**
	 * 读取记录  攻击 防御  租赁
	 */
	public SystemResult getRecords(Player player) throws SQLException {
		SystemResult result = new SystemResult();

		RecordBag recordBag = getRecordBag(player);

		List<AttackRecord> attackRecords = recordBag.readAtkRecords();
		List<DefenceRecord> defenceRecords = recordBag.readDefRecords();
		List<RentRecord> rentRecords = recordBag.readRentRecords();

		result.setMap("attackRecords", attackRecords);
		result.setMap("defenceRecords", defenceRecords);
		result.setMap("rentRecords", rentRecords);

		return result;

	}

	private void removeExcess(Player player, RecordBag recordBag) {
		List<Long> removeAttackRecordIds = recordBag.removeExcessAttackRecord();
		List<Long> removeDefenceRecordIds = recordBag.removeExcessDefenceRecord();
		List<Long> removeRentRecordIds = recordBag.removeExcessRentRecord();

		RecordDao recordDao = DaoFactory.getInstance().borrowRecordDao();

		if (removeAttackRecordIds != null && !removeAttackRecordIds.isEmpty()) {
			recordDao.deleteAttackRecords(player, removeAttackRecordIds);
		}
		if (removeDefenceRecordIds != null && !removeDefenceRecordIds.isEmpty()) {
			recordDao.deleteAttackRecords(player, removeDefenceRecordIds);
		}
		if (removeRentRecordIds != null && !removeRentRecordIds.isEmpty()) {
			recordDao.deleteRentRecords(player, removeRentRecordIds);
		}

		DaoFactory.getInstance().returnRecordDao(recordDao);

	}

	private long getRecordStoreId() {
		return Utils.getOneLongId();
	}

	public void addPvpRecord(Player attacker, Player defender, PvpBattleResult pvpBattleResult) {

		//对attacker的操作
		RecordBag attackerRecordBag = getRecordBag(attacker);

		AttackRecord attackRecord = new AttackRecord(getRecordStoreId(), defender.getId(), 0, pvpBattleResult.isWin() ? 1 : 0, pvpBattleResult.getAttackerWinNum(),
				pvpBattleResult.isWin() ? pvpBattleResult.getPvpBattle().getWinCup() : pvpBattleResult.getPvpBattle().getLoseCup(), pvpBattleResult.attackerWinLootCash(),
				pvpBattleResult.getLootExpId(), pvpBattleResult.getLootChipName(), pvpBattleResult.getLootBergId(), Clock.currentTimeSecond());
		List<Long> removeAttackRecordIds = attackerRecordBag.putAttackRecord(attackRecord, true, true);//加入新的记录,如果超过数量,删除并返回要删除的

		RecordDao recordDao1 = DaoFactory.getInstance().borrowRecordDao();

		recordDao1.saveAttackRecord(attacker, attackRecord);
		if (removeAttackRecordIds != null && !removeAttackRecordIds.isEmpty()) {
			recordDao1.deleteAttackRecords(attacker, removeAttackRecordIds);
		}
		DaoFactory.getInstance().returnRecordDao(recordDao1);

		//对defender的操作,在线的话不加入日志
		if (!defender.checkOnLine()) {
			RecordBag defenderRecordBag = getRecordBag(defender);
			DefenceRecord defenceRecord = new DefenceRecord(getRecordStoreId(), pvpBattleResult.getPvpBattle().getType(), 0, attacker.getId(), pvpBattleResult.isWin() ? 0 : 1,
					pvpBattleResult.getDefenderWinNum(), pvpBattleResult.isWin() ? pvpBattleResult.getPvpBattle().getWinCup() : pvpBattleResult.getPvpBattle().getLoseCup(),
					pvpBattleResult.attackerWinLootCash(), pvpBattleResult.getLootExpId(), pvpBattleResult.getLootChipName(), pvpBattleResult.getLootBergId(), 0,
					Clock.currentTimeSecond());
			List<Long> removeDefenceRecordIds = defenderRecordBag.putDefenceRecord(defenceRecord, true, true);//加入新的记录,如果超过数量,删除并返回要删除的

			RecordDao recordDao2 = DaoFactory.getInstance().borrowRecordDao();

			recordDao2.saveDefenceRecord(defender, defenceRecord);
			if (removeDefenceRecordIds != null && !removeDefenceRecordIds.isEmpty()) {
				recordDao2.deleteDefenceRecords(defender, removeDefenceRecordIds);
			}
			DaoFactory.getInstance().returnRecordDao(recordDao2);
		}

	}

	//复仇这条防守记录
	public void revengeDefenceRecord(Player player, long defenceRecordId) {
		//对attacker的操作
		RecordBag recordBag = getRecordBag(player);
		//复仇操作
		DefenceRecord defenceRecord = recordBag.revengeDefenceRecord(defenceRecordId, true);
		if (defenceRecord != null) {
			RecordDao recordDao = DaoFactory.getInstance().borrowRecordDao();
			recordDao.updateDefenceRecord(player, defenceRecord);
			DaoFactory.getInstance().returnRecordDao(recordDao);
		}

	}

	public void addAttackRecordWithNpc(Player attacker, PvpBattleResult pvpBattleResult) {

		int attackerWin = pvpBattleResult.isWin() ? 1 : 0;

		//对attacker的操作
		RecordBag attackerRecordBag = getRecordBag(attacker);

		AttackRecord attackRecord = new AttackRecord(getRecordStoreId(), pvpBattleResult.getPvpBattle().getPvpNpc().getId(), 1, attackerWin, pvpBattleResult.getAttackerWinNum(),
				pvpBattleResult.isWin() ? pvpBattleResult.getPvpBattle().getWinCup() : pvpBattleResult.getPvpBattle().getLoseCup(), pvpBattleResult.attackerWinLootCash(), null,
				null, null, Clock.currentTimeSecond());
		List<Long> removeAttackRecordIds = attackerRecordBag.putAttackRecord(attackRecord, true, true);//加入新的记录,如果超过数量,删除并返回要删除的

		RecordDao recordDao = DaoFactory.getInstance().borrowRecordDao();

		recordDao.saveAttackRecord(attacker, attackRecord);
		if (removeAttackRecordIds != null && !removeAttackRecordIds.isEmpty()) {
			recordDao.deleteAttackRecords(attacker, removeAttackRecordIds);
		}

		DaoFactory.getInstance().returnRecordDao(recordDao);

	}

	public void addRentRecord(Player player, RentOrder rentOrder) {

		RentRecord rentRecord = new RentRecord(getRecordStoreId(), rentOrder.getTenantId(), rentOrder.getCash(), rentOrder.getRentTime());
		RecordBag recordBag = getRecordBag(player);
		List<Long> deleteRecordIds = recordBag.putRentRecord(rentRecord, true, true);

		RecordDao recordDao = DaoFactory.getInstance().borrowRecordDao();
		recordDao.saveRentRecord(player, rentRecord);
		if (deleteRecordIds != null && !deleteRecordIds.isEmpty()) {
			recordDao.deleteRentRecords(player, deleteRecordIds);
		}
		DaoFactory.getInstance().returnRecordDao(recordDao);

	}

	public List<DefenceRecord> readDefenceRecordsNoRead(Player player) {
		List<DefenceRecord> records = null;
		//对attacker的操作
		RecordBag recordBag = getRecordBag(player);
		records = recordBag.readDefenceRecordNoRead();

		if (records != null && !records.isEmpty()) {
			RecordDao recordDao = DaoFactory.getInstance().borrowRecordDao();
			recordDao.updateDefenceRecords(player, records);
			DaoFactory.getInstance().returnRecordDao(recordDao);
		}

		return records;
	}

}
