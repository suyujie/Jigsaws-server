package server.node.system.record;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.entity.AbstractEntity;
import gamecore.io.ByteArrayGameOutput;
import gamecore.util.Clock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Root;
import server.node.system.npc.NpcPlayer;
import server.node.system.player.Player;

/**
 * 记录
 */
public class RecordBag extends AbstractEntity {

	private static final Logger logger = LogManager.getLogger(RecordBag.class.getName());

	private static final long serialVersionUID = 7080159462201696942L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "record_bag_";

	private final static int MaxRecordNum = 10;//10条最多
	private final static int maxRecordTime = 5 * 24 * 60 * 60;//5天最多

	private List<AttackRecord> attackRecords = new ArrayList<AttackRecord>();
	private List<DefenceRecord> defenceRecords = new ArrayList<DefenceRecord>();
	private List<RentRecord> rentRecords = new ArrayList<RentRecord>();

	public RecordBag() {
	}

	public RecordBag(Long id) {
		super(RecordBag.generateCacheKey(id));
	}

	public List<AttackRecord> getAttackRecords() {
		return attackRecords;
	}

	public void setAttackRecords(List<AttackRecord> attackRecords) {
		this.attackRecords = attackRecords;
	}

	public List<DefenceRecord> getDefenceRecords() {
		return defenceRecords;
	}

	public void setDefenceRecords(List<DefenceRecord> defenceRecords) {
		this.defenceRecords = defenceRecords;
	}

	public void setRentRecords(List<RentRecord> rentRecords) {
		this.rentRecords = rentRecords;
	}

	public List<Long> putAttackRecord(AttackRecord attackRecord, boolean removeExcess, boolean sync) {
		List<Long> removeList = new ArrayList<Long>();

		attackRecords.add(0, attackRecord);

		if (removeExcess) {
			Iterator<AttackRecord> iter = attackRecords.iterator();
			int i = 0;
			while (iter.hasNext()) {
				AttackRecord record = iter.next();
				if (record.getT() + maxRecordTime < Clock.currentTimeSecond() || i++ > MaxRecordNum) {//超过保存期
					removeList.add(record.getId());
					iter.remove();
				}
			}
		}
		if (sync) {
			this.synchronize();
		}
		return removeList;
	}

	public List<Long> putDefenceRecord(DefenceRecord defenceRecord, boolean removeExcess, boolean sync) {
		List<Long> removeList = new ArrayList<Long>();
		defenceRecords.add(0, defenceRecord);
		if (removeExcess) {
			Iterator<DefenceRecord> iter = defenceRecords.iterator();
			int i = 0;
			while (iter.hasNext()) {
				DefenceRecord record = iter.next();
				if (record.getT() + maxRecordTime < Clock.currentTimeSecond() || i++ > MaxRecordNum) {//超过保存期
					removeList.add(record.getId());
					iter.remove();
				}
			}
		}
		if (sync) {
			this.synchronize();
		}
		return removeList;
	}

	public List<Long> putRentRecord(RentRecord rentRecord, boolean removeExcess, boolean sync) {
		List<Long> removeList = new ArrayList<Long>();
		rentRecords.add(0, rentRecord);
		if (removeExcess) {
			Iterator<RentRecord> iter = rentRecords.iterator();
			int i = 0;
			while (iter.hasNext()) {
				RentRecord record = iter.next();
				if (record.getT() + maxRecordTime < Clock.currentTimeSecond() || i++ > MaxRecordNum) {//超过保存期
					removeList.add(record.getId());
					iter.remove();
				}
			}
		}
		if (sync) {
			this.synchronize();
		}
		return removeList;
	}

	public List<Long> removeExcessAttackRecord() {
		List<Long> removeIds = new ArrayList<Long>();
		Iterator<AttackRecord> iter = attackRecords.iterator();
		int i = 0;
		while (iter.hasNext()) {
			AttackRecord record = iter.next();
			if (record.getT() + maxRecordTime < Clock.currentTimeSecond() || i++ > MaxRecordNum) {//超过保存期
				removeIds.add(record.getId());
				iter.remove();
			}
		}
		return removeIds;
	}

