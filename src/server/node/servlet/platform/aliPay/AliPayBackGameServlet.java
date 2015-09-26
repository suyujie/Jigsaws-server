package server.node.servlet.platform.aliPay;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.node.action.AbstractAction;

@WebServlet("/alipay/back_game")
public class AliPayBackGameServlet extends AbstractAction {

	private static final long serialVersionUID = -6460204763899580568L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		PrintWriter out = resp.getWriter();

		out.println("<html>");
		out.println("<body>");

		out.println("关闭浏览器，返回游戏");

		out.println("</body>");

		out.println("</html>");

		out.close();

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

}
