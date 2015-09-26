package gamecore.servlet;

import gamecore.util.HttpUtils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeader;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/** 
 * 抽象 HTTP Servlet
 */
public class AbstractHttpServlet extends HttpServlet {

	private static final long serialVersionUID = -824199080913869265L;

	public final static String COOKIE = HttpHeader.COOKIE.asString();

	public AbstractHttpServlet() {
		super();
	}

	/** 读取包体数据并转为 JSON
	 * @throws IOException 
	 */
	protected JSONObject readRequestAsJson(HttpServletRequest request) throws IOException, JSONException {
		return HttpUtils.readRequestStreamAsJSON(request);
	}

	/** 封装并写入 JSON 数据。
	 * @throws IOException 
	 */
	protected void wrapResponse(HttpServletResponse response, JSONObject json) throws IOException {
		response.setContentType("text/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.print(json.toString());
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
		}
	}

	/** 封装响应数据。
	 */
	protected void wrapResponse(HttpServletResponse response, int status) throws IOException {
		response.setContentType("text/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(status);
		response.setHeader("Server", "Node");

		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.println("{}");
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
		}
	}

	/** 封装为 200 状态，并写入 JSON 数据。
	 * @throws IOException 
	 */
	protected void toJson(HttpServletResponse response, JSONObject json) throws IOException {
		response.setContentType("text/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.print(json.toString());
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
		}
	}

}
