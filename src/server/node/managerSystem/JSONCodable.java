package server.node.managerSystem;

import com.alibaba.fastjson.JSONObject;

/** 可进行 JSON 编码接口定义。
 */
public interface JSONCodable {

	public JSONObject toJSON();
}
