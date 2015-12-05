package gamecore.db;

import gamecore.task.TaskCenter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 异步 批量执行sql
 * @author suiyujie
 */
public class AsyncDBBacthTask extends DataAccessObject implements Runnable {

	private static Logger logger = LogManager.getLogger(AsyncDBBacthTask.class.getName());

	private List<String> sqls;
	private List<Object[]> args;
	private DBOperator dBOperator;
	private Long tagId;

	public AsyncDBBacthTask(DBOperator dBOperator, Long tagId, List<String> sqls, List<Object[]> args) {
		super();
		this.sqls = sqls;
		this.args = args;
		this.dBOperator = dBOperator;
		this.tagId = tagId;
	}

	@Override
	public void run() {

		final byte[] monitor = new byte[0];

		Iterator<String> sqlIterator = sqls.iterator();
		Iterator<Object[]> argsIterator = args.iterator();

		if (sqls.size() != args.size()) {
			return;
		}

		while (sqlIterator.hasNext() && argsIterator.hasNext()) {

			//sql
			final String currentSql = sqlIterator.next();
			final Object[] currentArgs = argsIterator.next();

			TaskCenter.getInstance().executeWithSlidingWindow(new Runnable() {
				@Override
				public void run() {
					Connection connection = getConn(dBOperator, tagId);
					PreparedStatement ps = null;
					try {
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
					} catch (Exception e) {
						logger.error(DbDebugUtil.toDebugSql(currentSql, currentArgs));
						e.printStackTrace();
					} finally {
						close(connection);
					}
					synchronized (monitor) {
						monitor.notifyAll();
					}
				}
			});
		}

		do {
			synchronized (monitor) {
				try {
					monitor.wait(10000);
				} catch (InterruptedException e) {
					logger.error(e);
				}
			}
		} while (sqlIterator.hasNext() && argsIterator.hasNext());
	}
}
