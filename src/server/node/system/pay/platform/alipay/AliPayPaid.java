package server.node.system.pay.platform.alipay;

import gamecore.system.SystemResult;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.ConfigManager;
import server.node.system.Root;
import server.node.system.log.PayLog;
import server.node.system.pay.AbstractPaid;
import server.node.system.pay.PayChannel;
import server.node.system.pay.PayStatus;
import server.node.system.pay.platform.alipay.config.AlipayConfig;
import server.node.system.pay.platform.alipay.util.AlipaySubmit;
import server.node.system.player.GoldType;
import server.node.system.player.Player;
import server.node.system.rechargePackage.ItemType;
import server.node.system.rechargePackage.RechargePackage;
import common.coin.CoinType;

public class AliPayPaid extends AbstractPaid {

	private static Logger logger = LogManager.getLogger(AliPayPaid.class.getName());

	//创建订单
	public SystemResult createOrder(Player player, RechargePackage rechargePackage, String orderId, CoinType coinType) {
		SystemResult result = new SystemResult();

		/**
		 * 组装请求报文
		 */
		//支付类型
		String payment_type = "1";
		//必填，不能修改
		//服务器异步通知页面路径
		String notify_url = "/alipay/call_back";
		//商户订单号
		String out_trade_no = orderId;
		//商户网站订单系统中唯一订单号，必填

		//订单名称
		String subject = rechargePackage.getId();
		//付款金额
		DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
		String total_fee = df.format(rechargePackage.getCoin().get(coinType) / 100F);//返回的是String类型的

		//商品展示地址
		//String show_url = "http://www.baidu.com";
		//必填，需以http://开头的完整路径，例如：http://www.商户网址.com/myorder.html

		//订单描述
		String body = rechargePackage.getId().toString();
		//选填

		//////////////////////////////////////////////////////////////////////////////////

		//把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("body", body);
		sParaTemp.put("notify_url", ConfigManager.getInstance().nodeDomain + notify_url);
		sParaTemp.put("out_trade_no", out_trade_no);
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("payment_type", payment_type);
		sParaTemp.put("seller_id", AlipayConfig.seller_id);
		sParaTemp.put("service", "alipay.wap.create.direct.pay.by.user");
		//sParaTemp.put("show_url", show_url);
		sParaTemp.put("subject", subject);
		sParaTemp.put("total_fee", total_fee);

		String form = AlipaySubmit.buildRequest(sParaTemp, "get", "ok");

		//加入待支付订单
		PayLog payLog = new PayLog(player.getId(), PayChannel.Ali, out_trade_no, rechargePackage.getId(), coinType, rechargePackage.getCoin().get(CoinType.USD), null,
				PayStatus.WAITING_PAID);

		addPayLog(payLog);

		result.setMap("form", form);

		return result;
	}

	//支付宝通知，订单支付，状态变为OK
	public SystemResult aliPaySuccess(String orderId, String info) throws SQLException {
		SystemResult result = new SystemResult();

		PayLog payLog = readPayLog(PayChannel.Ali, orderId);

		if (payLog.getPayStatus() == PayStatus.WAITING_PAID) {
			Player player = Root.playerSystem.getPlayer(payLog.getPlayerId());
			RechargePackage rechargePackage = Root.rechargePackageSystem.getRechargePackage(payLog.getRechargePackageId());
			int buyGold = rechargePackage.getItem().get(ItemType.GOLD);
			//给玩家加钻石
			Root.playerSystem.changeGold(player, buyGold, GoldType.BUY, true);

			Root.paySystem.doubleORmonthCard(player, rechargePackage);

			//更新订单状态，-->完成订单
			payLog.setPayStatus(PayStatus.OK);
			payLog.setInfo(info);
			updatePayLogStatusAndInfo(payLog);
		}

		return result;
	}

}
