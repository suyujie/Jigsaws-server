package gamecore.db;

import gamecore.entity.AbstractEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.jdbc.Blob;

/**
 * 同步执行sql
 * @author suiyujie
 */
public class SyncDBUtil extends DataAccessObject {

	private static Logger logger = LogManager.getLogger(SyncDBUtil.class.getName());

	private static final String Move = "move";

	public static boolean execute(DBOperator dBOperator, Long tagId, String sql, Object[] args) {

		boolean result = false;

		Connection connection = getConn(dBOperator, tagId);
		PreparedStatement ps = null;

		try {
			ps = connection.prepareCall(sql);

			for (int i = 1; i <= args.length; i++) {

				Object o = args[i - 1];

				if (o == null) {
					ps.setNull(i, Types.NULL);
				} else if (o.getClass() == Integer.class) {
					ps.setInt(i, (Integer) o);
				} else if (o.getClass() == Long.class) {
					ps.setLong(i, (Long) o);
				} else if (o.getClass() == String.class) {
					ps.setString(i, (String) o);
				} else if (o instanceof byte[]) {
					ps.setObject(i, o);
				}

			}
			result = ps.executeUpdate() == 1;

		} catch (Exception e) {
			logger.error(sql);
			for (int j = 1; j <= args.length; j++) {
				Object o = args[j - 1];
				if (o == null) {
					logger.error("null  ");
				} else if (o.getClass() == Integer.class) {
					logger.error((Integer) o + "  ");
				} else if (o.getClass() == Long.class) {
					logger.error((Long) o + "  ");
				} else if (o.getClass() == String.class) {
					logger.error((String) o + "  ");
				} else if (o.getClass() == Blob.class) {
					logger.error((String) o + "  ");
				} else if (o.getClass() == AbstractEntity.class) {
					logger.error(o + "  ");
				}
			}
			e.printStackTrace();
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(connection);
		}
		return result;

	}

	public static Map<String, Object> readMap(DBOperator dBOperator, Long tagId, String sql, Object[] args, boolean checkAll) throws SQLException {

		QueryRunner query = new QueryRunner();
		Connection conn = getConn(dBOperator, tagId);
		Map<String, Object> map = null;
		try {
			map = query.query(conn, sql, new MapHandler(), args);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}

		//如果没有找到，所有的库一起找
		if (checkAll && map == null) {
			map = readMap(dBOperator, sql, args);
			if (map != null) {//数据应该迁移
				map.put(Move, true);
			}
		}

		return map;
	}

	public static Map<String, Object> readMap(DBOperator dBOperator, String sql, Object[] args) {

		Map<String, Object> map = null;

		Map<Integer, Connection> connections = getConns(dBOperator);

		ExecutorService pool = Executors.newFixedThreadPool(connections.size());

		List<Future<Map<String, Object>>> list = new ArrayList<Future<Map<String, Object>>>();

		Iterator<Entry<Integer, Connection>> it = connections.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Connection> entry = (Entry<Integer, Connection>) it.next();
			Callable<Map<String, Object>> c = new DBCallableReadMap((Integer) entry.getKey(), (Connection) entry.getValue(), sql, args);
			Future<Map<String, Object>> f = pool.submit(c);
			list.add(f);
		}

		pool.shutdown();

		for (Future<Map<String, Object>> f : list) {
			try {
				map = f.get();
				if (map != null) {
					return map;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return map;

	}

	public static List<Map<String, Object>> readList(DBOperator dBOperator, Long tagId, String sql, Object[] args, boolean checkAll) {

		QueryRunner query = new QueryRunner();
		Connection conn = getConn(dBOperator, tagId);
		List<Map<String, Object>> list = null;
		try {
			list = query.query(conn, sql, new MapListHandler(), args);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}

		if (checkAll && (list == null || list.isEmpty())) {
			list = readList(dBOperator, sql, args);
		}

		return list;
	}

	public static List<Map<String, Object>> readList(DBOperator dBOperator, String sql, Object[] args) {

		List<Map<String, Object>> list = new ArrayList<>();

		Map<Integer, Connection> connections = getConns(dBOperator);

		ExecutorService pool = Executors.newFixedThreadPool(connections.size());

		List<Future<List<Map<String, Object>>>> futureList = new ArrayList<Future<List<Map<String, Object>>>>();

		Iterator<Entry<Integer, Connection>> it = connections.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Connection> entry = (Entry<Integer, Connection>) it.next();
			Callable<List<Map<String, Object>>> c = new DBCallableReadList((Integer) entry.getKey(), (Connection) entry.getValue(), sql, args);
			Future<List<Map<String, Object>>> f = pool.submit(c);
			futureList.add(f);
		}

		pool.shutdown();

		for (Future<List<Map<String, Object>>> f : futureList) {
			try {
				list.addAll(f.get());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return list.isEmpty() ? null : list;

	}

}

/**
 * 多线程访问数据库,读取map
 */
class DBCallableReadMap extends DataAccessObject implements Callable<Map<String, Object>> {

	public static final String DbTag = "dbTag";

	private Integer dbtag;
	private Connection conn;
	private String sql;
	private Object[] args;

	/**
	 * 线程类构造函数，传入线程序号
	 * @param taskNum
	 */
	public DBCallableReadMap(Integer dbtag, Connection conn, String sql, Object[] args) {
		this.dbtag = dbtag;
		this.conn = conn;
		this.sql = sql;
		this.args = args;

	}

	public Map<String, Object> call() throws Exception {

		QueryRunner query = new QueryRunner();
		Map<String, Object> map = null;
		try {
			map = query.query(conn, sql, new MapHandler(), args);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
		if (map != null) {
			map.put(DbTag, dbtag);
		}
		return map;
	}

}

/**
 * 多线程访问数据库,读取list
 */
class DBCallableReadList extends DataAccessObject implements Callable<List<Map<String, Object>>> {

	public static final String DbTag = "dbTag";

	private Integer dbtag;
	private Connection conn;
	private String sql;
	private Object[] args;

	/**
	 * 线程类构造函数，传入线程序号
	 * @param taskNum
	 */
	public DBCallableReadList(Integer dbtag, Connection conn, String sql, Object[] args) {
		this.dbtag = dbtag;
		this.conn = conn;
		this.sql = sql;
		this.args = args;

	}

	public List<Map<String, Object>> call() throws Exception {

		QueryRunner query = new QueryRunner();
		List<Map<String, Object>> listResult = null;
		List<Map<String, Object>> list = new ArrayList<>();
		try {
			listResult = query.query(conn, sql, new MapListHandler(), args);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
		for (Map<String, Object> map : listResult) {
			map.put(DbTag, dbtag);
			list.add(map);
		}

		return list;
	}

}
