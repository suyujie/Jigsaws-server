package gamecore.serialize;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class SerializerJson {

	public static String serialize(Object object) {
		return JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
	}

	public static <T> T deSerialize(String str, Class<T> clazz) {
		T t = null;
		try {
			t = (T) JSON.parseObject(str, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	public static <T> List<T> deSerializeList(String str, Class<T> clazz) {
		List<T> list = null;
		try {
			list = (List<T>) JSON.parseArray(str, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static <T> T deSerializeMap(String str, TypeReference<T> type) {
		T r = JSON.parseObject(str, type);
		return r;
	}

}
