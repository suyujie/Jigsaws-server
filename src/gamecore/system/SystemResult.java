package gamecore.system;

import java.util.HashMap;

import com.alibaba.fastjson.JSONObject;

/**
 * 系统处理结果。
 */
public final class SystemResult {

	private static final String EC = "errorCode";

	private int code = ErrorCode.NO_ERROR;
	private Object bindle;
	private HashMap<String, Object> map;

	public SystemResult(int code) {
		this.code = code;
	}

	public SystemResult() {
		this.code = ErrorCode.NO_ERROR;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}

	public void setBindle(Object bindle) {
		this.bindle = bindle;
	}

	public Object getBindle() {
		return this.bindle;
	}

	public void setMap(String key, Object object) {
		if (map == null) {
			map = new HashMap<String, Object>();
		}
		map.put(key, object);
	}

	public Object getMap(String key) {
		if (map == null) {
			return null;
		}
		return map.get(key);
	}

	/**
	 * 将 SystemResult 转为 JSON 的实用函数。
	 * @param systemResult
	 * @return
	 */
	public static JSONObject toJSON(SystemResult systemResult) {
		JSONObject jo = new JSONObject();
		jo.put(EC, systemResult.code);
		return jo;
	}

	/**
	 * 将指定错误码转为 SystemResult 的 JSON 格式的实用函数。
	 * @param code
	 * @return
	 */
	public static JSONObject toJSON(int code) {
		JSONObject jo = new JSONObject();
		jo.put(EC, code);
		return jo;
	}
}
