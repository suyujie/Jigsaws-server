package server.node.servlet;

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

import gamecore.action.ActionDispatcher;
import gamecore.io.ByteArrayGameInput;
import gamecore.io.ByteArrayGameOutput;
import gamecore.io.GameInput;
import gamecore.message.GameRequest;
import gamecore.message.GameResponse;
import gamecore.servlet.AbstractHttpServlet;
import server.node.system.Root;

@WebServlet("/game_stream")
public class GameStreamServlet extends AbstractHttpServlet {

	private static final Logger logger = LogManager.getLogger(GameStreamServlet.class.getName());

	private static final long serialVersionUID = -6473800797573243293L;

	public GameStreamServlet() {

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		long tb = System.currentTimeMillis();
		Integer commandId = 0;

		OutputStream out = resp.getOutputStream();

		byte[] respBytes = null;

		if (Root.getInstance().isRun()) {

			byte[] msgBytes = readRequestStream(req);

			GameRequest reqMsg = decode(msgBytes);

			commandId = reqMsg.getCommandId();

			logger.debug("--doPost--" + commandId);

			try {
				respBytes = encode(execAction(reqMsg));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		long te = System.currentTimeMillis();
		logger.info(new StringBuffer("run ").append(Root.getInstance().isRun()).append(" comandId[").append(commandId)
				.append("]").append(" bodyLength[").append(respBytes.length).append("]").append(" time[")
				.append(tb + "->" + te + "=" + (te - tb)).append("]").toString());

		out.write(respBytes);
		out.flush();
		out.close();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		long tb = System.currentTimeMillis();
		int commandId = 0;

		OutputStream out = resp.getOutputStream();

		byte[] respBytes = null;

		if (Root.getInstance().isRun()) {

			byte[] msgBytes = readRequestStream(req);

			GameRequest reqMsg = decode(msgBytes);

			commandId = reqMsg.getCommandId();

			logger.debug("--doGet--" + commandId);

			try {
				respBytes = encode(execAction(reqMsg));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		long te = System.currentTimeMillis();
		logger.info(new StringBuffer("run ").append(Root.getInstance().isRun()).append(" comandId[").append(commandId)
				.append("]").append(" bodyLength[").append(respBytes.length).append("]").append(" time[")
				.append(tb + "->" + te + "=" + (te - tb)).append("]").toString());

		out.write(respBytes);
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
	public static GameRequest decode(byte[] data) {

		GameInput in = new ByteArrayGameInput(data);

		try {

			// 指令代码
			Integer cmdId = in.getInt();

			String sessionId = in.getString();

			GameRequest req = new GameRequest(cmdId, sessionId, in.getBytesNoLength());

			return req;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 编码响应消息。
	 */
	public static byte[] encode(GameResponse resp) {

		ByteArrayGameOutput out = new ByteArrayGameOutput();

		// commandId
		out.putInt(resp.getCommandId());

		out.put(resp.getStatus());

		if (resp.getBody() != null) {
			out.putBytesNoLength(resp.getBody());
		}

		byte[] bytes = out.toByteArray();

		return bytes;
	}

	// 执行Action
	private GameResponse execAction(GameRequest msg) {
		GameResponse respMsg = ActionDispatcher.dispatchAction(msg);
		return respMsg;
	}

}
