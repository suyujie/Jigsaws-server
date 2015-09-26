package server.node.system.pay;

import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.util.Clock;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Map;

import server.node.system.log.PayLog;

public abstract class AbstractPaid {

	public long getDBTagId(String payChannel, String orderId) {
		return new StringBuffer().append(payChannel).append("_").append(orderId).toString().hashCode();
	}

	/**
	 * 添加支付日志,支付日志不排队，即时写入
	 */
	public void addPayLog(PayLog payLog) {
		String sql = "INSERT INTO  t_pay_log (player_id, order_id,pay_channel,recharge_package_id,coin_type,coin,info,ct,st,status) VALUES (?,?,?,?,?,?,?,?,?,?)";
		Object[] args = { payLog.getPlayerId(), payLog.getOrderId(), payLog.getPayChannel().asCode(), payLog.getRechargePackageId(),
				payLog.getCoinType() == null ? null : payLog.getCoinType().asCode(), payLog.getCoin(), payLog.getInfo(), payLog.getCt(), payLog.getSt(),
				payLog.getPayStatus().asCode() };
		SyncDBUtil.execute(DBOperator.Log, payLog.getDBTagId(), sql, args);
	}

	/**
	 * 添加尝试支付日志
	 */
	public void addPayTryLog(PayLog payLog) {
		String sql = "INSERT INTO t_pay_try_log (player_id, pay_channel,recharge_package_id,t) VALUES (?,?,?,?)";
		Object[] args = { payLog.getPlayerId(), payLog.getPayChannel().asCode(), payLog.getRechargePackageId(), Clock.currentTimeSecond() };
		SyncDBUtil.execute(DBOperator.Log, payLog.getDBTagId(), sql, args);
	}

	/**
	 * 更新支付日志
	 */
	public void updatePayLogStatus(PayLog payLog) {
		String sql = "UPDATE t_pay_log set status = ? , st = ? WHERE pay_channel = ? and order_id = ?";
		Object[] args = { payLog.getPayStatus().asCode(), Clock.currentTimeSecond(), payLog.getPayChannel().asCode(), payLog.getOrderId() };
		SyncDBUtil.execute(DBOperator.Log, payLog.getDBTagId(), sql, args);
	}

	/**
	 * 更新支付日志
	 */
	public void updatePayLogStatusAndInfo(PayLog payLog) {
		String sql = "UPDATE t_pay_log set status = ? ,info= ? , st = ? WHERE pay_channel = ? and order_id = ?";
		Object[] args = { payLog.getPayStatus().asCode(), payLog.getInfo(), Clock.currentTimeSecond(), payLog.getPayChannel().asCode(), payLog.getOrderId() };
		SyncDBUtil.execute(DBOperator.Log, payLog.getDBTagId(), sql, args);
	}

	/**
	 * 读取
	 * @throws SQLException 
	 */
	public PayLog readPayLog(PayChannel payChannel, String orderId) throws SQLException {
		String sql = "select * from t_pay_log where pay_channel = ? and order_id = ? limit 0,1";
		Object[] args = { payChannel.asCode(), orderId };
		Map<String, Object> map = SyncDBUtil.readMap(DBOperator.Log, getDBTagId(payChannel.asCode(), orderId), sql, args, false);

		if (map == null) {
			return null;
		} else {
			Long playerId = ((BigInteger) map.get("player_id")).longValue();
			String rechargePackageId = (String) map.get("recharge_package_id");
			PayStatus payStatus = PayStatus.asEnum(((Long) map.get("status")).intValue());
			PayLog payLog = new PayLog(playerId, payChannel, orderId, rechargePackageId, payStatus);
			return payLog;
		}

	}
}
