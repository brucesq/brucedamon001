/**
 * 
 */
package com.hunthawk.reader.enhance.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author BruceSun
 * 
 */
// 作用：提供字符串形式的日期到毫秒的转换，和毫秒到字符串日期以及年、月、日的转换
public class ToolDateUtil {

	private static GregorianCalendar calendar = new GregorianCalendar();

	public static final String DATE_YEAR_MONTH_FORMAT = "yyyyMM";

	public static final String MONTH_FORMAT = "yyyy-MM";

	public static final String DATE_FORMAT = "yyyy-MM-dd";
	
	public static final String DATE_FORMAT1 = "yyyyMMdd";

	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final String DATETIME_FORMAT2 = "yyyy-MM-dd HH:mm";

	public static final String DATETIME_FORMAT3 = "yyyy-MM-dd HH:mm";

	public static final String DATETIME_FORMAT4 = "yy-MM-dd HH:mm:ss";

	public static final String TIME_FORMAT = "yyyyMMddHHmmss";

	public static final String DATE_FORMAT2 = "MM/dd/yy";

	public static final String DATE_FORMAT3 = "MM-dd HH:mm:ss";

	public static final String DATE_FORMAT4 = "yyyyMMdd_HHmm";

	public ToolDateUtil() {

	}

	// 提供“yyyy-mm-dd”形式的字符串到毫秒的转换
	public static long getMillis(String dateString) {
		String[] date = dateString.split("-");
		return getMillis(date[0], date[1], date[2]);
	}

	// 根据输入的年、月、日，转换成毫秒表示的时间
	public static long getMillis(int year, int month, int day) {
		GregorianCalendar calendar = new GregorianCalendar(year, month, day);
		return calendar.getTimeInMillis();
	}

	// 根据输入的年、月、日，转换成毫秒表示的时间
	public static long getMillis(String yearString, String monthString,
			String dayString) {
		int year = Integer.parseInt(yearString);
		int month = Integer.parseInt(monthString);
		int day = Integer.parseInt(dayString);
		return getMillis(year, month, day);
	}

	// 获得当前时间的毫秒表示
	public static long getNow() {
		GregorianCalendar now = new GregorianCalendar();
		return now.getTimeInMillis();
	}

	// 根据输入的毫秒数，获得日期字符串
	public static String getDate(long millis, String format) {
		calendar.setTimeInMillis(millis);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String result = sdf.format(calendar.getTime());
		return result;
	}

	// 根据输入的毫秒数，获得年份
	public static int getYear(long millis) {
		calendar.setTimeInMillis(millis);
		return calendar.get(Calendar.YEAR);
	}

	// 根据输入的毫秒数，获得月份
	public static int getMonth(long millis) {
		calendar.setTimeInMillis(millis);
		return calendar.get(Calendar.MONTH);
	}

	// 根据输入的毫秒数，获得日期
	public static int getDay(long millis) {
		calendar.setTimeInMillis(millis);
		return calendar.get(Calendar.DATE);
	}

	// 根据输入的毫秒数，获得小时
	public static int getHour(long millis) {
		calendar.setTimeInMillis(millis);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	// 根据输入的毫秒数，获得分钟
	public static int getMinute(long millis) {
		calendar.setTimeInMillis(millis);
		return calendar.get(Calendar.MINUTE);
	}

	// 根据输入的毫秒数，获得秒
	public static int getSecond(long millis) {
		calendar.setTimeInMillis(millis);
		return calendar.get(Calendar.SECOND);
	}

	public static int stringToInt(String stringValue) {
		return stringToInt(stringValue, -1);
	}

	public static int stringToInt(String stringValue, int defaultValue) {
		int intValue = defaultValue;
		if (stringValue != null) {
			try {
				intValue = Integer.parseInt(stringValue);
			} catch (NumberFormatException ex) {
				intValue = defaultValue;
			}
		}
		return intValue;
	}

	/**
	 * 获得当前年
	 * 
	 * @return java.lang.String
	 */
	public static String getNowYear() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		return String.valueOf(year);
	}

