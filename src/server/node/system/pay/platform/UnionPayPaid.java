package server.node.system.pay.platform;

import gamecore.system.SystemResult;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.ConfigManager;
import server.node.system.Root;
import server.node.system.log.PayLog;
import server.node.system.pay.AbstractPaid;
import server.node.system.pay.PayChannel;
import server.node.system.pay.PayStatus;
import server.node.system.player.GoldType;
import server.node.system.player.Player;
import server.node.system.rechargePackage.ItemType;
import server.node.system.rechargePackage.RechargePackage;

import com.unionpay.acp.sdk.SDKConfig;
import com.unionpay.acp.sdk.SDKUtil;
import common.coin.CoinType;

public class UnionPayPaid extends AbstractPaid {

	private static Logger logger = LogManager.getLogger(UnionPayPaid.class.getName());

	String merId = "898110257340179";//商户的银联账号  
	String callBackUrl = "/unionpay/call_back";
	String frontUrl = "/unionpay/back_game";

	public static String encoding = "UTF-8";

	public static String version = "5.0.0";

	//准备支付的数据
	public SystemResult createOrder(Player player, RechargePackage rechargePackage, String orderId, CoinType coinType) {
		SystemResult result = new SystemResult();

		String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();

		/**
		 * 组装请求报文
		 */
		Map<String, String> data = new HashMap<String, String>();

		data.put("version", version);// 版本号
		data.put("encoding", encoding);// 字符集编码 默认"UTF-8"
		data.put("signMethod", "01");// 签名方法 01 RSA
		data.put("txnType", "01");// 交易类型 01-消费
		data.put("txnSubType", "01");// 交易子类型 01:自助消费 02:订购 03:分期付款
		data.put("bizType", "000201");// 业务类型
		data.put("channelType", "08");// 渠道类型，07-PC，08-手机
		// 前台通知地址 ，控件接入方式无作用
		data.put("frontUrl", ConfigManager.getInstance().nodeDomain + frontUrl);
		//data.put("frontUrl", "");
		// 后台通知地址
		data.put("backUrl", ConfigManager.getInstance().nodeDomain + callBackUrl);
		data.put("accessType", "0"); // 接入类型，商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
		data.put("merId", merId);// 商户号码，请改成自己的商户号
		data.put("orderId", orderId);// 商户订单号，8-40位数字字母
		data.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));// 订单发送时间，取系统时间
		data.put("txnAmt", rechargePackage.getCoin().get(coinType).toString());// 交易金额，单位分
		data.put("currencyCode", "156");// 交易币种 RMB

		// 请求方保留域，透传字段，查询、通知、对账文件中均会原样出现
		//		JSONObject json = new JSONObject();
		//		json.put("playerId", player.getId());
		//		json.put("nodeTag", server.node.system.ConfigManager.getInstance().tag);
		//		json.put("coinType", coinType.asCode());
		//		json.put("rechargePackageId", rechargePackage.getId());

		//data.put("reqReserved", json.toJSONString());
		// 订单描述，可不上送，上送时控件中会显示该信息
		//data.put("orderDesc", "订单描述:" + rechargePackage.getDesc().get(LangType.zh_CN));

		Map<String, String> submitFromData = signData(data);

		String form = createHtml(requestFrontUrl, submitFromData);

		result.setMap("form", form);

		//加入待支付订单
		PayLog payLog = new PayLog(player.getId(), PayChannel.Union, orderId, rechargePackage.getId(), coinType, rechargePackage.getCoin().get(CoinType.USD), null,
				PayStatus.WAITING_PAID);

		addPayLog(payLog);

		return result;
	}

	/**
	 * 构造HTTP POST交易表单的方法示例
	 * @param action 表单提交地址
	 * @param hiddens  以MAP形式存储的表单键值
	 * @return 构造好的HTTP POST交易表单
	 */
	public static String createHtml(String action, Map<String, String> hiddens) {
		StringBuffer sf = new StringBuffer();
		sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/></head><body>");
		sf.append("<form id = \"pay_form\" action=\"" + action + "\" method=\"post\">");
		if (null != hiddens && 0 != hiddens.size()) {
			Set<Entry<String, String>> set = hiddens.entrySet();
			Iterator<Entry<String, String>> it = set.iterator();
			while (it.hasNext()) {
				Entry<String, String> ey = it.next();
				String key = ey.getKey();
				String value = ey.getValue();
				sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + value + "\"/>");
			}
		}
		sf.append("</form>");
		sf.append("</body>");
		sf.append("<script type=\"text/javascript\">");
		sf.append("document.all.pay_form.submit();");
		sf.append("</script>");
		sf.append("</html>");
		return sf.toString();
	}

	public static Map<String, String> signData(Map<String, ?> contentData) {
		Entry<String, String> obj = null;
		Map<String, String> submitFromData = new HashMap<String, String>();
		for (Iterator<?> it = contentData.entrySet().iterator(); it.hasNext();) {
			obj = (Entry<String, String>) it.next();
			String value = obj.getValue();
			if (StringUtils.isNotBlank(value)) {
				// 对value值进行去除前后空处理
				submitFromData.put(obj.getKey(), value.trim());
			}
		}
		/**
		 * 签名
		 */
		SDKUtil.sign(submitFromData, encoding);

		return submitFromData;
	}

	public String getUrlParamsByMap(Map<String, String> map) {
		if (map == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue());
			sb.append("&");
		}
		String s = sb.toString();
		if (s.endsWith("&")) {
			s = org.apache.commons.lang.StringUtils.substringBeforeLast(s, "&");
		}
		return s;
	}

	//银联发来通知，订单支付，状态变为OK
	public SystemResult unionPaySuccess(String orderId, String info) throws SQLException {
		SystemResult result = new SystemResult();

		PayLog payLog = readPayLog(PayChannel.Union, orderId);

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
