package gamecore.db;

import gamecore.entity.AbstractEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.jdbc.Blob;

public class AsyncDBTransactionTask extends DataAccessObject implements Runnable {

	private static Logger logger = LogManager.getLogger(AsyncDBTransactionTask.class.getName());

	private List<String> sqls;
	private List<Object[]> args;
	private DBOperator dBOperator;
	private Long tagId;

	public AsyncDBTransactionTask(DBOperator dBOperator, Long tagId, List<String> sqls, List<Object[]> args) {
		super();
		this.sqls = sqls;
		this.args = args;
		this.dBOperator = dBOperator;
		this.tagId = tagId;
	}

	@Override
	public void run() {

		Connection connection = getConn(this.dBOperator, tagId);
		PreparedStatement ps = null;

		try {
			connection.setAutoCommit(false);

			if (sqls.size() == args.size()) {

				for (int j = 0; j < sqls.size(); j++) {

					String currentSql = sqls.get(j);
					Object[] currentArgs = args.get(j);

					ps = connection.prepareStatement(currentSql);

					for (int i = 1; i <= currentArgs.length; i++) {

						Object o = currentArgs[i - 1];
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
					ps.close();
				}
			}

			connection.commit();

		} catch (Exception e) {
			try {//回滚
				connection.rollback();
			} catch (SQLException e1) {
				logger.error(e1);
			}
			for (int i = 0; i < sqls.size(); i++) {
				logger.error(sqls.get(i));
				Object[] currentArgs = args.get(i);
				for (int j = 1; j <= currentArgs.length; j++) {

					Object o = currentArgs[j - 1];

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
			}
			e.printStackTrace();
		} finally {

			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				logger.error(e);
			}

			close(connection);

		}
	}
}
