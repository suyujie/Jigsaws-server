package gamecore.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 异步操作数据库
 * @author suiyujie
 */
public class AsyncDBTask extends DataAccessObject implements Runnable {

	private static Logger logger = LogManager.getLogger(AsyncDBTask.class.getName());

	private DBOperator dBOperator;
	private String sql;
	private Object[] args;
	private Long tagId;

	public AsyncDBTask(DBOperator dBOperator, Long tagId, String sql, Object[] args) {
		super();
		this.sql = sql;
		this.args = args;
		this.dBOperator = dBOperator;
		this.tagId = tagId;
	}

	@Override
	public void run() {
		Connection connection = getConn(this.dBOperator, tagId);
		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement(sql);

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
			ps.execute();

		} catch (Exception e) {
			logger.error(DbDebugUtil.toDebugSql(sql, args));
			e.printStackTrace();
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				logger.error(e);
			}
			close(connection);
		}
	}
}
