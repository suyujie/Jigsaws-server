package gamecore.util;

import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;

import com.alibaba.fastjson.JSONObject;

public class NetAccessTool {

	private static final Logger logger = LogManager.getLogger(NetAccessTool.class.getName());

	//访问所有的服务器
	public static String accessServer(String url, JSONObject args, HttpMethod method) {

		String result = null;

		HttpClient client = new HttpClient();
		try {
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		ContentResponse responseResult;

		try {

			if (args == null) {
				args = new JSONObject();
			}

			StringContentProvider cp = new StringContentProvider(args.toString());

			responseResult = client.newRequest(url).method(method).content(cp).send();

			if (HttpServletResponse.SC_OK == responseResult.getStatus()) {
				String content = new String(responseResult.getContent(), Charset.forName("UTF-8"));
				result = content;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new StringBuffer("access manager server error : ").append(url).append("-").append(args).append("-").append(method.asString()));
		}

		try {
			client.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
