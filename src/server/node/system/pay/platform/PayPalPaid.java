package server.node.system.pay.platform;

import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;

import java.sql.SQLException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Root;
import server.node.system.log.PayLog;
import server.node.system.pay.AbstractPaid;
import server.node.system.pay.PayChannel;
import server.node.system.pay.PayStatus;
import server.node.system.player.GoldType;
import server.node.system.player.Player;
import server.node.system.rechargePackage.ItemType;
import server.node.system.rechargePackage.RechargePackage;
import common.coin.CoinType;

/**
 * paypal付费流程
 * 
 * 1，客户端--->paypal		
 * 		客户端通过sdk通知paypal付费
 * 		游戏服务器什么都不做
 * 2,paypal--->服务器		
 * 		paypal通知游戏服务器，有人付费，告知 订单号等信息
 * 		服务器记录订单，订单状态为，已支付未加钻
 * 
 * 3，客户端---->服务器		
 * 		客户端拿着订单号来请求服务器，查看付费情况
 * 		服务器查找订单日志，
 * 			如果是处于已支付未加钻状态。那就加钻，并处理月卡和首充翻倍的逻辑。同时更新支付日志状态为OK
 * 			如果其他状态，返给客户端错误信息
 */

public class PayPalPaid extends AbstractPaid {

	private static Logger logger = LogManager.getLogger(PayPalPaid.class.getName());

	/**
	 * 客户端来请求是否支付成功，如果成功，加gold
	 * 需要等待paypal通知服务器后台
	 * @return
	 * @throws SQLException 
	 */
	public SystemResult buyGold(Player player, RechargePackage rechargePackage, String orderId, CoinType coinType) throws SQLException {
		SystemResult result = new SystemResult();

		//从日志库中读取订单
		PayLog oldLog = readPayLog(PayChannel.PayPal, orderId);

		if (oldLog != null) {//有这个订单
			if (oldLog.getPayStatus() == PayStatus.PAID_COIN_NO_ADD_GOLD) {//等待加钱
				int buyGold = rechargePackage.getItem().get(ItemType.GOLD);

				//加gold
				Root.playerSystem.changeGold(player, buyGold, GoldType.BUY, true);

				PayLog payLog = new PayLog(player.getId(), PayChannel.PayPal, orderId, rechargePackage.getId(), PayStatus.OK);

				updatePayLogStatus(payLog);

				result.setCode(ErrorCode.NO_ERROR);

			} else {
				logger.error("hava payLog,but not [PayStatus.PAID_COIN_NO_ADD_GOLD],is [" + oldLog.getPayStatus().asDesc() + "]");
			}
		}

		return result;

	}

	public void saveNoUsedOrder(String order, Map<String, String> map) {
		PayLog payLog = new PayLog(null, PayChannel.PayPal, order, null, PayStatus.PAID_COIN_NO_ADD_GOLD);
		payLog.setInfo(map.toString());
		addPayLog(payLog);
	}

}