	public List<Long> removeExcessDefenceRecord() {
		List<Long> removeIds = new ArrayList<Long>();

		Iterator<DefenceRecord> iter = defenceRecords.iterator();
		int i = 0;
		while (iter.hasNext()) {
			DefenceRecord record = iter.next();
			if (record.getT() + maxRecordTime < Clock.currentTimeSecond() || i++ > MaxRecordNum) {//超过保存期
				removeIds.add(record.getId());
				iter.remove();
			}
		}
		return removeIds;
	}

	public List<Long> removeExcessRentRecord() {
		List<Long> removeIds = new ArrayList<Long>();
		Iterator<RentRecord> iter = rentRecords.iterator();
		int i = 0;
		while (iter.hasNext()) {
			RentRecord record = iter.next();
			if (record.getT() + maxRecordTime < Clock.currentTimeSecond() || i++ > MaxRecordNum) {//超过保存期
				removeIds.add(record.getId());
				iter.remove();
			}
		}
		return removeIds;
	}

	//不在线被打记录
	public List<DefenceRecord> readDefenceRecordNoRead() {
		List<DefenceRecord> records = new ArrayList<>();
		for (DefenceRecord defenceRecord : defenceRecords) {
			if (defenceRecord.getStatus() == 0) {
				defenceRecord.setStatus(1);
				records.add(defenceRecord);
			}
		}
		this.synchronize();
		return records;
	}

	//对这条防守记录复仇
	public DefenceRecord revengeDefenceRecord(long id, boolean sync) {
		DefenceRecord dr = null;
		for (DefenceRecord defenceRecord : defenceRecords) {
			if (defenceRecord.getId() == id) {
				defenceRecord.setRevengeNum(defenceRecord.getRevengeNum() + 1);
				dr = defenceRecord;
			}
		}
		if (sync) {
			this.synchronize();
		}
		return dr;
	}

	//读取可用的攻击日志
	public List<AttackRecord> readAtkRecords() throws SQLException {
		List<AttackRecord> list = new ArrayList<AttackRecord>();
		for (AttackRecord attackRecord : attackRecords) {
			if (attackRecord.getIsNpc() == 1) {
				NpcPlayer opponentNpcPlayer = RedisHelperJson.getNpcPlayer(attackRecord.getOpponentId());
				if (opponentNpcPlayer != null) {
					attackRecord.setOpponentNpcPlayer(opponentNpcPlayer);
				}
			} else {
				Player opponentPlayer = Root.playerSystem.getPlayer(attackRecord.getOpponentId());
				if (opponentPlayer != null) {
					attackRecord.setOpponentPlayer(opponentPlayer);
				}
			}
			if (attackRecord.getOpponentNpcPlayer() != null || attackRecord.getOpponentPlayer() != null) {
				list.add(attackRecord);
			}
		}

		return list;
	}

	//读取可用的攻击日志
	public List<DefenceRecord> readDefRecords() throws SQLException {
		List<DefenceRecord> list = new ArrayList<DefenceRecord>();
		for (DefenceRecord defenceRecord : defenceRecords) {
			Player opponentPlayer = Root.playerSystem.getPlayer(defenceRecord.getOpponentId());
			if (opponentPlayer != null) {
				defenceRecord.setOpponentPlayer(opponentPlayer);
				list.add(defenceRecord);
			}
		}

		return list;
	}

	//读取可用的租借
	public List<RentRecord> readRentRecords() throws SQLException {
		List<RentRecord> list = new ArrayList<RentRecord>();
		for (RentRecord rentRecord : rentRecords) {
			Player tenantPlayer = Root.playerSystem.getPlayer(rentRecord.getTenantId());
			if (tenantPlayer != null) {
				rentRecord.setTenantPlayer(tenantPlayer);
				list.add(rentRecord);
			}
		}

		return list;
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(RecordBag.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

	public byte[] toArrayAsDefenderNotOnline() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			bago.put((byte) rentRecords.size());
			for (RentRecord rentRecord : rentRecords) {
				bago.putBytesNoLength(rentRecord.toByteArray());
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}

}
