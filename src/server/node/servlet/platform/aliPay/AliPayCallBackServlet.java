package server.node.servlet.platform.aliPay;

import gamecore.servlet.AbstractHttpServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.pay.platform.alipay.AliPayPaid;
import server.node.system.pay.platform.alipay.util.AlipayNotify;

@WebServlet("/alipay/call_back")
public class AliPayCallBackServlet extends AbstractHttpServlet {

	private static final long serialVersionUID = -9187676953361412830L;

	private static final Logger logger = LogManager.getLogger(AliPayCallBackServlet.class);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		logger.info("AliPayCallBackServlet receive msg BEGIN");

		//获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = req.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}

		//		gmt_create=2015-08-07 15:32:27, 
		//		buyer_email=1579645400@qq.com, 
		//		notify_time=2015-08-07 15:32:28, 
		//		gmt_payment=2015-08-07 15:32:27, 
		//		seller_email=pyw@rulesgames.com, 
		//		quantity=1, 
		//		subject=ceshi100, 
		//		use_coupon=N, 
		//		sign=igIBzTRwmDdhj3qn2PjxnQyHv4ulHspGpiKcGbor6TqfdPhFsx+WTDBVBIcFtxaGQeSAUzTz0ZLGdSEWX0LX8c42zzd1Vp50ff0f6b4kRdn8pCKaj93RCbElGSly95EzCViM3fnWp3B+qALNxccJEoZspbQ4NdZJ2XriTvYn5xA=,
		//		body=ceshi100, 
		//		buyer_id=2088302983253924,
		//		notify_id=a4fdafc56698822cc0111a1eafa4965374, 
		//		notify_type=trade_status_sync, 
		//		payment_type=1, 
		//		out_trade_no=dee9fb59-769d-4e87-b61c-e90220dde1e2, 
		//		price=0.01, 
		//		trade_status=TRADE_SUCCESS, 
		//		total_fee=0.01, 
		//		trade_no=2015080700001000920073220029, 
		//		sign_type=RSA, seller_id=2088911212136718, 
		//		is_total_fee_adjust=N

		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		//商户订单号

		String out_trade_no = new String(req.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
		//支付宝交易号
		String trade_no = new String(req.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
		//交易状态
		String trade_status = new String(req.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");

		PrintWriter out = resp.getWriter();

		if (AlipayNotify.verify(params)) {//验证成功

			logger.error("Verify signature result [success]");

			if (trade_status.equals("TRADE_FINISHED")) {
				logger.error("Trade result [TRADE_FINISHED]");
			} else if (trade_status.equals("TRADE_SUCCESS")) {
				try {
					new AliPayPaid().aliPaySuccess(out_trade_no, params.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			out.println("success"); //请不要修改或删除

		} else {//验证失败
			logger.error("Verify signature result [failure]");
			out.println("fail");
		}

		out.close();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				if (null == res.get(en) || "".equals(res.get(en))) {
					res.remove(en);
				}
			}
		}
		return res;
	}

}
