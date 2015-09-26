package gamecore.util;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static final DateFormat sdf_t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static final DateFormat sdf_d = new SimpleDateFormat("yyyy-MM-dd");

	public static Date string2Date(String s) {
		Date date = new Date();
		try {
			date = sdf_d.parse(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String date2String(Date d) {
		String dateStr = "";
		try {
			dateStr = sdf_d.format(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}

	public static long date2Long(Date d) {
		return d.getTime();
	}

	public static Date long2Date(Long l) {
		if (l == null) {
			return null;
		}
		Date date = new Date(l);
		return date;
	}

	public static Date bigInt2Date(BigInteger l) {
		if (l == null) {
			return null;
		}
		Date date = new Date(l.longValue());
		return date;
	}

	public static String long2String(long d) {
		if (d == 0) {
			return "";
		}
		String dateStr = "";
		try {
			dateStr = sdf_d.format(long2Date(d));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}

	public static long string2Long(String s) {
		Date date = string2Date(s);
		long d = date2Long(date);
		return d;
	}

	public static String bigInt2String(BigInteger d) {
		if (d.longValue() == 0) {
			return "";
		}
		String dateStr = "";
		try {
			dateStr = sdf_d.format(bigInt2Date(d));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}

	public static int getIntervalDaysOfTwoDays(long dayA, long dayB) {

		if (dayA > dayB) {//保证A在B之前
			long dayC = dayA;
			dayA = dayB;
			dayB = dayC;
		}

		Calendar calendarA = Calendar.getInstance();
		Calendar calendarB = Calendar.getInstance();
		calendarA.setTime(new Date(dayA));
		calendarB.setTime(new Date(dayB));

		int yearA = calendarA.get(Calendar.YEAR);
		int yearB = calendarB.get(Calendar.YEAR);

		int days = 0;

		days = calendarB.get(Calendar.DAY_OF_YEAR) - calendarA.get(Calendar.DAY_OF_YEAR);
		if (yearA != yearB) {
			do {
				days += calendarA.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
				calendarA.add(Calendar.YEAR, 1);
			} while (calendarA.get(Calendar.YEAR) != yearB);
		}

		return days;

	}

	/*
	 * 判断两个日期是否是同一天
	 */
	public static boolean isSameDay(long dateA, long dateB) {
		Calendar calendarA = Calendar.getInstance();
		Calendar calendarB = Calendar.getInstance();
		calendarA.setTime(new Date(dateA));
		calendarB.setTime(new Date(dateB));

		int yearA = calendarA.get(Calendar.YEAR);
		int yearB = calendarB.get(Calendar.YEAR);
		int dayA = calendarA.get(Calendar.DAY_OF_YEAR);
		int dayB = calendarB.get(Calendar.DAY_OF_YEAR);

		if (yearA == yearB && dayA == dayB) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * 获取日期是星期几
	 */
	public static int dayOfWeek(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(time));
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	/*
	 * 获取日期是星期几
	 */
	public static int dayOfWeek() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	public static int dayOfYear() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	//获取日期这一天开始的时间戳
	public static long dayBegin(Date d) {
		Calendar db = Calendar.getInstance();
		db.setTime(d);
		db.set(Calendar.HOUR_OF_DAY, 0);
		db.set(Calendar.MINUTE, 0);
		db.set(Calendar.SECOND, 0);
		db.set(Calendar.MILLISECOND, 0);
		return db.getTimeInMillis();
	}

	public static long dayBegin(long d) {
		return dayBegin(new Date(d));
	}

	//获取日期这一天结束的时间戳
	public static long dayEnd(Date d) {
		Calendar db = Calendar.getInstance();
		db.setTime(d);
		db.set(Calendar.HOUR_OF_DAY, 23);
		db.set(Calendar.MINUTE, 59);
		db.set(Calendar.SECOND, 59);
		db.set(Calendar.MILLISECOND, 999);
		return db.getTimeInMillis();
	}

	//获取日期这一天结束的时间戳
	public static long dayEnd(long d) {
		return dayEnd(new Date(d));
	}

	/*
	 * 获取日期所在月的第一天
	 */
	public static long getFirstDayOfMonth(long time) {
		Date date = DateUtils.long2Date(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		//给该月减去当前日期天数
		calendar.add(Calendar.DAY_OF_MONTH, -(calendar.get(Calendar.DAY_OF_MONTH) - 1));

		return DateUtils.date2Long(calendar.getTime());
	}

	/*
	 * 获取日期所在月的最后一天
	 */
	public static long getLastDayOfMonth(long time) {
		Date date = DateUtils.long2Date(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		//用该月总天数减去该月当前日期天数
		calendar.add(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH));

		return DateUtils.date2Long(calendar.getTime());
	}

	/*
	 * 获取日期所在周的第一天
	 */
	public static long getFirstDayOfWeek(long time) {
		Date date = DateUtils.long2Date(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		calendar.add(Calendar.DAY_OF_WEEK, -calendar.get(Calendar.DAY_OF_WEEK) + 1);

		return DateUtils.date2Long(calendar.getTime());
	}

	/*
	 * 获取日期所在周的最后一天
	 */
	public static long getLastDayOfWeek(long time) {
		Date date = DateUtils.long2Date(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		calendar.add(Calendar.DAY_OF_WEEK, 7 - calendar.get(Calendar.DAY_OF_WEEK));

		return DateUtils.date2Long(calendar.getTime());
	}

	//	public static void main(String[] args) {
	//		System.out.println(dayOfWeek());
	//
	//		long d = System.currentTimeMillis();
	//
	//		System.out.println(dayBegin(d));
	//		System.out.println(dayEnd(d));
	//	}

}
