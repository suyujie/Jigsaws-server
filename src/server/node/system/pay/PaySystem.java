package server.node.system.pay;

import java.sql.SQLException;
import java.util.UUID;

import gamecore.system.AbstractSystem;
import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;
import gamecore.util.Clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unionpay.acp.sdk.SDKConfig;

import server.node.system.Root;
import server.node.system.log.PayLog;
import server.node.system.monthCard.MonthCard;
import server.node.system.monthCard.MonthCardSystem;
import server.node.system.pay.platform.PayPalPaid;
import server.node.system.pay.platform.TestPaid;
import server.node.system.pay.platform.UnionPayPaid;
import server.node.system.pay.platform.WinPhonePaid;
import server.node.system.pay.platform.alipay.AliPayPaid;
import server.node.system.player.GoldType;
import server.node.system.player.Player;
import server.node.system.rechargePackage.ItemType;
import server.node.system.rechargePackage.RechargePackage;
import server.node.system.rechargePackage.RechargePackageBag;
import common.coin.CoinType;

public class PaySystem extends AbstractSystem {

	private static final Logger logger = LogManager.getLogger(PaySystem.class);

	@Override
	public boolean startup() {
		System.out.println("PaySystem start....");

		SDKConfig.getConfig().loadPropertiesFromSrc();

		System.out.println("PaySystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {

	}

	public void payTry(Player player, String rechargePackageId, String channelFrom) {

		if (player != null && rechargePackageId != null) {

			PayChannel payChannel = PayChannel.asEnum(channelFrom);

			PayLog payLog = new PayLog(player.getId(), payChannel, null, rechargePackageId, null);

			switch (payChannel) {
			case Wp:
				new WinPhonePaid().addPayTryLog(payLog);
				break;
			case PayPal:
				new PayPalPaid().addPayTryLog(payLog);
				break;
			case Ali:
				new AliPayPaid().addPayTryLog(payLog);
				break;
			case Union:
				new UnionPayPaid().addPayTryLog(payLog);
				break;
			default:
				logger.error("try paid : unknow pay channel");
			}
		}

	}

	private boolean canBuyMonthCard(Player player) throws SQLException {
		MonthCard monthCard = Root.monthCardSystem.getMonthCard(player, false);
		if (monthCard != null && !monthCard.checkTimeOut(Clock.currentTimeSecond())) {
			return false;
		}
		return true;
	}

	//支付成功了，验证下是不是首次支付翻倍或者月卡,因为月卡是不翻倍的
	public boolean doubleORmonthCard(Player player, RechargePackage rechargePackage) throws SQLException {

		boolean isGoldDouble = false;

		RechargePackageBag rechargePackageBag = Root.rechargePackageSystem.getRechargePackageBag(player);
		//月卡
		if (rechargePackage.getId().equals(MonthCardSystem.MonthCardId)) {
			Root.monthCardSystem.buyMonthCard(player);
		} else {//翻倍
			boolean isFirst = rechargePackageBag.checkIsFirst(rechargePackage.getId());
			if (isFirst) {
				int gold = rechargePackage.getItem().get(ItemType.GOLD);
				Root.playerSystem.changeGold(player, gold, GoldType.FIRST_BUY_DOUBLE, true);
				isGoldDouble = true;
			}
		}

		Root.rechargePackageSystem.buyedRechargePackage(player, rechargePackageBag, rechargePackage);

		return isGoldDouble;
	}

	public SystemResult payTest(Player player, String rechargePackageId, CoinType coinType) throws SQLException {

		SystemResult result = new SystemResult();

		RechargePackage rechargePackage = Root.rechargePackageSystem.getRechargePackage(rechargePackageId);

		//验证月卡是否重复购买
		if (rechargePackage.getId().equals(MonthCardSystem.MonthCardId)) {
			if (!canBuyMonthCard(player)) {//不能买,已经有月卡了
				logger.error("can not buy monthcard,beacuse repeay");
				result.setCode(ErrorCode.MONTH_CARD_REPEAT_BUY);
				return result;
			}
		}

		result = new TestPaid().buyGold(player, rechargePackage, coinType);
		//支付成功了，验证下是不是首次支付翻倍或者月卡
		if (result.getCode() == ErrorCode.NO_ERROR) {
			boolean isGoldDouble = doubleORmonthCard(player, rechargePackage);
			if (isGoldDouble) {
				result.setMap("isGoldDouble", isGoldDouble);
			}
		}
		result.setBindle(rechargePackage);
		return result;
	}

	public SystemResult payWinPhone(Player player, String rechargePackageId, String winPhoneReceipts, CoinType coinType) throws SQLException {

		SystemResult result = new SystemResult();

		RechargePackage rechargePackage = Root.rechargePackageSystem.getRechargePackage(rechargePackageId);
		//验证月卡是否重复购买
		if (rechargePackage.getId().equals(MonthCardSystem.MonthCardId)) {
			if (!canBuyMonthCard(player)) {//不能买,已经有月卡了
				result.setCode(ErrorCode.MONTH_CARD_REPEAT_BUY);
				return result;
			}
		}
		result = new WinPhonePaid().buyGold(player, rechargePackage, winPhoneReceipts, coinType);

		//支付成功了，验证下是不是首次支付翻倍或者月卡
		if (result.getCode() == ErrorCode.NO_ERROR) {
			boolean isGoldDouble = doubleORmonthCard(player, rechargePackage);
			if (isGoldDouble) {
				result.setMap("isGoldDouble", isGoldDouble);
			}
		}

		result.setBindle(rechargePackage);

		return result;
	}

	public SystemResult payPaypal(Player player, String rechargePackageId, String order, CoinType coinType) throws SQLException {

		SystemResult result = new SystemResult();

		RechargePackage rechargePackage = Root.rechargePackageSystem.getRechargePackage(rechargePackageId);
		//验证月卡是否重复购买
		if (rechargePackage.getId().equals(MonthCardSystem.MonthCardId)) {
			if (!canBuyMonthCard(player)) {//不能买,已经有月卡了
				result.setCode(ErrorCode.MONTH_CARD_REPEAT_BUY);
				return result;
			}
		}
		result = new PayPalPaid().buyGold(player, rechargePackage, order, coinType);

		//支付成功了，验证下是不是首次支付翻倍或者月卡
		if (result.getCode() == ErrorCode.NO_ERROR) {
			boolean isGoldDouble = doubleORmonthCard(player, rechargePackage);
			if (isGoldDouble) {
				result.setMap("isGoldDouble", isGoldDouble);
			}
		}

		result.setBindle(rechargePackage);
		return result;
	}

	//客户端想要用银联付款，生成一个订单号，这时候不创建订单，而是等接下来的servlet来做
	public SystemResult unionPayWantOrder(Player player, String rechargePackageId, CoinType coinType) throws SQLException {
		logger.info("unionPayWantOrder");

		SystemResult result = new SystemResult();
		RechargePackage rechargePackage = Root.rechargePackageSystem.getRechargePackage(rechargePackageId);
		//验证月卡是否重复购买
		if (rechargePackage.getId().equals(MonthCardSystem.MonthCardId)) {
			if (!canBuyMonthCard(player)) {//不能买,已经有月卡了
				result.setCode(ErrorCode.MONTH_CARD_REPEAT_BUY);
				return result;
			}
		}

		if (result.getCode() == ErrorCode.NO_ERROR) {

			String orderId = UUID.randomUUID().toString().replaceAll("-", "");

			result.setMap(
					"url",
					new StringBuffer("/unionpay/pay_req?playerId=").append(player.getId()).append("&orderId=").append(orderId).append("&rechargePackageId=")
							.append(rechargePackageId).toString());
			result.setMap("orderId", orderId);
			result.setBindle(rechargePackage);
		}

		return result;
	}

	public SystemResult unionPayCreateOrder(Player player, String rechargePackageId, String orderId, CoinType coinType) throws SQLException {

		SystemResult result = new SystemResult();
		RechargePackage rechargePackage = Root.rechargePackageSystem.getRechargePackage(rechargePackageId);
		//验证月卡是否重复购买
		if (rechargePackage.getId().equals(MonthCardSystem.MonthCardId)) {
			if (!canBuyMonthCard(player)) {//不能买,已经有月卡了
				result.setCode(ErrorCode.MONTH_CARD_REPEAT_BUY);
				return result;
			}
		}

		result = new UnionPayPaid().createOrder(player, rechargePackage, orderId, coinType);

		if (result.getCode() == ErrorCode.NO_ERROR) {
			result.setBindle(rechargePackage);
		}

		return result;
	}

	public SystemResult unionPayCheckOrder(Player player, String orderId) throws SQLException {

		logger.info("unionPayCheckOrder  orderId:" + orderId);

		SystemResult result = new SystemResult();

		PayLog payLog = new UnionPayPaid().readPayLog(PayChannel.Union, orderId);

		if (payLog != null) {
			if (payLog.getPayStatus() == PayStatus.OK) {
				result.setCode(ErrorCode.NO_ERROR);
				logger.debug("unionPayCheckOrder   payStatus : " + payLog.getPayStatus());
			} else {
				result.setCode(ErrorCode.RECHARGE_WAITING);
				logger.debug("unionPayCheckOrder   payStatus : " + payLog.getPayStatus());
			}
		} else {
			result.setCode(ErrorCode.RECHARGE_NO_ORDER);
			logger.error("unionPayCheckOrder : no this order ,orderId:[" + orderId + "] ");
		}

		return result;
	}

	//客户端想要用支付宝付款，生成一个订单号，这时候不创建订单，而是等接下来的servlet来做
	public SystemResult alipayWantOrder(Player player, String rechargePackageId, CoinType coinType) throws SQLException {
		logger.info("alipayWantOrder");

		SystemResult result = new SystemResult();
		RechargePackage rechargePackage = Root.rechargePackageSystem.getRechargePackage(rechargePackageId);
		//验证月卡是否重复购买
		if (rechargePackage.getId().equals(MonthCardSystem.MonthCardId)) {
			if (!canBuyMonthCard(player)) {//不能买,已经有月卡了
				result.setCode(ErrorCode.MONTH_CARD_REPEAT_BUY);
				return result;
			}
		}

		if (result.getCode() == ErrorCode.NO_ERROR) {

			String orderId = UUID.randomUUID().toString();

			result.setMap("url",
					new StringBuffer("/alipay/pay_req?playerId=").append(player.getId()).append("&orderId=").append(orderId).append("&rechargePackageId=")
							.append(rechargePackageId).toString());
			result.setMap("orderId", orderId);
			result.setBindle(rechargePackage);
		}

		return result;
	}

	public SystemResult alipayCreateOrder(Player player, String rechargePackageId, String orderId, CoinType coinType) throws SQLException {
		logger.info("createOrderAlipay");

		SystemResult result = new SystemResult();
		RechargePackage rechargePackage = Root.rechargePackageSystem.getRechargePackage(rechargePackageId);
		//验证月卡是否重复购买
		if (rechargePackage.getId().equals(MonthCardSystem.MonthCardId)) {
			if (!canBuyMonthCard(player)) {//不能买,已经有月卡了
				result.setCode(ErrorCode.MONTH_CARD_REPEAT_BUY);
				return result;
			}
		}

		result = new AliPayPaid().createOrder(player, rechargePackage, orderId, coinType);

		if (result.getCode() == ErrorCode.NO_ERROR) {
			result.setBindle(rechargePackage);
		}

		return result;
	}

	//检查此订单是否支付成功
	public SystemResult alipayCheckOrder(Player player, String orderId) throws SQLException {

		logger.info("alipayCheckOrder  orderId:" + orderId);

		SystemResult result = new SystemResult();

		PayLog payLog = new AliPayPaid().readPayLog(PayChannel.Ali, orderId);

		if (payLog != null) {
			if (payLog.getPayStatus() == PayStatus.OK) {
				result.setCode(ErrorCode.NO_ERROR);
				logger.debug("alipayCheckOrder   payStatus : " + payLog.getPayStatus());
			} else {
				result.setCode(ErrorCode.RECHARGE_WAITING);
				logger.debug("alipayCheckOrder   payStatus : " + payLog.getPayStatus());
			}
		} else {
			result.setCode(ErrorCode.RECHARGE_NO_ORDER);
			logger.error("alipayCheckOrder : no this order ,orderId:[" + orderId + "] ");
		}

		return result;
	}

}
