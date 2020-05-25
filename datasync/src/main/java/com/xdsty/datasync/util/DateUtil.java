package com.xdsty.datasync.util;

import org.apache.commons.lang.time.FastDateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author 张富华
 * @date 2020/3/19 15:24
 */
public class DateUtil {
    /**
     * 日期格式化 yyyy-MM-dd
     */
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd";
    /**
     * 日期格式化 yyyy-MM-dd HH:mm
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    /**
     * 日期格式化 yyyy-MM-dd HH:mm:ss
     */
    public static final String DATE_SECOND_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * ThreadLocal
     */
    private static final ThreadLocal<HashMap<String, SimpleDateFormat>> CUSTOMER_MAP_THREAD = new
            ThreadLocal<>();

    private static FastDateFormat dateFormat = FastDateFormat.getInstance(DEFAULT_PATTERN);

    private static FastDateFormat dateTimeFormat = FastDateFormat.getInstance(DATE_TIME_PATTERN);

    private static FastDateFormat dateSecondFormat = FastDateFormat.getInstance(DATE_SECOND_PATTERN);

    /**
     * Description: 日期转化指定格式的字符串型日期
     *
     * @param pUtilDate java.util.Date
     * @param pFormat   日期格式
     * @return 字符串格式日期
     */

    public static String date2String(Date pUtilDate, String pFormat) {
        String result = "";
        if (pUtilDate != null) {
            SimpleDateFormat sdf = getSimpleDateFormat(pFormat);
            result = sdf.format(pUtilDate);
        }
        return result;
    }

    /**
     * Description: 日期转化指定格式的字符串型日期
     *
     * @param pUtilDate java.util.Date
     * @return 字符串格式日期
     */
    public static String date2String(
            Date pUtilDate) {
        return dateFormat.format(pUtilDate);
    }


    public static String dateTimeToString(Date pUtilDate) {
        if (pUtilDate == null) {
            return "";
        }
        return dateTimeFormat.format(pUtilDate);
    }


    /**
     * Description: 日期转化指定格式的字符串型日期
     *
     * @param pUtilDate java.util.Date
     * @return 字符串格式日期
     */
    public static String date2SecondString(
            Date pUtilDate) {
        return dateSecondFormat.format(pUtilDate);
    }

    /**
     * Description: 将日期字符串转换成日期型
     *
     * @param dateStr
     * @return
     */
    public static Date dateString2Date(String dateStr) {
        return dateString2Date(dateStr, DEFAULT_PATTERN);
    }

    /**
     * Description: 将日期字符串转换成年月日时分秒类型
     *
     * @param dateStr
     * @return
     */
    public static Date dateString2MinDate(String dateStr) {
        return dateString2Date(dateStr, DATE_TIME_PATTERN);
    }

    /**
     * Description: 将日期字符串转换成年月日时分秒类型
     *
     * @param dateStr
     * @return
     */
    public static Date dateString2SecondDate(String dateStr) {
        return dateString2Date(dateStr, DATE_SECOND_PATTERN);
    }

    /**
     * Description: 将日期字符串转换成指定格式日期
     *
     * @param dateStr
     * @param partner
     * @return
     */
    public static Date dateString2Date(String dateStr, String partner) {

        try {
            SimpleDateFormat formatter = getSimpleDateFormat(partner);
            ParsePosition pos = new ParsePosition(0);
            return formatter.parse(dateStr, pos);
        } catch (NullPointerException e) {
            return null;
        }
    }

    private static SimpleDateFormat getSimpleDateFormat(String pattern) {
        SimpleDateFormat simpleDateFormat;
        HashMap<String, SimpleDateFormat> simpleDateFormatMap = CUSTOMER_MAP_THREAD.get();
        if (simpleDateFormatMap != null && simpleDateFormatMap.containsKey(pattern)) {
            simpleDateFormat = simpleDateFormatMap.get(pattern);
        } else {
            simpleDateFormat = new SimpleDateFormat(pattern);
            if (simpleDateFormatMap == null) {
                simpleDateFormatMap = new HashMap<>();
            }
            simpleDateFormatMap.put(pattern, simpleDateFormat);
            CUSTOMER_MAP_THREAD.set(simpleDateFormatMap);
        }

        return simpleDateFormat;
    }

    /**
     * Description: 获取指定日期的月份
     *
     * @param pDate java.util.Date
     * @return int 月份
     */
    public static int getMonthOfDate(Date pDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(pDate);
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * Description: 获取日期字符串的月份
     *
     * @param date 字符串日期
     * @return int 月份
     */
    public static int getMonthOfDate(String date) {
        return getMonthOfDate(dateString2Date(date));
    }

    /**
     * 获取指定日期的日份
     *
     * @param pDate util.Date日期
     * @return int 日份
     */
    public static int getDayOfDate(Date pDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(pDate);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获得指定日期及指定天数之内的所有日期列表
     *
     * @param pDate 指定日期 格式:yyyy-MM-dd
     * @param count 取指定日期后的count天
     * @return
     * @throws ParseException
     */
    public static List<String> getDatePeriodDay(String pDate, int count)
            throws ParseException {
        List<String> v = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse(pDate));
        v.add(sdf.format(calendar.getTime()));

        for (int i = 0; i < count - 1; i++) {
            calendar.add(Calendar.DATE, 1);
            v.add(dateFormat.format(calendar.getTime()));
        }

        return v;
    }
}
