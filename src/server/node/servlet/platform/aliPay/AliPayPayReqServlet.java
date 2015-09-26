package server.node.servlet.platform.aliPay;

import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.action.AbstractAction;
import server.node.system.Root;
import server.node.system.player.Player;
import common.coin.CoinType;

@WebServlet("/alipay/pay_req")
public class AliPayPayReqServlet extends AbstractAction {

	private static final long serialVersionUID = -1956043055048622206L;

	private static final Logger logger = LogManager.getLogger(AliPayPayReqServlet.class);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		logger.info("receive msg BEGIN");

		Long playerId = req.getParameter("playerId").trim() == "" ? null : Long.parseLong(req.getParameter("playerId").trim());
		String rechargePackageId = req.getParameter("rechargePackageId");
		String orderId = req.getParameter("orderId");

		try {
			Player player = Root.playerSystem.getPlayer(playerId);

			CoinType coinType = getCoinType(null);

			SystemResult result = Root.paySystem.alipayCreateOrder(player, rechargePackageId, orderId, coinType);

			PrintWriter out = resp.getWriter();

			if (result.getCode() == ErrorCode.MONTH_CARD_REPEAT_BUY) {//月卡重复,不能买
				out.println("<html>");
				out.println(ErrorCode.MONTH_CARD_REPEAT_BUY);
				out.println("</html>");
			} else {

				out.println("<html>");

				out.println(result.getMap("form"));

				out.println("</html>");

			}

			out.close();

			logger.info("AliPayListenerServlet receive msg END");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

}
