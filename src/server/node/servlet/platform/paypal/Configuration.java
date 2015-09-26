package server.node.servlet.platform.paypal;

import java.util.HashMap;
import java.util.Map;

public class Configuration {

	public static final Map<String, String> getAcctAndConfig() {
		Map<String, String> configMap = new HashMap<String, String>();
		configMap.putAll(getConfig());

		//live
		configMap.put("acct1.UserName", "453577387_api1.qq.com");
		configMap.put("acct1.Password", "XQ5WJEPN8AGUUUP6");
		configMap.put("acct1.Signature", "AqCjC3WdNOa0E8E4Y1pPF-Ey1oDJA3AlxGxIC9MteMMp9kUTaETeSs9I");

		//sandbox
		//		configMap.put("acct1.UserName", "453577387-facilitator_api1.qq.com");
		//		configMap.put("acct1.Password", "MYZZM522N7V7HU5N");
		//		configMap.put("acct1.Signature", "A7H5.DQI08Q.0d7XqJ.u5bM.W.wvAx2W25JM75gfqPUnA39ol0OlHsnR");

		return configMap;
	}

	public static final Map<String, String> getConfig() {
		Map<String, String> configMap = new HashMap<String, String>();

		//Sandbox or live
		configMap.put("mode", "live");

		return configMap;
	}

}