	/**
	 * 获得当前月
	 * 
	 * @return java.lang.String
	 */
	public static String getNowMonth() {
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH) + 1;
		if (month < 10) {
			return "0" + month;
		} else {
			return String.valueOf(month);
		}
	}

	/**
	 * 获得当前日
	 * 
	 * @return java.lang.String
	 */
	public static String getNowDay() {
		return dateToString(new Date(), "dd");

	}

	/**
	 * 得到两个日期之间的所有天数字符串
	 * 
	 * @param beginDate
	 *            String
	 * @param endDate
	 *            String
	 * @return java.lang.String[]
	 * @throws ParseException
	 */
	public static String[] getTwoDateDiffer(String beginDate, String endDate)
			throws ParseException {

		SimpleDateFormat timeFormatter = new SimpleDateFormat(DATE_FORMAT);
		Date dateBegin = timeFormatter.parse(beginDate);
		Date dateEnd = timeFormatter.parse(endDate);
		long millionsecondsInOneDay = (24 * 3600 * 1000);
		long distinctionDays = (dateEnd.getTime() - dateBegin.getTime())
				/ millionsecondsInOneDay + 1;

		String[] dateArray = new String[new Long(distinctionDays).intValue()];

		if (distinctionDays == 1) {
			dateArray[0] = timeFormatter.format(dateBegin);
		} else {
			for (int i = 0; i < distinctionDays; i++) {
				Date tempDate = new Date(dateBegin.getTime() + i
						* millionsecondsInOneDay);
				dateArray[i] = timeFormatter.format(tempDate);
			}
		}
		return dateArray;

	}

	public static long getTwoDateDiff(String beginDate, String endDate,
			String date_format) throws ParseException {

		SimpleDateFormat timeFormatter = new SimpleDateFormat(date_format);
		Date dateBegin = timeFormatter.parse(beginDate);
		Date dateEnd = timeFormatter.parse(endDate);

		long millionsecondsInOneMinute = (60 * 1000);

		long distinctionMinute = (dateEnd.getTime() - dateBegin.getTime())
				/ millionsecondsInOneMinute;

		return distinctionMinute;
	}

	public static long getTwoDateDiff2(String date) throws ParseException {

		SimpleDateFormat timeFormatter = new SimpleDateFormat(DATETIME_FORMAT2);
		Date dateBegin = timeFormatter.parse(date);
		Date dateEnd = timeFormatter.parse(getToday(DATETIME_FORMAT2));

		long millionsecondsInOneMinute = (60 * 1000);

		long distinctionMinute = (dateBegin.getTime() - dateEnd.getTime())
				/ millionsecondsInOneMinute;

		return distinctionMinute;
	}

	public static String getYesterday() {

		long now = ToolDateUtil.getNow();
		String yesterday = getDate(now - 1000 * 60 * 60 * 24,
				ToolDateUtil.DATE_FORMAT);

		return yesterday;
	}

	public static String getTodayBefore(int n) {

		long now = ToolDateUtil.getNow();
		String day = getDate(now - 1000 * 60 * 60 * 24 * n,
				ToolDateUtil.DATE_FORMAT);

		return day;
	}

	public static Date getTodayAfter(int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, n);
		return calendar.getTime();
	}

	public static int getTwoDateday(String endDate) throws ParseException {

		SimpleDateFormat timeFormatter = new SimpleDateFormat(DATE_FORMAT);
		String beginDate = getToday(DATE_FORMAT);
		Date dateBegin = timeFormatter.parse(beginDate);
		Date dateEnd = timeFormatter.parse(endDate);
		long millionsecondsInOneDay = (24 * 3600 * 1000);
		long m = dateEnd.getTime() - dateBegin.getTime();
		long i = m / millionsecondsInOneDay;

		return Integer.parseInt(Long.toString(i));

	}

	public static int getTwoDateday(String beginDate, String endDate)
			throws ParseException {

		SimpleDateFormat timeFormatter = new SimpleDateFormat(DATE_FORMAT);
		Date dateBegin = timeFormatter.parse(beginDate);
		Date dateEnd = timeFormatter.parse(endDate);
		long millionsecondsInOneDay = (24 * 3600 * 1000);
		long m = dateEnd.getTime() - dateBegin.getTime();
		long i = m / millionsecondsInOneDay;

		return Integer.parseInt(Long.toString(i));

	}

	public static void main(String[] args) throws Exception {

	}

	// 得到上个月的最后一天
	public static String getLastMonthEndDay() {
		Calendar c = Calendar.getInstance();
		// 得到本月第一天
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
		// 本月第一天的前一天就是上个月最后一天
		c.add(Calendar.DAY_OF_MONTH, -1);
		java.util.Date date = c.getTime();
		String result = ToolDateUtil.dateToString(date,
				ToolDateUtil.DATE_FORMAT);
		return result;
	}

	// 得到上个月的第一天
	public static String getLastMonthBeginDay() {
		Calendar c = Calendar.getInstance();
		// 得到本月第一天
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
		// 本月第一天的前一天就是上个月最后一天
		c.add(Calendar.DAY_OF_MONTH, -1);
		// 设置上个月的第一天
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);

		java.util.Date date = c.getTime();
		String result = ToolDateUtil.dateToString(date,
				ToolDateUtil.DATE_FORMAT);
		return result;
	}

	// 得到本月的第一天
	public static String getMonthBeginDay() {
		Calendar c = Calendar.getInstance();
		// 得到本月第一天
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
		java.util.Date date = c.getTime();
		String result = ToolDateUtil.dateToString(date,
				ToolDateUtil.DATE_FORMAT);
		return result;
	}

	public static Date stringToDate(String stringValue) {
		return stringToDate(stringValue, ToolDateUtil.DATETIME_FORMAT);
	}

	public static Date stringToDate(String stringValue, String format) {
		Date dateValue = null;
		if (stringValue != null) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				dateValue = dateFormat.parse(stringValue);

			} catch (ParseException ex) {
			}
		}
		return dateValue;
	}

	public static String dateToString(Date dateValue) {
		return dateToString(dateValue, ToolDateUtil.DATETIME_FORMAT);
	}

	public static String dateToString(Date dateValue, String format) {
		if (dateValue == null) {
			return null;
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			return dateFormat.format(dateValue);
		}
	}

	public static Date getDate() {
		Calendar c = Calendar.getInstance();
		return stringToDate(dateToString(c.getTime()));
	}

	public static Date getDate(String format) {
		Calendar c = Calendar.getInstance();
		return stringToDate(dateToString(c.getTime(), format));
	}

	public static String convertTohtmlFormat(String text) {
		text = text.replaceAll("<", "&lt;");
		text = text.replaceAll(">", "&gt;");
		text = text.replaceAll("\n", "<br>");
		return text;
	}

	public static String getToday() {
		return dateToString(new Date(), DATE_FORMAT);
	}

	public static String getToday(String format) {
		String dateStr = dateToString(new Date(), format);
		return dateStr;
	}

	/**
	 * 判断某个日期是否跟另一个日期一样
	 * 
	 * @param oneDate
	 *            Date
	 * @param twodate
	 *            Date
	 * @return int
	 */
	public static int dateCompareDate(Date oneDate, Date twodate) {

		SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT);
		return (stringToDate(sf.format(oneDate), DATE_FORMAT))
				.compareTo(stringToDate(sf.format(twodate), DATE_FORMAT));
	}

	public static String convertDateFormat(String strd, String f1, String f2) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(f1);
			SimpleDateFormat sdf2 = new SimpleDateFormat(f2);
			return sdf2.format(sdf.parse(strd));
		} catch (Exception ex) {
			return null;
		}
	}

	public static boolean isBetween(String startDate, String endDate,
			String useDate) {
		try {
			int startToToday = getTwoDateday(startDate, useDate);
			int todayToEndday = getTwoDateday(useDate, endDate);

			if ((startToToday >= 0) && (todayToEndday >= 0))
				return true;
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static String formatDate(String strd, String f1) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(f1);
			SimpleDateFormat sdf2 = new SimpleDateFormat(f1);
			return sdf2.format(sdf.parse(strd));
		} catch (Exception ex) {
			return null;
		}
	}

	public static String formatDate(String strd, String f1, String f2) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(f1);
			SimpleDateFormat sdf2 = new SimpleDateFormat(f2);
			return sdf2.format(sdf.parse(strd));
		} catch (Exception ex) {
			return null;
		}
	}
}
