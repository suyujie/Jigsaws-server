package server.node.servlet;

import gamecore.action.ActionDispatcher;
import gamecore.message.RequestJson;
import gamecore.message.ResponseJson;
import gamecore.servlet.AbstractHttpServlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Root;

import com.alibaba.fastjson.JSONObject;

@WebServlet("/ttl")
public class TellTheSecretServlet extends AbstractHttpServlet {

	private static final long serialVersionUID = 5764052240012555875L;

	private static final Logger logger = LogManager.getLogger(TellTheSecretServlet.class.getName());

	public TellTheSecretServlet() {

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		long tb = System.currentTimeMillis();
		String commandId = "";

		OutputStream out = resp.getOutputStream();

		JSONObject respJson = null;

		JSONObject json = readRequestAsJson(req);

		RequestJson rj = decode(json);

		commandId = rj.getCommandId();

		try {
			respJson = encode(execAction(rj));
		} catch (Exception e) {
			e.printStackTrace();
		}

		long te = System.currentTimeMillis();
		logger.info(new StringBuffer("run ").append(Root.getInstance().isRun()).append(" comandId[").append(commandId).append("]").append(" bodyLength[")
				.append(respJson.toString().length()).append("]").append(" time[").append(tb + "->" + te + "=" + (te - tb)).append("]").toString());

		out.write(respJson.toString().getBytes());
		out.flush();
		out.close();
	}

	public static byte[] readRequestStream(HttpServletRequest request) throws IOException {
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			InputStream is = request.getInputStream();
			if (null != is) {
				byte[] buffer = new byte[1024];
				int len = -1;
				while ((len = is.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
			}
		} catch (IOException e) {
			logger.error(e);
			return null;
		} finally {
			try {
				if (null != out) {
					out.close();
				}
			} catch (Exception e) {
			}
		}
		return out.toByteArray();
	}

	/**
	 * 解码请求消息。
	 */
	public static RequestJson decode(JSONObject data) {

		String commandId = data.getString("commandId");
		data.remove("commandId");

		String sessionId = data.getString("sessionId");
		data.remove("sessionId");

		RequestJson req = new RequestJson(commandId, data);

		return req;

	}

	/**
	 * 编码响应消息。
	 */
	public static JSONObject encode(ResponseJson respJson) {
		return respJson.getBody();
	}

	// 执行Action
	private ResponseJson execAction(RequestJson rj) {
		ResponseJson resp = ActionDispatcher.dispatchAction(rj);
		return resp;
	}

}
