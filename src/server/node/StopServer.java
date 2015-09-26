package server.node;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;

import server.node.system.ConfigManager;

/**
 * 
 */
public final class StopServer {

	public static void main(String[] args) {
		System.out.println("> Try to stop local server");

		HttpClient client = new HttpClient();
		try {
			client.start();
			ConfigManager.getInstance().load();
			System.out.println("=======http://127.0.0.1:" + ConfigManager.getInstance().mgmtPort + "/stopserver");
			ContentResponse response = client.newRequest("http://127.0.0.1:" + ConfigManager.getInstance().mgmtPort + "/stopserver").method(HttpMethod.POST).send();
			System.out.println("=======" + response.getContentAsString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			client.stop();
		} catch (Exception e) {
		}

		System.out.println("> Posted stop local server");

		System.exit(0);
	}
}
