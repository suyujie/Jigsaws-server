package common.microsoft;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PushUtils {

	private static Logger logger = LogManager.getLogger(PushUtils.class.getName());

	private static String msgXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><wp:Notification xmlns:wp=\"WPNotification\"><wp:Toast><wp:Text1>HeRobot</wp:Text1>"
			+ "<wp:Text2>{message}</wp:Text2>"
			//	+ "<wp:Param></wp:Param>" 
			+ "</wp:Toast>" + "</wp:Notification>";

	public static void send(String pushUrl, String message) {

		if (pushUrl != null && message != null) {

			String msg = msgXml.replace("{message}", message);

			HttpURLConnection con = null;
			URL url = null;
			try {
				url = new URL(pushUrl);
				con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
				con.setDoOutput(true);
				con.setDoInput(true);
				con.setUseCaches(false);

				String guid = UUID.randomUUID().toString();
				con.setRequestProperty("X-MessageID", guid);
				con.setRequestProperty("Content-Type", "text/xml;charset=utf-8");

				con.setRequestProperty("X-WindowsPhone-Target", "toast");
				con.setRequestProperty("X-NotificationClass", "2");

				OutputStream out = con.getOutputStream();
				out.write(msg.getBytes("utf-8"));
				out.flush();

				logger.info(con.getResponseMessage());

				out.close();
				con.disconnect();
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	public static void main(String[] args) {
		//	String url = "http://s.notify.live.net/u/1/sin/H2QAAADXrPTQ9jigsVeXQMyDYNvUOLILFjKKs2aG8DPWjy9PDV9xNgdMPFucT5HoPFKxGUgLa821k5q4iWoHkmH90r6BD3Jjrs2BmIIZyl5uyoSPzH4Y5YbF_JiH_d8cnTCn2Nk/d2luZG93c3Bob25lZGVmYXVsdA/OqsTaGJCw0axKNvTO-2y1A/s72_R0qH7ROxTdNUcdC8HTsclDY";
		String url = "http://s.notify.live.net/u/1/sin/H2QAAAAflNDciC3qh8MykQddEjTDvKVdh5tFk_N76kBft5XHw9Z8-bV93s2Nc_Ms33vqAE-seevgx0pIYVHOibW44vTsGUyHQc9k0FN6dQPq9PFqAPHY0nz1Rj7xYmauriYonNk/d2luZG93c3Bob25lZGVmYXVsdA/e_2WE6KkDk2vkyW9yPLqCA/E8MkFfXQl-IBiGPG6esfU99r5g4";
		send(url, "机器人修理好了");
	}

}
