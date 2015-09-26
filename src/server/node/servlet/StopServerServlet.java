package server.node.servlet;

import gamecore.servlet.AbstractHttpServlet;
import gamecore.task.TaskCenter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.NodeServer;

import com.alibaba.fastjson.JSONObject;

/**
 * 关闭服务器。
 */
@WebServlet("/stopserver")
public class StopServerServlet extends AbstractHttpServlet {

	private static final long serialVersionUID = -4145492721352415421L;

	private static Logger logger = LogManager.getLogger(StopServerServlet.class.getName());

	public StopServerServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject ret = new JSONObject();

		String url = request.getRequestURL().toString();
		if (url.indexOf("localhost") == 7 || url.indexOf("127.0.0.1") == 7) {
			// 执行服务器关闭
			TaskCenter.getInstance().execute(new Runnable() {
				@Override
				public void run() {
					System.out.println("server stoping");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						logger.error(e);
					}
					try {
						NodeServer.getInstance().stopServer();
					} catch (Exception e) {
						logger.error(e);
					}
				}
			});
		} else {
			System.out.println(url);
		}

		this.wrapResponse(response, ret);
	}
}
