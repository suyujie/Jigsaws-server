package gamecore.db;

import java.beans.PropertyVetoException;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.dbutils.DbUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public final class DBManager {

	private final static Logger logger = LogManager.getLogger(DBManager.class.getName());

	private HashMap<Integer, ComboPooledDataSource> DB_M = null;
	private HashMap<Integer, ComboPooledDataSource> DB_S = null;
	private HashMap<Integer, ComboPooledDataSource> DB_LOG = null;

	private final String ConfigFile_M = "properties/mysql_m.xml";
	private final String ConfigFile_S = "properties/mysql_s.xml";
	private final String ConfigFile_Log = "properties/mysql_log.xml";

	protected static DBManager dbManager = new DBManager();

	/**
	 * 获取唯一实例.
	 * @return
	 */
	public static DBManager getInstance() {
		if (dbManager != null) {
			return dbManager;
		} else {
			dbManager = new DBManager();
			dbManager.initConnPools();
			return dbManager;
		}
	}

	/**
	 * 初始化连接池
	 */
	public boolean initConnPools() {

		try {
			DB_M = createDataSource(ConfigFile_M);
			DB_S = createDataSource(ConfigFile_S);
			DB_LOG = createDataSource(ConfigFile_Log);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}

		return true;
	}

	private HashMap<Integer, ComboPooledDataSource> createDataSource(String configFile) throws PropertyVetoException {

		HashMap<Integer, ComboPooledDataSource> m = new HashMap<Integer, ComboPooledDataSource>();

		File file = new File(this.getClass().getResource("/").getPath() + configFile);

		if (!file.exists()) {
			logger.error("NOT FOUND mysql config file!");
			return null;
		}

		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(file);
			Element root = doc.getRootElement();

			Element dbs = root.element("mysqls");
			if (null != dbs) {
				@SuppressWarnings("unchecked")
				List<Element> dbList = dbs.elements("mysql");
				if (null != dbList) {
					for (Element db : dbList) {
						Integer tag = Integer.parseInt(db.attributeValue("tag"));
						String driver = db.element("driver").getTextTrim(); // 驱动器
						String databaseUrl = db.element("url").getTextTrim(); // 数据库连接URL
						String databaseUser = db.element("user").getTextTrim(); // 数据库用户名
						String databasePassword = db.element("password").getTextTrim(); // 数据库密码
						int minPoolSize = Integer.parseInt(db.element("minPoolSize").getTextTrim());// 连接池中保留的最小连接数
						int maxPoolSize = Integer.parseInt(db.element("maxPoolSize").getTextTrim());// 连接池中保留的最大连接数
						int initialPoolSize = Integer.parseInt(db.element("initialPoolSize").getTextTrim());// 初始化时池中的连接数
						int maxIdleTime = Integer.parseInt(db.element("maxIdleTime").getTextTrim());// 每个连接允许的最大空闲时间
						int acquireIncrement = Integer.parseInt(db.element("acquireIncrement").getTextTrim());// 当连接池中的连接耗尽时，一次同时获取的连接数
						int acquireRetryAttempts = Integer.parseInt(db.element("acquireRetryAttempts").getTextTrim());// 从数据库获取连接失败后重复尝试的次数
						int acquireRetryDelay = Integer.parseInt(db.element("acquireRetryDelay").getTextTrim());// 两次重试连接中间间隔的时间，单位：毫秒
						boolean testConnectionOnCheckin = Boolean.parseBoolean(db.element("testConnectionOnCheckin").getTextTrim());// 取到连接时是否同时校验连接的有效性
						boolean testConnectionOnCheckout = Boolean.parseBoolean(db.element("testConnectionOnCheckout").getTextTrim());// 在提交数据操作时是否校验有效性
						String automaticTestTable = db.element("automaticTestTable").getTextTrim();// c3p0 自建的测试表表名
						int idleConnectionTestPeriod = Integer.parseInt(db.element("idleTestPeriod").getTextTrim());// 检查所有连接池中的空闲连接的时间周期，单位：秒
						int checkoutTimeout = Integer.parseInt(db.element("checkoutTimeout").getTextTrim());// 当连接池中没有连接时，客户端程序获取新连接时的超时时间，单位：毫秒

						ComboPooledDataSource cpds = new ComboPooledDataSource();
						cpds.setDriverClass(driver);
						cpds.setJdbcUrl(databaseUrl);
						cpds.setUser(databaseUser);
						cpds.setPassword(databasePassword);
						cpds.setMinPoolSize(minPoolSize);
						cpds.setMaxPoolSize(maxPoolSize);
						cpds.setInitialPoolSize(initialPoolSize);
						cpds.setMaxIdleTime(maxIdleTime);
						cpds.setAcquireIncrement(acquireIncrement);
						cpds.setAcquireRetryAttempts(acquireRetryAttempts);
						cpds.setAcquireRetryDelay(acquireRetryDelay);
						cpds.setTestConnectionOnCheckin(testConnectionOnCheckin);
						cpds.setTestConnectionOnCheckout(testConnectionOnCheckout);
						cpds.setAutomaticTestTable(automaticTestTable);
						cpds.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
						cpds.setCheckoutTimeout(checkoutTimeout);
						m.put(tag, cpds);

					}
				}
			}

		} catch (Exception e) {
			logger.error("read config xml error", e);
			return null;
		}

		return m;
	}

	/**
	 * 获取指定的数据连接
	 */
	public Connection getThatConnection(DBOperator dbOperator, int tagId) {
		try {
			ComboPooledDataSource cpds = selectCPDS(dbOperator).get(tagId);
			return cpds.getConnection();
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	/**
	 * 获取数据库连接
	 * @param dBOperator
	 * @return
	 */
	public Connection getConnection(DBOperator dbOperator, Long tagId) {
		try {
			ComboPooledDataSource cpds = selectCPDS(dbOperator, tagId);
			return cpds.getConnection();
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	/**
	 * 获取多个数据库连接
	 * @param dBOperator
	 * @return
	 */
	public HashMap<Integer, Connection> getConnections(DBOperator dbOperator) {
		HashMap<Integer, Connection> map = new HashMap<Integer, Connection>();
		try {
			HashMap<Integer, ComboPooledDataSource> cpds = selectCPDS(dbOperator);

			Iterator<Entry<Integer, ComboPooledDataSource>> it = cpds.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, ComboPooledDataSource> entry = (Entry<Integer, ComboPooledDataSource>) it.next();
				map.put((Integer) entry.getKey(), ((ComboPooledDataSource) entry.getValue()).getConnection());
			}

			return map;
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	/**
	 * 关闭连接。
	 * @param conn
	 */
	public void closeConnection(Connection conn) {
		try {
			DbUtils.close(conn);
		} catch (SQLException e) {
			logger.error("#closeConnection", e);
		}
	}

	private ComboPooledDataSource selectCPDS(DBOperator dbOperator, Long tagId) {
		HashMap<Integer, ComboPooledDataSource> map = selectCPDS(dbOperator);
		return selectCPDS(dbOperator).get(((int) (tagId % map.size())) + 1);
	}

	private ComboPooledDataSource getCPDS(DBOperator dbOperator, Integer tagId) {
		return selectCPDS(dbOperator).get(tagId);
	}

	private HashMap<Integer, ComboPooledDataSource> selectCPDS(DBOperator dbOperator) {
		if (dbOperator == DBOperator.Write)
			return DB_M;
		if (dbOperator == DBOperator.Read)
			return DB_S;
		if (dbOperator == DBOperator.Log)
			return DB_LOG;
		return null;
	}

	//获取数据库的tag
	public List<Integer> getDbTags(DBOperator dbOperator) {

		HashMap<Integer, ComboPooledDataSource> map = selectCPDS(dbOperator);

		List<Integer> dbTags = new ArrayList<>();

		Iterator<Entry<Integer, ComboPooledDataSource>> iterator = map.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<Integer, ComboPooledDataSource> entry = iterator.next();
			Integer key = entry.getKey();
			dbTags.add(key);
		}

		return dbTags;

	}

	//HashMap<dbTag1, HashMap<全部,忙,闲, num>>
	public HashMap<String, Integer> getNumConnections(DBOperator dbOperator, Integer tag) {

		HashMap<String, Integer> nums = new HashMap<String, Integer>();

		ComboPooledDataSource cpds = getCPDS(dbOperator, tag);
		try {
			nums.put("all", cpds.getNumConnections());
			nums.put("busy", cpds.getNumBusyConnections());
			nums.put("idle", cpds.getNumIdleConnections());
		} catch (SQLException e) {
			logger.error(e);
		}

		return nums;
	}

}
