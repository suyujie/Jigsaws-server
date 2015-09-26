package gamecore.cache.redis;

import gamecore.security.MD5;
import gamecore.serialize.SerializerJson;
import gamecore.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisUtilJson {

	private static Logger logger = LogManager.getLogger(JedisUtilJson.class.getName());

	private final String ConfigFile = "properties/redis.xml";
	protected static JedisUtilJson jedisUtil = new JedisUtilJson();// 创建全局的唯一实例

	protected static TreeMap<Integer, JedisPool> virtualNodeMap = new TreeMap<Integer, JedisPool>();//虚拟节点对应的真实节点
	protected static Map<Integer, JedisPool> realNodeMap = new HashMap<Integer, JedisPool>();//所有的真实节点
	protected static List<Integer> errorRealNodes = new ArrayList<Integer>();
	protected static final int NODE_NUM = 100;

	protected static List<RedisConfigBean> configBeans = new ArrayList<RedisConfigBean>();

	/**
	 * 获取唯一实例.
	 * @return
	 */
	public static JedisUtilJson getInstance() {
		if (jedisUtil != null) {
			return jedisUtil;
		} else {
			jedisUtil = new JedisUtilJson();
			jedisUtil.init();
			return jedisUtil;
		}
	}

	public boolean init() {

		//读取配置文件
		boolean b = readXMLConfig();
		if (!b) {
			return false;
		}

		//读取真实物理节点
		if (configBeans != null && !configBeans.isEmpty()) {

			for (RedisConfigBean redisConfigBean : configBeans) {

				JedisPoolConfig poolConfig = new JedisPoolConfig();

				poolConfig.setMaxTotal(redisConfigBean.getMaxTotal());
				poolConfig.setMaxIdle(redisConfigBean.getMaxIdle());
				poolConfig.setMaxWaitMillis(redisConfigBean.getMaxWaitMillis());
				poolConfig.setTestOnBorrow(redisConfigBean.isTestOnBorrow());
				poolConfig.setTestOnReturn(redisConfigBean.isTestOnReturn());

				JedisPool jedisPool = null;
				if (redisConfigBean.getAuth() != null) {
					jedisPool = new JedisPool(poolConfig, redisConfigBean.getHost(), redisConfigBean.getPort(), 0, redisConfigBean.getAuth());
				} else {
					jedisPool = new JedisPool(poolConfig, redisConfigBean.getHost(), redisConfigBean.getPort());
				}

				if (testCache(jedisPool)) {
					realNodeMap.put(redisConfigBean.getTag(), jedisPool);
				} else {
					logger.error("redis is error ,host[" + redisConfigBean.getHost() + ":" + redisConfigBean.getPort() + "]");
				}

			}

		}

		//生成虚拟节点，并对应物理node
		for (Entry<Integer, JedisPool> entry : realNodeMap.entrySet()) {
			Integer tag = entry.getKey();
			JedisPool client = entry.getValue();
			for (int n = 0; n < NODE_NUM; n++) {// 一个真实机器节点关联NODE_NUM个虚拟节点
				virtualNodeMap.put(getVirtualNodeHashCode(tag, n), client);
			}
		}

		if (realNodeMap.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean readXMLConfig() {
		//读取配置文件，生成真实节点
		File file = new File(this.getClass().getResource("/").getPath() + ConfigFile);

		if (!file.exists()) {
			logger.error("not found redis config file!");
			return false;
		}

		SAXReader reader = new SAXReader();
		Document doc;

		//读取真实物理节点
		try {

			doc = reader.read(file);

			Element root = doc.getRootElement();

			Element redises = root.element("redises");

			if (null != redises) {

				@SuppressWarnings("unchecked")
				List<Element> redisList = redises.elements("redis");
				if (null != redisList) {
					for (Element config : redisList) {
						Integer tag = Integer.parseInt(config.attributeValue("tag"));
						String host = config.element("host").getTextTrim(); // ip
						Integer port = Integer.parseInt(config.element("port").getTextTrim()); // port
						Integer maxTotal = Integer.parseInt(config.element("maxTotal").getTextTrim());
						Integer maxIdle = Integer.parseInt(config.element("maxIdle").getTextTrim());
						Integer maxWaitMillis = Integer.parseInt(config.element("maxWaitMillis").getTextTrim());
						boolean testOnBorrow = Boolean.parseBoolean(config.element("testOnBorrow").getTextTrim());
						boolean testOnReturn = Boolean.parseBoolean(config.element("testOnReturn").getTextTrim());

						String auth = null;
						if (config.element("auth") != null && !config.element("auth").getTextTrim().equals("")) {
							auth = config.element("auth").getTextTrim(); // auth
						}

						RedisConfigBean bean = new RedisConfigBean(tag, host, port, auth, maxTotal, maxIdle, maxWaitMillis, testOnBorrow, testOnReturn);

						configBeans.add(bean);
					}
				}

			} else {
				logger.error("config is null");
			}

		} catch (Exception e) {
			logger.error("init redis error : " + e);
			return false;
		}

		return true;
	}

	public static Integer getVirtualNodeHashCode(int tag, int n) {
		return Utils.hashCode(MD5.encode(new StringBuffer("S_").append(tag).append("_N_").append(n).toString()));
	}

	/**
	 * 返回该虚拟节点对应的真实机器节点的信息
	 */
	private JedisPool getJedisPool(String key) {
		if (key == null) {
			return null;
		}
		SortedMap<Integer, JedisPool> tail = virtualNodeMap.tailMap(Utils.hashCode(MD5.encode(key))); // 沿环的顺时针找到一个虚拟节点
		if (tail.size() == 0) {
			return virtualNodeMap.get(virtualNodeMap.firstKey());
		}
		return tail.get(tail.firstKey()); // 返回该虚拟节点对应的真实机器节点的信息   
	}

	public void setBytes(String key, byte[] bytes, int sec) {
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.setex(key.getBytes(), sec, bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}

	public byte[] getBytes(String key) {
		if (key == null) {
			return null;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.get(key.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return null;
	}

	public <T> T get(String key, Class<T> t) {
		if (key == null) {
			return null;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			T result = (T) SerializerJson.deSerialize(jedis.get(key), t);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return null;
	}

	public void setForHour(String key, Object value, int hours) {
		if (key == null || value == null) {
			return;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.setex(key, hours * 3600, SerializerJson.serialize(value));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}

	public void setForSec(String key, Object value, int sec) {
		if (key == null || value == null) {
			return;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.setex(key, sec, SerializerJson.serialize(value));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}

	public void del(String key) {
		if (key == null) {
			return;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.del(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}

	public <T> Object hashGet(String key, String filed, Class<T> t) {
		if (key == null || filed == null) {
			return null;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			T result = (T) SerializerJson.deSerialize(jedis.hget(key, filed), t);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return null;
	}

	public <T> List<T> hashAllValues(String key, Class<T> t) {
		if (key == null) {
			return null;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			List<T> list = new ArrayList<>();
			List<String> values = jedis.hvals(key);
			for (String s : values) {
				list.add((T) SerializerJson.deSerialize(s, t));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return null;
	}

	public void hashDel(String key, String filed) {
		if (key == null || filed == null) {
			return;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.hdel(key, filed);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}

	public void hashAdd(String key, String filed, Object value) {
		if (key == null || value == null || value == null) {
			return;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.hset(key, filed, SerializerJson.serialize(value));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}

	public void setAdd(String key, Object value) {
		if (key == null || value == null) {
			return;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.sadd(key, SerializerJson.serialize(value));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}

	public void setRemove(String key, Object value) {
		if (key == null || value == null) {
			return;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.srem(key, SerializerJson.serialize(value));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}

	public long setSize(String key) {
		if (key == null) {
			return 0;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.scard(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return 0;
	}

	public <T> T setRandGet(String key, Class<T> t) {
		if (key == null) {
			return null;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			return (T) SerializerJson.deSerialize(jedis.srandmember(key), t);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return null;
	}

	public <T> List<T> setRandGet(String key, int count, Class<T> t) {
		if (key == null) {
			return null;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			List<T> list = new ArrayList<>();
			List<String> valueSet = jedis.srandmember(key, count);
			for (String s : valueSet) {
				list.add((T) SerializerJson.deSerialize(s, t));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return null;
	}

	public boolean setExist(String key, Object value) {
		if (key == null) {
			return false;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.sismember(key, SerializerJson.serialize(value));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return false;
	}

	public boolean exists(String key) {
		if (key == null) {
			return false;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.exists(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return false;
	}

	public void sortedSetAdd(String key, Object value, int score) {
		if (key == null || value == null) {
			return;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.zadd(key, score, SerializerJson.serialize(value));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}

	public <T> List<T> sortedSetGet(String key, int offset, int count, boolean asc, Class<T> t) {
		if (key == null) {
			return null;
		}
		JedisPool jedisPool = getJedisPool(key);
		Jedis jedis = jedisPool.getResource();
		try {
			List<T> list = new ArrayList<>();
			Set<String> strSet = new HashSet<String>();
			if (asc) {
				strSet = jedis.zrangeByScore(key, 0, 10000, offset, count);
			} else {
				strSet = jedis.zrevrangeByScore(key, 10000, 0, offset, count);
			}
			for (String s : strSet) {
				list.add((T) SerializerJson.deSerialize(s, t));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return null;
	}

	public boolean testCache(JedisPool pool) {

		Jedis jedis = pool.getResource();
		try {
			String test = jedis.set("test", "test");

			if (test.equals("OK")) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				pool.returnResource(jedis);
			}
		}
		return false;
	}

	public static void main(String[] args) {

		JedisUtilJson.getInstance().init();

		List<String> list = JedisUtilJson.getInstance().sortedSetGet("rank_cup_all", 0, 100, false, String.class);

		System.out.println(list.size());
		for (String s : list) {
			System.out.println(s);
		}

	}
}