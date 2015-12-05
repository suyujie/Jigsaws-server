package gamecore.db;

import java.util.Arrays;

/**
 * db 调试
 */
public class DbDebugUtil extends DataAccessObject {

	public static String toDebugSql(String sql, Object[] args) {

		String result = sql;

		for (Object arg : args) {

			if (arg == null) {
				result = result.replaceFirst("\\?", "null");
			} else if (arg.getClass() == Integer.class) {
				result = result.replaceFirst("\\?", ((Integer) arg).toString());
			} else if (arg.getClass() == Long.class) {
				result = result.replaceFirst("\\?", ((Long) arg).toString());
			} else if (arg.getClass() == String.class) {
				result = result.replaceFirst("\\?", (String) arg);
			} else if (arg instanceof byte[]) {
				result = result.replaceFirst("\\?", Arrays.toString((byte[]) arg));
			}

		}

		return result;

	}

}
