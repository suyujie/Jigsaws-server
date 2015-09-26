package gamecore.db;

import java.sql.Connection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * DbUtils父类，包含了一些常用的方法，获得连接、关闭连接 . 开启关闭事务
 */
public class DataAccessObject {

	private static Logger logger = LogManager.getLogger(DataAccessObject.class.getName());

	public DataAccessObject() {
	}

	public static Map<Integer, Connection> getConns(DBOperator dBOperator) {
		Map<Integer, Connection> conns = DBManager.getInstance().getConnections(dBOperator);
		return conns;
	}

	public static Connection getConn(DBOperator dBOperator, Long tagId) {
		Connection conn = DBManager.getInstance().getConnection(dBOperator, tagId);
		return conn;
	}

	public static void close(Connection conn) {
		if (null == conn) {
			return;
		}
		try {
			DBManager.getInstance().closeConnection(conn);
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
