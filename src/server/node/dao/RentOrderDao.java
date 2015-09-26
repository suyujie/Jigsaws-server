package server.node.dao;

import gamecore.db.AsyncDBTask;
import gamecore.db.DBOperator;
import gamecore.db.SyncDBUtil;
import gamecore.task.TaskCenter;

import java.util.List;
import java.util.Map;

import server.node.system.ConfigManager;
import server.node.system.player.Player;
import server.node.system.rent.RentOrder;
import server.node.system.rent.RentOrderStatus;

public class RentOrderDao {

	/**
	 * 保存 rentOrder
	 */
	public void save(Player player, RentOrder order) {
		String sql = "insert into t_rent_order(id,player_id,score, tenant_id, robot_id,robot_slot, cash, end_time,rent_time) values (?,?,?,?,?,?,?,?,?)";
		Object[] args = { order.getId(), player.getId(), order.getScore(), order.getTenantId(), order.getRobotId(), order.getRobotSlot(), order.getCash(), order.getEndTime(),
				order.getRentTime() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, player.getId(), sql, args));
	}

	/**
	 * 更新 rentOrder
	 */
//	public void update(RentOrder order) {
//		String sql = "update t_rent_order set tenant_id = ? ,status = ? ,end_time = ? , rent_time = ? where id = ?";
//		Object[] args = { order.getTenantId(), order.getStatus(), order.getEndTime(), order.getRentTime(), order.getId() };
//		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, order.getPlayerId(), sql, args));
//	}

	/**
	 * 删除 rentOrder
	 */
	public void delete(RentOrder order) {
		String sql = "delete from t_rent_order where id = ?";
		Object[] args = { order.getId() };
		TaskCenter.getInstance().executeWithSlidingWindow(new AsyncDBTask(DBOperator.Write, order.getPlayerId(), sql, args));
	}

	/**
	 * 根据玩家order
	 */
	public List<Map<String, Object>> readRentOrders(Player player) {
		String sql = "select * from t_rent_order where player_id = ?";
		Object[] args = { player.getId() };
		List<Map<String, Object>> list = SyncDBUtil.readList(DBOperator.Read, player.getId(), sql, args, false);
		return list;
	}

	/**
	 * 读取wait rentOrder list
	 */
	public List<Map<String, Object>> readWaitRentOrders() {
		String sql = "select * from t_rent_order where mod(id,?)=? and status = ? and tenant_id is null order by end_time asc limit 0,1000";
		Object[] args = { ConfigManager.getInstance().nodeNum, ConfigManager.getInstance().tag - 1, RentOrderStatus.Wait.asCode() };
		List<Map<String, Object>> list = SyncDBUtil.readList(DBOperator.Read, sql, args);
		return list;
	}

}
